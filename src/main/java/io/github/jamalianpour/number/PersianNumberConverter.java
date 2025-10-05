package io.github.jamalianpour.number;

/**
 * Converts numbers and digits between Persian/Farsi, Arabic, and English numeral systems.
 * Handles both individual digits and complete numbers in strings.
 */
public class PersianNumberConverter {

    // Persian/Farsi digits (۰-۹)
    private static final char[] PERSIAN_DIGITS = {'۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'};

    // Arabic-Indic digits (٠-٩)
    private static final char[] ARABIC_DIGITS = {'٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'};

    // English/Western digits (0-9)
    private static final char[] ENGLISH_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * Converts English/Western digits to Persian digits in a string.
     * Example: "123" becomes "۱۲۳", "Hello 123" becomes "Hello ۱۲۳"
     *
     * @param input the input string containing digits to convert
     * @return string with Persian digits, or original string if input is null/empty
     */
    public static String toPersianDigits(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= '0' && c <= '9') {
                result.append(PERSIAN_DIGITS[c - '0']);
            } else if (isArabicDigit(c)) {
                result.append(PERSIAN_DIGITS[getArabicDigitValue(c)]);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Converts Persian and Arabic digits to English/Western digits in a string.
     * Example: "۱۲۳" becomes "123", "سلام ۱۲۳" becomes "سلام 123"
     *
     * @param input the input string containing Persian/Arabic digits to convert
     * @return string with English digits, or original string if input is null/empty
     */
    public static String toEnglishDigits(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (isPersianDigit(c)) {
                result.append(ENGLISH_DIGITS[getPersianDigitValue(c)]);
            } else if (isArabicDigit(c)) {
                result.append(ENGLISH_DIGITS[getArabicDigitValue(c)]);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Converts Persian digits to Arabic-Indic digits in a string.
     *
     * @param input the input string
     * @return string with Arabic digits
     */
    public static String toArabicDigits(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= '0' && c <= '9') {
                result.append(ARABIC_DIGITS[c - '0']);
            } else if (isPersianDigit(c)) {
                result.append(ARABIC_DIGITS[getPersianDigitValue(c)]);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Converts all digits in a string to Persian, regardless of their current type.
     *
     * @param input the input string
     * @return string with all digits converted to Persian
     */
    public static String convertAllToPersian(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            int digitValue = getDigitValue(c);
            if (digitValue >= 0) {
                result.append(PERSIAN_DIGITS[digitValue]);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Converts all digits in a string to English, regardless of their current type.
     *
     * @param input the input string
     * @return string with all digits converted to English
     */
    public static String convertAllToEnglish(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            int digitValue = getDigitValue(c);
            if (digitValue >= 0) {
                result.append(ENGLISH_DIGITS[digitValue]);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Converts a single digit character to Persian.
     *
     * @param digit the digit character
     * @return the Persian digit character
     */
    public static char toPersianDigit(char digit) {
        int value = getDigitValue(digit);
        if (value >= 0) {
            return PERSIAN_DIGITS[value];
        }
        return digit;
    }

    /**
     * Converts a single digit character to English.
     *
     * @param digit the digit character
     * @return the English digit character
     */
    public static char toEnglishDigit(char digit) {
        int value = getDigitValue(digit);
        if (value >= 0) {
            return ENGLISH_DIGITS[value];
        }
        return digit;
    }

    /**
     * Converts a single digit character to Arabic.
     *
     * @param digit the digit character
     * @return the Arabic digit character
     */
    public static char toArabicDigit(char digit) {
        int value = getDigitValue(digit);
        if (value >= 0) {
            return ARABIC_DIGITS[value];
        }
        return digit;
    }

    /**
     * Converts an integer to a string with Persian digits.
     *
     * @param number the number to convert
     * @return string representation with Persian digits
     */
    public static String toPersianNumber(int number) {
        return toPersianDigits(String.valueOf(number));
    }

    /**
     * Converts a long to a string with Persian digits.
     *
     * @param number the number to convert
     * @return string representation with Persian digits
     */
    public static String toPersianNumber(long number) {
        return toPersianDigits(String.valueOf(number));
    }

    /**
     * Converts a double to a string with Persian digits.
     *
     * @param number the number to convert
     * @return string representation with Persian digits
     */
    public static String toPersianNumber(double number) {
        return toPersianDigits(String.valueOf(number));
    }

    /**
     * Parses a string containing Persian or Arabic digits to an integer.
     * Automatically converts digits before parsing.
     *
     * @param persianNumber the string with Persian/Arabic digits (e.g., "۱۲۳")
     * @return the parsed integer value
     * @throws NumberFormatException if the string cannot be parsed as an integer
     */
    public static int parseInteger(String persianNumber) {
        String englishNumber = toEnglishDigits(persianNumber);
        return Integer.parseInt(englishNumber);
    }

    /**
     * Parses a string containing Persian or Arabic digits to a long.
     *
     * @param persianNumber the string with Persian/Arabic digits
     * @return the parsed long
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static long parseLong(String persianNumber) {
        String englishNumber = toEnglishDigits(persianNumber);
        return Long.parseLong(englishNumber);
    }

    /**
     * Parses a string containing Persian or Arabic digits to a double.
     *
     * @param persianNumber the string with Persian/Arabic digits
     * @return the parsed double
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static double parseDouble(String persianNumber) {
        String englishNumber = toEnglishDigits(persianNumber);
        return Double.parseDouble(englishNumber);
    }

    /**
     * Checks if a character is a Persian digit (۰-۹).
     *
     * @param c the character to check
     * @return true if the character is a Persian digit, false otherwise
     */
    public static boolean isPersianDigit(char c) {
        return c >= '۰' && c <= '۹';
    }

    /**
     * Checks if a character is an Arabic digit.
     *
     * @param c the character to check
     * @return true if Arabic digit, false otherwise
     */
    public static boolean isArabicDigit(char c) {
        return c >= '٠' && c <= '٩';
    }

    /**
     * Checks if a character is an English digit.
     *
     * @param c the character to check
     * @return true if English digit, false otherwise
     */
    public static boolean isEnglishDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Checks if a character is any type of digit (Persian, Arabic, or English).
     *
     * @param c the character to check
     * @return true if any type of digit, false otherwise
     */
    public static boolean isDigit(char c) {
        return isEnglishDigit(c) || isPersianDigit(c) || isArabicDigit(c);
    }

    /**
     * Gets the numeric value of a Persian digit character.
     *
     * @param c the Persian digit character
     * @return the numeric value (0-9) or -1 if not a Persian digit
     */
    public static int getPersianDigitValue(char c) {
        if (isPersianDigit(c)) {
            return c - '۰';
        }
        return -1;
    }

    /**
     * Gets the numeric value of an Arabic digit character.
     *
     * @param c the Arabic digit character
     * @return the numeric value (0-9) or -1 if not an Arabic digit
     */
    public static int getArabicDigitValue(char c) {
        if (isArabicDigit(c)) {
            return c - '٠';
        }
        return -1;
    }

    /**
     * Gets the numeric value of any digit character (Persian, Arabic, or English).
     *
     * @param c the digit character
     * @return the numeric value (0-9) or -1 if not a digit
     */
    public static int getDigitValue(char c) {
        if (isEnglishDigit(c)) {
            return c - '0';
        } else if (isPersianDigit(c)) {
            return getPersianDigitValue(c);
        } else if (isArabicDigit(c)) {
            return getArabicDigitValue(c);
        }
        return -1;
    }

    /**
     * Counts the number of Persian digits in a string.
     *
     * @param input the input string
     * @return the count of Persian digits
     */
    public static int countPersianDigits(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (char c : input.toCharArray()) {
            if (isPersianDigit(c)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if a string contains any Persian digits.
     *
     * @param input the input string to check
     * @return true if the string contains at least one Persian digit, false otherwise
     */
    public static boolean containsPersianDigits(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        for (char c : input.toCharArray()) {
            if (isPersianDigit(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a string contains only Persian digits.
     *
     * @param input the input string
     * @return true if contains only Persian digits, false otherwise
     */
    public static boolean isAllPersianDigits(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        for (char c : input.toCharArray()) {
            if (!isPersianDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
