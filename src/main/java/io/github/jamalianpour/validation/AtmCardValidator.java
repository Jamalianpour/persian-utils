package io.github.jamalianpour.validation;

import io.github.jamalianpour.number.PersianNumberConverter;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Validator and utilities for ATM/Debit cards with Iranian bank card support.
 * Implements the Luhn algorithm for card number validation and provides BIN identification.
 */
public class AtmCardValidator {

    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^[0-9]{16}$");
    private static final Pattern CVV2_PATTERN = Pattern.compile("^[0-9]{3,4}$");
    private static final Pattern EXPIRY_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/?([0-9]{2}|[0-9]{4})$");

    // Iranian bank BIN (Bank Identification Number) ranges
    private static final Map<String, CardIssuerInfo> CARD_ISSUERS = new HashMap<>();
    static {
        // Iranian Banks - 6-digit BIN prefixes
        CARD_ISSUERS.put("603770", new CardIssuerInfo("603770", "بانک کشاورزی", "Bank Keshavarzi", "KESHAVARZI"));
        CARD_ISSUERS.put("639607", new CardIssuerInfo("639607", "بانک اقتصاد نوین", "Eghtesad Novin Bank", "EN"));
        CARD_ISSUERS.put("627760", new CardIssuerInfo("627760", "پست بانک", "Post Bank", "POST"));
        CARD_ISSUERS.put("639599", new CardIssuerInfo("639599", "بانک قوامین", "Ghavamin Bank", "GHAVAMIN"));
        CARD_ISSUERS.put("504862", new CardIssuerInfo("504862", "بانک شهر", "Bank Shahr", "SHAHR"));
        CARD_ISSUERS.put("627412", new CardIssuerInfo("627412", "بانک اقتصاد نوین", "Eghtesad Novin Bank", "EN"));
        CARD_ISSUERS.put("622106", new CardIssuerInfo("622106", "بانک پارسیان", "Bank Parsian", "PARSIAN"));
        CARD_ISSUERS.put("639347", new CardIssuerInfo("639347", "بانک پاسارگاد", "Bank Pasargad", "PASARGAD"));
        CARD_ISSUERS.put("639348", new CardIssuerInfo("639348", "بانک پاسارگاد", "Bank Pasargad", "PASARGAD"));
        CARD_ISSUERS.put("627488", new CardIssuerInfo("627488", "بانک کارآفرین", "Karafarin Bank", "KARAFARIN"));
        CARD_ISSUERS.put("621986", new CardIssuerInfo("621986", "بانک سامان", "Saman Bank", "SAMAN"));
        CARD_ISSUERS.put("639346", new CardIssuerInfo("639346", "بانک سینا", "Sina Bank", "SINA"));
        CARD_ISSUERS.put("627648", new CardIssuerInfo("627648", "بانک توسعه صادرات", "Export Development Bank", "EDBI"));
        CARD_ISSUERS.put("627961", new CardIssuerInfo("627961", "بانک صنعت و معدن", "Bank Sanat va Madan", "SANAT"));
        CARD_ISSUERS.put("639370", new CardIssuerInfo("639370", "بانک مهر اقتصاد", "Mehr Eghtesad Bank", "MEHR_EGHTESAD"));
        CARD_ISSUERS.put("606373", new CardIssuerInfo("606373", "بانک مهر ایران", "Mehr Iran Bank", "MEHR_IRAN"));
        CARD_ISSUERS.put("627593", new CardIssuerInfo("627593", "بانک ایران زمین", "Iran Zamin Bank", "IRAN_ZAMIN"));
        CARD_ISSUERS.put("636214", new CardIssuerInfo("636214", "بانک آینده", "Bank Ayandeh", "AYANDEH"));
        CARD_ISSUERS.put("627353", new CardIssuerInfo("627353", "بانک تجارت", "Bank Tejarat", "TEJARAT"));
        CARD_ISSUERS.put("603799", new CardIssuerInfo("603799", "بانک ملی", "Bank Melli", "MELLI"));
        CARD_ISSUERS.put("589210", new CardIssuerInfo("589210", "بانک سپه", "Bank Sepah", "SEPAH"));
        CARD_ISSUERS.put("627381", new CardIssuerInfo("627381", "بانک انصار", "Ansar Bank", "ANSAR"));
        CARD_ISSUERS.put("505785", new CardIssuerInfo("505785", "بانک توسعه تعاون", "Tose'e Ta'avon Bank", "TT"));
        CARD_ISSUERS.put("636949", new CardIssuerInfo("636949", "بانک حکمت ایرانیان", "Hekmat Iranian Bank", "HEKMAT"));
        CARD_ISSUERS.put("627495", new CardIssuerInfo("627495", "بانک دی", "Bank Day", "DAY"));
        CARD_ISSUERS.put("991975", new CardIssuerInfo("991975", "بانک ملت", "Bank Mellat", "MELLAT"));
        CARD_ISSUERS.put("610433", new CardIssuerInfo("610433", "بانک ملت", "Bank Mellat", "MELLAT"));
        CARD_ISSUERS.put("627951", new CardIssuerInfo("627951", "بانک ملت", "Bank Mellat", "MELLAT"));
        CARD_ISSUERS.put("603769", new CardIssuerInfo("603769", "بانک صادرات", "Bank Saderat", "SADERAT"));
        CARD_ISSUERS.put("603794", new CardIssuerInfo("603794", "بانک صادرات", "Bank Saderat", "SADERAT"));
        CARD_ISSUERS.put("627597", new CardIssuerInfo("627597", "بانک صادرات", "Bank Saderat", "SADERAT"));
        CARD_ISSUERS.put("627640", new CardIssuerInfo("627640", "بانک رسالت", "Resalat Bank", "RESALAT"));
        CARD_ISSUERS.put("639588", new CardIssuerInfo("639588", "بانک ملل", "Bank Melal", "MELAL"));
        CARD_ISSUERS.put("627568", new CardIssuerInfo("627568", "بانک دی", "Bank Day", "DAY"));
        CARD_ISSUERS.put("502229", new CardIssuerInfo("502229", "بانک پاسارگاد", "Bank Pasargad", "PASARGAD"));
        CARD_ISSUERS.put("504706", new CardIssuerInfo("504706", "بانک شهر", "Bank Shahr", "SHAHR"));
        CARD_ISSUERS.put("627610", new CardIssuerInfo("627610", "بانک سپه", "Bank Sepah", "SEPAH"));
        CARD_ISSUERS.put("627656", new CardIssuerInfo("627656", "موسسه اعتباری توسعه", "Tose'e Credit Institute", "TOSEE"));
        CARD_ISSUERS.put("628023", new CardIssuerInfo("628023", "بانک مسکن", "Bank Maskan", "MASKAN"));
        CARD_ISSUERS.put("627884", new CardIssuerInfo("627884", "بانک پارسیان", "Bank Parsian", "PARSIAN"));
        CARD_ISSUERS.put("639343", new CardIssuerInfo("639343", "بانک کشاورزی", "Bank Keshavarzi", "KESHAVARZI"));
        CARD_ISSUERS.put("505801", new CardIssuerInfo("505801", "موسسه اعتباری کوثر", "Kosar Credit Institute", "KOSAR"));
        CARD_ISSUERS.put("581874", new CardIssuerInfo("581874", "بانک قرض الحسنه مهر", "Gharzolhasane Mehr", "MEHR"));
        CARD_ISSUERS.put("627917", new CardIssuerInfo("627917", "بانک خاورمیانه", "Middle East Bank", "MEB"));
        CARD_ISSUERS.put("627623", new CardIssuerInfo("627623", "بانک گردشگری", "Tourism Bank", "TOURISM"));
        CARD_ISSUERS.put("639349", new CardIssuerInfo("639349", "بانک حکمت", "Hekmat Bank", "HEKMAT"));

        // International card networks
        CARD_ISSUERS.put("4", new CardIssuerInfo("4", "ویزا", "Visa", "VISA"));
        CARD_ISSUERS.put("5", new CardIssuerInfo("5", "مستر کارت", "MasterCard", "MASTERCARD"));
        CARD_ISSUERS.put("6", new CardIssuerInfo("6", "دیسکاور", "Discover", "DISCOVER"));
    }

