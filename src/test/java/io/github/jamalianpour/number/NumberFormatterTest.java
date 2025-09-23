package io.github.jamalianpour.number;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NumberFormatter.
 */
class NumberFormatterTest {

    @Test
    @DisplayName("Should add comma separators to integers")
    void testAddCommaSeparator() {
        assertEquals("1,234", NumberFormatter.addSeparator(1234));
        assertEquals("1,234,567", NumberFormatter.addSeparator(1234567));
        assertEquals("1,000,000", NumberFormatter.addSeparator(1000000));
        assertEquals("-1,234,567", NumberFormatter.addSeparator(-1234567));
        assertEquals("0", NumberFormatter.addSeparator(0));
        assertEquals("999", NumberFormatter.addSeparator(999));
    }

    @Test
    @DisplayName("Should add comma separators to decimals")
    void testAddCommaSeparatorDecimals() {
        assertEquals("1,234.56", NumberFormatter.addSeparator(1234.56));
        assertEquals("1,234,567.89", NumberFormatter.addSeparator(1234567.89));
        assertEquals("1,000.0", NumberFormatter.addSeparator(1000.0));
        assertEquals("-1,234.567", NumberFormatter.addSeparator(-1234.567));
        assertEquals("0.123", NumberFormatter.addSeparator(0.123));
    }

    @Test
    @DisplayName("Should add Persian separators")
    void testAddPersianSeparator() {
        String result1 = NumberFormatter.addPersianSeparator(1234567);
        assertEquals("۱٬۲۳۴٬۵۶۷", result1);

        String result2 = NumberFormatter.addPersianSeparator(1234.56);
        assertEquals("۱٬۲۳۴٫۵۶", result2);

        String result3 = NumberFormatter.addPersianSeparator(-9876543);
        assertEquals("-۹٬۸۷۶٬۵۴۳", result3);
    }

    @ParameterizedTest
    @CsvSource({
            "1234567, COMMA, '1,234,567'",
            "1234567, SPACE, '1 234 567'",
            "1234567, UNDERSCORE, '1_234_567'",
            "1234567, APOSTROPHE, '1''234''567'",
            "1234567, NONE, '1234567'"
    })
    @DisplayName("Should format with different separator styles")
    void testDifferentSeparatorStyles(long number, NumberFormatter.SeparatorStyle style, String expected) {
        assertEquals(expected, NumberFormatter.addSeparator(number, style));
    }

    @Test
    @DisplayName("Should remove separators from formatted numbers")
    void testRemoveSeparator() {
        assertEquals("1234567", NumberFormatter.removeSeparator("1,234,567"));
        assertEquals("1234567.89", NumberFormatter.removeSeparator("1,234,567.89"));
        assertEquals("1234567", NumberFormatter.removeSeparator("1 234 567"));
        assertEquals("1234567", NumberFormatter.removeSeparator("1_234_567"));
        assertEquals("1234567", NumberFormatter.removeSeparator("1'234'567"));
        assertEquals("-1234567", NumberFormatter.removeSeparator("-1,234,567"));
    }

    @Test
    @DisplayName("Should remove Persian separators")
    void testRemovePersianSeparator() {
        assertEquals("1234567", NumberFormatter.removeSeparator("۱٬۲۳۴٬۵۶۷"));
        assertEquals("1234.56", NumberFormatter.removeSeparator("۱٬۲۳۴٫۵۶"));
        assertEquals("-9876543", NumberFormatter.removeSeparator("-۹٬۸۷۶٬۵۴۳"));

        // Mixed Persian and English
        assertEquals("1234567", NumberFormatter.removeSeparator("1٬234٬567"));
    }

    @Test
    @DisplayName("Should parse formatted numbers to long")
    void testParseLong() {
        assertEquals(1234567L, NumberFormatter.parseLong("1,234,567"));
        assertEquals(1234567L, NumberFormatter.parseLong("1 234 567"));
        assertEquals(1234567L, NumberFormatter.parseLong("۱٬۲۳۴٬۵۶۷"));
        assertEquals(-1234567L, NumberFormatter.parseLong("-1,234,567"));
        assertEquals(1234L, NumberFormatter.parseLong("1,234.56")); // Truncates decimal
    }

