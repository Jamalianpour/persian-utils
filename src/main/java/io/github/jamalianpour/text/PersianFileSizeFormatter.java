package io.github.jamalianpour.text;

import io.github.jamalianpour.number.PersianNumberConverter;
import io.github.jamalianpour.number.NumberToWords;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Formats file sizes in Persian with appropriate units.
 * Supports both binary (1024) and decimal (1000) modes with Persian translations.
 * Examples: 1024 → "۱ کیلوبایت", 1048576 → "۱ مگابایت"
 */
public class PersianFileSizeFormatter {

    /**
     * Calculation mode for file size units
     */
    public enum SizeMode {
        BINARY(1024),   // 1024 bytes = 1 KB (standard for storage)
        DECIMAL(1000);  // 1000 bytes = 1 KB (standard for network)

        private final int base;

        SizeMode(int base) {
            this.base = base;
        }

        public int getBase() {
            return base;
        }
    }

    /**
     * Number formatting style
     */
    public enum NumberStyle {
        /**
         * ۱۰۲۴ بایت
         */
        NUMERIC,
        /**
         * یک هزار و بیست و چهار بایت
         */
        WORDS
    }

    /**
     * Unit display style
     */
    public enum UnitStyle {
        /**
         * کیلوبایت
         */
        FULL,
        /**
         * کیلوبایت (Persian doesn't commonly use KB abbreviations)
         */
        SHORT,
        /**
         * KB (for technical contexts)
         */
        ENGLISH
    }

    /**
     * File size units with Persian translations
     */
    private enum FileSizeUnit {
        /**
         * بایت
         */
        BYTE("بایت", "بایت", "B", 0),
        /**
         * کیلوبایت
         */
        KILOBYTE("کیلوبایت", "کیلوبایت", "KB", 1),
        /**
         * مگابایت
         */
        MEGABYTE("مگابایت", "مگابایت", "MB", 2),
        /**
         * گیگابایت
         */
        GIGABYTE("گیگابایت", "گیگابایت", "GB", 3),
        /**
         * ترابایت
         */
        TERABYTE("ترابایت", "ترابایت", "TB", 4),
        /**
         * پتابایت
         */
        PETABYTE("پتابایت", "پتابایت", "PB", 5),
        /**
         * اگزابایت
         */
        EXABYTE("اگزابایت", "اگزابایت", "EB", 6),
        /**
         * زتابایت
         */
        ZETTABYTE("زتابایت", "زتابایت", "ZB", 7),
        /**
         * یوتابایت
         */
        YOTTABYTE("یوتابایت", "یوتابایت", "YB", 8);

        private final String persianName;
        private final String shortForm;
        private final String englishAbbreviation;
        private final int power;

        FileSizeUnit(String persianName, String shortForm, String englishAbbreviation, int power) {
            this.persianName = persianName;
            this.shortForm = shortForm;
            this.englishAbbreviation = englishAbbreviation;
            this.power = power;
        }

        /**
         * Returns the Persian name of the file size unit.
         *
         * @return the Persian name of the file size unit
         */
        public String getPersianName() {
            return persianName;
        }

        /**
         * Returns the short form of the file size unit (e.g., "کیلو" for کیلوبایت).
         *
         * @return the short form of the file size unit
         */
        public String getShortForm() {
            return shortForm;
        }

        /**
         * Returns the English abbreviation of the file size unit (e.g., "KB" for کیلوبایت).
         *
         * @return the English abbreviation of the file size unit
         */
        public String getEnglishAbbreviation() {
            return englishAbbreviation;
        }

        /**
         * Returns the power of the file size unit.
         * For example, the power of کیلوبایت is 3, because 1 کیلوبایت is equal to 1024 bytes.
         *
         * @return the power of the file size unit
         */
        public int getPower() {
            return power;
        }
    }

    /**
     * Formats file size with default settings (binary mode, numeric style, full units).
     * Example: 1024 becomes "۱ کیلوبایت"
     *
     * @param bytes the file size in bytes
     * @return formatted Persian file size string
     */
    public static String format(long bytes) {
        return format(bytes, SizeMode.BINARY, NumberStyle.NUMERIC, UnitStyle.FULL, 1);
    }

    /**
     * Formats file size with specified calculation mode.
     *
     * @param bytes the file size in bytes
     * @param mode  the calculation mode (BINARY uses 1024, DECIMAL uses 1000)
     * @return formatted Persian file size string
     */
    public static String format(long bytes, SizeMode mode) {
        return format(bytes, mode, NumberStyle.NUMERIC, UnitStyle.FULL, 1);
    }