    /**
     * Card issuer information holder class.
     */
    public static class CardIssuerInfo {
        private final String bin;
        private final String persianName;
        private final String englishName;
        private final String code;

        /**
         * constructor for card issuer information holder class.
         * @param bin bin of card
         * @param persianName Persian name of bank
         * @param englishName English name of bank
         * @param code card number
         */
        public CardIssuerInfo(String bin, String persianName, String englishName, String code) {
            this.bin = bin;
            this.persianName = persianName;
            this.englishName = englishName;
            this.code = code;
        }

        /**
         * Gets the 6-digit BIN (Bank Identification Number) associated with this card issuer.
         *
         * @return the 6-digit BIN
         */
        public String getBin() { return bin; }

        /**
         * Gets the Persian name associated with this card issuer.
         *
         * @return the Persian name of the card issuer
         */
        public String getPersianName() { return persianName; }

        /**
         * Gets the English name associated with this card issuer.
         *
         * @return the English name of the card issuer
         */
        public String getEnglishName() { return englishName; }

        /**
         * Gets the code associated with this card issuer.
         *
         * @return the code of the card issuer
         */
        public String getCode() { return code; }

        @Override
        public String toString() {
            return String.format("%s (%s) - BIN: %s", persianName, englishName, bin);
        }
    }

