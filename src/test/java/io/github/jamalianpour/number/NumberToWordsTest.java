package io.github.jamalianpour.number;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NumberToWords converter.
 */
class NumberToWordsTest {

    @ParameterizedTest
    @CsvSource({
            "0, صفر",
            "1, یک",
            "5, پنج",
            "10, ده",
            "11, یازده",
            "15, پانزده",
            "19, نوزده",
            "20, بیست",
            "21, بیست و یک",
            "25, بیست و پنج",
            "30, سی",
            "50, پنجاه",
            "99, نود و نه",
            "100, یکصد",
            "101, یکصد و یک",
            "111, یکصد و یازده",
            "120, یکصد و بیست",
            "199, یکصد و نود و نه",
            "200, دویست",
            "300, سیصد",
            "500, پانصد",
            "999, نهصد و نود و نه",
            "1000, یک هزار",
            "1001, یک هزار و یک",
            "1100, یک هزار و یکصد",
            "1234, یک هزار و دویست و سی و چهار",
            "2000, دو هزار",
            "10000, ده هزار",
            "11000, یازده هزار",
            "100000, یکصد هزار",
            "123456, یکصد و بیست و سه هزار و چهارصد و پنجاه و شش",
            "1000000, یک میلیون",
            "1000001, یک میلیون و یک",
            "1234567, یک میلیون و دویست و سی و چهار هزار و پانصد و شصت و هفت"
    })
    @DisplayName("Should convert numbers to Persian words")
    void testToPersian(long number, String expected) {
        assertEquals(expected, NumberToWords.toPersian(number));
    }

    @Test
    @DisplayName("Should handle negative numbers in Persian")
    void testNegativeNumbersPersian() {
        assertEquals("منفی یک", NumberToWords.toPersian(-1));
        assertEquals("منفی یکصد", NumberToWords.toPersian(-100));
        assertEquals("منفی یک هزار و دویست و سی و چهار", NumberToWords.toPersian(-1234));
    }

    @Test
    @DisplayName("Should handle large numbers in Persian")
    void testLargeNumbersPersian() {
        assertEquals("یک میلیارد", NumberToWords.toPersian(1_000_000_000L));
        assertEquals("دو میلیارد و پانصد میلیون", NumberToWords.toPersian(2_500_000_000L));
        assertEquals("یک بیلیون", NumberToWords.toPersian(1_000_000_000_000L));
    }

    @ParameterizedTest
    @CsvSource({
            "0, zero",
            "1, one",
            "5, five",
            "10, ten",
            "11, eleven",
            "15, fifteen",
            "19, nineteen",
            "20, twenty",
            "21, twenty-one",
            "25, twenty-five",
            "30, thirty",
            "50, fifty",
            "99, ninety-nine",
            "100, one hundred",
            "101, one hundred one",
            "111, one hundred eleven",
            "120, one hundred twenty",
            "199, one hundred ninety-nine",
            "200, two hundred",
            "999, nine hundred ninety-nine",
            "1000, one thousand",
            "1001, one thousand one",
            "1234, one thousand two hundred thirty-four",
            "10000, ten thousand",
            "100000, one hundred thousand",
            "123456, one hundred twenty-three thousand four hundred fifty-six",
            "1000000, one million",
            "1234567, one million two hundred thirty-four thousand five hundred sixty-seven"
    })
    @DisplayName("Should convert numbers to English words")
    void testToEnglish(long number, String expected) {
        assertEquals(expected, NumberToWords.toEnglish(number));
    }

    @Test
    @DisplayName("Should handle negative numbers in English")
    void testNegativeNumbersEnglish() {
        assertEquals("negative one", NumberToWords.toEnglish(-1));
        assertEquals("negative one hundred", NumberToWords.toEnglish(-100));
        assertEquals("negative one thousand two hundred thirty-four", NumberToWords.toEnglish(-1234));
    }

    @Test
    @DisplayName("Should handle decimal numbers in Persian")
    void testDecimalNumbersPersian() {
        assertEquals("یک ممیز پنج", NumberToWords.toPersian(1.5));
        assertEquals("سه ممیز یک چهار", NumberToWords.toPersian(3.14));
        assertEquals("یکصد و بیست و سه ممیز چهار پنج", NumberToWords.toPersian(123.45));
        assertEquals("صفر ممیز پنج", NumberToWords.toPersian(0.5));
    }

    @Test
    @DisplayName("Should handle decimal numbers in English")
    void testDecimalNumbersEnglish() {
        assertEquals("one point five", NumberToWords.toEnglish(1.5));
        assertEquals("three point one four", NumberToWords.toEnglish(3.14));
        assertEquals("one hundred twenty-three point four five", NumberToWords.toEnglish(123.45));
        assertEquals("zero point five", NumberToWords.toEnglish(0.5));
    }

    @Test
    @DisplayName("Should convert to Persian currency - Rial")
    void testPersianCurrencyRial() {
        assertEquals("یک ریال", NumberToWords.toPersianCurrency(1, NumberToWords.RIAL));
        assertEquals("یکصد ریال", NumberToWords.toPersianCurrency(100, NumberToWords.RIAL));
        assertEquals("یک هزار ریال", NumberToWords.toPersianCurrency(1000, NumberToWords.RIAL));
        assertEquals("یک میلیون ریال", NumberToWords.toPersianCurrency(1_000_000, NumberToWords.RIAL));
        assertEquals("منفی پانصد ریال", NumberToWords.toPersianCurrency(-500, NumberToWords.RIAL));
    }

