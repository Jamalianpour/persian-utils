package io.github.jamalianpour.validation;

import io.github.jamalianpour.number.PersianNumberConverter;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Validator and utilities for Iranian IBAN (SHEBA - شماره شبا).
 * Implements the IBAN mod-97 check digit algorithm for Iranian bank accounts.
 */
public class IranianIban {

    private static final int IBAN_LENGTH = 26; // IR + 24 digits
    private static final Pattern IBAN_PATTERN = Pattern.compile("^IR[0-9]{24}$");
    private static final Pattern DIGITS_ONLY_PATTERN = Pattern.compile("^[0-9]{24}$");

    // Iranian bank codes and names
    private static final Map<String, BankInfo> BANK_CODES = new HashMap<>();
    static {
        BANK_CODES.put("010", new BankInfo("010", "بانک مرکزی", "Central Bank", "CBI"));

        BANK_CODES.put("012", new BankInfo("012", "بانک ملت", "Bank Mellat", "BMT"));
        BANK_CODES.put("013", new BankInfo("013", "بانک رفاه", "Refah Bank", "RFH"));
        BANK_CODES.put("014", new BankInfo("014", "بانک مسکن", "Bank Maskan", "MSK"));
        BANK_CODES.put("015", new BankInfo("015", "بانک سپه", "Bank Sepah", "SEP"));
        BANK_CODES.put("016", new BankInfo("016", "بانک کشاورزی", "Bank Keshavarzi", "KSH"));
        BANK_CODES.put("017", new BankInfo("017", "بانک ملی", "Bank Melli", "MLI"));
        BANK_CODES.put("018", new BankInfo("018", "بانک تجارت", "Bank Tejarat", "TJR"));
        BANK_CODES.put("019", new BankInfo("019", "بانک صادرات", "Bank Saderat", "SDR"));
        BANK_CODES.put("020", new BankInfo("020", "بانک توسعه صادرات", "Export Development Bank", "EDB"));
        BANK_CODES.put("021", new BankInfo("021", "پست بانک", "Post Bank", "PST"));

        BANK_CODES.put("022", new BankInfo("022", "بانک توسعه تعاون", "Tose'e Ta'avon Bank", "TET"));
        BANK_CODES.put("054", new BankInfo("054", "بانک پارسیان", "Bank Parsian", "PRS"));
        BANK_CODES.put("055", new BankInfo("055", "بانک اقتصاد نوین", "Eghtesad Novin Bank", "EGN"));
        BANK_CODES.put("056", new BankInfo("056", "بانک سامان", "Saman Bank", "SMN"));
        BANK_CODES.put("057", new BankInfo("057", "بانک پاسارگاد", "Bank Pasargad", "PSG"));
        BANK_CODES.put("058", new BankInfo("058", "بانک سرمایه", "Sarmayeh Bank", "SRM"));
        BANK_CODES.put("059", new BankInfo("059", "بانک سینا", "Sina Bank", "SIN"));
        BANK_CODES.put("060", new BankInfo("060", "بانک مهر ایران", "Mehr Iran Bank", "MHR"));
        BANK_CODES.put("061", new BankInfo("061", "بانک شهر", "Bank Shahr", "SHR"));
        BANK_CODES.put("062", new BankInfo("062", "بانک آینده", "Bank Ayandeh", "AYN"));
        BANK_CODES.put("064", new BankInfo("064", "بانک گردشگری", "Tourism Bank", "TRS"));
        BANK_CODES.put("066", new BankInfo("066", "بانک دی", "Bank Day", "DAY"));
        BANK_CODES.put("069", new BankInfo("069", "بانک ایران زمین", "Iran Zamin Bank", "IZM"));
        BANK_CODES.put("070", new BankInfo("070", "بانک رسالت", "Resalat Bank", "RSL"));
        BANK_CODES.put("075", new BankInfo("075", "بانک ملل", "Bank Melal", "MLL"));
        BANK_CODES.put("078", new BankInfo("078", "بانک خاورمیانه", "Middle East Bank", "MEB"));

        BANK_CODES.put("090", new BankInfo("090", "بانک مهر", "Mehr Bank", "MHR"));
        BANK_CODES.put("092", new BankInfo("092", "موسسه اعتباری کوثر", "Kosar Credit Institution", "KSR"));
        BANK_CODES.put("093", new BankInfo("093", "موسسه اعتباری ایرانیان", "Iranian Credit Institution", "IRN"));
        BANK_CODES.put("094", new BankInfo("094", "موسسه اعتباری ملل", "Melal Credit Institution", "MCR"));
        BANK_CODES.put("095", new BankInfo("095", "موسسه اعتباری عسگریه", "Askarieh Credit Institution", "ASK"));
        BANK_CODES.put("096", new BankInfo("096", "موسسه اعتباری آرمان", "Arman Credit Institution", "ARM"));
    }

