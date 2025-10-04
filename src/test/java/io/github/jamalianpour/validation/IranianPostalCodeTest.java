package io.github.jamalianpour.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for IranianPostalCode class
 */
@DisplayName("Iranian Postal Code Validator Tests")
class IranianPostalCodeTest {

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @ParameterizedTest
        @DisplayName("Should validate correct postal codes from different provinces")
        @CsvSource({
                "1134567890, true",   // Tehran
                "5133112345, true",   // Tabriz (East Azerbaijan)
                "8134567890, true",   // Isfahan
                "3113112345, true",   // Karaj (Alborz)
                "7134567890, true",   // Shiraz (Fars)
                "9134567890, true",   // Mashhad (Khorasan Razavi)
                "4113112345, true",   // Rasht (Gilan)
                "4813112345, true",   // Sari (Mazandaran)
                "9813112345, true"    // Zahedan (Sistan and Baluchestan)
        })
        void shouldValidateCorrectPostalCodes(String postalCode, boolean expected) {
            assertEquals(expected, IranianPostalCode.isValid(postalCode));
        }

        @ParameterizedTest
        @DisplayName("Should reject invalid postal codes")
        @ValueSource(strings = {
                "1111111111",  // All same digits
                "0000000000",  // All zeros
                "1234567890",  // Sequential pattern
                "0123456789",  // Sequential pattern
                "123",         // Too short
                "12345",       // Too short
                "12345678901", // Too long
                "abcdefghij",  // Non-numeric
                "12345-6789",  // With hyphen (should be normalized first)
                "",            // Empty
                "9999999999"   // Out of valid range
        })
        void shouldRejectInvalidPostalCodes(String postalCode) {
            assertFalse(IranianPostalCode.isValid(postalCode));
        }

        @Test
        @DisplayName("Should reject null postal code")
        void shouldRejectNullPostalCode() {
            assertFalse(IranianPostalCode.isValid(null));
        }

        @Test
        @DisplayName("Should validate postal code with hyphen after normalization")
        void shouldValidatePostalCodeWithHyphen() {
            assertTrue(IranianPostalCode.isValid("11345-67890"));
        }

