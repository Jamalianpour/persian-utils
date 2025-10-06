package io.github.jamalianpour.text;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Comprehensive utilities for Persian/Farsi text processing, validation, and manipulation.
 * Handles Persian character detection, Arabic to Persian conversion, text normalization, and more.
 */
public class PersianTextUtils {

    // Persian alphabet characters
    private static final String PERSIAN_CHARS = "آابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهی";

    // Persian digits
    private static final String PERSIAN_DIGITS = "۰۱۲۳۴۵۶۷۸۹";

    // Arabic characters that should be converted to Persian
    private static final Map<Character, Character> ARABIC_TO_PERSIAN_MAP = new HashMap<>();

    static {
        ARABIC_TO_PERSIAN_MAP.put('ك', 'ک');  // Arabic Kaf to Persian Kaf
        ARABIC_TO_PERSIAN_MAP.put('ڪ', 'ک');  // Another Arabic Kaf variant
        ARABIC_TO_PERSIAN_MAP.put('ﻙ', 'ک');  // Arabic Kaf initial form
        ARABIC_TO_PERSIAN_MAP.put('ﻚ', 'ک');  // Arabic Kaf final form
        ARABIC_TO_PERSIAN_MAP.put('ي', 'ی');  // Arabic Yeh to Persian Yeh
        ARABIC_TO_PERSIAN_MAP.put('ې', 'ی');  // Another Arabic Yeh variant
        ARABIC_TO_PERSIAN_MAP.put('ى', 'ی');  // Arabic Alef Maksura to Persian Yeh
        ARABIC_TO_PERSIAN_MAP.put('ﻱ', 'ی');  // Arabic Yeh initial form
        ARABIC_TO_PERSIAN_MAP.put('ﻲ', 'ی');  // Arabic Yeh final form
        ARABIC_TO_PERSIAN_MAP.put('ﻯ', 'ی');  // Arabic Yeh isolated form
        ARABIC_TO_PERSIAN_MAP.put('ﻰ', 'ی');  // Arabic Yeh with tail
        ARABIC_TO_PERSIAN_MAP.put('ہ', 'ه');  // Arabic Heh Goal to Persian Heh
        ARABIC_TO_PERSIAN_MAP.put('ھ', 'ه');  // Arabic Heh Doachashmee to Persian Heh
        ARABIC_TO_PERSIAN_MAP.put('ە', 'ه');  // Another Heh variant
        ARABIC_TO_PERSIAN_MAP.put('ة', 'ه');  // Arabic Teh Marbuta to Persian Heh
        ARABIC_TO_PERSIAN_MAP.put('ۀ', 'ه');  // Persian Heh with Yeh above
        ARABIC_TO_PERSIAN_MAP.put('أ', 'ا');  // Arabic Alef with Hamza above
        ARABIC_TO_PERSIAN_MAP.put('إ', 'ا');  // Arabic Alef with Hamza below
        ARABIC_TO_PERSIAN_MAP.put('ؤ', 'و');  // Arabic Waw with Hamza
        ARABIC_TO_PERSIAN_MAP.put('ئ', 'ی');  // Arabic Yeh with Hamza
        ARABIC_TO_PERSIAN_MAP.put('ء', 'ی');  // Arabic Hamza

        // Arabic digits to Persian digits
        ARABIC_TO_PERSIAN_MAP.put('٠', '۰');
        ARABIC_TO_PERSIAN_MAP.put('١', '۱');
        ARABIC_TO_PERSIAN_MAP.put('٢', '۲');
        ARABIC_TO_PERSIAN_MAP.put('٣', '۳');
        ARABIC_TO_PERSIAN_MAP.put('٤', '۴');
        ARABIC_TO_PERSIAN_MAP.put('٥', '۵');
        ARABIC_TO_PERSIAN_MAP.put('٦', '۶');
        ARABIC_TO_PERSIAN_MAP.put('٧', '۷');
        ARABIC_TO_PERSIAN_MAP.put('٨', '۸');
        ARABIC_TO_PERSIAN_MAP.put('٩', '۹');
    }

    // Persian diacritics (Harakat)
    private static final String PERSIAN_DIACRITICS = "\u064B\u064C\u064D\u064E\u064F\u0650\u0651\u0652\u0653\u0654\u0655\u0656\u0657\u0658\u0670";

    // Zero-width characters
    private static final String ZERO_WIDTH_CHARS = "\u200C\u200D\u200E\u200F\u202A\u202B\u202C\u202D\u202E\u2066\u2067\u2068\u2069";