    /**
     * Bank information holder class.
     */
    public static class BankInfo {
        private final String code;
        private final String persianName;
        private final String englishName;
        private final String abbreviation;

        public BankInfo(String code, String persianName, String englishName, String abbreviation) {
            this.code = code;
            this.persianName = persianName;
            this.englishName = englishName;
            this.abbreviation = abbreviation;
        }

        public String getCode() { return code; }
        public String getPersianName() { return persianName; }
        public String getEnglishName() { return englishName; }
        public String getAbbreviation() { return abbreviation; }

        @Override
        public String toString() {
            return String.format("%s (%s) - %s", persianName, englishName, code);
        }
    }

    /**
     * Validates an Iranian IBAN (SHEBA) using the mod-97 check digit algorithm.
     * Supports various input formats and automatically normalizes the input.
     *
     * @param iban the IBAN to validate (with or without IR prefix, may contain spaces/hyphens)
     * @return true if the IBAN is valid according to mod-97 algorithm, false otherwise
     */
    public static boolean isValid(String iban) {
        if (iban == null || iban.isEmpty()) {
            return false;
        }

        // Normalize the IBAN
        iban = normalizeIban(iban);

        if (iban == null) {
            return false;
        }

        // Check basic format
        if (!IBAN_PATTERN.matcher(iban).matches()) {
            return false;
        }

        // Validate check digits using mod-97 algorithm
        return validateCheckDigits(iban);
    }