    /**
     * Validates an ATM/Debit card number using the Luhn algorithm.
     * Supports both Persian and English digits, and handles formatted input.
     *
     * @param cardNumber the card number to validate (16 digits, may contain spaces/hyphens)
     * @return true if the card number is valid according to Luhn algorithm, false otherwise
     */
    public static boolean isValid(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }

        // Normalize the card number
        cardNumber = normalizeCardNumber(cardNumber);

        if (cardNumber == null) {
            return false;
        }

        // Check basic format
        if (!CARD_NUMBER_PATTERN.matcher(cardNumber).matches()) {
            return false;
        }

        // Validate using Luhn algorithm
        return validateLuhn(cardNumber);
    }

    /**
     * Validates a card number using the Luhn algorithm.
     *
     * @param cardNumber the card number to validate
     * @return true if valid according to Luhn algorithm, false otherwise
     */
    private static boolean validateLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        // Process from right to left
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10) == 0;
    }

    /**
     * Normalizes a card number by removing spaces, converting Persian digits to English,
     * and validating the format.
     *
     * @param cardNumber the card number to normalize
     * @return normalized 16-digit card number, or null if invalid format
     */
    public static String normalizeCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return null;
        }

        // Convert Persian digits to English
        cardNumber = PersianNumberConverter.toEnglishDigits(cardNumber);

        // Remove all spaces and hyphens
        cardNumber = cardNumber.replaceAll("[\\s-]", "");

        // Check if it contains only digits
        if (!cardNumber.matches("\\d+")) {
            return null;
        }

        // Must be exactly 16 digits for standard cards
        if (cardNumber.length() != 16) {
            return null;
        }

        return cardNumber;
    }

    /**
     * Formats a card number with hyphens for better readability.
     * Example: "1234567890123456" becomes "1234-5678-9012-3456"
     *
     * @param cardNumber the card number to format
     * @return formatted card number (XXXX-XXXX-XXXX-XXXX) or null if invalid
     */
    public static String format(String cardNumber) {
        cardNumber = normalizeCardNumber(cardNumber);

        if (cardNumber == null) {
            return null;
        }

        // Format as: XXXX-XXXX-XXXX-XXXX
        return String.format("%s-%s-%s-%s",
                cardNumber.substring(0, 4),
                cardNumber.substring(4, 8),
                cardNumber.substring(8, 12),
                cardNumber.substring(12, 16));
    }

    /**
     * Formats a card number with spaces.
     *
     * @param cardNumber the card number to format
     * @return formatted card number (XXXX XXXX XXXX XXXX) or null if invalid
     */
    public static String formatWithSpaces(String cardNumber) {
        cardNumber = normalizeCardNumber(cardNumber);

        if (cardNumber == null) {
            return null;
        }

        // Format as: XXXX XXXX XXXX XXXX
        return String.format("%s %s %s %s",
                cardNumber.substring(0, 4),
                cardNumber.substring(4, 8),
                cardNumber.substring(8, 12),
                cardNumber.substring(12, 16));
    }

    /**
     * Masks a card number for security purposes, showing only first and last 4 digits.
     * Example: "1234567890123456" becomes "1234-****-****-3456"
     *
     * @param cardNumber the card number to mask
     * @return masked card number (XXXX-****-****-XXXX) or null if invalid
     */
    public static String mask(String cardNumber) {
        cardNumber = normalizeCardNumber(cardNumber);

        if (cardNumber == null) {
            return null;
        }

        // Show first 4 and last 4 digits only
        return String.format("%s-****-****-%s",
                cardNumber.substring(0, 4),
                cardNumber.substring(12, 16));
    }

    /**
     * Gets the BIN (Bank Identification Number) from a card number.
     *
     * @param cardNumber the card number
     * @return the 6-digit BIN or null if invalid
     */
    public static String getBin(String cardNumber) {
        cardNumber = normalizeCardNumber(cardNumber);

        if (cardNumber == null) {
            return null;
        }

        return cardNumber.substring(0, 6);
    }

    /**
     * Identifies the card issuer (bank) from a card number using BIN lookup.
     * Supports Iranian banks and international networks (Visa, MasterCard, etc.).
     *
     * @param cardNumber the card number to analyze
     * @return CardIssuerInfo containing bank details, or null if not identified
     */
    public static CardIssuerInfo getCardIssuer(String cardNumber) {
        cardNumber = normalizeCardNumber(cardNumber);

        if (cardNumber == null) {
            return null;
        }

        // Check Iranian banks first (6-digit BIN)
        String bin = getBin(cardNumber);
        if (CARD_ISSUERS.containsKey(bin)) {
            return CARD_ISSUERS.get(bin);
        }

        // Check international networks (first digit)
        String firstDigit = cardNumber.substring(0, 1);
        if (CARD_ISSUERS.containsKey(firstDigit)) {
            return CARD_ISSUERS.get(firstDigit);
        }

        return null;
    }

    /**
     * Validates a CVV2/CVC2 code format.
     * Accepts 3 or 4 digit codes and converts Persian digits.
     *
     * @param cvv2 the CVV2 code to validate
     * @return true if format is valid (3-4 digits), false otherwise
     */
    public static boolean isValidCvv2(String cvv2) {
        if (cvv2 == null || cvv2.isEmpty()) {
            return false;
        }

        // Convert Persian digits to English
        cvv2 = PersianNumberConverter.toEnglishDigits(cvv2.trim());

        return CVV2_PATTERN.matcher(cvv2).matches();
    }

    /**
     * Validates a card expiry date.
     *
     * @param expiry the expiry date (MM/YY or MM/YYYY format)
     * @return true if valid format, false otherwise
     */
    public static boolean isValidExpiry(String expiry) {
        if (expiry == null || expiry.isEmpty()) {
            return false;
        }

        // Convert Persian digits to English
        expiry = PersianNumberConverter.toEnglishDigits(expiry.trim());

        return EXPIRY_PATTERN.matcher(expiry).matches();
    }

    /**
     * Checks if the card has expired based on expiry date.
     *
     * @param month expiry month (1-12)
     * @param year expiry year (2-digit or 4-digit)
     * @return true if expired, false otherwise
     */
    public static boolean isExpired(int month, int year) {
        if (month < 1 || month > 12) {
            return true;
        }

        // Convert 2-digit year to 4-digit
        if (year < 100) {
            year += 2000;
        }

        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH) + 1;

        if (year < currentYear) {
            return true;
        } else if (year == currentYear) {
            return month < currentMonth;
        }

        return false;
    }

    /**
     * Information about a card.
     */
    public static class CardInfo {
        private final String cardNumber;
        private final boolean valid;
        private final String bin;
        private final CardIssuerInfo issuer;
        private final String formatted;
        private final String masked;

        /**
         * constructor for Information about a card.
         * @param cardNumber card number
         */
        public CardInfo(String cardNumber) {
            this.cardNumber = normalizeCardNumber(cardNumber);
            this.valid = this.cardNumber != null && AtmCardValidator.isValid(this.cardNumber);

            if (valid) {
                this.bin = AtmCardValidator.getBin(this.cardNumber);
                this.issuer = getCardIssuer(this.cardNumber);
                this.formatted = format(this.cardNumber);
                this.masked = mask(this.cardNumber);
            } else {
                this.bin = null;
                this.issuer = null;
                this.formatted = null;
                this.masked = null;
            }
        }


        /**
         * Gets the original card number.
         *
         * @return the original card number
         */
        public String getCardNumber() { return cardNumber; }
        /**
         * Checks if the card is valid according to Luhn algorithm and expiration.
         *
         * @return true if the card is valid, false otherwise
         */
        public boolean isValid() { return valid; }

        /**
         * Gets the 6-digit BIN (Bank Identification Number) associated with this card.
         *
         * @return the 6-digit BIN or null if card is invalid
         */
        public String getBin() { return bin; }

        /**
         * Gets the card issuer associated with this card.
         *
         * @return the card issuer associated with this card, or null if the card is invalid
         */
        public CardIssuerInfo getIssuer() { return issuer; }

        /**
         * Gets the card number formatted with hyphens for better readability.
         * Example: "1234567890123456" becomes "1234-5678-9012-3456"
         *
         * @return the formatted card number (XXXX-XXXX-XXXX-XXXX) or null if invalid
         */
        public String getFormatted() { return formatted; }

        /**
         * Gets the card number masked for security purposes, showing only first and last 4 digits.
         * Example: "1234567890123456" becomes "1234-****-****-3456"
         *
         * @return the masked card number (XXXX-****-****-XXXX) or null if invalid
         */
        public String getMasked() { return masked; }

        @Override
        public String toString() {
            if (!valid) {
                return "Invalid Card Number";
            }

            String issuerName = issuer != null ? issuer.getEnglishName() : "Unknown";
            return String.format("Card: %s (Issuer: %s)", masked, issuerName);
        }
    }

    /**
     * Generates a valid test card number using the Luhn algorithm.
     * WARNING: This should only be used for testing purposes.
     *
     * @param bin the 6-digit BIN
     * @param accountNumber 9-digit account number
     * @return a valid 16-digit card number with correct check digit
     */
    public static String generateTestCard(String bin, String accountNumber) {
        if (bin == null || bin.length() != 6 ||
                accountNumber == null || accountNumber.length() != 9) {
            throw new IllegalArgumentException("BIN must be 6 digits and account number must be 9 digits");
        }

        // Convert to English digits
        bin = PersianNumberConverter.toEnglishDigits(bin);
        accountNumber = PersianNumberConverter.toEnglishDigits(accountNumber);

        // Validate digits only
        if (!bin.matches("\\d{6}") || !accountNumber.matches("\\d{9}")) {
            throw new IllegalArgumentException("BIN and account number must contain only digits");
        }

        String partialCard = bin + accountNumber;

        // Calculate check digit using Luhn algorithm
        int sum = 0;
        boolean alternate = true; // Start with true as we're calculating for position 16

        // Process the 15 digits
        for (int i = partialCard.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(partialCard.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        // Calculate check digit
        int checkDigit = (10 - (sum % 10)) % 10;

        return partialCard + checkDigit;
    }

    /**
     * Validates multiple card numbers at once.
     *
     * @param cardNumbers list of card numbers to validate
     * @return map of card number to validation result
     */
    public static Map<String, Boolean> validateBatch(List<String> cardNumbers) {
        Map<String, Boolean> results = new LinkedHashMap<>();

        for (String cardNumber : cardNumbers) {
            results.put(cardNumber, isValid(cardNumber));
        }

        return results;
    }

    /**
     * Gets all known card issuers.
     *
     * @return unmodifiable map of BIN to CardIssuerInfo
     */
    public static Map<String, CardIssuerInfo> getAllCardIssuers() {
        return Collections.unmodifiableMap(CARD_ISSUERS);
    }

    /**
     * Searches for card issuers by name.
     *
     * @param query search query
     * @return list of matching issuers
     */
    public static List<CardIssuerInfo> searchIssuers(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        query = query.toLowerCase();
        List<CardIssuerInfo> results = new ArrayList<>();
        Set<String> addedBanks = new HashSet<>(); // To avoid duplicates

        for (CardIssuerInfo issuer : CARD_ISSUERS.values()) {
            if (!addedBanks.contains(issuer.getEnglishName())) {
                if (issuer.getPersianName().toLowerCase().contains(query) ||
                        issuer.getEnglishName().toLowerCase().contains(query) ||
                        issuer.getCode().toLowerCase().contains(query) ||
                        issuer.getBin().equals(query)) {
                    results.add(issuer);
                    addedBanks.add(issuer.getEnglishName());
                }
            }
        }

        return results;
    }

    /**
     * Checks if a card number belongs to an Iranian bank.
     *
     * @param cardNumber the card number to check
     * @return true if Iranian bank card, false otherwise
     */
    public static boolean isIranianCard(String cardNumber) {
        CardIssuerInfo issuer = getCardIssuer(cardNumber);

        if (issuer == null) {
            return false;
        }

        // Check if it's not an international network
        return !issuer.getCode().equals("VISA") &&
                !issuer.getCode().equals("MASTERCARD") &&
                !issuer.getCode().equals("DISCOVER");
    }

    /**
     * Converts card number to Persian digits format.
     *
     * @param cardNumber the card number
     * @return card number with Persian digits or null if invalid
     */
    public static String toPersianFormat(String cardNumber) {
        String formatted = formatWithSpaces(cardNumber);

        if (formatted == null) {
            return null;
        }

        return PersianNumberConverter.toPersianDigits(formatted);
    }
}