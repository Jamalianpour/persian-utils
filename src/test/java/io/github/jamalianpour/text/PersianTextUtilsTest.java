package io.github.jamalianpour.text;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static io.github.jamalianpour.text.PersianTextUtils.TextDirection;

/**
 * Unit tests for PersianTextUtils.
 */
class PersianTextUtilsTest {

    @Test
    @DisplayName("Should detect Persian characters correctly")
    void testContainsPersian() {
        assertTrue(PersianTextUtils.containsPersian("سلام"));
        assertTrue(PersianTextUtils.containsPersian("Hello سلام"));
        assertTrue(PersianTextUtils.containsPersian("123 تست 456"));
        assertFalse(PersianTextUtils.containsPersian("Hello World"));
        assertFalse(PersianTextUtils.containsPersian("123456"));
        assertFalse(PersianTextUtils.containsPersian(""));
        assertFalse(PersianTextUtils.containsPersian(null));
    }

    @Test
    @DisplayName("Should check if text is entirely Persian")
    void testIsPersian() {
        assertTrue(PersianTextUtils.isPersian("سلام"));
        assertTrue(PersianTextUtils.isPersian("سلام دنیا"));
        assertTrue(PersianTextUtils.isPersian("۱۲۳۴۵"));
        assertTrue(PersianTextUtils.isPersian("سلام، خوبی؟"));
        assertFalse(PersianTextUtils.isPersian("سلام Hello"));
        assertFalse(PersianTextUtils.isPersian("Hello"));
        assertFalse(PersianTextUtils.isPersian("123"));
    }

    @Test
    @DisplayName("Should check Persian text in strict mode")
    void testIsPersianStrict() {
        // Only letters, no digits or punctuation
        assertTrue(PersianTextUtils.isPersianStrict("سلام دنیا", false, false));
        assertFalse(PersianTextUtils.isPersianStrict("سلام۱۲۳", false, false));
        assertFalse(PersianTextUtils.isPersianStrict("سلام؟", false, false));

        // Allow digits
        assertTrue(PersianTextUtils.isPersianStrict("سلام۱۲۳", true, false));
        assertFalse(PersianTextUtils.isPersianStrict("سلام123", true, false));

        // Allow punctuation
        assertTrue(PersianTextUtils.isPersianStrict("سلام، خوبی؟", false, true));
        assertTrue(PersianTextUtils.isPersianStrict("«سلام»", false, true));
    }

    @ParameterizedTest
    @CsvSource({
            "يك, یک",        // Arabic Yeh to Persian Yeh
            "كتاب, کتاب",    // Arabic Kaf to Persian Kaf
            "يك كتاب, یک کتاب",
            "٠١٢٣٤, ۰۱۲۳۴",  // Arabic digits to Persian digits
            "ى, ی",          // Arabic Alef Maksura to Persian Yeh
            "ة, ه",          // Arabic Teh Marbuta to Persian Heh
            "أحمد, احمد",    // Arabic Alef with Hamza
            "إيران, ایران",  // Arabic Alef with Hamza below
            "يكي از روزها, یکی از روزها"
    })
    @DisplayName("Should convert Arabic characters to Persian")
    void testArabicToPersian(String arabic, String expectedPersian) {
        assertEquals(expectedPersian, PersianTextUtils.arabicToPersian(arabic));
    }

    @Test
    @DisplayName("Should normalize Persian text")
    void testNormalize() {
        // Test Arabic to Persian conversion
        assertEquals("یک کتاب", PersianTextUtils.normalize("يك كتاب"));

        // Test removing diacritics
        String withDiacritics = "سَلامٌ";
        String normalized = PersianTextUtils.normalize(withDiacritics);
        assertEquals("سلام", normalized);

        // Test whitespace normalization
        assertEquals("سلام دنیا", PersianTextUtils.normalize("سلام   دنیا"));
        assertEquals("سلام دنیا", PersianTextUtils.normalize("  سلام  دنیا  "));
    }

    @Test
    @DisplayName("Should remove diacritics")
    void testRemoveDiacritics() {
        assertEquals("سلام", PersianTextUtils.removeDiacritics("سَلامٌ"));
        assertEquals("کتاب", PersianTextUtils.removeDiacritics("کِتابُ"));
        assertEquals("محمد", PersianTextUtils.removeDiacritics("مُحَمَّد"));
    }

