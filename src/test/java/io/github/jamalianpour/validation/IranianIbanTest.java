package io.github.jamalianpour.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IranianIban validator.
 */
class IranianIbanTest {

    @Test
    @DisplayName("Should validate known valid IBANs")
    void testValidIbans() {
        // Generate valid IBANs for testing
        String iban1 = IranianIban.generateIban("017", "0000000000000001234");
        assertNotNull(iban1);
        assertTrue(IranianIban.isValid(iban1));

        assertTrue(IranianIban.isValid("700610000004001002485140"));

        String iban2 = IranianIban.generateIban("019", "9876543210123456789");
        assertNotNull(iban2);
        assertTrue(IranianIban.isValid(iban2));

        String iban3 = IranianIban.generateIban("054", "1111111111111111111");
        assertNotNull(iban3);
        assertTrue(IranianIban.isValid(iban3));
    }

    @Test
    @DisplayName("Should reject invalid IBANs")
    void testInvalidIbans() {
        assertFalse(IranianIban.isValid(null));
        assertFalse(IranianIban.isValid(""));
        assertFalse(IranianIban.isValid("IR"));
        assertFalse(IranianIban.isValid("IR12345"));                    // Too short
        assertFalse(IranianIban.isValid("IR123456789012345678901234567")); // Too long
        assertFalse(IranianIban.isValid("GB12345678901234567890123456")); // Wrong country
        assertFalse(IranianIban.isValid("IR00000000000000000000000000")); // Invalid check digits
        assertFalse(IranianIban.isValid("IRABCDEFGHIJKLMNOPQRSTUVWXYZ")); // Letters
    }

    @Test
    @DisplayName("Should handle Persian digits in IBAN")
    void testPersianDigits() {
        String iban = IranianIban.generateIban("017", "1234567890123456789");
        assertNotNull(iban);

        // Convert to Persian
        String persianIban = IranianIban.toPersian(iban);
        assertNotNull(persianIban);
        assertTrue(persianIban.startsWith("IR"));
        assertTrue(persianIban.substring(2).contains("۰") ||
                persianIban.substring(2).contains("۱") ||
                persianIban.substring(2).contains("۲"));

        // Should still be valid
        assertTrue(IranianIban.isValid(persianIban));

        // Convert back
        String backToEnglish = IranianIban.fromPersian(persianIban);
        assertEquals(iban, backToEnglish);
    }

    @Test
    @DisplayName("Should normalize IBANs correctly")
    void testNormalizeIban() {
        String iban = IranianIban.generateIban("017", "1234567890123456789");
        assertNotNull(iban);

        // Test various formats
        assertEquals(iban, IranianIban.normalizeIban(iban));
        assertEquals(iban, IranianIban.normalizeIban(iban.toLowerCase()));
        assertEquals(iban, IranianIban.normalizeIban("ir" + iban.substring(2)));

        // With spaces
        String withSpaces = iban.substring(0, 4) + " " + iban.substring(4, 8) + " " + iban.substring(8);
        assertEquals(iban, IranianIban.normalizeIban(withSpaces));

        // Without IR prefix (just 24 digits)
        assertEquals(iban, IranianIban.normalizeIban(iban.substring(2)));

        // Invalid inputs
        assertNull(IranianIban.normalizeIban("12345"));
        assertNull(IranianIban.normalizeIban("invalid"));
    }

    @Test
    @DisplayName("Should format IBANs correctly")
    void testFormatIban() {
        String iban = IranianIban.generateIban("017", "1234567890123456789");
        assertNotNull(iban);

        // Format with spaces
        String formatted = IranianIban.format(iban);
        assertNotNull(formatted);
        assertTrue(formatted.contains(" "));
        assertEquals(7, formatted.split(" ").length); // Should have 7 groups
        assertTrue(formatted.matches("IR\\d{2} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{2}"));

        // Format compact
        String compact = IranianIban.formatCompact(iban);
        assertNotNull(compact);
        assertFalse(compact.contains(" "));
        assertEquals(iban, compact);
    }

