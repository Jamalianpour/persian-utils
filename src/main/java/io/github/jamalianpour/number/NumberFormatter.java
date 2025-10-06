package io.github.jamalianpour.number;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Utility class for formatting numbers with separators and removing separators from formatted numbers.
 * Supports various separator styles including English comma, Persian comma, space, and custom separators.
 */
public class NumberFormatter {

    /**
     * Enum representing different separator styles.
     */
    public enum SeparatorStyle {
        /**
         * 1,234,567.89
         */
        COMMA(',', '.'),
        /**
         * ۱٬۲۳۴٬۵۶۷٫۸۹ (Persian thousands separator and decimal separator)
         */
        PERSIAN('٬', '٫'),
        /**
         * 1 234 567.89
         */
        SPACE(' ', '.'),
        /**
         * 1_234_567.89
         */
        UNDERSCORE('_', '.'),
        /**
         * 1'234'567.89
         */
        APOSTROPHE('\'', '.'),
        /**
         * 1234567.89
         */
        NONE('\0', '.');

        private final char thousandSeparator;
        private final char decimalSeparator;

        SeparatorStyle(char thousandSeparator, char decimalSeparator) {
            this.thousandSeparator = thousandSeparator;
            this.decimalSeparator = decimalSeparator;
        }

        /**
         * Gets the thousand separator character for this style.
         *
         * @return the thousand separator character
         */
        public char getThousandSeparator() {
            return thousandSeparator;
        }

        /**
         * Gets the decimal separator character for this style.
         *
         * @return the decimal separator character
         */
        public char getDecimalSeparator() {
            return decimalSeparator;
        }
    }

    /**
     * Configuration class for number formatting options.
     */
    public static class FormatConfig {
        private SeparatorStyle style = SeparatorStyle.COMMA;
        private boolean usePersianDigits = false;
        private int decimalPlaces = -1; // -1 means no limit
        private boolean showPositiveSign = false;
        private String prefix = "";
        private String suffix = "";
        private boolean groupingEnabled = true;
        private int groupingSize = 3; // Standard thousand grouping

        /**
         * Sets the separator style for formatting.
         *
         * @param style the separator style to use
         * @return this FormatConfig for method chaining
         */
        public FormatConfig withStyle(SeparatorStyle style) {
            this.style = style;
            return this;
        }

        /**
         * Sets whether to use Persian digits in the output.
         *
         * @param usePersianDigits true to use Persian digits, false for English digits
         * @return this FormatConfig for method chaining
         */
        public FormatConfig withPersianDigits(boolean usePersianDigits) {
            this.usePersianDigits = usePersianDigits;
            return this;
        }

        /**
         * Sets the number of decimal places to display.
         *
         * @param decimalPlaces the number of decimal places (-1 for no limit)
         * @return this FormatConfig for method chaining
         */
        public FormatConfig withDecimalPlaces(int decimalPlaces) {
            this.decimalPlaces = decimalPlaces;
            return this;
        }

        /**
         * Sets whether to show the positive sign for positive numbers.
         *
         * @param showPositiveSign true to show '+' for positive numbers
         * @return this FormatConfig for method chaining
         */
        public FormatConfig withPositiveSign(boolean showPositiveSign) {
            this.showPositiveSign = showPositiveSign;
            return this;
        }

        /**
         * Sets a prefix to be added before the formatted number.
         *
         * @param prefix the prefix string (e.g., currency symbol)
         * @return this FormatConfig for method chaining
         */
        public FormatConfig withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * Sets a suffix to be added after the formatted number.
         *
         * @param suffix the suffix string (e.g., currency name, '%')
         * @return this FormatConfig for method chaining
         */
        public FormatConfig withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        /**
         * Sets whether thousand grouping is enabled.
         *
         * @param enabled true to enable thousand separators, false to disable
         * @return this FormatConfig for method chaining
         */
        public FormatConfig withGrouping(boolean enabled) {
            this.groupingEnabled = enabled;
            return this;
        }

        /**
         * Sets the grouping size for thousand separators.
         *
         * @param size the number of digits per group (typically 3)
         * @return this FormatConfig for method chaining
         */
        public FormatConfig withGroupingSize(int size) {
            this.groupingSize = size;
            return this;
        }
    }

