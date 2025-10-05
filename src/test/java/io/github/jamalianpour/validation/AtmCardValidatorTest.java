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
 * Unit tests for AtmCardValidator.
 */
class AtmCardValidatorTest {

    @Test
    @DisplayName("Should validate card numbers with Luhn algorithm")
    void testValidCardNumbers() {
        // Generate valid test cards
        String testCard1 = AtmCardValidator.generateTestCard("603799", "123456789");
        assertTrue(AtmCardValidator.isValid(testCard1));

        String testCard2 = AtmCardValidator.generateTestCard("627353", "987654321");
        assertTrue(AtmCardValidator.isValid(testCard2));

        String testCard3 = AtmCardValidator.generateTestCard("621986", "111111111");
        assertTrue(AtmCardValidator.isValid(testCard3));
    }

    @Test
    @DisplayName("Should reject invalid card numbers")
    void testInvalidCardNumbers() {
        assertFalse(AtmCardValidator.isValid(null));
        assertFalse(AtmCardValidator.isValid(""));
        assertFalse(AtmCardValidator.isValid("1234567890123456"));      // Invalid Luhn
        assertFalse(AtmCardValidator.isValid("123456789012345"));       // Too short
        assertFalse(AtmCardValidator.isValid("12345678901234567"));     // Too long
        assertFalse(AtmCardValidator.isValid("abcd1234efgh5678"));      // Contains letters
    }

    @Test
    @DisplayName("Should handle Persian digits in card numbers")
    void testPersianDigits() {
        String testCard = AtmCardValidator.generateTestCard("603799", "123456789");

        // Convert to Persian format
        String persianFormat = AtmCardValidator.toPersianFormat(testCard);
        assertNotNull(persianFormat);
        assertTrue(persianFormat.contains("۰") || persianFormat.contains("۱") ||
                persianFormat.contains("۲") || persianFormat.contains("۳"));
    }

    @Test
    @DisplayName("Should normalize card numbers correctly")
    void testNormalizeCardNumber() {
        String validCard = AtmCardValidator.generateTestCard("603799", "123456789");

        // Test various formats
        assertEquals(validCard, AtmCardValidator.normalizeCardNumber(validCard));
        assertEquals(validCard, AtmCardValidator.normalizeCardNumber(
                validCard.substring(0, 4) + " " + validCard.substring(4, 8) + " " +
                        validCard.substring(8, 12) + " " + validCard.substring(12)));
        assertEquals(validCard, AtmCardValidator.normalizeCardNumber(
                validCard.substring(0, 4) + "-" + validCard.substring(4, 8) + "-" +
                        validCard.substring(8, 12) + "-" + validCard.substring(12)));

        // Invalid inputs
        assertNull(AtmCardValidator.normalizeCardNumber("12345"));
        assertNull(AtmCardValidator.normalizeCardNumber("invalid"));
        assertNull(AtmCardValidator.normalizeCardNumber("1234-5678-9012-345"));  // Too short
    }

    @Test
    @DisplayName("Should format card numbers correctly")
    void testFormatCardNumber() {
        String testCard = AtmCardValidator.generateTestCard("603799", "123456789");

        // Format with hyphens
        String formatted = AtmCardValidator.format(testCard);
        assertNotNull(formatted);
        assertTrue(formatted.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}"));

        // Format with spaces
        String spacedFormat = AtmCardValidator.formatWithSpaces(testCard);
        assertNotNull(spacedFormat);
        assertTrue(spacedFormat.matches("\\d{4} \\d{4} \\d{4} \\d{4}"));
    }

    @Test
    @DisplayName("Should mask card numbers for security")
    void testMaskCardNumber() {
        String testCard = AtmCardValidator.generateTestCard("603799", "123456789");
        String masked = AtmCardValidator.mask(testCard);

        assertNotNull(masked);
        assertTrue(masked.matches("\\d{4}-\\*{4}-\\*{4}-\\d{4}"));
        assertTrue(masked.startsWith(testCard.substring(0, 4)));
        assertTrue(masked.endsWith(testCard.substring(12, 16)));
        assertTrue(masked.contains("****-****"));
    }

    @Test
    @DisplayName("Should extract BIN correctly")
    void testGetBin() {
        assertEquals("603799", AtmCardValidator.getBin("6037991234567890"));
        assertEquals("627353", AtmCardValidator.getBin("6273531234567890"));
        assertEquals("621986", AtmCardValidator.getBin("6219861234567890"));

        assertNull(AtmCardValidator.getBin("12345"));
        assertNull(AtmCardValidator.getBin(null));
    }