    @Test
    @DisplayName("Should identify Persian characters correctly")
    void testCharacterIdentification() {
        // Persian letters
        assertTrue(PersianTextUtils.isPersianLetter('س'));
        assertTrue(PersianTextUtils.isPersianLetter('ک'));
        assertTrue(PersianTextUtils.isPersianLetter('گ'));
        assertFalse(PersianTextUtils.isPersianLetter('a'));
        assertFalse(PersianTextUtils.isPersianLetter('1'));

        // Persian digits
        assertTrue(PersianTextUtils.isPersianDigit('۰'));
        assertTrue(PersianTextUtils.isPersianDigit('۵'));
        assertTrue(PersianTextUtils.isPersianDigit('۹'));
        assertFalse(PersianTextUtils.isPersianDigit('0'));
        assertFalse(PersianTextUtils.isPersianDigit('5'));

        // Persian punctuation
        assertTrue(PersianTextUtils.isPersianPunctuation('،'));
        assertTrue(PersianTextUtils.isPersianPunctuation('؛'));
        assertTrue(PersianTextUtils.isPersianPunctuation('؟'));
        assertFalse(PersianTextUtils.isPersianPunctuation(','));
        assertFalse(PersianTextUtils.isPersianPunctuation('?'));
    }

    @Test
    @DisplayName("Should count Persian characters")
    void testCountPersianChars() {
        assertEquals(4, PersianTextUtils.countPersianChars("سلام"));
        assertEquals(4, PersianTextUtils.countPersianChars("Hello سلام World"));
        assertEquals(5, PersianTextUtils.countPersianChars("۱۲۳۴۵"));
        assertEquals(0, PersianTextUtils.countPersianChars("Hello World"));
    }

    @Test
    @DisplayName("Should extract Persian words")
    void testExtractPersianWords() {
        List<String> words1 = PersianTextUtils.extractPersianWords("سلام دنیا");
        assertEquals(2, words1.size());
        assertEquals("سلام", words1.get(0));
        assertEquals("دنیا", words1.get(1));

        List<String> words2 = PersianTextUtils.extractPersianWords("Hello سلام World دنیا");
        assertEquals(2, words2.size());
        assertEquals("سلام", words2.get(0));
        assertEquals("دنیا", words2.get(1));

        List<String> words3 = PersianTextUtils.extractPersianWords("این یک تست است.");
        assertEquals(4, words3.size());
    }

    @Test
    @DisplayName("Should calculate Persian percentage")
    void testGetPersianPercentage() {
        assertEquals(100.0, PersianTextUtils.getPersianPercentage("سلام"), 0.1);
        assertEquals(50.0, PersianTextUtils.getPersianPercentage("سلام test"), 0.1);
        assertEquals(0.0, PersianTextUtils.getPersianPercentage("Hello World"), 0.1);

        // Mixed content
        String mixed = "Hello سلام World دنیا";
        double percentage = PersianTextUtils.getPersianPercentage(mixed);
        assertTrue(percentage > 30 && percentage < 50);
    }

    @Test
    @DisplayName("Should detect text direction")
    void testGetTextDirection() {
        assertEquals(TextDirection.RTL, PersianTextUtils.getTextDirection("سلام دنیا"));
        assertEquals(TextDirection.LTR, PersianTextUtils.getTextDirection("Hello World"));
        assertEquals(TextDirection.LTR, PersianTextUtils.getTextDirection("Hello سلام World"));
        assertEquals(TextDirection.NEUTRAL, PersianTextUtils.getTextDirection(""));
        assertEquals(TextDirection.NEUTRAL, PersianTextUtils.getTextDirection("123 456"));
    }

    @Test
    @DisplayName("Should detect mixed Persian-English text")
    void testIsMixedPersianEnglish() {
        assertTrue(PersianTextUtils.isMixedPersianEnglish("سلام Hello"));
        assertTrue(PersianTextUtils.isMixedPersianEnglish("Hello دنیا World"));
        assertFalse(PersianTextUtils.isMixedPersianEnglish("سلام دنیا"));
        assertFalse(PersianTextUtils.isMixedPersianEnglish("Hello World"));
        assertFalse(PersianTextUtils.isMixedPersianEnglish("۱۲۳ 456"));
    }

    @Test
    @DisplayName("Should add directional marks")
    void testDirectionalMarks() {
        String text = "سلام";

        // RLM
        String withRLM = PersianTextUtils.addRLM(text);
        assertEquals('\u200F', withRLM.charAt(0));

        // LRM
        String withLRM = PersianTextUtils.addLRM(text);
        assertEquals('\u200E', withLRM.charAt(0));

        // ZWNJ
        String withZWNJ = PersianTextUtils.addZWNJ("میخواهم", 2);
        assertEquals("می\u200Cخواهم", withZWNJ);
    }

