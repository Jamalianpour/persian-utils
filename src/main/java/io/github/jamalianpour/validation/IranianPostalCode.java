package io.github.jamalianpour.validation;

import io.github.jamalianpour.number.PersianNumberConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Validator and utilities for Iranian postal codes (کد پستی).
 * Iranian postal codes are 10 digits where the first 5 digits represent the region/city
 * and the last 5 digits represent the specific area/street/building.
 */
public class IranianPostalCode {

    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{10}$");
    private static final List<PostalCodeRange> POSTAL_CODE_RANGES = new ArrayList<>();

    static {
        loadPostalCodeData();
    }

    /**
     * Postal code range holder class.
     */
    public static class PostalCodeRange {
        private final String provincePersian;
        private final String provinceEnglish;
        private final String cityPersian;
        private final String cityEnglish;
        private final int rangeStart;
        private final int rangeEnd;

        public PostalCodeRange(String provincePersian, String provinceEnglish,
                               String cityPersian, String cityEnglish,
                               int rangeStart, int rangeEnd) {
            this.provincePersian = provincePersian;
            this.provinceEnglish = provinceEnglish;
            this.cityPersian = cityPersian;
            this.cityEnglish = cityEnglish;
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
        }

        public String getProvincePersian() {
            return provincePersian;
        }

        public String getProvinceEnglish() {
            return provinceEnglish;
        }

        public String getCityPersian() {
            return cityPersian;
        }

        public String getCityEnglish() {
            return cityEnglish;
        }

        public int getRangeStart() {
            return rangeStart;
        }

        public int getRangeEnd() {
            return rangeEnd;
        }

        @Override
        public String toString() {
            return String.format("%s - %s (%s - %s): %d-%d",
                    provincePersian, provinceEnglish,
                    cityPersian, cityEnglish,
                    rangeStart, rangeEnd);
        }
    }