    /**
     * Validates the check digits of an IBAN using mod-97 algorithm.
     *
     * @param iban the IBAN to validate
     * @return true if check digits are valid, false otherwise
     */
    private static boolean validateCheckDigits(String iban) {
        // Move first 4 characters to the end
        String rearranged = iban.substring(4) + iban.substring(0, 4);

        // Replace letters with numbers (A=10, B=11, ..., Z=35)
        StringBuilder numericString = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isDigit(c)) {
                numericString.append(c);
            } else {
                numericString.append(Character.getNumericValue(c));
            }
        }

        // Calculate mod-97
        BigInteger number = new BigInteger(numericString.toString());
        BigInteger mod97 = number.mod(BigInteger.valueOf(97));

        return mod97.equals(BigInteger.ONE);
    }

    /**
     * Normalizes an IBAN by converting Persian digits, removing formatting,
     * and adding IR prefix if needed.
     *
     * @param iban the IBAN to normalize
     * @return normalized 26-character IBAN (IR + 24 digits), or null if invalid format
     */
    public static String normalizeIban(String iban) {
        if (iban == null || iban.isEmpty()) {
            return null;
        }

        // Convert Persian digits to English
        iban = PersianNumberConverter.toEnglishDigits(iban);

        // Remove all spaces and non-alphanumeric characters
        iban = iban.replaceAll("[\\s-]", "");

        // Convert to uppercase
        iban = iban.toUpperCase();

        // If it's just 24 digits, add IR prefix
        if (DIGITS_ONLY_PATTERN.matcher(iban).matches()) {
            iban = "IR" + iban;
        }

        // Check length
        if (iban.length() != IBAN_LENGTH) {
            return null;
        }

        return iban;
    }

    /**
     * Formats an IBAN with spaces for better readability.
     * Example: "IR123456789012345678901234" becomes "IR12 3456 7890 1234 5678 9012 34"
     *
     * @param iban the IBAN to format
     * @return formatted IBAN with spaces, or null if invalid
     */
    public static String format(String iban) {
        iban = normalizeIban(iban);

        if (iban == null || !isValid(iban)) {
            return null;
        }

        // Format as: IR00 0000 0000 0000 0000 0000 00
        return String.format("%s %s %s %s %s %s %s",
                iban.substring(0, 4),
                iban.substring(4, 8),
                iban.substring(8, 12),
                iban.substring(12, 16),
                iban.substring(16, 20),
                iban.substring(20, 24),
                iban.substring(24, 26));
    }

    /**
     * Formats an IBAN without spaces.
     *
     * @param iban the IBAN to format
     * @return compact IBAN (IR00000000000000000000000000) or null if invalid
     */
    public static String formatCompact(String iban) {
        iban = normalizeIban(iban);

        if (iban == null || !isValid(iban)) {
            return null;
        }

        return iban;
    }

    /**
     * Extracts the 3-digit bank code from an IBAN.
     * The bank code is located at positions 5-7 of the IBAN.
     *
     * @param iban the IBAN to extract bank code from
     * @return 3-digit bank code, or null if IBAN is invalid
     */
    public static String getBankCode(String iban) {
        iban = normalizeIban(iban);

        if (iban == null || iban.length() < 7) {
            return null;
        }

        return iban.substring(4, 7);
    }

    /**
     * Gets detailed bank information from an IBAN using the bank code.
     * Provides Persian and English bank names and abbreviations.
     *
     * @param iban the IBAN to analyze
     * @return BankInfo object with bank details, or null if bank not found
     */
    public static BankInfo getBankInfo(String iban) {
        String bankCode = getBankCode(iban);

        if (bankCode == null) {
            return null;
        }

        return BANK_CODES.get(bankCode);
    }

    /**
     * Gets the account number from an IBAN.
     *
     * @param iban the IBAN
     * @return account number (last 19 digits after bank code) or null if invalid
     */
    public static String getAccountNumber(String iban) {
        iban = normalizeIban(iban);

        if (iban == null || iban.length() < IBAN_LENGTH) {
            return null;
        }

        return iban.substring(7, 26);
    }

    /**
     * Information extracted from an IBAN.
     */
    public static class IbanInfo {
        private final String iban;
        private final boolean valid;
        private final String bankCode;
        private final BankInfo bankInfo;
        private final String accountNumber;
        private final String formatted;
        private final String compact;

        public IbanInfo(String iban) {
            this.iban = normalizeIban(iban);
            this.valid = this.iban != null && IranianIban.isValid(this.iban);

            if (valid) {
                this.bankCode = IranianIban.getBankCode(this.iban);
                this.bankInfo = IranianIban.getBankInfo(this.iban);
                this.accountNumber = IranianIban.getAccountNumber(this.iban);
                this.formatted = format(this.iban);
                this.compact = formatCompact(this.iban);
            } else {
                this.bankCode = null;
                this.bankInfo = null;
                this.accountNumber = null;
                this.formatted = null;
                this.compact = null;
            }
        }

        public String getIban() { return iban; }
        public boolean isValid() { return valid; }
        public String getBankCode() { return bankCode; }
        public BankInfo getBankInfo() { return bankInfo; }
        public String getAccountNumber() { return accountNumber; }
        public String getFormatted() { return formatted; }
        public String getCompact() { return compact; }

        @Override
        public String toString() {
            if (!valid) {
                return "Invalid IBAN: " + iban;
            }

            String bankName = bankInfo != null ? bankInfo.getPersianName() : "Unknown Bank";
            return String.format("IBAN: %s (Bank: %s, Account: %s)",
                    formatted, bankName, accountNumber);
        }
    }

    /**
     * Calculates check digits for an Iranian IBAN.
     *
     * @param bankCode 3-digit bank code
     * @param accountNumber 19-digit account number
     * @return 2-digit check digits or null if invalid input
     */
    public static String calculateCheckDigits(String bankCode, String accountNumber) {
        if (bankCode == null || bankCode.length() != 3 ||
                accountNumber == null || accountNumber.length() != 19) {
            return null;
        }

        // Convert to English digits
        bankCode = PersianNumberConverter.toEnglishDigits(bankCode);
        accountNumber = PersianNumberConverter.toEnglishDigits(accountNumber);

        // Validate digits only
        if (!bankCode.matches("\\d{3}") || !accountNumber.matches("\\d{19}")) {
            return null;
        }

        // Create IBAN with check digits as "00"
        String tempIban = bankCode + accountNumber + "1827" + "00"; // IR = 1827 in numeric

        // Calculate check digits
        BigInteger number = new BigInteger(tempIban);
        int checkDigits = 98 - number.mod(BigInteger.valueOf(97)).intValue();

        return String.format("%02d", checkDigits);
    }

    /**
     * Generates a complete IBAN from bank code and account number.
     * Automatically calculates and adds the correct check digits.
     *
     * @param bankCode 3-digit bank code
     * @param accountNumber account number (will be used as-is, ensure it's 19 digits)
     * @return complete IBAN with calculated check digits, or null if invalid input
     */
    public static String generateIban(String bankCode, String accountNumber) {
        if (bankCode == null || accountNumber == null) {
            return null;
        }

        // Convert to English digits
        bankCode = PersianNumberConverter.toEnglishDigits(bankCode);
        accountNumber = PersianNumberConverter.toEnglishDigits(accountNumber);

        // Remove any non-digits
        bankCode = bankCode.replaceAll("\\D", "");
        accountNumber = accountNumber.replaceAll("\\D", "");

        // Validate bank code
        if (bankCode.length() != 3) {
            return null;
        }

        // Pad account number to 19 digits with leading zeros
        if (accountNumber.length() > 19) {
            return null;
        }
//        accountNumber = String.format("%019d", Long.parseLong(accountNumber));

        // Calculate check digits
        String checkDigits = calculateCheckDigits(bankCode, accountNumber);

        if (checkDigits == null) {
            return null;
        }

        return "IR" + checkDigits + bankCode + accountNumber;
    }

    /**
     * Validates multiple IBANs at once.
     *
     * @param ibans list of IBANs to validate
     * @return map of IBAN to validation result
     */
    public static Map<String, Boolean> validateBatch(List<String> ibans) {
        Map<String, Boolean> results = new LinkedHashMap<>();

        for (String iban : ibans) {
            results.put(iban, isValid(iban));
        }

        return results;
    }

    /**
     * Gets all available bank codes and their information.
     *
     * @return unmodifiable map of bank codes to BankInfo
     */
    public static Map<String, BankInfo> getAllBankCodes() {
        return Collections.unmodifiableMap(BANK_CODES);
    }

    /**
     * Searches for a bank by name (Persian or English).
     *
     * @param query search query
     * @return list of matching banks
     */
    public static List<BankInfo> searchBanks(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        query = query.toLowerCase();
        List<BankInfo> results = new ArrayList<>();

        for (BankInfo bank : BANK_CODES.values()) {
            if (bank.getPersianName().toLowerCase().contains(query) ||
                    bank.getEnglishName().toLowerCase().contains(query) ||
                    bank.getAbbreviation().toLowerCase().contains(query) ||
                    bank.getCode().equals(query)) {
                results.add(bank);
            }
        }

        return results;
    }

    /**
     * Converts a Persian IBAN (with Persian digits) to standard format.
     *
     * @param persianIban IBAN with Persian digits
     * @return standard IBAN with English digits or null if invalid
     */
    public static String fromPersian(String persianIban) {
        if (persianIban == null) {
            return null;
        }

        return normalizeIban(persianIban);
    }

    /**
     * Converts an IBAN to Persian format (with Persian digits).
     *
     * @param iban standard IBAN
     * @return IBAN with Persian digits or null if invalid
     */
    public static String toPersian(String iban) {
        iban = normalizeIban(iban);

        if (iban == null || !isValid(iban)) {
            return null;
        }

        // Keep IR in English, convert digits to Persian
        return "IR" + PersianNumberConverter.toPersianDigits(iban.substring(2));
    }
}