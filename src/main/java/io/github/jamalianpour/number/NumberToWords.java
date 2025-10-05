package io.github.jamalianpour.number;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Converts numbers to their word representation in Persian (Farsi) and English.
 * Supports whole numbers, decimals, negative numbers, and currency formatting.
 */
public class NumberToWords {

    // Persian number words
    private static final String[] PERSIAN_ONES = {
            "", "یک", "دو", "سه", "چهار", "پنج", "شش", "هفت", "هشت", "نه"
    };

    private static final String[] PERSIAN_TENS = {
            "", "", "بیست", "سی", "چهل", "پنجاه", "شصت", "هفتاد", "هشتاد", "نود"
    };

    private static final String[] PERSIAN_HUNDREDS = {
            "", "یکصد", "دویست", "سیصد", "چهارصد", "پانصد",
            "ششصد", "هفتصد", "هشتصد", "نهصد"
    };

    private static final String[] PERSIAN_TEN_TO_NINETEEN = {
            "ده", "یازده", "دوازده", "سیزده", "چهارده", "پانزده",
            "شانزده", "هفده", "هجده", "نوزده"
    };

    // English number words
    private static final String[] ENGLISH_ONES = {
            "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
    };

    private static final String[] ENGLISH_TENS = {
            "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    };

    private static final String[] ENGLISH_TEN_TO_NINETEEN = {
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
            "sixteen", "seventeen", "eighteen", "nineteen"
    };

    // Scale words
    private static final String[] PERSIAN_SCALE = {
            "", "هزار", "میلیون", "میلیارد", "بیلیون", "بیلیارد", "تریلیون", "تریلیارد"
    };

    private static final String[] ENGLISH_SCALE = {
            "", "thousand", "million", "billion", "trillion", "quadrillion", "quintillion", "sextillion"
    };

    // Currency names
    public static final String RIAL = "ریال";
    public static final String TOMAN = "تومان";
    public static final String DOLLAR = "dollar";
    public static final String EURO = "euro";
    public static final String POUND = "pound";

    private static final String TOMANS = "تومان";
    private static final String DOLLARS = "dollars";
    private static final String EUROS = "euros";
    private static final String POUNDS = "pounds";

    private static final String CENT = "cent";
    private static final String CENTS = "cents";
    private static final String DINAR = "دینار";

    /**
     * Converts a number to its Persian word representation.
     * Example: 123 becomes "یکصد و بیست و سه"
     *
     * @param number the number to convert
     * @return the Persian word representation
     */
    public static String toPersian(long number) {
        if (number == 0) {
            return "صفر";
        }

        if (number < 0) {
            return "منفی " + toPersian(-number);
        }

        return convertToPersian(number).trim();
    }

    /**
     * Converts a decimal number to its Persian word representation.
     * Example: 12.34 becomes "دوازده ممیز سه چهار"
     *
     * @param number the decimal number to convert
     * @return the Persian word representation including decimal part
     */
    public static String toPersian(double number) {
        if (number == 0) {
            return "صفر";
        }

        if (number < 0) {
            return "منفی " + toPersian(-number);
        }

        long wholePart = (long) number;
        double decimalPart = number - wholePart;

        if (decimalPart == 0) {
            return toPersian(wholePart);
        }

        // Format decimal part to avoid floating point issues
        DecimalFormat df = new DecimalFormat("#.##########", DecimalFormatSymbols.getInstance(Locale.US));
        String decimalStr = df.format(decimalPart).substring(2); // Remove "0."

        String wholeWords = toPersian(wholePart);
        String decimalWords = convertDecimalToPersian(decimalStr);

        return wholeWords + " ممیز " + decimalWords;
    }

    /**
     * Converts a BigDecimal to its Persian word representation.
     * Handles very large numbers and precise decimal values.
     * Example: new BigDecimal("123.456") becomes "یکصد و بیست و سه ممیز چهار پنج شش"
     *
     * @param number the BigDecimal number to convert
     * @return the Persian word representation including decimal part if present
     */
    public static String toPersian(BigDecimal number) {
        if (number.compareTo(BigDecimal.ZERO) == 0) {
            return "صفر";
        }

        if (number.compareTo(BigDecimal.ZERO) < 0) {
            return "منفی " + toPersian(number.negate());
        }

        BigInteger wholePart = number.toBigInteger();
        BigDecimal decimalPart = number.subtract(new BigDecimal(wholePart));

        if (decimalPart.compareTo(BigDecimal.ZERO) == 0) {
            return convertBigIntegerToPersian(wholePart);
        }

        String wholeWords = convertBigIntegerToPersian(wholePart);
        String decimalStr = decimalPart.toPlainString().substring(2); // Remove "0."
        String decimalWords = convertDecimalToPersian(decimalStr);

        return wholeWords + " ممیز " + decimalWords;
    }

    /**
     * Converts a number to its English word representation.
     * Example: 123 becomes "one hundred twenty-three"
     *
     * @param number the number to convert
     * @return the English word representation
     */
    public static String toEnglish(long number) {
        if (number == 0) {
            return "zero";
        }

        if (number < 0) {
            return "negative " + toEnglish(-number);
        }

        return convertToEnglish(number).trim();
    }

    /**
     * Converts a decimal number to its English word representation.
     * Example: 12.34 becomes "twelve point three four"
     *
     * @param number the decimal number to convert
     * @return the English word representation including decimal part
     */
    public static String toEnglish(double number) {
        if (number == 0) {
            return "zero";
        }

        if (number < 0) {
            return "negative " + toEnglish(-number);
        }

        long wholePart = (long) number;
        double decimalPart = number - wholePart;

        if (decimalPart == 0) {
            return toEnglish(wholePart);
        }

        DecimalFormat df = new DecimalFormat("#.##########", DecimalFormatSymbols.getInstance(Locale.US));
        String decimalStr = df.format(decimalPart).substring(2);

        String wholeWords = toEnglish(wholePart);
        String decimalWords = convertDecimalToEnglish(decimalStr);

        return wholeWords + " point " + decimalWords;
    }

    /**
     * Converts an amount to Persian currency words.
     * Example: 1500 with "ریال" becomes "یک هزار و پانصد ریال"
     *
     * @param amount   the amount to convert
     * @param currency the currency type (RIAL, TOMAN, etc.)
     * @return the Persian currency representation in words
     */
    public static String toPersianCurrency(long amount, String currency) {
        if (amount == 0) {
            return "صفر " + currency;
        }

        String amountWords = toPersian(Math.abs(amount));
        String currencyWord = Math.abs(amount) == 1 ? currency : currency;

        if (amount < 0) {
            return "منفی " + amountWords + " " + currencyWord;
        }

        return amountWords + " " + currencyWord;
    }

    /**
     * Converts a decimal amount to Persian currency words with decimal support.
     * The decimal part is converted to smaller currency units (e.g., Rial for Toman).
     * Example: 15.25 with "تومان" becomes "پانزده تومان و بیست و پنج ریال"
     *
     * @param amount   the decimal amount to convert
     * @param currency the main currency type (TOMAN, RIAL, etc.)
     * @return the Persian currency representation with decimal parts in smaller units
     */
    public static String toPersianCurrency(double amount, String currency) {
        long wholePart = (long) amount;
        int decimalPart = (int) Math.round((amount - wholePart) * 100);

        if (decimalPart == 0) {
            return toPersianCurrency(wholePart, currency);
        }

        String wholeWords = toPersian(Math.abs(wholePart));
        String decimalWords = toPersian(decimalPart);

        String mainCurrency = currency;
        String subCurrency = currency.equals(TOMAN) || currency.equals(TOMANS) ? RIAL : DINAR;

        String result = wholeWords + " " + mainCurrency + " و " + decimalWords + " " + subCurrency;

        if (amount < 0) {
            return "منفی " + result;
        }

        return result;
    }

    /**
     * Converts an amount to English currency words.
     * Example: 1500 with "dollar" becomes "one thousand five hundred dollars"
     *
     * @param amount   the amount to convert
     * @param currency the currency type (DOLLAR, EURO, POUND, etc.)
     * @return the English currency representation in words
     */
    public static String toEnglishCurrency(long amount, String currency) {
        if (amount == 0) {
            return "zero " + getPluralCurrency(currency, 0);
        }

        String amountWords = toEnglish(Math.abs(amount));
        String currencyWord = getPluralCurrency(currency, Math.abs(amount));

        if (amount < 0) {
            return "negative " + amountWords + " " + currencyWord;
        }

        return amountWords + " " + currencyWord;
    }

    /**
     * Converts a decimal amount to English currency words with cents.
     * The decimal part is converted to cents or equivalent sub-units.
     * Example: 15.25 with "dollar" becomes "fifteen dollars and twenty-five cents"
     *
     * @param amount   the decimal amount to convert
     * @param currency the currency type (DOLLAR, EURO, POUND, etc.)
     * @return the English currency representation with decimal parts as cents
     */
    public static String toEnglishCurrency(double amount, String currency) {
        long wholePart = (long) amount;
        int cents = (int) Math.round((amount - wholePart) * 100);

        if (cents == 0) {
            return toEnglishCurrency(wholePart, currency);
        }

        String wholeWords = toEnglish(Math.abs(wholePart));
        String centsWords = toEnglish(cents);

        String mainCurrency = getPluralCurrency(currency, Math.abs(wholePart));
        String subCurrency = cents == 1 ? CENT : CENTS;

        String result = wholeWords + " " + mainCurrency + " and " + centsWords + " " + subCurrency;

        if (amount < 0) {
            return "negative " + result;
        }

        return result;
    }

    // Private helper methods

    private static String convertToPersian(long number) {
        if (number == 0) {
            return "";
        }

        String result = "";
        int scaleIndex = 0;

        while (number > 0) {
            int group = (int) (number % 1000);
            if (group != 0) {
                String groupWords = convertGroupToPersian(group);
                if (scaleIndex > 0) {
                    groupWords += " " + PERSIAN_SCALE[scaleIndex];
                }
                if (!result.isEmpty()) {
                    result = groupWords + " و " + result;
                } else {
                    result = groupWords;
                }
            }
            number /= 1000;
            scaleIndex++;
        }

        return result;
    }

    private static String convertGroupToPersian(int number) {
        if (number == 0) {
            return "";
        }

        String result = "";

        int hundreds = number / 100;
        int remainder = number % 100;

        if (hundreds > 0) {
            result = PERSIAN_HUNDREDS[hundreds];
        }

        if (remainder >= 10 && remainder <= 19) {
            if (!result.isEmpty()) {
                result += " و ";
            }
            result += PERSIAN_TEN_TO_NINETEEN[remainder - 10];
        } else {
            int tens = remainder / 10;
            int ones = remainder % 10;

            if (tens > 0) {
                if (!result.isEmpty()) {
                    result += " و ";
                }
                result += PERSIAN_TENS[tens];
            }

            if (ones > 0) {
                if (!result.isEmpty()) {
                    result += " و ";
                }
                result += PERSIAN_ONES[ones];
            }
        }

        return result;
    }

    private static String convertToEnglish(long number) {
        if (number == 0) {
            return "";
        }

        String result = "";
        int scaleIndex = 0;

        while (number > 0) {
            int group = (int) (number % 1000);
            if (group != 0) {
                String groupWords = convertGroupToEnglish(group);
                if (scaleIndex > 0) {
                    groupWords += " " + ENGLISH_SCALE[scaleIndex];
                }
                if (!result.isEmpty()) {
                    result = groupWords + " " + result;
                } else {
                    result = groupWords;
                }
            }
            number /= 1000;
            scaleIndex++;
        }

        return result;
    }

    private static String convertGroupToEnglish(int number) {
        if (number == 0) {
            return "";
        }

        String result = "";

        int hundreds = number / 100;
        int remainder = number % 100;

        if (hundreds > 0) {
            result = ENGLISH_ONES[hundreds] + " hundred";
        }

        if (remainder >= 10 && remainder <= 19) {
            if (!result.isEmpty()) {
                result += " ";
            }
            result += ENGLISH_TEN_TO_NINETEEN[remainder - 10];
        } else {
            int tens = remainder / 10;
            int ones = remainder % 10;

            if (tens > 0) {
                if (!result.isEmpty()) {
                    result += " ";
                }
                result += ENGLISH_TENS[tens];
            }

            if (ones > 0) {
                if (!result.isEmpty()) {
                    if (tens > 0) {
                        result += "-";
                    } else {
                        result += " ";
                    }
                }
                result += ENGLISH_ONES[ones];
            }
        }

        return result;
    }

    private static String convertDecimalToPersian(String decimal) {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (char digit : decimal.toCharArray()) {
            int d = digit - '0';
            if (!first) {
                result.append(" ");
            }
            result.append(PERSIAN_ONES[d].isEmpty() ? "صفر" : PERSIAN_ONES[d]);
            first = false;
        }

        return result.toString();
    }

    private static String convertDecimalToEnglish(String decimal) {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (char digit : decimal.toCharArray()) {
            int d = digit - '0';
            if (!first) {
                result.append(" ");
            }
            result.append(ENGLISH_ONES[d].isEmpty() ? "zero" : ENGLISH_ONES[d]);
            first = false;
        }

        return result.toString();
    }

    private static String convertBigIntegerToPersian(BigInteger number) {
        if (number.compareTo(BigInteger.ZERO) == 0) {
            return "صفر";
        }

        // For very large numbers, use string representation
        String numStr = number.toString();
        if (numStr.length() > 21) { // Larger than quintillion
            return "عدد بسیار بزرگ";
        }

        return convertToPersian(number.longValue());
    }

    private static String getPluralCurrency(String currency, long amount) {
        if (amount == 1) {
            return currency;
        }

        switch (currency.toLowerCase()) {
            case "dollar":
                return DOLLARS;
            case "euro":
                return EUROS;
            case "pound":
                return POUNDS;
            default:
                return currency + "s";
        }
    }
}