    // Persian punctuation
    private static final String PERSIAN_PUNCTUATION = "،؛؟«»٪";

    // Pattern for Persian characters (including digits and diacritics)
    private static final Pattern PERSIAN_PATTERN = Pattern.compile(
            "[" + PERSIAN_CHARS + PERSIAN_DIGITS + PERSIAN_DIACRITICS + PERSIAN_PUNCTUATION + "]+");

    // Pattern for Persian words
    private static final Pattern PERSIAN_WORD_PATTERN = Pattern.compile(
            "[" + PERSIAN_CHARS + "]+");

    /**
     * Checks if a string contains any Persian characters.
     * Includes Persian letters, digits, and punctuation marks.
     *
     * @param text the text to check
     * @return true if the text contains at least one Persian character, false otherwise
     */
    public static boolean containsPersian(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        for (char c : text.toCharArray()) {
            if (isPersianChar(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a string is entirely composed of Persian characters.
     * Allows Persian letters, digits, punctuation, and whitespace.
     *
     * @param text the text to check
     * @return true if the text is entirely Persian (or empty), false otherwise
     */
    public static boolean isPersian(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        for (char c : text.toCharArray()) {
            if (!isPersianChar(c) && !Character.isWhitespace(c) && !isPersianPunctuation(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a string is entirely composed of Persian characters (strict mode - no English allowed).
     *
     * @param text             the text to check
     * @param allowDigits      whether to allow Persian digits
     * @param allowPunctuation whether to allow Persian punctuation
     * @return true if entirely Persian according to criteria, false otherwise
     */
    public static boolean isPersianStrict(String text, boolean allowDigits, boolean allowPunctuation) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        for (char c : text.toCharArray()) {
            if (Character.isWhitespace(c)) {
                continue;
            }

            if (isPersianLetter(c)) {
                continue;
            }

            if (allowDigits && isPersianDigit(c)) {
                continue;
            }

            if (allowPunctuation && isPersianPunctuation(c)) {
                continue;
            }

            return false;
        }
        return true;
    }

    /**
     * Converts Arabic characters to their Persian equivalents.
     * Example: converts Arabic ك and ي to Persian ک and ی
     *
     * @param text the text containing Arabic characters to convert
     * @return text with Arabic characters replaced by Persian equivalents
     */
    public static String arabicToPersian(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append(ARABIC_TO_PERSIAN_MAP.getOrDefault(c, c));
        }
        return result.toString();
    }

    /**
     * Normalizes Persian text by converting Arabic characters, removing diacritics,
     * zero-width characters, and normalizing whitespace.
     *
     * @param text the text to normalize
     * @return normalized Persian text suitable for processing and comparison
     */
    public static String normalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Convert Arabic to Persian
        text = arabicToPersian(text);

        // Remove diacritics
        text = removeDiacritics(text);

        // Remove zero-width characters
        text = removeZeroWidthChars(text);

        // Normalize whitespace
        text = normalizeWhitespace(text);

        return text;
    }

    /**
     * Removes Persian diacritics (Harakat) from text.
     * Diacritics include marks like Fatha, Damma, Kasra, etc.
     *
     * @param text the text to process
     * @return text without diacritics, preserving all other characters
     */
    public static String removeDiacritics(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (PERSIAN_DIACRITICS.indexOf(c) == -1) {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Removes zero-width characters from text.
     *
     * @param text the text to process
     * @return text without zero-width characters
     */
    public static String removeZeroWidthChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (ZERO_WIDTH_CHARS.indexOf(c) == -1) {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Normalizes whitespace in text (converts multiple spaces to single space).
     *
     * @param text the text to process
     * @return text with normalized whitespace
     */
    public static String normalizeWhitespace(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        return text.replaceAll("\\s+", " ").trim();
    }

    /**
     * Checks if a character is a Persian letter.
     *
     * @param c the character to check
     * @return true if Persian letter, false otherwise
     */
    public static boolean isPersianLetter(char c) {
        return PERSIAN_CHARS.indexOf(c) != -1;
    }

    /**
     * Checks if a character is a Persian digit.
     *
     * @param c the character to check
     * @return true if Persian digit, false otherwise
     */
    public static boolean isPersianDigit(char c) {
        return PERSIAN_DIGITS.indexOf(c) != -1;
    }

    /**
     * Checks if a character is Persian punctuation.
     *
     * @param c the character to check
     * @return true if Persian punctuation, false otherwise
     */
    public static boolean isPersianPunctuation(char c) {
        return PERSIAN_PUNCTUATION.indexOf(c) != -1;
    }

    /**
     * Checks if a character is any Persian character (letter, digit, or punctuation).
     *
     * @param c the character to check
     * @return true if Persian character, false otherwise
     */
    public static boolean isPersianChar(char c) {
        return isPersianLetter(c) || isPersianDigit(c) || isPersianPunctuation(c);
    }

    /**
     * Counts Persian characters in a text.
     *
     * @param text the text to analyze
     * @return count of Persian characters
     */
    public static int countPersianChars(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (char c : text.toCharArray()) {
            if (isPersianChar(c)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Extracts all Persian words from a text.
     * Words are defined as continuous sequences of Persian letters.
     *
     * @param text the text to process
     * @return list of Persian words found in the text
     */
    public static List<String> extractPersianWords(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> words = new ArrayList<>();
        Matcher matcher = PERSIAN_WORD_PATTERN.matcher(text);

        while (matcher.find()) {
            words.add(matcher.group());
        }

        return words;
    }

    /**
     * Gets the percentage of Persian content in a text.
     * Calculates the ratio of Persian characters to total non-whitespace characters.
     *
     * @param text the text to analyze
     * @return percentage of Persian content (0-100)
     */
    public static double getPersianPercentage(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int totalChars = 0;
        int persianChars = 0;

        for (char c : text.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                totalChars++;
                if (isPersianChar(c)) {
                    persianChars++;
                }
            }
        }

        if (totalChars == 0) {
            return 0;
        }

        return (persianChars * 100.0) / totalChars;
    }

    /**
     * Determines the primary text direction (RTL or LTR) based on character analysis.
     * Persian and Arabic characters are considered RTL, Latin characters are LTR.
     *
     * @param text the text to analyze
     * @return TextDirection.RTL, TextDirection.LTR, or TextDirection.NEUTRAL
     */
    public static TextDirection getTextDirection(String text) {
        if (text == null || text.isEmpty()) {
            return TextDirection.NEUTRAL;
        }

        int rtlCount = 0;
        int ltrCount = 0;

        for (char c : text.toCharArray()) {
            if (Character.isWhitespace(c)) {
                continue;
            }

            if (isPersianChar(c) || isArabicChar(c)) {
                rtlCount++;
            } else if (Character.isLetter(c)) {
                ltrCount++;
            }
        }

        if (rtlCount > ltrCount) {
            return TextDirection.RTL;
        } else if (ltrCount > rtlCount) {
            return TextDirection.LTR;
        } else {
            return TextDirection.NEUTRAL;
        }
    }

    /**
     * Checks if a character is an Arabic character.
     *
     * @param c the character to check
     * @return true if Arabic character, false otherwise
     */
    public static boolean isArabicChar(char c) {
        return (c >= 0x0600 && c <= 0x06FF) || // Arabic block
                (c >= 0x0750 && c <= 0x077F) || // Arabic Supplement
                (c >= 0x08A0 && c <= 0x08FF) || // Arabic Extended-A
                (c >= 0xFB50 && c <= 0xFDFF) || // Arabic Presentation Forms-A
                (c >= 0xFE70 && c <= 0xFEFF);   // Arabic Presentation Forms-B
    }

    /**
     * Checks if text contains mixed Persian and English content.
     *
     * @param text the text to check
     * @return true if contains both Persian and English, false otherwise
     */
    public static boolean isMixedPersianEnglish(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        boolean hasPersian = false;
        boolean hasEnglish = false;

        for (char c : text.toCharArray()) {
            if (isPersianChar(c)) {
                hasPersian = true;
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                hasEnglish = true;
            }

            if (hasPersian && hasEnglish) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds Right-to-Left mark (RLM) at the beginning of text.
     *
     * @param text the text to process
     * @return text with RLM mark
     */
    public static String addRLM(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return "\u200F" + text;
    }

    /**
     * Adds Left-to-Right mark (LRM) at the beginning of text.
     *
     * @param text the text to process
     * @return text with LRM mark
     */
    public static String addLRM(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return "\u200E" + text;
    }

    /**
     * Adds Zero-Width Non-Joiner (ZWNJ) between characters.
     *
     * @param text     the text to process
     * @param position the position to insert ZWNJ
     * @return text with ZWNJ inserted
     */
    public static String addZWNJ(String text, int position) {
        if (text == null || text.isEmpty() || position < 0 || position > text.length()) {
            return text;
        }
        return text.substring(0, position) + "\u200C" + text.substring(position);
    }

    /**
     * Converts Persian text to a format suitable for URLs (removes spaces, converts to lowercase).
     *
     * @param text the text to convert
     * @return URL-friendly Persian text
     */
    public static String toPersianSlug(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Normalize the text
        text = normalize(text);

        // Replace spaces with hyphens
        text = text.replaceAll("\\s+", "-");

        // Remove non-Persian and non-alphanumeric characters (except hyphens)
        text = text.replaceAll("[^" + PERSIAN_CHARS + PERSIAN_DIGITS + "a-zA-Z0-9\\-]", "");

        // Remove consecutive hyphens
        text = text.replaceAll("-+", "-");

        // Remove leading and trailing hyphens
        text = text.replaceAll("^-|-$", "");

        return text.toLowerCase();
    }

    /**
     * Text direction enumeration.
     */
    public enum TextDirection {
        /**
         * Right to left text
         */
        RTL("Right-to-Left"),
        /**
         * Left to right text
         */
        LTR("Left-to-Right"),
        /**
         * Neutral
         */
        NEUTRAL("Neutral");

        private final String description;

        TextDirection(String description) {
            this.description = description;
        }

        /**
         * @return get description of enum
         */
        public String getDescription() {
            return description;
        }
    }

    /**
     * Statistics about Persian content in a text.
     */
    public static class PersianTextStats {
        private final int totalChars;
        private final int persianChars;
        private final int englishChars;
        private final int persianWords;
        private final double persianPercentage;
        private final TextDirection direction;

        /**
         * Statistics about Persian content in a text.
         *
         * @param text input text
         */
        public PersianTextStats(String text) {
            if (text == null || text.isEmpty()) {
                this.totalChars = 0;
                this.persianChars = 0;
                this.englishChars = 0;
                this.persianWords = 0;
                this.persianPercentage = 0;
                this.direction = TextDirection.NEUTRAL;
            } else {
                this.totalChars = text.length();
                this.persianChars = countPersianChars(text);
                this.englishChars = countEnglishChars(text);
                this.persianWords = extractPersianWords(text).size();
                this.persianPercentage = getPersianPercentage();
                this.direction = getTextDirection(text);
            }
        }

        private int countEnglishChars(String text) {
            int count = 0;
            for (char c : text.toCharArray()) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    count++;
                }
            }
            return count;
        }

        /**
         * @return totalChars
         */
        public int getTotalChars() {
            return totalChars;
        }

        /**
         * @return persianChars
         */
        public int getPersianChars() {
            return persianChars;
        }

        /**
         * @return englishChars
         */
        public int getEnglishChars() {
            return englishChars;
        }

        /**
         * @return persianWords
         */
        public int getPersianWords() {
            return persianWords;
        }

        /**
         * @return persianPercentage
         */
        public double getPersianPercentage() {
            return persianPercentage;
        }

        /**
         * @return text direction
         */
        public TextDirection getDirection() {
            return direction;
        }

        @Override
        public String toString() {
            return String.format(
                    "Total: %d chars, Persian: %d (%.1f%%), English: %d, Persian words: %d, Direction: %s",
                    totalChars, persianChars, persianPercentage, englishChars, persianWords, direction
            );
        }
    }

    /**
     * Replaces English digits with Persian digits in text.
     *
     * @param text the text to process
     * @return text with Persian digits
     */
    public static String englishToPersianDigits(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        char[] persianDigits = {'۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'};
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (c >= '0' && c <= '9') {
                result.append(persianDigits[c - '0']);
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Fixes common Persian typing issues.
     *
     * @param text the text to fix
     * @return fixed text
     */
    public static String fixPersianTyping(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Convert Arabic characters to Persian
        text = arabicToPersian(text);

        // Fix Persian Yeh and Kaf issues
        text = text.replace("ي", "ی");
        text = text.replace("ك", "ک");

        // Fix spacing issues with punctuation
        text = text.replaceAll("\\s+([،؛؟!٪])", "$1");
        text = text.replaceAll("([،؛؟!])(?!\\s)", "$1 ");

        // Fix quotation marks
        text = text.replaceAll("\"([^\"]+)\"", "«$1»");

        return text;
    }
}