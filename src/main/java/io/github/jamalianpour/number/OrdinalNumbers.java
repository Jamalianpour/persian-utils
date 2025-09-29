package io.github.jamalianpour.number;

/**
 * Converts numbers to their ordinal representation in Persian and English.
 * Examples: 1st, 2nd, 3rd in English | اول، دوم، سوم in Persian
 */
public class OrdinalNumbers {

    // Persian ordinal words
    private static final String[] PERSIAN_ORDINALS_SPECIAL = {
            "", "اول", "دوم", "سوم", "چهارم", "پنجم", "ششم", "هفتم", "هشتم", "نهم",
            "دهم", "یازدهم", "دوازدهم", "سیزدهم", "چهاردهم", "پانزدهم",
            "شانزدهم", "هفدهم", "هجدهم", "نوزدهم", "بیستم"
    };

    private static final String[] PERSIAN_TENS_ORDINAL = {
            "", "", "بیستم", "سی‌ام", "چهلم", "پنجاهم", "شصتم", "هفتادم", "هشتادم", "نودم"
    };

    private static final String[] PERSIAN_HUNDREDS_ORDINAL = {
            "", "یکصدم", "دویستم", "سیصدم", "چهارصدم", "پانصدم",
            "ششصدم", "هفتصدم", "هشتصدم", "نهصدم"
    };

    // English ordinal suffixes
    private static final String[] ENGLISH_ORDINAL_SUFFIXES = {"th", "st", "nd", "rd"};

    /**
     * Converts a number to its ordinal representation in Persian.
     *
     * @param number the number to convert
     * @return the Persian ordinal representation
     */
    public static String toPersianOrdinal(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Ordinal numbers must be positive");
        }

        if (number <= 20) {
            return PERSIAN_ORDINALS_SPECIAL[number];
        }

        if (number < 100) {
            int tens = number / 10;
            int ones = number % 10;

            if (ones == 0) {
                return PERSIAN_TENS_ORDINAL[tens];
            } else {
                String base = NumberToWords.toPersian(number);
                return base + "م";
            }
        }

        if (number < 1000) {
            int hundreds = number / 100;
            int remainder = number % 100;

            if (remainder == 0) {
                return PERSIAN_HUNDREDS_ORDINAL[hundreds];
            } else {
                String base = NumberToWords.toPersian(number);
                return base + "م";
            }
        }

        // For numbers >= 1000
        String base = NumberToWords.toPersian(number);
        return base + "م";
    }

    /**
     * Converts a number to its ordinal representation in English words.
     * Example: 1 -> "first", 2 -> "second", 21 -> "twenty-first"
     *
     * @param number the number to convert
     * @return the English ordinal words
     */
    public static String toEnglishOrdinal(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Ordinal numbers must be positive");
        }

        // Special cases for 1-20
        switch (number) {
            case 1: return "first";
            case 2: return "second";
            case 3: return "third";
            case 4: return "fourth";
            case 5: return "fifth";
            case 6: return "sixth";
            case 7: return "seventh";
            case 8: return "eighth";
            case 9: return "ninth";
            case 10: return "tenth";
            case 11: return "eleventh";
            case 12: return "twelfth";
            case 13: return "thirteenth";
            case 14: return "fourteenth";
            case 15: return "fifteenth";
            case 16: return "sixteenth";
            case 17: return "seventeenth";
            case 18: return "eighteenth";
            case 19: return "nineteenth";
            case 20: return "twentieth";
            default:
        }

        // For numbers > 20
        if (number < 100) {
            int tens = number / 10;
            int ones = number % 10;

            String tensWord = "";
            switch (tens) {
                case 2: tensWord = "twenty"; break;
                case 3: tensWord = "thirty"; break;
                case 4: tensWord = "forty"; break;
                case 5: tensWord = "fifty"; break;
                case 6: tensWord = "sixty"; break;
                case 7: tensWord = "seventy"; break;
                case 8: tensWord = "eighty"; break;
                case 9: tensWord = "ninety"; break;
                default:
            }

            if (ones == 0) {
                return tensWord.substring(0, tensWord.length() - 1) + "ieth";
            } else {
                return tensWord + "-" + toEnglishOrdinal(ones);
            }
        }

        if (number == 100) return "one hundredth";
        if (number == 1000) return "one thousandth";
        if (number == 1000000) return "one millionth";

        // For complex numbers, convert to cardinal and add appropriate suffix
        String cardinal = NumberToWords.toEnglish(number);

        // Handle compound numbers
        if (number % 100 != 0 && number % 100 <= 20) {
            // Replace last word with ordinal
            String[] parts = cardinal.split(" ");
            parts[parts.length - 1] = toEnglishOrdinal(number % 100);
            return String.join(" ", parts);
        } else if (number % 10 == 0) {
            // Numbers ending in 10, 20, 30, etc.
            return cardinal.substring(0, cardinal.length() - 1) + "ieth";
        } else {
            // Replace last word with ordinal form
            String[] parts = cardinal.split("[ -]");
            String lastWord = parts[parts.length - 1];
            String ordinalLast = toEnglishOrdinal(number % 10);

            return cardinal.substring(0, cardinal.lastIndexOf(lastWord)) + ordinalLast;
        }
    }

    /**
     * Gets the ordinal suffix for a number in English (st, nd, rd, th).
     *
     * @param number the number
     * @return the appropriate suffix
     */
    public static String getEnglishOrdinalSuffix(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Ordinal numbers must be positive");
        }

        int mod100 = number % 100;
        int mod10 = number % 10;

        // Special cases for 11th, 12th, 13th
        if (mod100 >= 11 && mod100 <= 13) {
            return ENGLISH_ORDINAL_SUFFIXES[0]; // "th"
        }

        // Regular cases
        if (mod10 >= 1 && mod10 <= 3) {
            return ENGLISH_ORDINAL_SUFFIXES[mod10];
        }

        return ENGLISH_ORDINAL_SUFFIXES[0]; // "th"
    }

    /**
     * Formats a number with its English ordinal suffix.
     * Example: 1 -> "1st", 2 -> "2nd", 3 -> "3rd", 4 -> "4th"
     *
     * @param number the number to format
     * @return the formatted ordinal number
     */
    public static String formatEnglishOrdinal(int number) {
        return number + getEnglishOrdinalSuffix(number);
    }

    /**
     * Formats a number with its Persian ordinal suffix.
     * Example: 1 -> "۱م", 2 -> "۲م"
     *
     * @param number the number to format
     * @return the formatted ordinal number in Persian
     */
    public static String formatPersianOrdinal(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Ordinal numbers must be positive");
        }

        if (number <= 20) {
            return PERSIAN_ORDINALS_SPECIAL[number];
        }

        return PersianNumberConverter.toPersianDigits(String.valueOf(number)) + "م";
    }

    /**
     * Checks if a number requires special ordinal handling in Persian.
     *
     * @param number the number to check
     * @return true if the number has a special ordinal form
     */
    public static boolean hasSpecialPersianOrdinal(int number) {
        return number > 0 && number <= 20;
    }
}