package io.github.jamalianpour.validation;

import io.github.jamalianpour.number.PersianNumberConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Validator and utilities for Iranian National ID (کد ملی).
 * Implements the official check digit algorithm for 10-digit national IDs.
 */
public class IranianNationalId {

    private static final Pattern NATIONAL_ID_PATTERN = Pattern.compile("^[0-9]{10}$");

    // Province codes mapping (first 3 digits) - now static
    private static final Map<String, String> PROVINCE_CODES = new HashMap<>();

    // Static initialization block to load province codes
    static {
        String resourcePath = "NationalCode.csv"; // File in src/main/resources/

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(IranianNationalId.class.getClassLoader()
                                .getResourceAsStream(resourcePath)),
                        StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2); // split into 2 parts only
                if (parts.length == 2) {
                    String code = parts[0].trim();
                    String name = parts[1].trim();
                    if (code.contains("-")) {
                        String[] codes = code.split("-");
                        for (int i = 0; i < codes.length; i++) {
                            PROVINCE_CODES.put(codes[i], name);
                        }
                    } else {
                        PROVINCE_CODES.put(code, name);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading province codes: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Warning: " + resourcePath + " not found in classpath");
        }

    }

    /**
     * Validates an Iranian National ID.
     *
     * @param nationalId the national ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String nationalId) {
        if (nationalId == null || nationalId.isEmpty()) {
            return false;
        }

        // Convert Persian digits to English if present
        nationalId = PersianNumberConverter.toEnglishDigits(nationalId.trim());

        // Remove any hyphens or spaces
        nationalId = nationalId.replaceAll("[\\s-]", "");

        // Check basic format
        if (!NATIONAL_ID_PATTERN.matcher(nationalId).matches()) {
            return false;
        }

        // Check for invalid patterns (all same digits)
        if (hasAllSameDigits(nationalId)) {
            return false;
        }

        // Validate check digit
        return validateCheckDigit(nationalId);
    }

    /**
     * Validates the check digit of a national ID using the official algorithm.
     *
     * @param nationalId the 10-digit national ID
     * @return true if check digit is valid, false otherwise
     */
    private static boolean validateCheckDigit(String nationalId) {
        int sum = 0;

        // Calculate weighted sum of first 9 digits
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(nationalId.charAt(i));
            sum += digit * (10 - i);
        }

        int remainder = sum % 11;
        int checkDigit = Character.getNumericValue(nationalId.charAt(9));