    @Test
    @DisplayName("Should identify card issuers correctly")
    void testGetCardIssuer() {
        // Iranian banks
        String melliCard = AtmCardValidator.generateTestCard("603799", "123456789");
        AtmCardValidator.CardIssuerInfo melliIssuer = AtmCardValidator.getCardIssuer(melliCard);
        assertNotNull(melliIssuer);
        assertEquals("603799", melliIssuer.getBin());
        assertEquals("بانک ملی", melliIssuer.getPersianName());
        assertEquals("Bank Melli", melliIssuer.getEnglishName());

        String tejaratCard = AtmCardValidator.generateTestCard("627353", "987654321");
        AtmCardValidator.CardIssuerInfo tejaratIssuer = AtmCardValidator.getCardIssuer(tejaratCard);
        assertNotNull(tejaratIssuer);
        assertEquals("627353", tejaratIssuer.getBin());
        assertEquals("بانک تجارت", tejaratIssuer.getPersianName());

        // International networks
        String visaCard = "4111111111111111";  // Standard Visa test number
        AtmCardValidator.CardIssuerInfo visaIssuer = AtmCardValidator.getCardIssuer(visaCard);
        assertNotNull(visaIssuer);
        assertEquals("VISA", visaIssuer.getCode());
    }

    @Test
    @DisplayName("Should validate CVV2 codes")
    void testIsValidCvv2() {
        assertTrue(AtmCardValidator.isValidCvv2("123"));
        assertTrue(AtmCardValidator.isValidCvv2("456"));
        assertTrue(AtmCardValidator.isValidCvv2("7890"));
        assertTrue(AtmCardValidator.isValidCvv2("۱۲۳"));  // Persian digits

        assertFalse(AtmCardValidator.isValidCvv2("12"));    // Too short
        assertFalse(AtmCardValidator.isValidCvv2("12345")); // Too long
        assertFalse(AtmCardValidator.isValidCvv2("abc"));   // Letters
        assertFalse(AtmCardValidator.isValidCvv2(""));
        assertFalse(AtmCardValidator.isValidCvv2(null));
    }

    @Test
    @DisplayName("Should validate expiry dates")
    void testIsValidExpiry() {
        assertTrue(AtmCardValidator.isValidExpiry("12/25"));
        assertTrue(AtmCardValidator.isValidExpiry("01/30"));
        assertTrue(AtmCardValidator.isValidExpiry("06/2025"));
        assertTrue(AtmCardValidator.isValidExpiry("1225"));    // Without slash
        assertTrue(AtmCardValidator.isValidExpiry("۰۱/۲۵"));   // Persian digits

        assertFalse(AtmCardValidator.isValidExpiry("13/25"));  // Invalid month
        assertFalse(AtmCardValidator.isValidExpiry("00/25"));  // Invalid month
        assertFalse(AtmCardValidator.isValidExpiry("12"));     // Too short
        assertFalse(AtmCardValidator.isValidExpiry("ab/cd"));  // Letters
    }

    @Test
    @DisplayName("Should check card expiry correctly")
    void testIsExpired() {
        // Future dates - not expired
        assertFalse(AtmCardValidator.isExpired(12, 2030));
        assertFalse(AtmCardValidator.isExpired(6, 30));  // 2-digit year

        // Past dates - expired
        assertTrue(AtmCardValidator.isExpired(1, 2020));
        assertTrue(AtmCardValidator.isExpired(1, 20));    // 2-digit year

        // Invalid month
        assertTrue(AtmCardValidator.isExpired(13, 2030));
        assertTrue(AtmCardValidator.isExpired(0, 2030));
    }

    @Test
    @DisplayName("Should create CardInfo correctly")
    void testCardInfo() {
        String testCard = AtmCardValidator.generateTestCard("603799", "123456789");
        AtmCardValidator.CardInfo info = new AtmCardValidator.CardInfo(testCard);

        assertTrue(info.isValid());
        assertEquals("603799", info.getBin());
        assertNotNull(info.getIssuer());
        assertEquals("بانک ملی", info.getIssuer().getPersianName());
        assertNotNull(info.getFormatted());
        assertNotNull(info.getMasked());

        // Test invalid card
        AtmCardValidator.CardInfo invalidInfo = new AtmCardValidator.CardInfo("1234567890123456");
        assertFalse(invalidInfo.isValid());
        assertNull(invalidInfo.getBin());
        assertNull(invalidInfo.getIssuer());
    }

    @Test
    @DisplayName("Should generate valid test cards")
    void testGenerateTestCard() {
        // Test with different BINs
        String testCard1 = AtmCardValidator.generateTestCard("603799", "123456789");
        assertNotNull(testCard1);
        assertEquals(16, testCard1.length());
        assertTrue(AtmCardValidator.isValid(testCard1));
        assertTrue(testCard1.startsWith("603799"));

        String testCard2 = AtmCardValidator.generateTestCard("627353", "987654321");
        assertTrue(AtmCardValidator.isValid(testCard2));

        // Test invalid inputs
        assertThrows(IllegalArgumentException.class,
                () -> AtmCardValidator.generateTestCard("12345", "123456789"));  // BIN too short
        assertThrows(IllegalArgumentException.class,
                () -> AtmCardValidator.generateTestCard("123456", "12345"));     // Account too short
        assertThrows(IllegalArgumentException.class,
                () -> AtmCardValidator.generateTestCard("abcdef", "123456789")); // Non-numeric
    }

