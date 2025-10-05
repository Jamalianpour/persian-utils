package io.github.jamalianpour.text;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PersianFileSizeFormatterTest {

    @Test
    @DisplayName("Should format zero correctly")
    void testZeroFormatting() {
        assertEquals("۰ بایت", PersianFileSizeFormatter.format(0));
        assertEquals("۰ B", PersianFileSizeFormatter.format(0, 
            PersianFileSizeFormatter.SizeMode.BINARY, 
            PersianFileSizeFormatter.NumberStyle.NUMERIC, 
            PersianFileSizeFormatter.UnitStyle.ENGLISH, 1));
    }

    @ParameterizedTest
    @DisplayName("Should format bytes correctly")
    @CsvSource({
        "1, ۱ بایت",
        "10, ۱۰ بایت",
        "100, ۱۰۰ بایت",
        "512, ۵۱۲ بایت",
        "1023, ۱۰۲۳ بایت"
    })
    void testBytesFormatting(long bytes, String expected) {
        assertEquals(expected, PersianFileSizeFormatter.format(bytes));
    }

    @ParameterizedTest
    @DisplayName("Should format kilobytes correctly in binary mode")
    @CsvSource({
        "1024, ۱ کیلوبایت",
        "2048, ۲ کیلوبایت",
        "3072, ۳ کیلوبایت",
        "10240, ۱۰ کیلوبایت",
        "102400, ۱۰۰ کیلوبایت"
    })
    void testKilobytesFormatting(long bytes, String expected) {
        assertEquals(expected, PersianFileSizeFormatter.format(bytes));
    }

    @ParameterizedTest
    @DisplayName("Should format megabytes correctly in binary mode")
    @CsvSource({
        "1048576, ۱ مگابایت",
        "2097152, ۲ مگابایت",
        "5242880, ۵ مگابایت",
        "10485760, ۱۰ مگابایت",
        "104857600, ۱۰۰ مگابایت"
    })
    void testMegabytesFormatting(long bytes, String expected) {
        assertEquals(expected, PersianFileSizeFormatter.format(bytes));
    }

    @ParameterizedTest
    @DisplayName("Should format gigabytes correctly in binary mode")
    @CsvSource({
        "1073741824, ۱ گیگابایت",
        "2147483648, ۲ گیگابایت",
        "5368709120, ۵ گیگابایت"
    })
    void testGigabytesFormatting(long bytes, String expected) {
        assertEquals(expected, PersianFileSizeFormatter.format(bytes));
    }

    @Test
    @DisplayName("Should format terabytes correctly")
    void testTerabytesFormatting() {
        long oneTerabyte = 1024L * 1024 * 1024 * 1024;
        assertEquals("۱ ترابایت", PersianFileSizeFormatter.format(oneTerabyte));
        
        long twoTerabytes = 2L * oneTerabyte;
        assertEquals("۲ ترابایت", PersianFileSizeFormatter.format(twoTerabytes));
    }

    @Test
    @DisplayName("Should handle decimal mode correctly")
    void testDecimalMode() {
        assertEquals("۱ کیلوبایت", 
            PersianFileSizeFormatter.format(1000, PersianFileSizeFormatter.SizeMode.DECIMAL));
        assertEquals("۱ مگابایت", 
            PersianFileSizeFormatter.format(1000000, PersianFileSizeFormatter.SizeMode.DECIMAL));
        assertEquals("۱ گیگابایت", 
            PersianFileSizeFormatter.format(1000000000, PersianFileSizeFormatter.SizeMode.DECIMAL));
    }

    @Test
    @DisplayName("Should format with words style")
    void testWordsStyle() {
        assertEquals("یک کیلوبایت", 
            PersianFileSizeFormatter.format(1024, 
                PersianFileSizeFormatter.SizeMode.BINARY, 
                PersianFileSizeFormatter.NumberStyle.WORDS));
        assertEquals("دو مگابایت", 
            PersianFileSizeFormatter.format(2097152, 
                PersianFileSizeFormatter.SizeMode.BINARY, 
                PersianFileSizeFormatter.NumberStyle.WORDS));
        assertEquals("سه گیگابایت", 
            PersianFileSizeFormatter.format(3221225472L, 
                PersianFileSizeFormatter.SizeMode.BINARY, 
                PersianFileSizeFormatter.NumberStyle.WORDS));
    }

    @Test
    @DisplayName("Should format with English unit style")
    void testEnglishUnitStyle() {
        assertEquals("۱ KB", 
            PersianFileSizeFormatter.format(1024, 
                PersianFileSizeFormatter.SizeMode.BINARY, 
                PersianFileSizeFormatter.NumberStyle.NUMERIC, 
                PersianFileSizeFormatter.UnitStyle.ENGLISH, 0));
        assertEquals("۱ MB", 
            PersianFileSizeFormatter.format(1048576, 
                PersianFileSizeFormatter.SizeMode.BINARY, 
                PersianFileSizeFormatter.NumberStyle.NUMERIC, 
                PersianFileSizeFormatter.UnitStyle.ENGLISH, 0));
        assertEquals("۱ GB", 
            PersianFileSizeFormatter.format(1073741824, 
                PersianFileSizeFormatter.SizeMode.BINARY, 
                PersianFileSizeFormatter.NumberStyle.NUMERIC, 
                PersianFileSizeFormatter.UnitStyle.ENGLISH, 0));
    }

    @Test
    @DisplayName("Should handle decimal places correctly")
    void testDecimalPlaces() {
        // 1.5 KB
        long size = 1024 + 512;
        assertEquals("۱.۵ کیلوبایت", 
            PersianFileSizeFormatter.format(size, 
                PersianFileSizeFormatter.SizeMode.BINARY, 
                PersianFileSizeFormatter.NumberStyle.NUMERIC, 
                PersianFileSizeFormatter.UnitStyle.FULL, 1));
        
        // 2.25 MB
        long size2 = 1048576 * 2 + 1048576 / 4;
        assertEquals("۲.۲۵ مگابایت", 
            PersianFileSizeFormatter.format(size2, 
                PersianFileSizeFormatter.SizeMode.BINARY, 
                PersianFileSizeFormatter.NumberStyle.NUMERIC, 
                PersianFileSizeFormatter.UnitStyle.FULL, 2));
    }

    @Test
    @DisplayName("Should format human readable sizes")
    void testHumanReadableFormatting() {
        assertEquals("۱ کیلوبایت", PersianFileSizeFormatter.formatHumanReadable(1024));
        assertEquals("۲ کیلوبایت", PersianFileSizeFormatter.formatHumanReadable(2048));
        assertEquals("۱.۵۰ کیلوبایت", PersianFileSizeFormatter.formatHumanReadable(1536));
        assertEquals("۱۰.۳ مگابایت", PersianFileSizeFormatter.formatHumanReadable(10747904)); // 10.3 MB
    }

    @Test
    @DisplayName("Should format with words for accessibility")
    void testFormatWithWords() {
        assertEquals("یک کیلوبایت", PersianFileSizeFormatter.formatWithWords(1024));
        assertEquals("دو مگابایت", PersianFileSizeFormatter.formatWithWords(2097152));
        assertEquals("پنج گیگابایت", PersianFileSizeFormatter.formatWithWords(5368709120L));
    }

    @Test
    @DisplayName("Should format multiple sizes")
    void testFormatMultiple() {
        long[] sizes = {0, 1024, 1048576, 1073741824};
        String[] expected = {"۰ بایت", "۱ کیلوبایت", "۱ مگابایت", "۱ گیگابایت"};
        
        String[] result = PersianFileSizeFormatter.formatMultiple(sizes);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle null array in formatMultiple")
    void testFormatMultipleNull() {
        String[] result = PersianFileSizeFormatter.formatMultiple(null);
        assertEquals(0, result.length);
    }

    @Test
    @DisplayName("Should handle empty array in formatMultiple")
    void testFormatMultipleEmpty() {
        String[] result = PersianFileSizeFormatter.formatMultiple(new long[0]);
        assertEquals(0, result.length);
    }

    @Test
    @DisplayName("Should parse Persian file size strings")
    void testParsing() {
        assertEquals(1024, PersianFileSizeFormatter.parse("۱ کیلوبایت"));
        assertEquals(1048576, PersianFileSizeFormatter.parse("۱ مگابایت"));
        assertEquals(1073741824, PersianFileSizeFormatter.parse("۱ گیگابایت"));
        assertEquals(512, PersianFileSizeFormatter.parse("۵۱۲ بایت"));
    }

    @Test
    @DisplayName("Should handle empty and null strings in parse")
    void testParseEmptyAndNull() {
        assertEquals(0, PersianFileSizeFormatter.parse(null));
        assertEquals(0, PersianFileSizeFormatter.parse(""));
        assertEquals(0, PersianFileSizeFormatter.parse("   "));
    }

    @Test
    @DisplayName("Should throw exception for invalid parse input")
    void testParseInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> 
            PersianFileSizeFormatter.parse("invalid"));
        assertThrows(IllegalArgumentException.class, () -> 
            PersianFileSizeFormatter.parse("۱۰"));
        assertThrows(IllegalArgumentException.class, () -> 
            PersianFileSizeFormatter.parse("abc کیلوبایت"));
    }

    @Test
    @DisplayName("Should throw exception for negative file sizes")
    void testNegativeFileSize() {
        assertThrows(IllegalArgumentException.class, () -> 
            PersianFileSizeFormatter.format(-1));
        assertThrows(IllegalArgumentException.class, () -> 
            PersianFileSizeFormatter.format(-1024));
    }

    @Test
    @DisplayName("Should handle large file sizes")
    void testLargeFileSizes() {
        // Petabyte
        long petabyte = 1024L * 1024 * 1024 * 1024 * 1024;
        assertEquals("۱ پتابایت", PersianFileSizeFormatter.format(petabyte));
        
        // Exabyte
        long exabyte = petabyte * 1024;
        assertEquals("۱ اگزابایت", PersianFileSizeFormatter.format(exabyte));
    }

    @Test
    @DisplayName("Should test common utility methods")
    void testCommonUtilities() {
        assertEquals("۱۰۲۴ بایت", PersianFileSizeFormatter.Common.formatBytes(1024));
        assertEquals("۱ کیلوبایت", PersianFileSizeFormatter.Common.formatKilobytes(1));
        assertEquals("۲ مگابایت", PersianFileSizeFormatter.Common.formatMegabytes(2));
        assertEquals("۳ گیگابایت", PersianFileSizeFormatter.Common.formatGigabytes(3));
        assertEquals("۱ ترابایت", PersianFileSizeFormatter.Common.formatTerabytes(1));
    }

    @Test
    @DisplayName("Should test conversion utilities")
    void testConversionUtilities() {
        assertEquals(1024, PersianFileSizeFormatter.Common.kilobytesToBytes(1));
        assertEquals(1048576, PersianFileSizeFormatter.Common.megabytesToBytes(1));
        assertEquals(1073741824, PersianFileSizeFormatter.Common.gigabytesToBytes(1));
        assertEquals(1099511627776L, PersianFileSizeFormatter.Common.terabytesToBytes(1));
    }

    @ParameterizedTest
    @DisplayName("Should handle various sizes correctly")
    @ValueSource(longs = {0, 1, 10, 100, 1000, 1024, 10240, 102400, 1048576, 10485760})
    void testVariousSizes(long size) {
        String result = PersianFileSizeFormatter.format(size);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("بایت") || result.contains("کیلوبایت") || 
                  result.contains("مگابایت") || result.contains("گیگابایت"));
    }

    @Test
    @DisplayName("Should maintain consistency between modes")
    void testModeConsistency() {
        long size = 1000000; // 1 million bytes
        
        String binary = PersianFileSizeFormatter.format(size, PersianFileSizeFormatter.SizeMode.BINARY);
        String decimal = PersianFileSizeFormatter.format(size, PersianFileSizeFormatter.SizeMode.DECIMAL);
        
        assertNotEquals(binary, decimal); // Should produce different results
        assertTrue(binary.contains("کیلوبایت"));
        assertTrue(decimal.contains("مگابایت") || decimal.contains("کیلوبایت"));
    }

    @Test
    @DisplayName("Should handle double precision correctly")
    void testDoublePrecision() {
        double size = 1536.5; // 1.5 KB + 0.5 bytes
        String result = PersianFileSizeFormatter.format(size, 
            PersianFileSizeFormatter.SizeMode.BINARY,
            PersianFileSizeFormatter.NumberStyle.NUMERIC,
            PersianFileSizeFormatter.UnitStyle.FULL, 2);
        
        assertNotNull(result);
        assertTrue(result.contains("بایت"));
    }

    @Test
    @DisplayName("Should format edge cases correctly")
    void testEdgeCases() {
        // Just under 1 KB
        assertEquals("۱۰۲۳ بایت", PersianFileSizeFormatter.format(1023));
        
        // Exactly 1 KB
        assertEquals("۱ کیلوبایت", PersianFileSizeFormatter.format(1024));
        
        // Just over 1 KB
        assertEquals("۱ کیلوبایت", PersianFileSizeFormatter.format(1025));
        
        // Just under 1 MB
        assertEquals("۱۰۲۴ کیلوبایت", PersianFileSizeFormatter.format(1048575));
        
        // Exactly 1 MB
        assertEquals("۱ مگابایت", PersianFileSizeFormatter.format(1048576));
    }

    @Test
    @DisplayName("Should format fractional sizes with appropriate precision")
    void testFractionalSizes() {
        // 1.5 KB
        long size = 1024 + 512;
        String result = PersianFileSizeFormatter.formatHumanReadable(size);
        assertEquals("۱.۵۰ کیلوبایت", result);
        
        // 2.75 MB
        long size2 = (long)(2.75 * 1024 * 1024);
        String result2 = PersianFileSizeFormatter.formatHumanReadable(size2);
        assertEquals("۲.۷۵ مگابایت", result2);
    }

    @Test
    @DisplayName("Should handle maximum long value")
    void testMaxLongValue() {
        // Test with a very large number (close to Long.MAX_VALUE)
        long largeSize = Long.MAX_VALUE / 1024; // Avoid overflow
        String result = PersianFileSizeFormatter.format(largeSize);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Should use one of the larger units
        assertTrue(result.contains("بایت") || result.contains("کیلوبایت") || 
                  result.contains("مگابایت") || result.contains("گیگابایت") ||
                  result.contains("ترابایت") || result.contains("پتابایت") ||
                  result.contains("اگزابایت"));
    }
}