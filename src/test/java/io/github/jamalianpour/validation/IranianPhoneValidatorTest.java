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
 * Unit tests for IranianPhoneValidator.
 */
class IranianPhoneValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "09123456789",     // MCI
            "09121234567",     // MCI
            "09193456789",     // MCI
            "09901234567",     // MCI
            "09015678901",     // Irancell
            "09361234567",     // Irancell
            "09201234567",     // Rightel
            "09981234567",     // Shatel MVNO
            "+989123456789",   // International format
            "00989123456789",  // International with 00
            "9123456789"       // Without 0
    })
    @DisplayName("Should validate valid mobile numbers")
    void testValidMobileNumbers(String phoneNumber) {
        assertTrue(IranianPhoneValidator.isValid(phoneNumber));
        assertTrue(IranianPhoneValidator.isValidMobile(phoneNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "02112345678",     // Tehran
            "03112345678",     // Isfahan
            "04112345678",     // Tabriz
            "05112345678",     // Mashhad
            "07112345678",     // Shiraz
            "+982112345678",   // International format
            "00982112345678",  // International with 00
            "021-12345678"     // With hyphen
    })
    @DisplayName("Should validate valid landline numbers")
    void testValidLandlineNumbers(String phoneNumber) {
        assertTrue(IranianPhoneValidator.isValid(phoneNumber));
        assertTrue(IranianPhoneValidator.isValidLandline(phoneNumber));
    }

    @Test
    @DisplayName("Should reject invalid phone numbers")
    void testInvalidPhoneNumbers() {
        assertFalse(IranianPhoneValidator.isValid(null));
        assertFalse(IranianPhoneValidator.isValid(""));
        assertFalse(IranianPhoneValidator.isValid("12345"));         // Too short
        assertFalse(IranianPhoneValidator.isValid("091234567890")); // Too long
        assertFalse(IranianPhoneValidator.isValid("08923456789"));  // Invalid prefix
        assertFalse(IranianPhoneValidator.isValid("09623456789"));  // Invalid mobile prefix
        assertFalse(IranianPhoneValidator.isValid("02912345678"));  // Invalid area code
        assertFalse(IranianPhoneValidator.isValid("abcd1234567"));  // Contains letters
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "110", "115", "112", "125", "113", "194", "191", "190", "193", "147", "1818"
    })
    @DisplayName("Should recognize emergency numbers")
    void testEmergencyNumbers(String number) {
        assertTrue(IranianPhoneValidator.isEmergencyNumber(number));
        assertTrue(IranianPhoneValidator.isValid(number));
    }

    @Test
    @DisplayName("Should handle Persian digits in phone numbers")
    void testPersianDigits() {
        assertTrue(IranianPhoneValidator.isValid("۰۹۱۲۳۴۵۶۷۸۹"));
        assertTrue(IranianPhoneValidator.isValid("۰۲۱۱۲۳۴۵۶۷۸"));
        assertTrue(IranianPhoneValidator.isValid("۱۱۰")); // Emergency

        assertEquals("09123456789",
                IranianPhoneValidator.normalizePhoneNumber("۰۹۱۲۳۴۵۶۷۸۹"));
    }

    @Test
    @DisplayName("Should normalize phone numbers correctly")
    void testNormalizePhoneNumber() {
        // Mobile numbers
        assertEquals("09123456789",
                IranianPhoneValidator.normalizePhoneNumber("09123456789"));
        assertEquals("09123456789",
                IranianPhoneValidator.normalizePhoneNumber("+989123456789"));
        assertEquals("09123456789",
                IranianPhoneValidator.normalizePhoneNumber("00989123456789"));
        assertEquals("09123456789",
                IranianPhoneValidator.normalizePhoneNumber("9123456789"));
        assertEquals("09123456789",
                IranianPhoneValidator.normalizePhoneNumber("0912-345-6789"));
        assertEquals("09123456789",
                IranianPhoneValidator.normalizePhoneNumber("(0912) 345 6789"));

        // Landline numbers
        assertEquals("02112345678",
                IranianPhoneValidator.normalizePhoneNumber("02112345678"));
        assertEquals("02112345678",
                IranianPhoneValidator.normalizePhoneNumber("+982112345678"));
        assertEquals("02112345678",
                IranianPhoneValidator.normalizePhoneNumber("021-12345678"));

        // Emergency numbers
        assertEquals("110",
                IranianPhoneValidator.normalizePhoneNumber("110"));

        // Invalid
        assertNull(IranianPhoneValidator.normalizePhoneNumber("12345"));
        assertNull(IranianPhoneValidator.normalizePhoneNumber("invalid"));
    }

    @ParameterizedTest
    @CsvSource({
            "09123456789, MCI, همراه اول, Hamrah-e Aval",
            "09193456789, MCI, همراه اول, Hamrah-e Aval",
            "09901234567, MCI, همراه اول, Hamrah-e Aval",
            "09015678901, Irancell, ایرانسل, Irancell",
            "09361234567, Irancell, ایرانسل, Irancell",
            "09201234567, Rightel, رایتل, Rightel",
            "09981234567, Shatel, شاتل, Shatel",
            "09041234567, Aptel, اپتل, Aptel"
    })
    @DisplayName("Should identify mobile operators correctly")
    void testGetOperator(String phoneNumber, String expectedCode, String expectedPersian, String expectedEnglish) {
        IranianPhoneValidator.OperatorInfo operator = IranianPhoneValidator.getOperator(phoneNumber);

        assertNotNull(operator);
        assertEquals(expectedCode, operator.getCode());
        assertEquals(expectedPersian, operator.getPersianName());
        assertEquals(expectedEnglish, operator.getEnglishName());
    }

    @ParameterizedTest
    @CsvSource({
            "02112345678, 021, تهران",
            "03112345678, 031, اصفهان",
            "04112345678, 041, تبریز",
            "05112345678, 051, مشهد",
            "07112345678, 071, شیراز",
            "08112345678, 081, همدان",
            "08612345678, 086, یزد",
            "02512345678, 025, قم"
    })
    @DisplayName("Should extract area codes and city names correctly")
    void testAreaCodeAndCity(String phoneNumber, String expectedAreaCode, String expectedCity) {
        assertEquals(expectedAreaCode, IranianPhoneValidator.getAreaCode(phoneNumber));
        assertEquals(expectedCity, IranianPhoneValidator.getCityName(phoneNumber));
    }

    @Test
    @DisplayName("Should format phone numbers correctly")
    void testPhoneFormatting() {
        String mobile = "09123456789";

        // Mobile formatting
        assertEquals("0912-345-6789",
                IranianPhoneValidator.PhoneFormatter.formatDashed(mobile));
        assertEquals("0912 345 6789",
                IranianPhoneValidator.PhoneFormatter.formatSpaced(mobile));
        assertEquals("(0912) 345-6789",
                IranianPhoneValidator.PhoneFormatter.formatParentheses(mobile));
        assertEquals("+98 912 345 6789",
                IranianPhoneValidator.PhoneFormatter.formatInternational(mobile));
        assertEquals("۰۹۱۲ ۳۴۵ ۶۷۸۹",
                IranianPhoneValidator.PhoneFormatter.formatPersian(mobile));

        // Landline formatting
        String landline = "02112345678";

        assertEquals("021-12345678",
                IranianPhoneValidator.PhoneFormatter.formatDashed(landline));
        assertEquals("021 12345678",
                IranianPhoneValidator.PhoneFormatter.formatSpaced(landline));
        assertEquals("(021) 12345678",
                IranianPhoneValidator.PhoneFormatter.formatParentheses(landline));
        assertEquals("+98 21 12345678",
                IranianPhoneValidator.PhoneFormatter.formatInternational(landline));
    }

    @Test
    @DisplayName("Should create PhoneInfo correctly")
    void testPhoneInfo() {
        // Mobile number
        IranianPhoneValidator.PhoneInfo mobileInfo =
                new IranianPhoneValidator.PhoneInfo("09123456789");

        assertTrue(mobileInfo.isValid());
        assertTrue(mobileInfo.isMobile());
        assertFalse(mobileInfo.isLandline());
        assertFalse(mobileInfo.isEmergency());
        assertNotNull(mobileInfo.getOperator());
        assertEquals("MCI", mobileInfo.getOperator().getCode());
        assertNull(mobileInfo.getAreaCode());
        assertNull(mobileInfo.getCityName());
        assertEquals("0912-345-6789", mobileInfo.getFormatted());
        assertEquals("+98 912 345 6789", mobileInfo.getInternational());

        // Landline number
        IranianPhoneValidator.PhoneInfo landlineInfo =
                new IranianPhoneValidator.PhoneInfo("02112345678");

        assertTrue(landlineInfo.isValid());
        assertFalse(landlineInfo.isMobile());
        assertTrue(landlineInfo.isLandline());
        assertFalse(landlineInfo.isEmergency());
        assertNull(landlineInfo.getOperator());
        assertEquals("021", landlineInfo.getAreaCode());
        assertEquals("تهران", landlineInfo.getCityName());
        assertEquals("021-12345678", landlineInfo.getFormatted());
        assertEquals("+98 21 12345678", landlineInfo.getInternational());

        // Emergency number
        IranianPhoneValidator.PhoneInfo emergencyInfo =
                new IranianPhoneValidator.PhoneInfo("110");

        assertTrue(emergencyInfo.isValid());
        assertFalse(emergencyInfo.isMobile());
        assertFalse(emergencyInfo.isLandline());
        assertTrue(emergencyInfo.isEmergency());
        assertNull(emergencyInfo.getOperator());
        assertNull(emergencyInfo.getAreaCode());
        assertEquals("110", emergencyInfo.getInternational());

        // Invalid number
        IranianPhoneValidator.PhoneInfo invalidInfo =
                new IranianPhoneValidator.PhoneInfo("12345");

        assertFalse(invalidInfo.isValid());
        assertFalse(invalidInfo.isMobile());
        assertFalse(invalidInfo.isLandline());
        assertFalse(invalidInfo.isEmergency());
    }

    @Test
    @DisplayName("Should validate batch of phone numbers")
    void testValidateBatch() {
        List<String> phones = Arrays.asList(
                "09123456789",  // Valid mobile
                "02112345678",  // Valid landline
                "110",          // Emergency
                "12345",        // Invalid
                "09623456789"   // Invalid prefix
        );

        Map<String, Boolean> results = IranianPhoneValidator.validateBatch(phones);

        assertEquals(5, results.size());
        assertTrue(results.get("09123456789"));
        assertTrue(results.get("02112345678"));
        assertTrue(results.get("110"));
        assertFalse(results.get("12345"));
        assertFalse(results.get("09623456789"));
    }

    @Test
    @DisplayName("Should handle various input formats")
    void testVariousInputFormats() {
        String[] validFormats = {
                "09123456789",
                "0912-345-6789",
                "0912 345 6789",
                "(0912) 345-6789",
                "+989123456789",
                "00989123456789",
                "9123456789",
                "۰۹۱۲۳۴۵۶۷۸۹"
        };

        for (String format : validFormats) {
            assertTrue(IranianPhoneValidator.isValid(format),
                    "Should be valid: " + format);
            assertEquals("09123456789",
                    IranianPhoneValidator.normalizePhoneNumber(format),
                    "Should normalize to same number: " + format);
        }
    }

    @Test
    @DisplayName("Should distinguish between mobile and landline")
    void testMobileVsLandline() {
        // Mobile
        assertTrue(IranianPhoneValidator.isValidMobile("09123456789"));
        assertFalse(IranianPhoneValidator.isValidLandline("09123456789"));

        // Landline
        assertFalse(IranianPhoneValidator.isValidMobile("02112345678"));
        assertTrue(IranianPhoneValidator.isValidLandline("02112345678"));

        // Emergency (neither)
        assertFalse(IranianPhoneValidator.isValidMobile("110"));
        assertFalse(IranianPhoneValidator.isValidLandline("110"));
        assertTrue(IranianPhoneValidator.isEmergencyNumber("110"));
    }

    @Test
    @DisplayName("Should get all mobile operators")
    void testGetAllMobileOperators() {
        Map<String, IranianPhoneValidator.OperatorInfo> operators =
                IranianPhoneValidator.getAllMobileOperators();

        assertNotNull(operators);
        assertFalse(operators.isEmpty());
        assertTrue(operators.containsKey("0912")); // MCI
        assertTrue(operators.containsKey("0901")); // Irancell
        assertTrue(operators.containsKey("0920")); // Rightel

        // Should be unmodifiable
        assertThrows(UnsupportedOperationException.class,
                () -> operators.put("0999", new IranianPhoneValidator.OperatorInfo(
                        "TEST", "تست", "Test", IranianPhoneValidator.OperatorType.MOBILE)));
    }

    @Test
    @DisplayName("Should get all area codes")
    void testGetAllAreaCodes() {
        Map<String, String> areaCodes = IranianPhoneValidator.getAllAreaCodes();

        assertNotNull(areaCodes);
        assertFalse(areaCodes.isEmpty());
        assertTrue(areaCodes.containsKey("021")); // Tehran
        assertTrue(areaCodes.containsKey("031")); // Isfahan
        assertTrue(areaCodes.containsKey("041")); // Tabriz

        // Should be unmodifiable
        assertThrows(UnsupportedOperationException.class,
                () -> areaCodes.put("099", "Test City"));
    }
}