    /**
     * Formats file size with specified mode and number style.
     *
     * @param bytes       the file size in bytes
     * @param mode        the calculation mode (binary or decimal)
     * @param numberStyle the number formatting style
     * @return formatted Persian file size string
     */
    public static String format(long bytes, SizeMode mode, NumberStyle numberStyle) {
        return format(bytes, mode, numberStyle, UnitStyle.FULL, 1);
    }

    /**
     * Formats file size with all formatting options.
     *
     * @param bytes         the file size in bytes
     * @param mode          the calculation mode (binary or decimal)
     * @param numberStyle   the number formatting style
     * @param unitStyle     the unit display style
     * @param decimalPlaces number of decimal places to show
     * @return formatted Persian file size string
     */
    public static String format(long bytes, SizeMode mode, NumberStyle numberStyle,
                                UnitStyle unitStyle, int decimalPlaces) {
        if (bytes < 0) {
            throw new IllegalArgumentException("File size cannot be negative");
        }

        if (bytes == 0) {
            return formatZero(unitStyle);
        }

        // Find the appropriate unit
        FileSizeUnit unit = findBestUnit(bytes, mode);

        // Calculate the value
        double value = calculateValue(bytes, unit, mode);

        // Format the result
        return formatResult(value, unit, numberStyle, unitStyle, decimalPlaces);
    }


    /**
     * Formats a file size with all formatting options.
     *
     * @param bytes         the file size in bytes
     * @param mode          the calculation mode (binary or decimal)
     * @param numberStyle   the number formatting style
     * @param unitStyle     the unit display style
     * @param decimalPlaces number of decimal places to show
     * @return formatted Persian file size string
     */
    public static String format(double bytes, SizeMode mode, NumberStyle numberStyle,
                                UnitStyle unitStyle, int decimalPlaces) {
        if (bytes < 0) {
            throw new IllegalArgumentException("File size cannot be negative");
        }

        if (bytes == 0) {
            return formatZero(unitStyle);
        }

        // Find the appropriate unit
        FileSizeUnit unit = findBestUnit((long) bytes, mode);

        // Calculate the value
        double value = calculateValue(bytes, unit, mode);

        // Format the result
        return formatResult(value, unit, numberStyle, unitStyle, decimalPlaces);
    }

    /**
     * Provides human-readable file size with automatic precision.
     * Automatically determines appropriate decimal places based on size.
     *
     * @param bytes the file size in bytes
     * @return formatted Persian file size string with appropriate precision
     * @throws IllegalArgumentException if bytes is negative
     */
    public static String formatHumanReadable(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("File size cannot be negative");
        }

        if (bytes == 0) {
            return "۰ بایت";
        }

        FileSizeUnit unit = findBestUnit(bytes, SizeMode.BINARY);
        double value = calculateValue(bytes, unit, SizeMode.BINARY);

        // Automatically determine decimal places
        int decimalPlaces = (value >= 100) ? 0 : (value >= 10) ? 1 : 2;