    // Pre-configured formats
    /**
     * ENGLISH FORMAT
     */
    public static final FormatConfig ENGLISH_FORMAT = new FormatConfig()
            .withStyle(SeparatorStyle.COMMA)
            .withPersianDigits(false);

    /**
     * PERSIAN FORMAT
     */
    public static final FormatConfig PERSIAN_FORMAT = new FormatConfig()
            .withStyle(SeparatorStyle.PERSIAN)
            .withPersianDigits(true);

    /**
     * CURRENCY FORMAT
     */
    public static final FormatConfig CURRENCY_FORMAT = new FormatConfig()
            .withStyle(SeparatorStyle.COMMA)
            .withDecimalPlaces(2)
            .withPrefix("$");

    /**
     * PERSIAN CURRENCY FORMAT
     */
    public static final FormatConfig PERSIAN_CURRENCY_FORMAT = new FormatConfig()
            .withStyle(SeparatorStyle.PERSIAN)
            .withPersianDigits(true)
            .withSuffix(" ریال");

    /**
     * Adds thousand separators to a number using default comma style.
     * Example: 1234567 becomes "1,234,567"
     *
     * @param number the number to format
     * @return formatted string with separators
     */
    public static String addSeparator(long number) {
        return addSeparator(number, SeparatorStyle.COMMA);
    }

    /**
     * Adds thousand separators to a number using specified style.
     * Example with PERSIAN style: 1234567 becomes "۱٬۲۳۴٬۵۶۷"
     *
     * @param number the number to format
     * @param style  the separator style (COMMA, PERSIAN, SPACE, etc.)
     * @return formatted string with separators
     */
    public static String addSeparator(long number, SeparatorStyle style) {
        FormatConfig config = new FormatConfig().withStyle(style);
        return format(number, config);
    }

    /**
     * Adds thousand separators to a decimal number using default comma style.
     * Example: 1234567.89 becomes "1,234,567.89"
     *
     * @param number the decimal number to format
     * @return formatted string with separators
     */
    public static String addSeparator(double number) {
        return addSeparator(number, SeparatorStyle.COMMA);
    }

    /**
     * Adds thousand separators to a decimal number using specified style.
     * Example with PERSIAN style: 1234567.89 becomes "۱٬۲۳۴٬۵۶۷٫۸۹"
     *
     * @param number the decimal number to format
     * @param style  the separator style (COMMA, PERSIAN, SPACE, etc.)
     * @return formatted string with separators
     */
    public static String addSeparator(double number, SeparatorStyle style) {
        FormatConfig config = new FormatConfig().withStyle(style);
        return format(number, config);
    }

    /**
     * Formats a number using the specified configuration.
     * Provides full control over formatting including separators, digits, prefixes, and suffixes.
     *
     * @param number the number to format
     * @param config the format configuration specifying style, digits, prefixes, etc.
     * @return formatted string according to the configuration
     */
    public static String format(long number, FormatConfig config) {
        String result = formatInternal(String.valueOf(number), config);

        if (config.showPositiveSign && number > 0) {
            result = "+" + result;
        }

        result = config.prefix + result + config.suffix;

        if (config.usePersianDigits) {
            result = PersianNumberConverter.toPersianDigits(result);
        }

        return result;
    }

    /**
     * Formats a decimal number using the specified configuration.
     *
     * @param number the number to format
     * @param config the format configuration
     * @return formatted string
     */
    public static String format(double number, FormatConfig config) {
        String numberStr;

        if (config.decimalPlaces >= 0) {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(config.decimalPlaces);
            df.setMinimumFractionDigits(config.decimalPlaces);
            df.setGroupingUsed(false);
            numberStr = df.format(number);
        } else {
            numberStr = String.valueOf(number);
        }

        String result = formatInternal(numberStr, config);

        if (config.showPositiveSign && number > 0) {
            result = "+" + result;
        }

        result = config.prefix + result + config.suffix;

        if (config.usePersianDigits) {
            result = PersianNumberConverter.toPersianDigits(result);
        }

        return result;
    }