    @Test
    @DisplayName("Should validate batch of card numbers")
    void testValidateBatch() {
        String valid1 = AtmCardValidator.generateTestCard("603799", "111111111");
        String valid2 = AtmCardValidator.generateTestCard("627353", "222222222");

        List<String> cards = Arrays.asList(
                valid1,
                valid2,
                "1234567890123456",  // Invalid
                "invalid_card"       // Invalid
        );

        Map<String, Boolean> results = AtmCardValidator.validateBatch(cards);

        assertEquals(4, results.size());
        assertTrue(results.get(valid1));
        assertTrue(results.get(valid2));
        assertFalse(results.get("1234567890123456"));
        assertFalse(results.get("invalid_card"));
    }

    @Test
    @DisplayName("Should search card issuers")
    void testSearchIssuers() {
        // Search by Persian name
        List<AtmCardValidator.CardIssuerInfo> melliResults = AtmCardValidator.searchIssuers("ملی");
        assertFalse(melliResults.isEmpty());
        assertTrue(melliResults.stream().anyMatch(i -> i.getEnglishName().equals("Bank Melli")));

        // Search by English name
        List<AtmCardValidator.CardIssuerInfo> tejaratResults = AtmCardValidator.searchIssuers("tejarat");
        assertFalse(tejaratResults.isEmpty());
        assertTrue(tejaratResults.stream().anyMatch(i -> i.getBin().equals("627353")));

        // Search by BIN
        List<AtmCardValidator.CardIssuerInfo> binResults = AtmCardValidator.searchIssuers("603799");
        assertFalse(binResults.isEmpty());
        assertEquals("603799", binResults.get(0).getBin());

        // No results
        List<AtmCardValidator.CardIssuerInfo> noResults = AtmCardValidator.searchIssuers("xyz");
        assertTrue(noResults.isEmpty());
    }

    @Test
    @DisplayName("Should identify Iranian cards")
    void testIsIranianCard() {
        // Iranian bank cards
        String iranianCard = AtmCardValidator.generateTestCard("603799", "123456789");
        assertTrue(AtmCardValidator.isIranianCard(iranianCard));

        String anotherIranian = AtmCardValidator.generateTestCard("627353", "987654321");
        assertTrue(AtmCardValidator.isIranianCard(anotherIranian));

        // International cards
        String visaCard = "4111111111111111";
        assertFalse(AtmCardValidator.isIranianCard(visaCard));

        String masterCard = "5500000000000004";
        assertFalse(AtmCardValidator.isIranianCard(masterCard));

        // Invalid card
        assertFalse(AtmCardValidator.isIranianCard("invalid"));
    }

    @ParameterizedTest
    @CsvSource({
            "603799, بانک ملی, Bank Melli",
            "627353, بانک تجارت, Bank Tejarat",
            "621986, بانک سامان, Saman Bank",
            "622106, بانک پارسیان, Bank Parsian",
            "639347, بانک پاسارگاد, Bank Pasargad",
            "627760, پست بانک, Post Bank",
            "628023, بانک مسکن, Bank Maskan"
    })
    @DisplayName("Should map BINs to banks correctly")
    void testBankMapping(String bin, String persianName, String englishName) {
        String testCard = AtmCardValidator.generateTestCard(bin, "123456789");
        AtmCardValidator.CardIssuerInfo issuer = AtmCardValidator.getCardIssuer(testCard);

        assertNotNull(issuer);
        assertEquals(bin, issuer.getBin());
        assertEquals(persianName, issuer.getPersianName());
        assertEquals(englishName, issuer.getEnglishName());
    }

    @Test
    @DisplayName("Should handle various input formats")
    void testVariousInputFormats() {
        String baseCard = AtmCardValidator.generateTestCard("603799", "123456789");

        // Different formats that should all be valid
        assertTrue(AtmCardValidator.isValid(baseCard));
        assertTrue(AtmCardValidator.isValid(
                baseCard.substring(0, 4) + " " + baseCard.substring(4, 8) + " " +
                        baseCard.substring(8, 12) + " " + baseCard.substring(12)));
        assertTrue(AtmCardValidator.isValid(
                baseCard.substring(0, 4) + "-" + baseCard.substring(4, 8) + "-" +
                        baseCard.substring(8, 12) + "-" + baseCard.substring(12)));

        // Persian digits
        String persianCard = "۶۰۳۷۹۹" + baseCard.substring(6);
        assertTrue(AtmCardValidator.isValid(persianCard));
    }

    @Test
    @DisplayName("Should get all card issuers")
    void testGetAllCardIssuers() {
        Map<String, AtmCardValidator.CardIssuerInfo> issuers = AtmCardValidator.getAllCardIssuers();

        assertNotNull(issuers);
        assertFalse(issuers.isEmpty());
        assertTrue(issuers.containsKey("603799"));  // Bank Melli
        assertTrue(issuers.containsKey("627353"));  // Bank Tejarat
        assertTrue(issuers.containsKey("4"));        // Visa

        // Should be unmodifiable
        assertThrows(UnsupportedOperationException.class,
                () -> issuers.put("999999", new AtmCardValidator.CardIssuerInfo("999999", "Test", "Test", "TST")));
    }
}