        return formatResult(value, unit, NumberStyle.NUMERIC, UnitStyle.FULL, decimalPlaces);
    }

    /**
     * Formats file size with words for accessibility.
     * Example: 1024 becomes "یک کیلوبایت"
     *
     * @param bytes the file size in bytes
     * @return formatted Persian file size string with number words
     */
    public static String formatWithWords(long bytes) {
        return format(bytes, SizeMode.BINARY, NumberStyle.WORDS, UnitStyle.FULL, 0);
    }

    /**
     * Formats multiple file sizes for comparison.
     *
     * @param sizes array of file sizes in bytes
     * @return array of formatted Persian file size strings
     */
    public static String[] formatMultiple(long[] sizes) {
        if (sizes == null) {
            return new String[0];
        }

        String[] formatted = new String[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            formatted[i] = formatHumanReadable(sizes[i]);
        }
        return formatted;
    }

    /**
     * Parses a Persian file size string back to bytes (basic implementation).
     * Converts Persian digits and extracts numeric value and unit.
     *
     * @param sizeString the Persian file size string (e.g., "۱ کیلوبایت")
     * @return file size in bytes
     * @throws IllegalArgumentException if the string format is invalid
     */
    public static long parse(String sizeString) {
        if (sizeString == null || sizeString.trim().isEmpty()) {
            return 0;
        }

        String normalized = sizeString.trim().toLowerCase();

        // Convert Persian digits to English
        normalized = PersianNumberConverter.toEnglishDigits(normalized);

        // Simple parsing logic - extract number and unit
        String[] parts = normalized.split("\\s+");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid file size format: " + sizeString);
        }

        try {
            double value = Double.parseDouble(parts[0]);
            String unitStr = parts[1];

            // Find matching unit
            FileSizeUnit unit = findUnitByPersianName(unitStr);
            if (unit == null) {
                throw new IllegalArgumentException("Unknown unit: " + unitStr);
            }

            // Calculate bytes
            return (long) (value * Math.pow(SizeMode.BINARY.getBase(), unit.getPower()));

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in: " + sizeString, e);
        }
    }

    /**
     * Finds the best unit for the given byte size.
     */
    private static FileSizeUnit findBestUnit(long bytes, SizeMode mode) {
        FileSizeUnit[] units = FileSizeUnit.values();

        for (int i = units.length - 1; i >= 0; i--) {
            long threshold = (long) Math.pow(mode.getBase(), units[i].getPower());
            if (bytes >= threshold) {
                return units[i];
            }
        }

        return FileSizeUnit.BYTE;
    }

    /**
     * Calculates the value for the given unit.
     */
    private static double calculateValue(double bytes, FileSizeUnit unit, SizeMode mode) {
        if (unit == FileSizeUnit.BYTE) {
            return bytes;
        }

        double divisor = Math.pow(mode.getBase(), unit.getPower());
        return bytes / divisor;
    }

    /**
     * Formats the final result string.
     */
    private static String formatResult(double value, FileSizeUnit unit, NumberStyle numberStyle,
                                       UnitStyle unitStyle, int decimalPlaces) {
        // Round the value
        BigDecimal rounded = BigDecimal.valueOf(value).setScale(decimalPlaces, RoundingMode.HALF_UP);

        String numberPart;
        String unitPart;

        // Format number part
        if (numberStyle == NumberStyle.WORDS && rounded.longValue() == rounded.doubleValue()) {
            // Use words only for whole numbers
            numberPart = NumberToWords.toPersian(rounded.intValue());
        } else {
            // Format with Persian digits
            if (decimalPlaces > 0 && rounded.doubleValue() != rounded.longValue()) {
                numberPart = PersianNumberConverter.toPersianDigits(rounded.toString());
            } else {
                numberPart = PersianNumberConverter.toPersianDigits(String.valueOf(rounded.longValue()));
            }
        }

        // Format unit part
        switch (unitStyle) {
            case FULL:
                unitPart = unit.getPersianName();
                break;
            case SHORT:
                unitPart = unit.getShortForm();
                break;
            case ENGLISH:
                unitPart = unit.getEnglishAbbreviation();
                break;
            default:
                unitPart = unit.getPersianName();
        }

        return numberPart + " " + unitPart;
    }

    /**
     * Formats zero value.
     */
    private static String formatZero(UnitStyle unitStyle) {
        switch (unitStyle) {
            case ENGLISH:
                return "۰ B";
            default:
                return "۰ بایت";
        }
    }

    /**
     * Finds unit by Persian name.
     */
    private static FileSizeUnit findUnitByPersianName(String persianName) {
        for (FileSizeUnit unit : FileSizeUnit.values()) {
            if (unit.getPersianName().equals(persianName) ||
                    unit.getShortForm().equals(persianName)) {
                return unit;
            }
        }
        return null;
    }

    /**
     * Utility methods for common file sizes.
     */
    public static class Common {
        public static String formatBytes(long bytes) {
            return PersianNumberConverter.toPersianDigits(String.valueOf(bytes)) + " بایت";
        }

        public static String formatKilobytes(double kb) {
            return PersianNumberConverter.toPersianDigits(String.valueOf((long) kb)) + " کیلوبایت";
        }

        public static String formatMegabytes(double mb) {
            return PersianNumberConverter.toPersianDigits(String.valueOf((long) mb)) + " مگابایت";
        }

        public static String formatGigabytes(double gb) {
            return PersianNumberConverter.toPersianDigits(String.valueOf((long) gb)) + " گیگابایت";
        }

        public static String formatTerabytes(double tb) {
            return PersianNumberConverter.toPersianDigits(String.valueOf((long) tb)) + " ترابایت";
        }

        // Quick conversions
        public static long kilobytesToBytes(double kb) {
            return (long) (kb * 1024);
        }

        public static long megabytesToBytes(double mb) {
            return (long) (mb * 1024 * 1024);
        }

        public static long gigabytesToBytes(double gb) {
            return (long) (gb * 1024 * 1024 * 1024);
        }

        public static long terabytesToBytes(double tb) {
            return (long) (tb * 1024L * 1024 * 1024 * 1024);
        }
    }
}