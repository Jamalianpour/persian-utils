package io.github.jamalianpour.validation;

import io.github.jamalianpour.number.PersianNumberConverter;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Validator and utilities for Iranian phone numbers (mobile and landline).
 * Supports operator identification, area codes, and various formatting options.
 */
public class IranianPhoneValidator {

    // Mobile number patterns
//    private static final Pattern MOBILE_PATTERN = Pattern.compile("^(0|\\+98|0098)?9[0-9]{9}$");
    private static final Pattern NORMALIZED_MOBILE_PATTERN = Pattern.compile("^09[0-9]{9}$");

    // Landline pattern (area code + number)
//    private static final Pattern LANDLINE_PATTERN = Pattern.compile("^(0|\\+98|0098)?[1-8][0-9]{9}$");
//    private static final Pattern NORMALIZED_LANDLINE_PATTERN = Pattern.compile("^0[1-8][0-9]{9}$");

    // Emergency numbers
    private static final Set<String> EMERGENCY_NUMBERS = new HashSet<>(Arrays.asList(
            "110", "112", "113", "114", "115", "121", "122", "125", "194", "191", "190", "193", "147", "1818"
    ));

    // Mobile operator prefixes
    private static final Map<String, OperatorInfo> MOBILE_OPERATORS = new HashMap<>();
    static {
        // MCI (Hamrah-e Aval)
        MOBILE_OPERATORS.put("0910", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0911", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0912", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0913", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0914", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0915", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0916", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0917", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0918", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0919", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0990", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0991", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0992", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0993", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0994", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0996", new OperatorInfo("MCI", "همراه اول", "Hamrah-e Aval", OperatorType.MOBILE));

        // Irancell
        MOBILE_OPERATORS.put("0901", new OperatorInfo("Irancell", "ایرانسل", "Irancell", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0902", new OperatorInfo("Irancell", "ایرانسل", "Irancell", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0903", new OperatorInfo("Irancell", "ایرانسل", "Irancell", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0905", new OperatorInfo("Irancell", "ایرانسل", "Irancell", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0930", new OperatorInfo("Irancell", "ایرانسل", "Irancell", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0933", new OperatorInfo("Irancell", "ایرانسل", "Irancell", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0935", new OperatorInfo("Irancell", "ایرانسل", "Irancell", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0936", new OperatorInfo("Irancell", "ایرانسل", "Irancell", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0937", new OperatorInfo("Irancell", "ایرانسل", "Irancell", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0938", new OperatorInfo("Irancell", "ایرانسل", "Irancell", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0939", new OperatorInfo("Irancell", "ایرانسل", "Irancell", OperatorType.MOBILE));

        // Rightel
        MOBILE_OPERATORS.put("0920", new OperatorInfo("Rightel", "رایتل", "Rightel", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0921", new OperatorInfo("Rightel", "رایتل", "Rightel", OperatorType.MOBILE));
        MOBILE_OPERATORS.put("0922", new OperatorInfo("Rightel", "رایتل", "Rightel", OperatorType.MOBILE));

        // Shatel (MVNO - uses MCI network)
        MOBILE_OPERATORS.put("0998", new OperatorInfo("Shatel", "شاتل", "Shatel", OperatorType.MVNO));

        // Aptel (MVNO)
        MOBILE_OPERATORS.put("0904", new OperatorInfo("Aptel", "اپتل", "Aptel", OperatorType.MVNO));

        // TeleKish (MVNO on Irancell)
        MOBILE_OPERATORS.put("0932", new OperatorInfo("TeleKish", "تله کیش", "TeleKish", OperatorType.MVNO));
    }

    // Area codes for major cities
    private static final Map<String, String> AREA_CODES = new HashMap<>();
    static {
        AREA_CODES.put("021", "تهران");
        AREA_CODES.put("026", "کرج، البرز");
        AREA_CODES.put("041", "تبریز");
        AREA_CODES.put("044", "ارومیه");
        AREA_CODES.put("045", "اردبیل");
        AREA_CODES.put("051", "مشهد");
        AREA_CODES.put("053", "بیرجند");
        AREA_CODES.put("054", "زاهدان");
        AREA_CODES.put("056", "کرمان");
        AREA_CODES.put("058", "شهرکرد");
        AREA_CODES.put("061", "اهواز");
        AREA_CODES.put("066", "اراک");
        AREA_CODES.put("071", "شیراز");
        AREA_CODES.put("074", "یاسوج");
        AREA_CODES.put("076", "بندرعباس");
        AREA_CODES.put("077", "بوشهر");
        AREA_CODES.put("081", "همدان");
        AREA_CODES.put("083", "کرمانشاه");
        AREA_CODES.put("084", "ایلام");
        AREA_CODES.put("086", "یزد");
        AREA_CODES.put("087", "سنندج");
        AREA_CODES.put("011", "گرگان");
        AREA_CODES.put("013", "رشت");
        AREA_CODES.put("017", "گلستان");
        AREA_CODES.put("023", "سمنان");
        AREA_CODES.put("024", "زنجان");
        AREA_CODES.put("025", "قم");
        AREA_CODES.put("028", "قزوین");
        AREA_CODES.put("031", "اصفهان");
        AREA_CODES.put("034", "کرمان");
        AREA_CODES.put("035", "یزد");
        AREA_CODES.put("038", "چهارمحال و بختیاری");
    }

    /**
     * Operator type enumeration.
     */
    public enum OperatorType {
        MOBILE("Mobile Network Operator"),
        MVNO("Mobile Virtual Network Operator"),
        LANDLINE("Landline"),
        EMERGENCY("Emergency"),
        UNKNOWN("Unknown");

        private final String description;

        OperatorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Operator information holder class.
     */
    public static class OperatorInfo {
        private final String code;
        private final String persianName;
        private final String englishName;
        private final OperatorType type;

        public OperatorInfo(String code, String persianName, String englishName, OperatorType type) {
            this.code = code;
            this.persianName = persianName;
            this.englishName = englishName;
            this.type = type;
        }

        public String getCode() { return code; }
        public String getPersianName() { return persianName; }
        public String getEnglishName() { return englishName; }
        public OperatorType getType() { return type; }

        @Override
        public String toString() {
            return String.format("%s (%s) - %s", persianName, englishName, type);
        }
    }

    /**
     * Validates an Iranian phone number (mobile or landline).
     *
     * @param phoneNumber the phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        // Check if it's an emergency number
        if (isEmergencyNumber(phoneNumber)) {
            return true;
        }

        // Normalize the phone number
        phoneNumber = normalizePhoneNumber(phoneNumber);

        if (phoneNumber == null) {
            return false;
        }

        // Check if it's mobile or landline
        return isValidMobile(phoneNumber) || isValidLandline(phoneNumber);
    }

    /**
     * Validates if a phone number is a valid mobile number.
     *
     * @param phoneNumber the phone number to validate
     * @return true if valid mobile, false otherwise
     */
    public static boolean isValidMobile(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        phoneNumber = normalizePhoneNumber(phoneNumber);

        if (phoneNumber == null) {
            return false;
        }

        if (!NORMALIZED_MOBILE_PATTERN.matcher(phoneNumber).matches()) return false;

        return MOBILE_OPERATORS.containsKey(phoneNumber.substring(0,4));
    }

    /**
     * Validates if a phone number is a valid landline number.
     *
     * @param phoneNumber the phone number to validate
     * @return true if valid landline, false otherwise
     */
    public static boolean isValidLandline(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        phoneNumber = normalizePhoneNumber(phoneNumber);

        if (phoneNumber == null) {
            return false;
        }

        // Must be 11 digits starting with 0 (not 09)
        if (!phoneNumber.matches("^0[1-8][0-9]{9}$")) {
            return false;
        }

        // Check if area code exists
        String areaCode = phoneNumber.substring(0, 3);
        return AREA_CODES.containsKey(areaCode);
    }

    /**
     * Checks if a number is an emergency number.
     *
     * @param number the number to check
     * @return true if emergency number, false otherwise
     */
    public static boolean isEmergencyNumber(String number) {
        if (number == null) {
            return false;
        }

        number = PersianNumberConverter.toEnglishDigits(number.trim());
        return EMERGENCY_NUMBERS.contains(number);
    }

    /**
     * Normalizes a phone number to standard format.
     *
     * @param phoneNumber the phone number to normalize
     * @return normalized phone number or null if invalid
     */
    public static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }

        // Convert Persian digits to English
        phoneNumber = PersianNumberConverter.toEnglishDigits(phoneNumber);

        // Remove all non-digit characters
        phoneNumber = phoneNumber.replaceAll("[^0-9+]", "");

        // Handle international format
        if (phoneNumber.startsWith("+98")) {
            phoneNumber = "0" + phoneNumber.substring(3);
        } else if (phoneNumber.startsWith("0098")) {
            phoneNumber = "0" + phoneNumber.substring(4);
        } else if (phoneNumber.startsWith("98") && phoneNumber.length() == 12) {
            phoneNumber = "0" + phoneNumber.substring(2);
        }

        // Mobile numbers without 0
        if (phoneNumber.matches("^9[0-9]{9}$")) {
            phoneNumber = "0" + phoneNumber;
        }

        // Emergency numbers
        if (EMERGENCY_NUMBERS.contains(phoneNumber)) {
            return phoneNumber;
        }

        // Validate length
        if (phoneNumber.length() != 11) {
            return null;
        }

        // Must start with 0
        if (!phoneNumber.startsWith("0")) {
            return null;
        }

        return phoneNumber;
    }

    /**
     * Gets the operator information for a mobile number.
     *
     * @param phoneNumber the phone number
     * @return OperatorInfo or null if not found
     */
    public static OperatorInfo getOperator(String phoneNumber) {
        phoneNumber = normalizePhoneNumber(phoneNumber);

        if (phoneNumber == null || !isValidMobile(phoneNumber)) {
            return null;
        }

        String prefix = phoneNumber.substring(0, 4);
        return MOBILE_OPERATORS.get(prefix);
    }

    /**
     * Gets the area code and city for a landline number.
     *
     * @param phoneNumber the phone number
     * @return area code or null if not found
     */
    public static String getAreaCode(String phoneNumber) {
        phoneNumber = normalizePhoneNumber(phoneNumber);

        if (phoneNumber == null || !isValidLandline(phoneNumber)) {
            return null;
        }

        return phoneNumber.substring(0, 3);
    }

    /**
     * Gets the city name for a landline number.
     *
     * @param phoneNumber the phone number
     * @return city name or null if not found
     */
    public static String getCityName(String phoneNumber) {
        String areaCode = getAreaCode(phoneNumber);

        if (areaCode == null) {
            return null;
        }

        return AREA_CODES.get(areaCode);
    }

    /**
     * Formats a phone number in various styles.
     */
    public static class PhoneFormatter {

        /**
         * Formats as: 0912-345-6789
         */
        public static String formatDashed(String phoneNumber) {
            phoneNumber = normalizePhoneNumber(phoneNumber);

            if (phoneNumber == null) {
                return null;
            }

            if (isValidMobile(phoneNumber)) {
                return String.format("%s-%s-%s",
                        phoneNumber.substring(0, 4),
                        phoneNumber.substring(4, 7),
                        phoneNumber.substring(7, 11));
            } else if (isValidLandline(phoneNumber)) {
                return String.format("%s-%s",
                        phoneNumber.substring(0, 3),
                        phoneNumber.substring(3, 11));
            }

            return phoneNumber;
        }

        /**
         * Formats as: 0912 345 6789
         */
        public static String formatSpaced(String phoneNumber) {
            phoneNumber = normalizePhoneNumber(phoneNumber);

            if (phoneNumber == null) {
                return null;
            }

            if (isValidMobile(phoneNumber)) {
                return String.format("%s %s %s",
                        phoneNumber.substring(0, 4),
                        phoneNumber.substring(4, 7),
                        phoneNumber.substring(7, 11));
            } else if (isValidLandline(phoneNumber)) {
                return String.format("%s %s",
                        phoneNumber.substring(0, 3),
                        phoneNumber.substring(3, 11));
            }

            return phoneNumber;
        }

        /**
         * Formats as: (0912) 345-6789
         */
        public static String formatParentheses(String phoneNumber) {
            phoneNumber = normalizePhoneNumber(phoneNumber);

            if (phoneNumber == null) {
                return null;
            }

            if (isValidMobile(phoneNumber)) {
                return String.format("(%s) %s-%s",
                        phoneNumber.substring(0, 4),
                        phoneNumber.substring(4, 7),
                        phoneNumber.substring(7, 11));
            } else if (isValidLandline(phoneNumber)) {
                return String.format("(%s) %s",
                        phoneNumber.substring(0, 3),
                        phoneNumber.substring(3, 11));
            }

            return phoneNumber;
        }

        /**
         * Formats as international: +98 912 345 6789
         */
        public static String formatInternational(String phoneNumber) {
            phoneNumber = normalizePhoneNumber(phoneNumber);

            if (phoneNumber == null) {
                return null;
            }

            // Remove leading 0
            String withoutZero = phoneNumber.substring(1);

            if (isValidMobile(phoneNumber)) {
                return String.format("+98 %s %s %s",
                        withoutZero.substring(0, 3),
                        withoutZero.substring(3, 6),
                        withoutZero.substring(6, 10));
            } else if (isValidLandline(phoneNumber)) {
                return String.format("+98 %s %s",
                        withoutZero.substring(0, 2),
                        withoutZero.substring(2, 10));
            }

            return "+98 " + withoutZero;
        }

        /**
         * Formats with Persian digits.
         */
        public static String formatPersian(String phoneNumber) {
            String formatted = formatSpaced(phoneNumber);

            if (formatted == null) {
                return null;
            }

            return PersianNumberConverter.toPersianDigits(formatted);
        }
    }

    /**
     * Information about a phone number.
     */
    public static class PhoneInfo {
        private final String phoneNumber;
        private final boolean valid;
        private final boolean mobile;
        private final boolean landline;
        private final boolean emergency;
        private final OperatorInfo operator;
        private final String areaCode;
        private final String cityName;
        private final String formatted;
        private final String international;

        public PhoneInfo(String phoneNumber) {
            String normalized = normalizePhoneNumber(phoneNumber);

            if (normalized != null) {
                this.phoneNumber = normalized;
                this.emergency = isEmergencyNumber(phoneNumber);
                this.mobile = !emergency && isValidMobile(normalized);
                this.landline = !emergency && !mobile && isValidLandline(normalized);
                this.valid = emergency || mobile || landline;

                if (mobile) {
                    this.operator = IranianPhoneValidator.getOperator(this.phoneNumber);
                    this.areaCode = null;
                    this.cityName = null;
                } else if (landline) {
                    this.operator = null;
                    this.areaCode = IranianPhoneValidator.getAreaCode(this.phoneNumber);
                    this.cityName = IranianPhoneValidator.getCityName(this.phoneNumber);
                } else {
                    this.operator = null;
                    this.areaCode = null;
                    this.cityName = null;
                }

                this.formatted = PhoneFormatter.formatDashed(normalized);
                this.international = emergency ? phoneNumber : PhoneFormatter.formatInternational(normalized);
            } else {
                this.phoneNumber = phoneNumber;
                this.valid = false;
                this.mobile = false;
                this.landline = false;
                this.emergency = false;
                this.operator = null;
                this.areaCode = null;
                this.cityName = null;
                this.formatted = null;
                this.international = null;
            }
        }

        public String getPhoneNumber() { return phoneNumber; }
        public boolean isValid() { return valid; }
        public boolean isMobile() { return mobile; }
        public boolean isLandline() { return landline; }
        public boolean isEmergency() { return emergency; }
        public OperatorInfo getOperator() { return operator; }
        public String getAreaCode() { return areaCode; }
        public String getCityName() { return cityName; }
        public String getFormatted() { return formatted; }
        public String getInternational() { return international; }

        @Override
        public String toString() {
            if (!valid) {
                return "Invalid Phone Number: " + phoneNumber;
            }

            if (emergency) {
                return "Emergency Number: " + phoneNumber;
            } else if (mobile) {
                String op = operator != null ? operator.getEnglishName() : "Unknown";
                return String.format("Mobile: %s (Operator: %s)", formatted, op);
            } else if (landline) {
                return String.format("Landline: %s (City: %s)", formatted, cityName);
            }

            return phoneNumber;
        }
    }

    /**
     * Validates multiple phone numbers at once.
     *
     * @param phoneNumbers list of phone numbers to validate
     * @return map of phone number to validation result
     */
    public static Map<String, Boolean> validateBatch(List<String> phoneNumbers) {
        Map<String, Boolean> results = new LinkedHashMap<>();

        for (String phone : phoneNumbers) {
            results.put(phone, isValid(phone));
        }

        return results;
    }

    /**
     * Gets all mobile operators.
     *
     * @return unmodifiable map of prefix to OperatorInfo
     */
    public static Map<String, OperatorInfo> getAllMobileOperators() {
        return Collections.unmodifiableMap(MOBILE_OPERATORS);
    }

    /**
     * Gets all area codes.
     *
     * @return unmodifiable map of area code to city name
     */
    public static Map<String, String> getAllAreaCodes() {
        return Collections.unmodifiableMap(AREA_CODES);
    }

    /**
     * Gets all emergency numbers.
     *
     * @return unmodifiable set of emergency numbers
     */
    public static Set<String> getEmergencyNumbers() {
        return Collections.unmodifiableSet(EMERGENCY_NUMBERS);
    }
}