    @Test
    @DisplayName("Should extract bank code correctly")
    void testGetBankCode() {
        String iban1 = IranianIban.generateIban("017", "1234567890123456789");
        assertEquals("017", IranianIban.getBankCode(iban1));

        String iban2 = IranianIban.generateIban("054", "9876543210987654321");
        assertEquals("054", IranianIban.getBankCode(iban2));

        String iban3 = IranianIban.generateIban("019", "1111111111111111111");
        assertEquals("019", IranianIban.getBankCode(iban3));

        assertNull(IranianIban.getBankCode("invalid"));
        assertNull(IranianIban.getBankCode(null));
    }

    @Test
    @DisplayName("Should get bank information correctly")
    void testGetBankInfo() {
        String melliBan = IranianIban.generateIban("017", "1234567890123456789");
        IranianIban.BankInfo melliInfo = IranianIban.getBankInfo(melliBan);
        assertNotNull(melliInfo);
        assertEquals("017", melliInfo.getCode());
        assertEquals("بانک ملی", melliInfo.getPersianName());
        assertEquals("Bank Melli", melliInfo.getEnglishName());
        assertEquals("MLI", melliInfo.getAbbreviation());

        String parsianBan = IranianIban.generateIban("054", "9876543210987654321");
        IranianIban.BankInfo parsianInfo = IranianIban.getBankInfo(parsianBan);
        assertNotNull(parsianInfo);
        assertEquals("054", parsianInfo.getCode());
        assertEquals("بانک پارسیان", parsianInfo.getPersianName());

        // Unknown bank code
        String unknownBank = "IR00999" + "0".repeat(19);
        assertNull(IranianIban.getBankInfo(unknownBank));
    }

    @Test
    @DisplayName("Should extract account number correctly")
    void testGetAccountNumber() {
        String accountNum = "1234567890123456789";
        String iban = IranianIban.generateIban("017", accountNum);

        String extracted = IranianIban.getAccountNumber(iban);
        assertNotNull(extracted);
        assertEquals(19, extracted.length());
        // Account number should be padded with zeros if necessary
        assertTrue(extracted.endsWith("1234567890123456789") ||
                extracted.equals("0001234567890123456789".substring(3)));
    }

    @Test
    @DisplayName("Should create IbanInfo correctly")
    void testIbanInfo() {
        String iban = IranianIban.generateIban("017", "1234567890123456789");
        IranianIban.IbanInfo info = new IranianIban.IbanInfo(iban);

        assertTrue(info.isValid());
        assertEquals("017", IranianIban.getBankCode(iban));
        assertNotNull(IranianIban.getBankInfo(iban));
        assertEquals("بانک ملی", IranianIban.getBankInfo(iban).getPersianName());
        assertNotNull(info.getAccountNumber());
        assertNotNull(info.getFormatted());
        assertNotNull(info.getCompact());

        // Test invalid IBAN
        IranianIban.IbanInfo invalidInfo = new IranianIban.IbanInfo("invalid");
        assertFalse(invalidInfo.isValid());
        assertNull(invalidInfo.getBankCode());
        assertNull(invalidInfo.getBankInfo());
    }

    @Test
    @DisplayName("Should calculate check digits correctly")
    void testCalculateCheckDigits() {
        // Test with known values
        String checkDigits1 = IranianIban.calculateCheckDigits("017", "0000000000000001234");
        assertNotNull(checkDigits1);
        assertEquals(2, checkDigits1.length());

        // Verify by creating full IBAN and validating
        String iban = "IR" + checkDigits1 + "017" + "0000000000000001234";
        assertTrue(IranianIban.isValid(iban));

        // Test invalid inputs
        assertNull(IranianIban.calculateCheckDigits("12", "1234567890123456789"));  // Bank code too short
        assertNull(IranianIban.calculateCheckDigits("123", "123456789"));           // Account too short
        assertNull(IranianIban.calculateCheckDigits("abc", "1234567890123456789")); // Non-numeric
    }

    @Test
    @DisplayName("Should validate batch of IBANs")
    void testValidateBatch() {
        String valid1 = IranianIban.generateIban("017", "1234567890123456789");
        String valid2 = IranianIban.generateIban("054", "9876543210987654321");

        List<String> ibans = Arrays.asList(
                valid1,
                valid2,
                "IR00000000000000000000000000",  // Invalid
                "invalid_iban"                     // Invalid
        );

        Map<String, Boolean> results = IranianIban.validateBatch(ibans);

        assertEquals(4, results.size());
        assertTrue(results.get(valid1));
        assertTrue(results.get(valid2));
        assertFalse(results.get("IR00000000000000000000000000"));
        assertFalse(results.get("invalid_iban"));
    }