    @Test
    @DisplayName("Should convert text to Persian slug")
    void testToPersianSlug() {
        assertEquals("سلام-دنیا", PersianTextUtils.toPersianSlug("سلام دنیا"));
        assertEquals("یک-کتاب", PersianTextUtils.toPersianSlug("يك كتاب"));
        assertEquals("تست-۱۲۳", PersianTextUtils.toPersianSlug("تست ۱۲۳"));
        assertEquals("سلام-hello-دنیا", PersianTextUtils.toPersianSlug("سلام Hello دنیا"));
        assertEquals("سلام", PersianTextUtils.toPersianSlug("  سلام  "));
        assertEquals("سلام-دنیا", PersianTextUtils.toPersianSlug("سلام، دنیا!"));
    }

    @Test
    @DisplayName("Should convert English digits to Persian")
    void testEnglishToPersianDigits() {
        assertEquals("۱۲۳۴۵", PersianTextUtils.englishToPersianDigits("12345"));
        assertEquals("سال ۲۰۲۴", PersianTextUtils.englishToPersianDigits("سال 2024"));
        assertEquals("تست ۱۲۳ تست", PersianTextUtils.englishToPersianDigits("تست 123 تست"));
    }

    @Test
    @DisplayName("Should fix common Persian typing issues")
    void testFixPersianTyping() {
        // Fix Arabic characters
        assertEquals("یک کتاب", PersianTextUtils.fixPersianTyping("يك كتاب"));

        // Fix spacing with punctuation
        assertEquals("سلام! خوبی", PersianTextUtils.fixPersianTyping("سلام!خوبی"));

        // Fix quotation marks
        assertEquals("«سلام»", PersianTextUtils.fixPersianTyping("\"سلام\""));
    }

    @Test
    @DisplayName("Should generate Persian text statistics")
    void testPersianTextStats() {
        String text = "Hello سلام World دنیا 123 ۱۲۳";
        PersianTextUtils.PersianTextStats stats = new PersianTextUtils.PersianTextStats(text);

        assertTrue(stats.getTotalChars() > 0);
        assertTrue(stats.getPersianChars() > 0);
        assertTrue(stats.getEnglishChars() > 0);
        assertEquals(2, stats.getPersianWords());
        assertNotNull(stats.getDirection());

        // Test toString
        String statsString = stats.toString();
        assertNotNull(statsString);
        assertTrue(statsString.contains("Persian"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "سلام",
            "Hello",
            "123",
            "۱۲۳",
            "سلام Hello",
            "سلام، خوبی؟",
            "يك كتاب"
    })
    @DisplayName("Should handle various input types without error")
    void testRobustness(String input) {
        assertDoesNotThrow(() -> PersianTextUtils.containsPersian(input));
        assertDoesNotThrow(() -> PersianTextUtils.isPersian(input));
        assertDoesNotThrow(() -> PersianTextUtils.arabicToPersian(input));
        assertDoesNotThrow(() -> PersianTextUtils.normalize(input));
        assertDoesNotThrow(() -> PersianTextUtils.extractPersianWords(input));
        assertDoesNotThrow(() -> PersianTextUtils.getTextDirection(input));
    }

    @Test
    @DisplayName("Should handle null inputs gracefully")
    void testNullHandling() {
        assertFalse(PersianTextUtils.containsPersian(null));
        assertFalse(PersianTextUtils.isPersian(null));
        assertNull(PersianTextUtils.arabicToPersian(null));
        assertNull(PersianTextUtils.normalize(null));
        assertEquals(0, PersianTextUtils.countPersianChars(null));
        assertTrue(PersianTextUtils.extractPersianWords(null).isEmpty());
        assertEquals(0.0, PersianTextUtils.getPersianPercentage(null));
    }

    @Test
    @DisplayName("Should remove zero-width characters")
    void testRemoveZeroWidthChars() {
        String textWithZWNJ = "می\u200Cخواهم";
        assertEquals("میخواهم", PersianTextUtils.removeZeroWidthChars(textWithZWNJ));

        String textWithRLM = "\u200Fسلام";
        assertEquals("سلام", PersianTextUtils.removeZeroWidthChars(textWithRLM));

        String textWithLRM = "\u200Eسلام";
        assertEquals("سلام", PersianTextUtils.removeZeroWidthChars(textWithLRM));
    }
}