    /**
     * Loads postal code data from CSV file.
     */
    private static void loadPostalCodeData() {
        String resourcePath = "PostalCode.csv"; // File in src/main/resources/

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(IranianNationalId.class.getClassLoader()
                                .getResourceAsStream(resourcePath)),
                        StandardCharsets.UTF_8))) {
            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String provincePersian = parts[0].trim();
                    String provinceEnglish = parts[1].trim();
                    String cityPersian = parts[2].trim();
                    String cityEnglish = parts[3].trim();
                    int rangeStart = Integer.parseInt(parts[4].trim());
                    int rangeEnd = Integer.parseInt(parts[5].trim());

                    POSTAL_CODE_RANGES.add(new PostalCodeRange(
                            provincePersian, provinceEnglish,
                            cityPersian, cityEnglish,
                            rangeStart, rangeEnd
                    ));
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading postal code data: " + e.getMessage());
        }
    }

    /**
     * Validates an Iranian postal code.
     *
     * @param postalCode the postal code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String postalCode) {
        if (postalCode == null || postalCode.isEmpty()) {
            return false;
        }

        // Normalize the postal code
        postalCode = normalize(postalCode);

        if (postalCode == null) {
            return false;
        }

        // Check basic format
        if (!POSTAL_CODE_PATTERN.matcher(postalCode).matches()) {
            return false;
        }

        // Check for invalid patterns (all same digits, sequential)
        if (hasInvalidPattern(postalCode)) {
            return false;
        }

        // Check if postal code falls within valid ranges
        int regionCode = Integer.parseInt(postalCode.substring(0, 5));
        return findPostalCodeRange(regionCode) != null;
    }

    /**
     * Finds the postal code range for a given region code.
     *
     * @param regionCode the 5-digit region code
     * @return PostalCodeRange or null if not found
     */
    private static PostalCodeRange findPostalCodeRange(int regionCode) {
        for (PostalCodeRange range : POSTAL_CODE_RANGES) {
            if (regionCode >= range.getRangeStart() && regionCode <= range.getRangeEnd()) {
                return range;
            }
        }
        return null;
    }

    /**
     * Checks if a postal code has invalid patterns.
     *
     * @param postalCode the postal code to check
     * @return true if invalid pattern detected, false otherwise
     */
    private static boolean hasInvalidPattern(String postalCode) {
        // Check for all same digits (e.g., 1111111111)
        if (postalCode.chars().distinct().count() == 1) {
            return true;
        }

        // Check for simple sequences (e.g., 1234567890, 0123456789)
        if (postalCode.equals("1234567890") || postalCode.equals("0123456789")) {
            return true;
        }

        return false;
    }

    /**
     * Normalizes a postal code by removing formatting and converting Persian digits.
     *
     * @param postalCode the postal code to normalize
     * @return normalized postal code or null if invalid
     */
    public static String normalize(String postalCode) {
        if (postalCode == null || postalCode.isEmpty()) {
            return null;
        }

        // Convert Persian digits to English
        postalCode = PersianNumberConverter.toEnglishDigits(postalCode);

        // Remove all non-digit characters (spaces, hyphens, etc.)
        postalCode = postalCode.replaceAll("[^0-9]", "");

        // Check length
        if (postalCode.length() != 10) {
            return null;
        }

        return postalCode;
    }

    /**
     * Formats a postal code with a hyphen.
     *
     * @param postalCode the postal code to format
     * @return formatted postal code (XXXXX-XXXXX) or null if invalid
     */
    public static String format(String postalCode) {
        postalCode = normalize(postalCode);

        if (postalCode == null || !isValid(postalCode)) {
            return null;
        }

        return postalCode.substring(0, 5) + "-" + postalCode.substring(5);
    }

    /**
     * Formats a postal code with Persian digits.
     *
     * @param postalCode the postal code to format
     * @return Persian formatted postal code or null if invalid
     */
    public static String formatPersian(String postalCode) {
        String formatted = format(postalCode);

        if (formatted == null) {
            return null;
        }

        return PersianNumberConverter.toPersianDigits(formatted);
    }

    /**
     * Gets the postal code range information from a postal code.
     *
     * @param postalCode the postal code
     * @return PostalCodeRange or null if not found
     */
    public static PostalCodeRange getPostalCodeRange(String postalCode) {
        postalCode = normalize(postalCode);

        if (postalCode == null) {
            return null;
        }

        int regionCode = Integer.parseInt(postalCode.substring(0, 5));
        return findPostalCodeRange(regionCode);
    }

    /**
     * Gets the province name from a postal code.
     *
     * @param postalCode the postal code
     * @return province name in Persian or null if not found
     */
    public static String getProvinceName(String postalCode) {
        PostalCodeRange range = getPostalCodeRange(postalCode);
        return range != null ? range.getProvincePersian() : null;
    }

    /**
     * Gets the city name from a postal code.
     *
     * @param postalCode the postal code
     * @return city name in Persian or null if not found
     */
    public static String getCityName(String postalCode) {
        PostalCodeRange range = getPostalCodeRange(postalCode);
        return range != null ? range.getCityPersian() : null;
    }

    /**
     * Gets the region code (first 5 digits) from a postal code.
     *
     * @param postalCode the postal code
     * @return region code or null if invalid
     */
    public static String getRegionCode(String postalCode) {
        postalCode = normalize(postalCode);

        if (postalCode == null) {
            return null;
        }

        return postalCode.substring(0, 5);
    }

    /**
     * Gets the local code (last 5 digits) from a postal code.
     *
     * @param postalCode the postal code
     * @return local code or null if invalid
     */
    public static String getLocalCode(String postalCode) {
        postalCode = normalize(postalCode);

        if (postalCode == null) {
            return null;
        }

        return postalCode.substring(5);
    }

    /**
     * Information about a postal code.
     */
    public static class PostalCodeInfo {
        private final String postalCode;
        private final boolean valid;
        private final PostalCodeRange postalCodeRange;
        private final String regionCode;
        private final String localCode;
        private final String formatted;
        private final String formattedPersian;

        public PostalCodeInfo(String postalCode) {
            this.postalCode = normalize(postalCode);
            this.valid = IranianPostalCode.isValid(this.postalCode);

            if (valid) {
                this.postalCodeRange = IranianPostalCode.getPostalCodeRange(this.postalCode);
                this.regionCode = IranianPostalCode.getRegionCode(this.postalCode);
                this.localCode = IranianPostalCode.getLocalCode(this.postalCode);
                this.formatted = format(this.postalCode);
                this.formattedPersian = formatPersian(this.postalCode);
            } else {
                this.postalCodeRange = null;
                this.regionCode = null;
                this.localCode = null;
                this.formatted = null;
                this.formattedPersian = null;
            }
        }

        public String getPostalCode() {
            return postalCode;
        }

        public boolean isValid() {
            return valid;
        }

        public PostalCodeRange getPostalCodeRange() {
            return postalCodeRange;
        }

        public String getRegionCode() {
            return regionCode;
        }

        public String getLocalCode() {
            return localCode;
        }

        public String getFormatted() {
            return formatted;
        }

        public String getFormattedPersian() {
            return formattedPersian;
        }

        @Override
        public String toString() {
            if (!valid) {
                return "Invalid Postal Code: " + postalCode;
            }

            String province = postalCodeRange != null ? postalCodeRange.getProvincePersian() : "Unknown";
            String city = postalCodeRange != null ? postalCodeRange.getCityPersian() : "Unknown";

            return String.format("Postal Code: %s (Province: %s, City: %s)",
                    formatted, province, city);
        }
    }

    /**
     * Validates multiple postal codes at once.
     *
     * @param postalCodes list of postal codes to validate
     * @return map of postal code to validation result
     */
    public static Map<String, Boolean> validateBatch(List<String> postalCodes) {
        Map<String, Boolean> results = new LinkedHashMap<>();

        for (String code : postalCodes) {
            results.put(code, isValid(code));
        }

        return results;
    }

    /**
     * Gets all unique provinces from the loaded data.
     *
     * @return list of unique province names (Persian)
     */
    public static List<String> getAllProvinces() {
        Set<String> provinces = new LinkedHashSet<>();

        for (PostalCodeRange range : POSTAL_CODE_RANGES) {
            provinces.add(range.getProvincePersian());
        }

        return new ArrayList<>(provinces);
    }

    /**
     * Gets all cities for a specific province.
     *
     * @param provinceName province name in Persian or English
     * @return list of cities in that province
     */
    public static List<String> getCitiesInProvince(String provinceName) {
        List<String> cities = new ArrayList<>();

        for (PostalCodeRange range : POSTAL_CODE_RANGES) {
            if (range.getProvincePersian().equals(provinceName) ||
                    range.getProvinceEnglish().equalsIgnoreCase(provinceName)) {
                cities.add(range.getCityPersian());
            }
        }

        return cities;
    }

    /**
     * Searches for provinces or cities by name (Persian or English).
     *
     * @param query search query
     * @return list of matching postal code ranges
     */
    public static List<PostalCodeRange> search(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        query = query.toLowerCase();
        List<PostalCodeRange> results = new ArrayList<>();

        for (PostalCodeRange range : POSTAL_CODE_RANGES) {
            if (range.getProvincePersian().toLowerCase().contains(query) ||
                    range.getProvinceEnglish().toLowerCase().contains(query) ||
                    range.getCityPersian().toLowerCase().contains(query) ||
                    range.getCityEnglish().toLowerCase().contains(query)) {
                results.add(range);
            }
        }

        return results;
    }

    /**
     * Gets postal code statistics for a list of codes.
     *
     * @param postalCodes list of postal codes
     * @return statistics about province distribution
     */
    public static Map<String, Integer> getProvinceDistribution(List<String> postalCodes) {
        Map<String, Integer> distribution = new HashMap<>();

        for (String code : postalCodes) {
            String provinceName = getProvinceName(code);
            if (provinceName != null) {
                distribution.put(provinceName, distribution.getOrDefault(provinceName, 0) + 1);
            }
        }

        return distribution;
    }

    /**
     * Gets postal code statistics for cities.
     *
     * @param postalCodes list of postal codes
     * @return statistics about city distribution
     */
    public static Map<String, Integer> getCityDistribution(List<String> postalCodes) {
        Map<String, Integer> distribution = new HashMap<>();

        for (String code : postalCodes) {
            String cityName = getCityName(code);
            if (cityName != null) {
                distribution.put(cityName, distribution.getOrDefault(cityName, 0) + 1);
            }
        }

        return distribution;
    }

    /**
     * Gets the total number of loaded postal code ranges.
     *
     * @return count of postal code ranges
     */
    public static int getLoadedRangesCount() {
        return POSTAL_CODE_RANGES.size();
    }
}