        @Test
        @DisplayName("Should validate postal code with spaces after normalization")
        void shouldValidatePostalCodeWithSpaces() {
            assertTrue(IranianPostalCode.isValid("11345 67890"));
        }
    }

    @Nested
    @DisplayName("Normalization Tests")
    class NormalizationTests {

        @ParameterizedTest
        @DisplayName("Should normalize postal codes correctly")
        @CsvSource({
                "11345-67890, 1134567890",
                "11345 67890, 1134567890",
                "11345  67890, 1134567890",
                "11345-678-90, 1134567890"
        })
        void shouldNormalizePostalCodes(String input, String expected) {
            assertEquals(expected, IranianPostalCode.normalize(input));
        }

        @Test
        @DisplayName("Should convert Persian digits to English")
        void shouldConvertPersianDigitsToEnglish() {
            String persianCode = "۱۱۳۴۵۶۷۸۹۰";
            String normalized = IranianPostalCode.normalize(persianCode);
            assertEquals("1134567890", normalized);
        }

        @Test
        @DisplayName("Should handle mixed Persian and English digits")
        void shouldHandleMixedDigits() {
            String mixedCode = "۱۱345۶۷۸۹۰";
            String normalized = IranianPostalCode.normalize(mixedCode);
            assertEquals("1134567890", normalized);
        }

        @Test
        @DisplayName("Should return null for invalid length after normalization")
        void shouldReturnNullForInvalidLength() {
            assertNull(IranianPostalCode.normalize("123"));
            assertNull(IranianPostalCode.normalize("12345678901"));
        }

        @Test
        @DisplayName("Should return null for null input")
        void shouldReturnNullForNullInput() {
            assertNull(IranianPostalCode.normalize(null));
        }

        @Test
        @DisplayName("Should return null for empty input")
        void shouldReturnNullForEmptyInput() {
            assertNull(IranianPostalCode.normalize(""));
        }
    }

    @Nested
    @DisplayName("Formatting Tests")
    class FormattingTests {

        @ParameterizedTest
        @DisplayName("Should format valid postal codes")
        @CsvSource({
                "1134567890, 11345-67890",
                "5133112345, 51331-12345",
                "8134567890, 81345-67890"
        })
        void shouldFormatValidPostalCodes(String input, String expected) {
            assertEquals(expected, IranianPostalCode.format(input));
        }

        @Test
        @DisplayName("Should format postal code with Persian digits")
        void shouldFormatWithPersianDigits() {
            String formatted = IranianPostalCode.formatPersian("1134567890");
            assertEquals("۱۱۳۴۵-۶۷۸۹۰", formatted);
        }

        @Test
        @DisplayName("Should format postal code that already has hyphen")
        void shouldFormatPostalCodeWithHyphen() {
            String formatted = IranianPostalCode.format("11345-67890");
            assertEquals("11345-67890", formatted);
        }

        @Test
        @DisplayName("Should return null when formatting invalid postal code")
        void shouldReturnNullForInvalidFormat() {
            assertNull(IranianPostalCode.format("1111111111"));
            assertNull(IranianPostalCode.format("123"));
        }

        @Test
        @DisplayName("Should return null when formatting null input")
        void shouldReturnNullForNullFormatInput() {
            assertNull(IranianPostalCode.format(null));
        }
    }

    @Nested
    @DisplayName("Location Information Tests")
    class LocationInformationTests {

        @Test
        @DisplayName("Should get correct province for Tehran postal code")
        void shouldGetTeheranProvince() {
            String province = IranianPostalCode.getProvinceName("1134567890");
            assertEquals("تهران", province);
        }

        @Test
        @DisplayName("Should get correct province for Tabriz postal code")
        void shouldGetTabrizProvince() {
            String province = IranianPostalCode.getProvinceName("5133112345");
            assertEquals("آذربایجان شرقی", province);
        }

        @Test
        @DisplayName("Should get correct city for Tabriz postal code")
        void shouldGetTabrizCity() {
            String city = IranianPostalCode.getCityName("5133112345");
            assertEquals("تبریز", city);
        }

        @Test
        @DisplayName("Should get correct city for Isfahan postal code")
        void shouldGetIsfahanCity() {
            String city = IranianPostalCode.getCityName("8134567890");
            assertEquals("اصفهان", city);
        }

        @Test
        @DisplayName("Should return null for invalid postal code location")
        void shouldReturnNullForInvalidLocation() {
            assertNull(IranianPostalCode.getProvinceName("9999999999"));
            assertNull(IranianPostalCode.getCityName("9999999999"));
        }

        @Test
        @DisplayName("Should get complete postal code range information")
        void shouldGetCompletePostalCodeRange() {
            IranianPostalCode.PostalCodeRange range =
                    IranianPostalCode.getPostalCodeRange("5133112345");

            assertNotNull(range);
            assertEquals("آذربایجان شرقی", range.getProvincePersian());
            assertEquals("East Azerbaijan", range.getProvinceEnglish());
            assertEquals("تبریز", range.getCityPersian());
            assertEquals("Tabriz", range.getCityEnglish());
            assertTrue(range.getRangeStart() <= 51331);
            assertTrue(range.getRangeEnd() >= 51331);
        }
    }

    @Nested
    @DisplayName("Region and Local Code Tests")
    class RegionAndLocalCodeTests {

        @Test
        @DisplayName("Should extract region code correctly")
        void shouldExtractRegionCode() {
            assertEquals("11345", IranianPostalCode.getRegionCode("1134567890"));
            assertEquals("51331", IranianPostalCode.getRegionCode("5133112345"));
        }

        @Test
        @DisplayName("Should extract local code correctly")
        void shouldExtractLocalCode() {
            assertEquals("67890", IranianPostalCode.getLocalCode("1134567890"));
            assertEquals("12345", IranianPostalCode.getLocalCode("5133112345"));
        }

        @Test
        @DisplayName("Should return null for invalid postal code parts")
        void shouldReturnNullForInvalidParts() {
            assertNull(IranianPostalCode.getRegionCode("123"));
            assertNull(IranianPostalCode.getLocalCode("123"));
            assertNull(IranianPostalCode.getRegionCode(null));
            assertNull(IranianPostalCode.getLocalCode(null));
        }
    }

    @Nested
    @DisplayName("PostalCodeInfo Tests")
    class PostalCodeInfoTests {

        @Test
        @DisplayName("Should create valid PostalCodeInfo")
        void shouldCreateValidPostalCodeInfo() {
            IranianPostalCode.PostalCodeInfo info =
                    new IranianPostalCode.PostalCodeInfo("1134567890");

            assertTrue(info.isValid());
            assertEquals("1134567890", info.getPostalCode());
            assertEquals("11345", info.getRegionCode());
            assertEquals("67890", info.getLocalCode());
            assertEquals("11345-67890", info.getFormatted());
            assertEquals("۱۱۳۴۵-۶۷۸۹۰", info.getFormattedPersian());
            assertNotNull(info.getPostalCodeRange());
        }

        @Test
        @DisplayName("Should create invalid PostalCodeInfo")
        void shouldCreateInvalidPostalCodeInfo() {
            IranianPostalCode.PostalCodeInfo info =
                    new IranianPostalCode.PostalCodeInfo("1111111111");

            assertFalse(info.isValid());
            assertNull(info.getRegionCode());
            assertNull(info.getLocalCode());
            assertNull(info.getFormatted());
            assertNull(info.getFormattedPersian());
            assertNull(info.getPostalCodeRange());
        }

        @Test
        @DisplayName("Should normalize postal code in PostalCodeInfo")
        void shouldNormalizeInPostalCodeInfo() {
            IranianPostalCode.PostalCodeInfo info =
                    new IranianPostalCode.PostalCodeInfo("11345-67890");

            assertTrue(info.isValid());
            assertEquals("1134567890", info.getPostalCode());
        }

        @Test
        @DisplayName("Should handle Persian digits in PostalCodeInfo")
        void shouldHandlePersianDigitsInInfo() {
            IranianPostalCode.PostalCodeInfo info =
                    new IranianPostalCode.PostalCodeInfo("۱۱۳۴۵۶۷۸۹۰");

            assertTrue(info.isValid());
            assertEquals("1134567890", info.getPostalCode());
        }

        @Test
        @DisplayName("Should generate correct toString for valid postal code")
        void shouldGenerateCorrectToStringForValid() {
            IranianPostalCode.PostalCodeInfo info =
                    new IranianPostalCode.PostalCodeInfo("1134567890");

            String str = info.toString();
            assertTrue(str.contains("11345-67890"));
            assertTrue(str.contains("تهران"));
        }

        @Test
        @DisplayName("Should generate correct toString for invalid postal code")
        void shouldGenerateCorrectToStringForInvalid() {
            IranianPostalCode.PostalCodeInfo info =
                    new IranianPostalCode.PostalCodeInfo("1111111111");

            String str = info.toString();
            assertTrue(str.contains("Invalid"));
        }
    }

    @Nested
    @DisplayName("Batch Validation Tests")
    class BatchValidationTests {

        @Test
        @DisplayName("Should validate batch of postal codes")
        void shouldValidateBatch() {
            List<String> codes = Arrays.asList(
                    "1134567890",
                    "5133112345",
                    "1111111111",
                    "123"
            );

            Map<String, Boolean> results = IranianPostalCode.validateBatch(codes);

            assertEquals(4, results.size());
            assertTrue(results.get("1134567890"));
            assertTrue(results.get("5133112345"));
            assertFalse(results.get("1111111111"));
            assertFalse(results.get("123"));
        }

        @Test
        @DisplayName("Should handle empty batch")
        void shouldHandleEmptyBatch() {
            List<String> codes = List.of();
            Map<String, Boolean> results = IranianPostalCode.validateBatch(codes);

            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Should maintain order in batch validation")
        void shouldMaintainOrderInBatch() {
            List<String> codes = Arrays.asList(
                    "1134567890",
                    "5133112345",
                    "8134567890"
            );

            Map<String, Boolean> results = IranianPostalCode.validateBatch(codes);

            List<String> keys = new ArrayList<>(results.keySet());
            assertEquals("1134567890", keys.get(0));
            assertEquals("5133112345", keys.get(1));
            assertEquals("8134567890", keys.get(2));
        }
    }

    @Nested
    @DisplayName("Search and Query Tests")
    class SearchAndQueryTests {

        @Test
        @DisplayName("Should search by Persian province name")
        void shouldSearchByPersianProvince() {
            List<IranianPostalCode.PostalCodeRange> results =
                    IranianPostalCode.search("تهران");

            assertFalse(results.isEmpty());
            assertTrue(results.stream().allMatch(r ->
                    r.getProvincePersian().contains("تهران")));
        }

        @Test
        @DisplayName("Should search by English province name")
        void shouldSearchByEnglishProvince() {
            List<IranianPostalCode.PostalCodeRange> results =
                    IranianPostalCode.search("Tehran");

            assertFalse(results.isEmpty());
            assertTrue(results.stream().allMatch(r ->
                    r.getProvinceEnglish().toLowerCase().contains("tehran")));
        }

        @Test
        @DisplayName("Should search by city name")
        void shouldSearchByCity() {
            List<IranianPostalCode.PostalCodeRange> results =
                    IranianPostalCode.search("تبریز");

            assertFalse(results.isEmpty());
            assertTrue(results.stream().anyMatch(r ->
                    r.getCityPersian().contains("تبریز")));
        }

        @Test
        @DisplayName("Should return empty list for null search query")
        void shouldReturnEmptyListForNullQuery() {
            List<IranianPostalCode.PostalCodeRange> results =
                    IranianPostalCode.search(null);

            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list for empty search query")
        void shouldReturnEmptyListForEmptyQuery() {
            List<IranianPostalCode.PostalCodeRange> results =
                    IranianPostalCode.search("");

            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Should search case-insensitively")
        void shouldSearchCaseInsensitively() {
            List<IranianPostalCode.PostalCodeRange> results1 =
                    IranianPostalCode.search("tehran");
            List<IranianPostalCode.PostalCodeRange> results2 =
                    IranianPostalCode.search("TEHRAN");

            assertEquals(results1.size(), results2.size());
        }
    }

    @Nested
    @DisplayName("Province and City Listing Tests")
    class ProvinceAndCityListingTests {

        @Test
        @DisplayName("Should get all provinces")
        void shouldGetAllProvinces() {
            List<String> provinces = IranianPostalCode.getAllProvinces();

            assertFalse(provinces.isEmpty());
            assertTrue(provinces.contains("تهران"));
            assertTrue(provinces.contains("آذربایجان شرقی"));
            assertTrue(provinces.contains("اصفهان"));
        }

        @Test
        @DisplayName("Should have unique provinces")
        void shouldHaveUniqueProvinces() {
            List<String> provinces = IranianPostalCode.getAllProvinces();
            long uniqueCount = provinces.stream().distinct().count();

            assertEquals(provinces.size(), uniqueCount);
        }

        @Test
        @DisplayName("Should get cities in province by Persian name")
        void shouldGetCitiesInProvinceByPersianName() {
            List<String> cities = IranianPostalCode.getCitiesInProvince("تهران");

            assertFalse(cities.isEmpty());
            assertTrue(cities.stream().anyMatch(c -> c.contains("۱۱")));
        }

        @Test
        @DisplayName("Should get cities in province by English name")
        void shouldGetCitiesInProvinceByEnglishName() {
            List<String> cities = IranianPostalCode.getCitiesInProvince("Tehran");

            assertFalse(cities.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list for non-existent province")
        void shouldReturnEmptyListForNonExistentProvince() {
            List<String> cities = IranianPostalCode.getCitiesInProvince("NonExistent");

            assertTrue(cities.isEmpty());
        }

        @Test
        @DisplayName("Should get cities for Isfahan province")
        void shouldGetCitiesForIsfahan() {
            List<String> cities = IranianPostalCode.getCitiesInProvince("اصفهان");

            assertFalse(cities.isEmpty());
            assertTrue(cities.contains("اصفهان"));
            assertTrue(cities.contains("کاشان"));
        }
    }

    @Nested
    @DisplayName("Distribution Analysis Tests")
    class DistributionAnalysisTests {

        @Test
        @DisplayName("Should analyze province distribution")
        void shouldAnalyzeProvinceDistribution() {
            List<String> codes = Arrays.asList(
                    "1134567890",  // Tehran
                    "1145678901",  // Tehran
                    "5133112345",  // East Azerbaijan
                    "8134567890",  // Isfahan
                    "8145678901"   // Isfahan
            );

            Map<String, Integer> distribution =
                    IranianPostalCode.getProvinceDistribution(codes);

            assertEquals(2, distribution.get("تهران"));
            assertEquals(1, distribution.get("آذربایجان شرقی"));
            assertEquals(2, distribution.get("اصفهان"));
        }

        @Test
        @DisplayName("Should analyze city distribution")
        void shouldAnalyzeCityDistribution() {
            List<String> codes = Arrays.asList(
                    "1134567890",  // Tehran District 11
                    "1145678901",  // Tehran District 11
                    "5133112345"   // Tabriz
            );

            Map<String, Integer> distribution =
                    IranianPostalCode.getCityDistribution(codes);

            assertTrue(distribution.containsKey("تبریز"));
            assertEquals(1, distribution.get("تبریز"));
        }

        @Test
        @DisplayName("Should handle empty list in distribution")
        void shouldHandleEmptyListInDistribution() {
            List<String> codes = Arrays.asList();

            Map<String, Integer> provinceDistribution =
                    IranianPostalCode.getProvinceDistribution(codes);
            Map<String, Integer> cityDistribution =
                    IranianPostalCode.getCityDistribution(codes);

            assertTrue(provinceDistribution.isEmpty());
            assertTrue(cityDistribution.isEmpty());
        }

        @Test
        @DisplayName("Should skip invalid codes in distribution")
        void shouldSkipInvalidCodesInDistribution() {
            List<String> codes = Arrays.asList(
                    "1134567890",
                    "1111111111",  // Invalid
                    "5133112345"
            );

            Map<String, Integer> distribution =
                    IranianPostalCode.getProvinceDistribution(codes);

            assertEquals(2, distribution.size());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle postal code with leading zeros")
        void shouldHandleLeadingZeros() {
            // This would be a valid format if the range exists
            String code = "0123456789";
            // Just check it doesn't crash
            assertDoesNotThrow(() -> IranianPostalCode.isValid(code));
        }

        @Test
        @DisplayName("Should handle very long input after normalization fails")
        void shouldHandleVeryLongInput() {
            String longCode = "12345678901234567890";
            assertNull(IranianPostalCode.normalize(longCode));
            assertFalse(IranianPostalCode.isValid(longCode));
        }

        @Test
        @DisplayName("Should handle special characters in input")
        void shouldHandleSpecialCharacters() {
            String code = "11345!@#67890";
            String normalized = IranianPostalCode.normalize(code);
            assertEquals("1134567890", normalized);
        }

        @Test
        @DisplayName("Should handle Unicode characters")
        void shouldHandleUnicodeCharacters() {
            String code = "11345你好67890";
            String normalized = IranianPostalCode.normalize(code);
            assertEquals("1134567890", normalized);
        }

        @Test
        @DisplayName("Should get loaded ranges count")
        void shouldGetLoadedRangesCount() {
            int count = IranianPostalCode.getLoadedRangesCount();
            assertTrue(count > 0, "Should have loaded postal code ranges");
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete workflow for valid postal code")
        void shouldHandleCompleteWorkflow() {
            String input = "۱۱۳۴۵-۶۷۸۹۰";  // Persian digits with hyphen

            // Normalize
            String normalized = IranianPostalCode.normalize(input);
            assertEquals("1134567890", normalized);

            // Validate
            assertTrue(IranianPostalCode.isValid(normalized));

            // Format
            String formatted = IranianPostalCode.format(normalized);
            assertEquals("11345-67890", formatted);

            // Get location
            String province = IranianPostalCode.getProvinceName(normalized);
            assertEquals("تهران", province);

            // Create info object
            IranianPostalCode.PostalCodeInfo info =
                    new IranianPostalCode.PostalCodeInfo(input);
            assertTrue(info.isValid());
        }

        @Test
        @DisplayName("Should handle batch processing workflow")
        void shouldHandleBatchProcessingWorkflow() {
            List<String> codes = Arrays.asList(
                    "1134567890",
                    "5133112345",
                    "8134567890",
                    "1111111111"
            );

            // Validate batch
            Map<String, Boolean> validationResults =
                    IranianPostalCode.validateBatch(codes);
            assertEquals(4, validationResults.size());

            // Get distributions
            Map<String, Integer> provinceDistribution =
                    IranianPostalCode.getProvinceDistribution(codes);
            assertTrue(provinceDistribution.size() >= 3);

            // Search for related items
            List<IranianPostalCode.PostalCodeRange> tehranResults =
                    IranianPostalCode.search("تهران");
            assertFalse(tehranResults.isEmpty());
        }
    }
}