    /**
     * Formats a BigDecimal using the specified configuration.
     *
     * @param number the number to format
     * @param config the format configuration
     * @return formatted string
     */
    public static String format(BigDecimal number, FormatConfig config) {
        String numberStr;

        if (config.decimalPlaces >= 0) {
            numberStr = number.setScale(config.decimalPlaces, BigDecimal.ROUND_HALF_UP).toPlainString();
        } else {
            numberStr = number.toPlainString();
        }

        String result = formatInternal(numberStr, config);

        if (config.showPositiveSign && number.compareTo(BigDecimal.ZERO) > 0) {
            result = "+" + result;
        }

        result = config.prefix + result + config.suffix;

        if (config.usePersianDigits) {
            result = PersianNumberConverter.toPersianDigits(result);
        }

        return result;
    }

    /**
     * Adds Persian thousand separators to a number.
     *
     * @param number the number to format
     * @return formatted string with Persian separators and digits
     */
    public static String addPersianSeparator(long number) {
        return format(number, PERSIAN_FORMAT);
    }

    /**
     * Adds Persian thousand separators to a decimal number.
     *
     * @param number the number to format
     * @return formatted string with Persian separators and digits
     */
    public static String addPersianSeparator(double number) {
        return format(number, PERSIAN_FORMAT);
    }

    /**
     * Removes all separators from a formatted number string.
     * Converts Persian/Arabic digits to English and removes formatting characters.
     *
     * @param formattedNumber the formatted number string (e.g., "1,234.56", "۱٬۲۳۴٫۵۶")
     * @return clean number string without separators (e.g., "1234.56")
     */
    public static String removeSeparator(String formattedNumber) {
        if (formattedNumber == null || formattedNumber.isEmpty()) {
            return formattedNumber;
        }

        // First convert any Persian/Arabic digits to English
        String result = PersianNumberConverter.toEnglishDigits(formattedNumber);

        // Remove common separators
        result = result.replaceAll("[,٬\\s_']", "");

        // Replace Persian decimal separator with standard decimal point
        result = result.replace('٫', '.');

        // Remove any currency symbols and common prefixes/suffixes
        result = result.replaceAll("[^0-9.+-]", "");

        // Handle multiple decimal points (keep only first)
        int firstDecimal = result.indexOf('.');
        if (firstDecimal >= 0) {
            String beforeDecimal = result.substring(0, firstDecimal);
            String afterDecimal = result.substring(firstDecimal + 1).replace(".", "");
            result = beforeDecimal + "." + afterDecimal;
        }

        return result;
    }

    /**
     * Parses a formatted number string to a long value.
     *
     * @param formattedNumber the formatted number string
     * @return the parsed long value
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static long parseLong(String formattedNumber) {
        String cleanNumber = removeSeparator(formattedNumber);

        // Handle decimal numbers by truncating
        int decimalIndex = cleanNumber.indexOf('.');
        if (decimalIndex >= 0) {
            cleanNumber = cleanNumber.substring(0, decimalIndex);
        }

        return Long.parseLong(cleanNumber);
    }

    /**
     * Parses a formatted number string to a double value.
     *
     * @param formattedNumber the formatted number string
     * @return the parsed double value
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static double parseDouble(String formattedNumber) {
        String cleanNumber = removeSeparator(formattedNumber);
        return Double.parseDouble(cleanNumber);
    }

    /**
     * Parses a formatted number string to a BigDecimal value.
     *
     * @param formattedNumber the formatted number string
     * @return the parsed BigDecimal value
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static BigDecimal parseBigDecimal(String formattedNumber) {
        String cleanNumber = removeSeparator(formattedNumber);
        return new BigDecimal(cleanNumber);
    }

    /**
     * Formats a number as currency with appropriate separators and symbols.
     * Supports major currencies including Iranian Rial and Toman with Persian formatting.
     *
     * @param amount       the amount to format
     * @param currencyCode the currency code (USD, EUR, IRR, IRT, etc.)
     * @return formatted currency string (e.g., "$1,234.56", "۱٬۲۳۴ ریال")
     */
    public static String formatCurrency(double amount, String currencyCode) {
        FormatConfig config = new FormatConfig()
                .withStyle(SeparatorStyle.COMMA)
                .withDecimalPlaces(2);

        switch (currencyCode.toUpperCase()) {
            case "USD":
                config.withPrefix("$");
                break;
            case "EUR":
                config.withPrefix("€");
                break;
            case "GBP":
                config.withPrefix("£");
                break;
            case "IRR":
            case "RIAL":
                config.withStyle(SeparatorStyle.PERSIAN)
                        .withPersianDigits(true)
                        .withDecimalPlaces(0)
                        .withSuffix(" ریال");
                break;
            case "IRT":
            case "TOMAN":
                config.withStyle(SeparatorStyle.PERSIAN)
                        .withPersianDigits(true)
                        .withDecimalPlaces(0)
                        .withSuffix(" تومان");
                break;
            default:
                config.withSuffix(" " + currencyCode);
        }

        return format(amount, config);
    }

