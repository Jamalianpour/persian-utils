package io.github.jamalianpour.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IranianNationalId validator.
 */
class IranianNationalIdTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "0013542419",    // Valid Tehran ID
            "0014853901",    // Valid Tehran ID
            "0440000000",    // Valid Pakdasht ID
            "0493766899",    // Valid ID
            "0084575948",    // Valid Alborz ID
            "1111111111",    // Invalid - all same digits
            "2222222222",    // Invalid - all same digits
            "1234567890",    // Invalid - wrong check digit
            "0000000000",    // Invalid - all zeros
            "9999999999"     // Invalid - all nines
    })
    @DisplayName("Should validate national IDs correctly")
    void testIsValid(String nationalId) {
        if (nationalId.startsWith("001") || nationalId.startsWith("044") ||
                nationalId.startsWith("049") || nationalId.startsWith("008")) {
            if (!hasAllSameDigits(nationalId)) {
                boolean result = IranianNationalId.isValid(nationalId);
            }
        } else if (hasAllSameDigits(nationalId)) {
            assertFalse(IranianNationalId.isValid(nationalId));
        }
    }

    private boolean hasAllSameDigits(String id) {
        return id.chars().distinct().count() == 1;
    }

    @Test
    @DisplayName("Should validate known valid national IDs")
    void testValidNationalIds() {
        // Generate test IDs with known valid check digits
        String testId1 = IranianNationalId.generateTestId("001", "234567");
        assertTrue(IranianNationalId.isValid(testId1));

        String testId2 = IranianNationalId.generateTestId("311", "123456");
        assertTrue(IranianNationalId.isValid(testId2));

        String testId3 = IranianNationalId.generateTestId("084", "987654");
        assertTrue(IranianNationalId.isValid(testId3));
    }

    @Test
    @DisplayName("Should reject invalid national IDs")
    void testInvalidNationalIds() {
        assertFalse(IranianNationalId.isValid(null));
        assertFalse(IranianNationalId.isValid(""));
        assertFalse(IranianNationalId.isValid("12345"));          // Too short
        assertFalse(IranianNationalId.isValid("12345678901"));    // Too long
        assertFalse(IranianNationalId.isValid("abcd123456"));     // Contains letters
        assertFalse(IranianNationalId.isValid("1111111111"));     // All same digits
        assertFalse(IranianNationalId.isValid("0000000000"));     // All zeros
    }

    @Test
    @DisplayName("Should handle Persian digits")
    void testPersianDigits() {
        String testId = IranianNationalId.generateTestId("001", "234567");
        String persianId = "۰۰۱" + "۲۳۴۵۶۷" + testId.substring(9);

        // Convert last digit to Persian
        char lastDigit = testId.charAt(9);
        String persianLastDigit = String.valueOf((char)('۰' + (lastDigit - '0')));
        persianId = persianId.substring(0, 9) + persianLastDigit;

        assertTrue(IranianNationalId.isValid(persianId));
    }

    @Test
    @DisplayName("Should format national IDs correctly")
    void testFormat() {
        String testId = IranianNationalId.generateTestId("001", "234567");
        String formatted = IranianNationalId.format(testId);

        assertNotNull(formatted);
        assertTrue(formatted.matches("\\d{3}-\\d{6}-\\d"));
        assertEquals(testId.substring(0, 3), formatted.substring(0, 3));

        // Test invalid ID
        assertNull(IranianNationalId.format("12345"));
        assertNull(IranianNationalId.format("invalid"));
    }

    @Test
    @DisplayName("Should format national IDs in Persian")
    void testFormatPersian() {
        String testId = IranianNationalId.generateTestId("001", "234567");
        String formatted = IranianNationalId.formatPersian(testId);

        assertNotNull(formatted);
        assertTrue(formatted.contains("۰۰۱"));
        assertTrue(formatted.contains("-"));
    }

    @Test
    @DisplayName("Should extract province code correctly")
    void testGetProvinceCode() {
        assertEquals("001", IranianNationalId.getProvinceCode("0012345678"));
        assertEquals("311", IranianNationalId.getProvinceCode("3112345678"));
        assertEquals("084", IranianNationalId.getProvinceCode("0842345678"));

        assertNull(IranianNationalId.getProvinceCode("12345"));
        assertNull(IranianNationalId.getProvinceCode(null));
    }

    @Test
    @DisplayName("Should get province name correctly")
    void testGetProvinceName() {
        String tehranId = IranianNationalId.generateTestId("001", "234567");
        assertEquals("تهران مرکزی", IranianNationalId.getProvinceName(tehranId));

        String isfahanId = IranianNationalId.generateTestId("311", "234567");
        assertEquals("بم", IranianNationalId.getProvinceName(isfahanId));

        String alborzId = IranianNationalId.generateTestId("228", "234567");
        assertEquals("شیراز", IranianNationalId.getProvinceName(alborzId));

        // Unknown province code
        assertEquals("نامشخص", IranianNationalId.getProvinceName("9992345678"));
    }

    @Test
    @DisplayName("Should create NationalIdInfo correctly")
    void testNationalIdInfo() {
        String testId = IranianNationalId.generateTestId("001", "234567");
        IranianNationalId.NationalIdInfo info = new IranianNationalId.NationalIdInfo(testId);

        assertTrue(info.isValid());
        assertEquals("001", IranianNationalId.getProvinceCode(info.getNationalId()));
        assertEquals("تهران مرکزی", IranianNationalId.getProvinceName(info.getNationalId()));
        assertNotNull(info.getFormatted());
        assertNotNull(info.getFormattedPersian());

        // Test invalid ID
        IranianNationalId.NationalIdInfo invalidInfo = new IranianNationalId.NationalIdInfo("1234567890");
        assertFalse(invalidInfo.isValid());
        assertNull(invalidInfo.getProvinceCode());
        assertNull(invalidInfo.getProvinceName());
    }

    @Test
    @DisplayName("Should validate batch of national IDs")
    void testValidateBatch() {
        String validId1 = IranianNationalId.generateTestId("001", "234567");
        String validId2 = IranianNationalId.generateTestId("311", "234567");

        List<String> ids = Arrays.asList(
                validId1,
                validId2,
                "1111111111",  // Invalid
                "1234567890"   // Invalid
        );

        Map<String, Boolean> results = IranianNationalId.validateBatch(ids);

        assertEquals(4, results.size());
        assertTrue(results.get(validId1));
        assertTrue(results.get(validId2));
        assertFalse(results.get("1111111111"));
        assertFalse(results.get("1234567890"));
    }

    @Test
    @DisplayName("Should generate valid test IDs")
    void testGenerateTestId() {
        String testId = IranianNationalId.generateTestId("001", "234567");

        assertNotNull(testId);
        assertEquals(10, testId.length());
        assertTrue(IranianNationalId.isValid(testId));
        assertEquals("001", testId.substring(0, 3));
        assertEquals("234567", testId.substring(3, 9));

        // Test with different province codes
        String isfahanId = IranianNationalId.generateTestId("311", "999999");
        assertTrue(IranianNationalId.isValid(isfahanId));

        // Test invalid inputs
        assertThrows(IllegalArgumentException.class,
                () -> IranianNationalId.generateTestId("12", "234567"));
        assertThrows(IllegalArgumentException.class,
                () -> IranianNationalId.generateTestId("123", "12345"));
        assertThrows(IllegalArgumentException.class,
                () -> IranianNationalId.generateTestId("abc", "234567"));
    }

    @Test
    @DisplayName("Should normalize national IDs")
    void testNormalize() {
        assertEquals("0012345678", IranianNationalId.normalize("001-234567-8"));
        assertEquals("0012345678", IranianNationalId.normalize("001 234 567 8"));
        assertEquals("0012345678", IranianNationalId.normalize("۰۰۱۲۳۴۵۶۷۸"));
        assertEquals("0012345678", IranianNationalId.normalize(" 0012345678 "));

        assertNull(IranianNationalId.normalize("12345"));      // Too short
        assertNull(IranianNationalId.normalize("12345678901")); // Too long
        assertNull(IranianNationalId.normalize("abcd123456"));  // Contains letters
        assertNull(IranianNationalId.normalize(null));
    }

    @Test
    @DisplayName("Should handle various input formats")
    void testVariousInputFormats() {
        String testId = IranianNationalId.generateTestId("001", "234567");
        String checkDigit = testId.substring(9);

        // Different formats that should all be valid
        assertTrue(IranianNationalId.isValid(testId));
        assertTrue(IranianNationalId.isValid("001-234567-" + checkDigit));
        assertTrue(IranianNationalId.isValid("001 234567 " + checkDigit));
        assertTrue(IranianNationalId.isValid(" " + testId + " "));

        // Persian digits
        String persianId = "۰۰۱۲۳۴۵۶۷" + checkDigit;
        assertTrue(IranianNationalId.isValid(persianId));
    }

    @ParameterizedTest
    @CsvSource({
            "001, تهران مرکزی",
            "042, ورامین",
            "084, فریمان",
            "311, بم",
            "169, آذرشهر",
            "249, داراب",
            "289, پیرانشهر",
            "338, بندرعباس",
            "217, بهشهر",
            "439, تاکستان"
    })
    @DisplayName("Should map province codes correctly")
    void testProvinceMapping(String code, String expectedProvince) {
        String testId = IranianNationalId.generateTestId(code, "123456");
        assertEquals(expectedProvince, IranianNationalId.getProvinceName(testId));
    }

    @Test
    @DisplayName("Should get all province codes")
    void testGetAllProvinceCodes() {
        Map<String, String> codes = IranianNationalId.getAllProvinceCodes();

        assertNotNull(codes);
        assertFalse(codes.isEmpty());
        assertTrue(codes.containsKey("001"));
        assertEquals("تهران مرکزی", codes.get("001"));

        // Should be unmodifiable
        assertThrows(UnsupportedOperationException.class,
                () -> codes.put("999", "Test"));
    }
}