    @Test
    @DisplayName("Should search banks correctly")
    void testSearchBanks() {
        // Search by Persian name
        List<IranianIban.BankInfo> mellatResults = IranianIban.searchBanks("ملت");
        assertFalse(mellatResults.isEmpty());
        assertTrue(mellatResults.stream().anyMatch(b -> b.getCode().equals("012")));

        // Search by English name
        List<IranianIban.BankInfo> melliResults = IranianIban.searchBanks("melli");
        assertFalse(melliResults.isEmpty());
        assertTrue(melliResults.stream().anyMatch(b -> b.getCode().equals("017")));

        // Search by code
        List<IranianIban.BankInfo> codeResults = IranianIban.searchBanks("054");
        assertFalse(codeResults.isEmpty());
        assertEquals("054", codeResults.get(0).getCode());

        // Search by abbreviation
        List<IranianIban.BankInfo> abbrResults = IranianIban.searchBanks("MLI");
        assertFalse(abbrResults.isEmpty());
        assertTrue(abbrResults.stream().anyMatch(b -> b.getCode().equals("017")));

        // No results
        List<IranianIban.BankInfo> noResults = IranianIban.searchBanks("xyz");
        assertTrue(noResults.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "012, بانک ملت, Bank Mellat",
            "017, بانک ملی, Bank Melli",
            "019, بانک صادرات, Bank Saderat",
            "054, بانک پارسیان, Bank Parsian",
            "056, بانک سامان, Saman Bank",
            "057, بانک پاسارگاد, Bank Pasargad",
            "062, بانک آینده, Bank Ayandeh"
    })
    @DisplayName("Should map bank codes correctly")
    void testBankMapping(String code, String persianName, String englishName) {
        String iban = IranianIban.generateIban(code, "1234567890123456789");
        IranianIban.BankInfo info = IranianIban.getBankInfo(iban);

        assertNotNull(info);
        assertEquals(code, info.getCode());
        assertEquals(persianName, info.getPersianName());
        assertEquals(englishName, info.getEnglishName());
    }

    @Test
    @DisplayName("Should handle various input formats")
    void testVariousInputFormats() {
        String baseIban = IranianIban.generateIban("017", "1234567890123456789");
        assertNotNull(baseIban);

        // Different formats that should all be valid
        assertTrue(IranianIban.isValid(baseIban));
        assertTrue(IranianIban.isValid(baseIban.toLowerCase()));
        assertTrue(IranianIban.isValid("ir" + baseIban.substring(2)));

        // With spaces
        String formatted = IranianIban.format(baseIban);
        assertTrue(IranianIban.isValid(formatted));

        // Without IR prefix
        assertTrue(IranianIban.isValid(baseIban.substring(2)));

        // Persian digits
        String persian = IranianIban.toPersian(baseIban);
        assertTrue(IranianIban.isValid(persian));
    }

    @Test
    @DisplayName("Should get all bank codes")
    void testGetAllBankCodes() {
        Map<String, IranianIban.BankInfo> codes = IranianIban.getAllBankCodes();

        assertNotNull(codes);
        assertFalse(codes.isEmpty());
        assertTrue(codes.containsKey("017")); // Bank Melli
        assertTrue(codes.containsKey("019")); // Bank Saderat
        assertTrue(codes.containsKey("054")); // Bank Parsian

        // Should be unmodifiable
        assertThrows(UnsupportedOperationException.class,
                () -> codes.put("999", new IranianIban.BankInfo("999", "Test", "Test", "TST")));
    }

    @Test
    @DisplayName("Should handle edge cases")
    void testEdgeCases() {
        // Maximum valid account number
        String maxAccount = IranianIban.generateIban("017", "9999999999999999999");
        assertNotNull(maxAccount);
        assertTrue(IranianIban.isValid(maxAccount));

        // Account number with leading zeros
        String zeroAccount = IranianIban.generateIban("017", "0000000000000000001");
        assertNotNull(zeroAccount);
        assertTrue(IranianIban.isValid(zeroAccount));
    }
}