        // Check digit validation rules
        if (remainder < 2) {
            return checkDigit == remainder;
        } else {
            return checkDigit == (11 - remainder);
        }
    }

    /**
     * Checks if all digits in the national ID are the same.
     *
     * @param nationalId the national ID
     * @return true if all digits are the same, false otherwise
     */
    private static boolean hasAllSameDigits(String nationalId) {
        char firstDigit = nationalId.charAt(0);
        for (int i = 1; i < nationalId.length(); i++) {
            if (nationalId.charAt(i) != firstDigit) {
                return false;
            }
        }
        return true;
    }

    /**
     * Formats a national ID with hyphens for better readability.
     *
     * @param nationalId the national ID to format
     * @return formatted national ID or null if invalid
     */
    public static String format(String nationalId) {
        if (nationalId == null || nationalId.isEmpty()) {
            return null;
        }

        // Convert to English digits and remove formatting
        nationalId = PersianNumberConverter.toEnglishDigits(nationalId.trim());
        nationalId = nationalId.replaceAll("[\\s-]", "");

        if (!NATIONAL_ID_PATTERN.matcher(nationalId).matches()) {
            return null;
        }

        // Format as: XXX-XXXXXX-X
        return nationalId.substring(0, 3) + "-" +
                nationalId.substring(3, 9) + "-" +
                nationalId.substring(9);
    }

    /**
     * Formats a national ID with Persian digits.
     *
     * @param nationalId the national ID to format
     * @return Persian formatted national ID or null if invalid
     */
    public static String formatPersian(String nationalId) {
        String formatted = format(nationalId);
        if (formatted == null) {
            return null;
        }
        return PersianNumberConverter.toPersianDigits(formatted);
    }

    /**
     * Extracts the province/city code from a national ID.
     *
     * @param nationalId the national ID
     * @return province code (first 3 digits) or null if invalid
     */
    public static String getProvinceCode(String nationalId) {
        if (nationalId == null || nationalId.isEmpty()) {
            return null;
        }

        nationalId = PersianNumberConverter.toEnglishDigits(nationalId.trim());
        nationalId = nationalId.replaceAll("[\\s-]", "");

        if (!NATIONAL_ID_PATTERN.matcher(nationalId).matches()) {
            return null;
        }

        return nationalId.substring(0, 3);
    }

    /**
     * Gets the province/city name based on the national ID.
     *
     * @param nationalId the national ID
     * @return province/city name or null if not found
     */
    public static String getProvinceName(String nationalId) {
        String provinceCode = getProvinceCode(nationalId);
        if (provinceCode == null) {
            return null;
        }

        return PROVINCE_CODES.getOrDefault(provinceCode, "نامشخص");
    }

    /**
     * Information extracted from a national ID.
     */
    public static class NationalIdInfo {
        private final String nationalId;
        private final boolean valid;
        private final String provinceCode;
        private final String provinceName;
        private final String formatted;
        private final String formattedPersian;

        public NationalIdInfo(String nationalId) {
            this.nationalId = normalizeNationalId(nationalId);
            this.valid = IranianNationalId.isValid(nationalId);

            if (valid) {
                this.provinceCode = IranianNationalId.getProvinceCode(this.nationalId);
                this.provinceName = IranianNationalId.getProvinceName(this.nationalId);
                this.formatted = IranianNationalId.format(this.nationalId);
                this.formattedPersian = IranianNationalId.formatPersian(this.nationalId);
            } else {
                this.provinceCode = null;
                this.provinceName = null;
                this.formatted = null;
                this.formattedPersian = null;
            }
        }

        private String normalizeNationalId(String id) {
            if (id == null) return null;
            id = PersianNumberConverter.toEnglishDigits(id.trim());
            return id.replaceAll("[\\s-]", "");
        }

        public String getNationalId() { return nationalId; }
        public boolean isValid() { return valid; }
        public String getProvinceCode() { return provinceCode; }
        public String getProvinceName() { return provinceName; }
        public String getFormatted() { return formatted; }
        public String getFormattedPersian() { return formattedPersian; }

        @Override
        public String toString() {
            if (!valid) {
                return "Invalid National ID: " + nationalId;
            }
            return String.format("National ID: %s (Province: %s - %s)",
                    formatted, provinceCode, provinceName);
        }
    }

    /**
     * Validates multiple national IDs at once.
     *
     * @param nationalIds list of national IDs to validate
     * @return map of national ID to validation result
     */
    public static Map<String, Boolean> validateBatch(List<String> nationalIds) {
        Map<String, Boolean> results = new LinkedHashMap<>();

        for (String id : nationalIds) {
            results.put(id, isValid(id));
        }

        return results;
    }

    /**
     * Generates a valid test national ID for development purposes.
     * WARNING: These should only be used for testing, not for real identification.
     *
     * @param provinceCode the 3-digit province code
     * @param uniqueNumber a 6-digit unique number
     * @return a valid national ID with correct check digit
     */
    public static String generateTestId(String provinceCode, String uniqueNumber) {
        if (provinceCode == null || provinceCode.length() != 3 ||
                uniqueNumber == null || uniqueNumber.length() != 6) {
            throw new IllegalArgumentException("Province code must be 3 digits and unique number must be 6 digits");
        }

        // Convert to English digits
        provinceCode = PersianNumberConverter.toEnglishDigits(provinceCode);
        uniqueNumber = PersianNumberConverter.toEnglishDigits(uniqueNumber);

        // Validate that they are all digits
        if (!provinceCode.matches("\\d{3}") || !uniqueNumber.matches("\\d{6}")) {
            throw new IllegalArgumentException("Province code and unique number must contain only digits");
        }

        String baseId = provinceCode + uniqueNumber;

        // Calculate check digit
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(baseId.charAt(i));
            sum += digit * (10 - i);
        }

        int remainder = sum % 11;
        int checkDigit;

        if (remainder < 2) {
            checkDigit = remainder;
        } else {
            checkDigit = 11 - remainder;
        }

        return baseId + checkDigit;
    }

    /**
     * Normalizes a national ID by removing formatting and converting to English digits.
     *
     * @param nationalId the national ID to normalize
     * @return normalized national ID or null if invalid format
     */
    public static String normalize(String nationalId) {
        if (nationalId == null || nationalId.isEmpty()) {
            return null;
        }

        // Convert Persian digits to English
        nationalId = PersianNumberConverter.toEnglishDigits(nationalId.trim());

        // Remove all non-digit characters
        nationalId = nationalId.replaceAll("[^0-9]", "");

        // Check if it's exactly 10 digits
        if (nationalId.length() != 10) {
            return null;
        }

        return nationalId;
    }

    /**
     * Gets all available province codes and their names.
     *
     * @return unmodifiable map of province codes to names
     */
    public static Map<String, String> getAllProvinceCodes() {
        return Collections.unmodifiableMap(PROVINCE_CODES);
    }
}