    /**
     * Formats a number as a percentage with appropriate decimal places.
     *
     * @param value         the percentage value
     * @param decimalPlaces number of decimal places
     * @return formatted percentage string
     */
    public static String formatPercentage(double value, int decimalPlaces) {
        FormatConfig config = new FormatConfig()
                .withDecimalPlaces(decimalPlaces)
                .withSuffix("%")
                .withGrouping(false);

        return format(value, config);
    }

    /**
     * Formats a phone number with appropriate separators.
     *
     * @param phoneNumber the phone number
     * @param pattern     the pattern (e.g., "### ### ####" or "###-###-####")
     * @return formatted phone number
     */
    public static String formatPhoneNumber(String phoneNumber, String pattern) {
        // Remove all non-digits
        String digits = phoneNumber.replaceAll("[^0-9]", "");

        StringBuilder result = new StringBuilder();
        int digitIndex = 0;

        for (char c : pattern.toCharArray()) {
            if (c == '#') {
                if (digitIndex < digits.length()) {
                    result.append(digits.charAt(digitIndex++));
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Formats a credit card number with spaces every 4 digits.
     *
     * @param cardNumber the credit card number
     * @return formatted credit card number
     */
    public static String formatCreditCard(String cardNumber) {
        // Remove all non-digits
        String digits = cardNumber.replaceAll("[^0-9]", "");

        // Add space every 4 digits
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                result.append(' ');
            }
            result.append(digits.charAt(i));
        }

        return result.toString();
    }

    // Private helper methods

    private static String formatInternal(String numberStr, FormatConfig config) {
        if (!config.groupingEnabled || config.style == SeparatorStyle.NONE) {
            return numberStr.replace('.', config.style.getDecimalSeparator());
        }

        boolean isNegative = numberStr.startsWith("-");
        if (isNegative) {
            numberStr = numberStr.substring(1);
        }

        String integerPart;
        String decimalPart = "";

        int decimalIndex = numberStr.indexOf('.');
        if (decimalIndex >= 0) {
            integerPart = numberStr.substring(0, decimalIndex);
            decimalPart = numberStr.substring(decimalIndex + 1);
        } else {
            integerPart = numberStr;
        }

        // Add thousand separators to integer part
        StringBuilder formatted = new StringBuilder();
        int count = 0;

        for (int i = integerPart.length() - 1; i >= 0; i--) {
            if (count > 0 && count % config.groupingSize == 0) {
                formatted.insert(0, config.style.getThousandSeparator());
            }
            formatted.insert(0, integerPart.charAt(i));
            count++;
        }

        String result = formatted.toString();

        // Add decimal part if exists
        if (!decimalPart.isEmpty()) {
            result += config.style.getDecimalSeparator() + decimalPart;
        }

        if (isNegative) {
            result = "-" + result;
        }

        return result;
    }

    /**
     * Validates if a string is a properly formatted number.
     *
     * @param formattedNumber the formatted number string
     * @return true if valid formatted number, false otherwise
     */
    public static boolean isValidFormattedNumber(String formattedNumber) {
        if (formattedNumber == null || formattedNumber.isEmpty()) {
            return false;
        }

        try {
            parseDouble(formattedNumber);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Gets the number of decimal places in a formatted number.
     *
     * @param formattedNumber the formatted number string
     * @return number of decimal places, or 0 if no decimals
     */
    public static int getDecimalPlaces(String formattedNumber) {
        String clean = removeSeparator(formattedNumber);
        int decimalIndex = clean.indexOf('.');

        if (decimalIndex < 0) {
            return 0;
        }

        return clean.length() - decimalIndex - 1;
    }
}