    @Test
    @DisplayName("Should parse formatted numbers to double")
    void testParseDouble() {
        assertEquals(1234567.0, NumberFormatter.parseDouble("1,234,567"), 0.001);
        assertEquals(1234567.89, NumberFormatter.parseDouble("1,234,567.89"), 0.001);
        assertEquals(1234.56, NumberFormatter.parseDouble("۱٬۲۳۴٫۵۶"), 0.001);
        assertEquals(-1234567.5, NumberFormatter.parseDouble("-1,234,567.5"), 0.001);
    }

    @Test
    @DisplayName("Should parse formatted numbers to BigDecimal")
    void testParseBigDecimal() {
        assertEquals(new BigDecimal("1234567"),
                NumberFormatter.parseBigDecimal("1,234,567"));
        assertEquals(new BigDecimal("1234567.89"),
                NumberFormatter.parseBigDecimal("1,234,567.89"));
        assertEquals(new BigDecimal("1234.56"),
                NumberFormatter.parseBigDecimal("۱٬۲۳۴٫۵۶"));
    }

    @Test
    @DisplayName("Should format with custom configuration")
    void testCustomFormatConfig() {
        NumberFormatter.FormatConfig config1 = new NumberFormatter.FormatConfig()
                .withStyle(NumberFormatter.SeparatorStyle.COMMA)
                .withDecimalPlaces(2)
                .withPrefix("$");

        assertEquals("$1,234.57", NumberFormatter.format(1234.567, config1));

        NumberFormatter.FormatConfig config2 = new NumberFormatter.FormatConfig()
                .withStyle(NumberFormatter.SeparatorStyle.PERSIAN)
                .withPersianDigits(true)
                .withSuffix(" تومان");

        assertEquals("۱٬۲۳۴٬۵۶۷ تومان", NumberFormatter.format(1234567L, config2));

        NumberFormatter.FormatConfig config3 = new NumberFormatter.FormatConfig()
                .withStyle(NumberFormatter.SeparatorStyle.SPACE)
                .withPositiveSign(true)
                .withDecimalPlaces(3);

        assertEquals("+1 234.568", NumberFormatter.format(1234.5678, config3));
    }

    @Test
    @DisplayName("Should format currency correctly")
    void testFormatCurrency() {
        assertEquals("$1,234.57", NumberFormatter.formatCurrency(1234.567, "USD"));
        assertEquals("€1,234.57", NumberFormatter.formatCurrency(1234.567, "EUR"));
        assertEquals("£1,234.57", NumberFormatter.formatCurrency(1234.567, "GBP"));

        String rialResult = NumberFormatter.formatCurrency(1234567, "IRR");
        assertEquals("۱٬۲۳۴٬۵۶۷ ریال", rialResult);

        String tomanResult = NumberFormatter.formatCurrency(123456, "TOMAN");
        assertEquals("۱۲۳٬۴۵۶ تومان", tomanResult);
    }

    @Test
    @DisplayName("Should format percentage correctly")
    void testFormatPercentage() {
        assertEquals("12.35%", NumberFormatter.formatPercentage(12.345, 2));
        assertEquals("100.0%", NumberFormatter.formatPercentage(100, 1));
        assertEquals("0.1235%", NumberFormatter.formatPercentage(0.12345, 4));
        assertEquals("-5.50%", NumberFormatter.formatPercentage(-5.5, 2));
    }

    @Test
    @DisplayName("Should format phone numbers")
    void testFormatPhoneNumber() {
        assertEquals("091 234 5678",
                NumberFormatter.formatPhoneNumber("09123456789", "### ### ####"));
        assertEquals("(091) 234-5678",
                NumberFormatter.formatPhoneNumber("09123456789", "(###) ###-####"));
        assertEquals("0912-345-6789",
                NumberFormatter.formatPhoneNumber("09123456789", "####-###-####"));

        // Should handle numbers with existing formatting
        assertEquals("091 234 5678",
                NumberFormatter.formatPhoneNumber("091-234-5678", "### ### ####"));
    }

    @Test
    @DisplayName("Should format credit card numbers")
    void testFormatCreditCard() {
        assertEquals("1234 5678 9012 3456",
                NumberFormatter.formatCreditCard("1234567890123456"));
        assertEquals("1234 5678 9012 3456",
                NumberFormatter.formatCreditCard("1234-5678-9012-3456"));
        assertEquals("1234 5678",
                NumberFormatter.formatCreditCard("12345678"));
    }