    @Test
    @DisplayName("Should convert to Persian currency - Toman")
    void testPersianCurrencyToman() {
        assertEquals("یک تومان", NumberToWords.toPersianCurrency(1, NumberToWords.TOMAN));
        assertEquals("ده تومان", NumberToWords.toPersianCurrency(10, NumberToWords.TOMAN));
        assertEquals("یکصد و پنجاه تومان", NumberToWords.toPersianCurrency(150, NumberToWords.TOMAN));
        assertEquals("یک هزار و پانصد تومان", NumberToWords.toPersianCurrency(1500, NumberToWords.TOMAN));
    }

    @Test
    @DisplayName("Should convert to Persian currency with decimals")
    void testPersianCurrencyWithDecimals() {
        assertEquals("یک تومان و پنجاه ریال",
                NumberToWords.toPersianCurrency(1.50, NumberToWords.TOMAN));
        assertEquals("یکصد و بیست و سه تومان و چهل و پنج ریال",
                NumberToWords.toPersianCurrency(123.45, NumberToWords.TOMAN));
        assertEquals("صفر تومان",
                NumberToWords.toPersianCurrency(0.00, NumberToWords.TOMAN));
    }

    @Test
    @DisplayName("Should convert to English currency - Dollar")
    void testEnglishCurrencyDollar() {
        assertEquals("one dollar", NumberToWords.toEnglishCurrency(1, NumberToWords.DOLLAR));
        assertEquals("two dollars", NumberToWords.toEnglishCurrency(2, NumberToWords.DOLLAR));
        assertEquals("one hundred dollars", NumberToWords.toEnglishCurrency(100, NumberToWords.DOLLAR));
        assertEquals("one thousand dollars", NumberToWords.toEnglishCurrency(1000, NumberToWords.DOLLAR));
        assertEquals("negative five hundred dollars", NumberToWords.toEnglishCurrency(-500, NumberToWords.DOLLAR));
    }

    @Test
    @DisplayName("Should convert to English currency with cents")
    void testEnglishCurrencyWithCents() {
        assertEquals("one dollar and fifty cents",
                NumberToWords.toEnglishCurrency(1.50, NumberToWords.DOLLAR));
        assertEquals("ten dollars and one cent",
                NumberToWords.toEnglishCurrency(10.01, NumberToWords.DOLLAR));
        assertEquals("one hundred twenty-three dollars and forty-five cents",
                NumberToWords.toEnglishCurrency(123.45, NumberToWords.DOLLAR));
        assertEquals("zero dollars",
                NumberToWords.toEnglishCurrency(0.00, NumberToWords.DOLLAR));
    }

    @Test
    @DisplayName("Should convert to English currency - Euro")
    void testEnglishCurrencyEuro() {
        assertEquals("one euro", NumberToWords.toEnglishCurrency(1, NumberToWords.EURO));
        assertEquals("ten euros", NumberToWords.toEnglishCurrency(10, NumberToWords.EURO));
        assertEquals("fifty euros and twenty-five cents",
                NumberToWords.toEnglishCurrency(50.25, NumberToWords.EURO));
    }

    @Test
    @DisplayName("Should convert to English currency - Pound")
    void testEnglishCurrencyPound() {
        assertEquals("one pound", NumberToWords.toEnglishCurrency(1, NumberToWords.POUND));
        assertEquals("five pounds", NumberToWords.toEnglishCurrency(5, NumberToWords.POUND));
        assertEquals("twenty pounds and fifty cents",
                NumberToWords.toEnglishCurrency(20.50, NumberToWords.POUND));
    }

    @Test
    @DisplayName("Should handle BigDecimal conversion")
    void testBigDecimalConversion() {
        BigDecimal bd1 = new BigDecimal("123.45");
        assertEquals("یکصد و بیست و سه ممیز چهار پنج", NumberToWords.toPersian(bd1));

        BigDecimal bd2 = new BigDecimal("1000000000000");
        assertEquals("یک بیلیون", NumberToWords.toPersian(bd2));

        BigDecimal bd3 = new BigDecimal("0");
        assertEquals("صفر", NumberToWords.toPersian(bd3));

        BigDecimal bd4 = new BigDecimal("-999");
        assertEquals("منفی نهصد و نود و نه", NumberToWords.toPersian(bd4));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 10, 100, 1000, 10000, 100000, 1000000})
    @DisplayName("Should handle round numbers correctly")
    void testRoundNumbers(long number) {
        String persian = NumberToWords.toPersian(number);
        String english = NumberToWords.toEnglish(number);

        assertNotNull(persian);
        assertNotNull(english);
        assertFalse(persian.contains("null"));
        assertFalse(english.contains("null"));
    }

    @Test
    @DisplayName("Should handle edge cases")
    void testEdgeCases() {
        // Zero
        assertEquals("صفر", NumberToWords.toPersian(0));
        assertEquals("zero", NumberToWords.toEnglish(0));

        // Maximum long value
        long maxLong = Long.MAX_VALUE;
        String persianMax = NumberToWords.toPersian(maxLong);
        String englishMax = NumberToWords.toEnglish(maxLong);
        assertNotNull(persianMax);
        assertNotNull(englishMax);

        // Very small decimal
        assertEquals("صفر ممیز صفر صفر یک", NumberToWords.toPersian(0.001));
        assertEquals("zero point zero zero one", NumberToWords.toEnglish(0.001));
    }

    @Test
    @DisplayName("Should maintain consistency in conversion")
    void testConsistency() {
        // Same number should always produce same result
        String persian1 = NumberToWords.toPersian(12345);
        String persian2 = NumberToWords.toPersian(12345);
        assertEquals(persian1, persian2);

        String english1 = NumberToWords.toEnglish(12345);
        String english2 = NumberToWords.toEnglish(12345);
        assertEquals(english1, english2);
    }
}