    @Test
    @DisplayName("Should handle BigDecimal formatting")
    void testBigDecimalFormatting() {
        BigDecimal big1 = new BigDecimal("1234567890123456789.123456789");
        NumberFormatter.FormatConfig config = new NumberFormatter.FormatConfig()
                .withStyle(NumberFormatter.SeparatorStyle.COMMA)
                .withDecimalPlaces(3);

        String result = NumberFormatter.format(big1, config);
        assertEquals("1,234,567,890,123,456,789.123", result);

        BigDecimal big2 = new BigDecimal("-999999999.99");
        String result2 = NumberFormatter.format(big2, NumberFormatter.CURRENCY_FORMAT);
        assertEquals("$-999,999,999.99", result2);
    }

    @Test
    @DisplayName("Should validate formatted numbers")
    void testIsValidFormattedNumber() {
        assertTrue(NumberFormatter.isValidFormattedNumber("1,234,567"));
        assertTrue(NumberFormatter.isValidFormattedNumber("1,234.56"));
        assertTrue(NumberFormatter.isValidFormattedNumber("۱٬۲۳۴٫۵۶"));
        assertTrue(NumberFormatter.isValidFormattedNumber("-1,234,567"));
        assertTrue(NumberFormatter.isValidFormattedNumber("1 234 567"));

        assertFalse(NumberFormatter.isValidFormattedNumber("abc"));
        assertFalse(NumberFormatter.isValidFormattedNumber(""));
        assertFalse(NumberFormatter.isValidFormattedNumber(null));
    }

    @Test
    @DisplayName("Should get decimal places count")
    void testGetDecimalPlaces() {
        assertEquals(0, NumberFormatter.getDecimalPlaces("1,234"));
        assertEquals(2, NumberFormatter.getDecimalPlaces("1,234.56"));
        assertEquals(3, NumberFormatter.getDecimalPlaces("1,234.567"));
        assertEquals(2, NumberFormatter.getDecimalPlaces("۱٬۲۳۴٫۵۶"));
        assertEquals(0, NumberFormatter.getDecimalPlaces("1,234,567"));
    }

    @Test
    @DisplayName("Should handle custom grouping size")
    void testCustomGroupingSize() {
        NumberFormatter.FormatConfig config = new NumberFormatter.FormatConfig()
                .withStyle(NumberFormatter.SeparatorStyle.SPACE)
                .withGroupingSize(4);

        assertEquals("123 4567", NumberFormatter.format(1234567L, config));
        assertEquals("1 2345 6789", NumberFormatter.format(123456789L, config));
    }

    @Test
    @DisplayName("Should handle no grouping")
    void testNoGrouping() {
        NumberFormatter.FormatConfig config = new NumberFormatter.FormatConfig()
                .withStyle(NumberFormatter.SeparatorStyle.COMMA)
                .withGrouping(false);

        assertEquals("1234567", NumberFormatter.format(1234567L, config));
        assertEquals("1234567.89", NumberFormatter.format(1234567.89, config));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1,234,567",
            "1 234 567",
            "1_234_567",
            "1'234'567",
            "۱٬۲۳۴٬۵۶۷",
            "$1,234,567",
            "1,234,567 ریال"
    })
    @DisplayName("Should correctly round-trip format and parse")
    void testRoundTrip(String formatted) {
        // Parse and format again should give consistent results
        if (NumberFormatter.isValidFormattedNumber(formatted)) {
            double parsed = NumberFormatter.parseDouble(formatted);
            assertDoesNotThrow(() -> NumberFormatter.addSeparator(parsed));
        }
    }

    @Test
    @DisplayName("Should handle edge cases")
    void testEdgeCases() {
        // Very large numbers
        assertEquals("9,223,372,036,854,775,807",
                NumberFormatter.addSeparator(Long.MAX_VALUE));
        assertEquals("-9,223,372,036,854,775,808",
                NumberFormatter.addSeparator(Long.MIN_VALUE));

        // Zero
        assertEquals("0", NumberFormatter.addSeparator(0));
        assertEquals("0.00", NumberFormatter.format(0.0,
                new NumberFormatter.FormatConfig().withDecimalPlaces(2)));

        // Single digit
        assertEquals("5", NumberFormatter.addSeparator(5));

        // Exactly 1000
        assertEquals("1,000", NumberFormatter.addSeparator(1000));
    }
}