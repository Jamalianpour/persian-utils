package io.github.jamalianpour.date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PersianRelativeTimeFormatterTest {

    @Test
    @DisplayName("Should format 'now' correctly")
    void testNowFormatting() {
        assertEquals("هم‌اکنون", PersianRelativeTimeFormatter.formatSecondsAgo(0));
        assertEquals("هم‌اکنون", PersianRelativeTimeFormatter.formatSecondsAgo(5));
        assertEquals("هم‌اکنون", PersianRelativeTimeFormatter.formatSecondsAgo(9));
    }

    @Test
    @DisplayName("Should format few moments ago")
    void testFewMomentsAgo() {
        assertEquals("چند لحظه پیش", PersianRelativeTimeFormatter.formatSecondsAgo(30));
        assertEquals("چند لحظه پیش", PersianRelativeTimeFormatter.formatSecondsAgo(45));
        assertEquals("چند لحظه پیش", PersianRelativeTimeFormatter.formatSecondsAgo(59));
    }

    @ParameterizedTest
    @DisplayName("Should format minutes correctly in numeric style")
    @CsvSource({
        "60, ۱ دقیقه پیش",
        "120, ۲ دقیقه پیش",
        "300, ۵ دقیقه پیش",
        "900, ۱۵ دقیقه پیش",
        "1800, ۳۰ دقیقه پیش",
        "3540, ۵۹ دقیقه پیش"
    })
    void testMinutesFormatting(long seconds, String expected) {
        assertEquals(expected, PersianRelativeTimeFormatter.formatSecondsAgo(seconds));
    }

    @ParameterizedTest
    @DisplayName("Should format hours correctly in numeric style")
    @CsvSource({
        "3600, ۱ ساعت پیش",
        "7200, ۲ ساعت پیش",
        "18000, ۵ ساعت پیش",
        "43200, ۱۲ ساعت پیش",
        "82800, ۲۳ ساعت پیش"
    })
    void testHoursFormatting(long seconds, String expected) {
        assertEquals(expected, PersianRelativeTimeFormatter.formatSecondsAgo(seconds));
    }

    @ParameterizedTest
    @DisplayName("Should format days correctly in numeric style")
    @CsvSource({
        "86400, ۱ روز پیش",
        "172800, ۲ روز پیش",
        "432000, ۵ روز پیش",
        "604800, ۱ هفته پیش"
    })
    void testDaysFormatting(long seconds, String expected) {
        assertEquals(expected, PersianRelativeTimeFormatter.formatSecondsAgo(seconds));
    }

    @ParameterizedTest
    @DisplayName("Should format weeks correctly in numeric style")
    @CsvSource({
        "604800, ۱ هفته پیش",
        "1209600, ۲ هفته پیش",
        "2419200, ۴ هفته پیش"
    })
    void testWeeksFormatting(long seconds, String expected) {
        // For weeks, we need to test actual week boundaries
        long oneWeek = 7 * 24 * 60 * 60; // 604800
        long twoWeeks = 2 * oneWeek; // 1209600
        long fourWeeks = 4 * oneWeek; // 2419200
        
        assertEquals("۱ هفته پیش", PersianRelativeTimeFormatter.formatSecondsAgo(oneWeek));
        assertEquals("۲ هفته پیش", PersianRelativeTimeFormatter.formatSecondsAgo(twoWeeks));
        assertEquals("۴ هفته پیش", PersianRelativeTimeFormatter.formatSecondsAgo(fourWeeks));
    }

    @Test
    @DisplayName("Should format months correctly")
    void testMonthsFormatting() {
        long oneMonth = 30L * 24 * 60 * 60; // 30 days
        long twoMonths = 2 * oneMonth;
        long sixMonths = 6 * oneMonth;
        
        assertEquals("۱ ماه پیش", PersianRelativeTimeFormatter.formatSecondsAgo(oneMonth));
        assertEquals("۲ ماه پیش", PersianRelativeTimeFormatter.formatSecondsAgo(twoMonths));
        assertEquals("۶ ماه پیش", PersianRelativeTimeFormatter.formatSecondsAgo(sixMonths));
    }

    @Test
    @DisplayName("Should format years correctly")
    void testYearsFormatting() {
        long oneYear = 365L * 24 * 60 * 60; // 365 days
        long twoYears = 2 * oneYear;
        long fiveYears = 5 * oneYear;
        
        assertEquals("۱ سال پیش", PersianRelativeTimeFormatter.formatSecondsAgo(oneYear));
        assertEquals("۲ سال پیش", PersianRelativeTimeFormatter.formatSecondsAgo(twoYears));
        assertEquals("۵ سال پیش", PersianRelativeTimeFormatter.formatSecondsAgo(fiveYears));
    }

    @Test
    @DisplayName("Should format with WORDS style")
    void testWordsStyle() {
        assertEquals("یک دقیقه پیش", 
                    PersianRelativeTimeFormatter.formatSecondsAgo(60, PersianRelativeTimeFormatter.FormatStyle.WORDS));
        assertEquals("دو ساعت پیش", 
                    PersianRelativeTimeFormatter.formatSecondsAgo(7200, PersianRelativeTimeFormatter.FormatStyle.WORDS));
        assertEquals("سه روز پیش", 
                    PersianRelativeTimeFormatter.formatSecondsAgo(259200, PersianRelativeTimeFormatter.FormatStyle.WORDS));
    }

    @Test
    @DisplayName("Should format with SHORT style")
    void testShortStyle() {
        assertEquals("۱د پیش", 
                    PersianRelativeTimeFormatter.formatSecondsAgo(60, PersianRelativeTimeFormatter.FormatStyle.SHORT));
        assertEquals("۲س پیش", 
                    PersianRelativeTimeFormatter.formatSecondsAgo(7200, PersianRelativeTimeFormatter.FormatStyle.SHORT));
        assertEquals("۳ر پیش", 
                    PersianRelativeTimeFormatter.formatSecondsAgo(259200, PersianRelativeTimeFormatter.FormatStyle.SHORT));
    }

    @Test
    @DisplayName("Should format with FUZZY style")
    void testFuzzyStyle() {
        assertEquals("یک دقیقه پیش", 
                    PersianRelativeTimeFormatter.formatSecondsAgo(60, PersianRelativeTimeFormatter.FormatStyle.FUZZY));
        assertEquals("چند دقیقه پیش", 
                    PersianRelativeTimeFormatter.formatSecondsAgo(180, PersianRelativeTimeFormatter.FormatStyle.FUZZY));
        assertEquals("یک ساعت پیش", 
                    PersianRelativeTimeFormatter.formatSecondsAgo(3600, PersianRelativeTimeFormatter.FormatStyle.FUZZY));
        assertEquals("چند ساعت پیش", 
                    PersianRelativeTimeFormatter.formatSecondsAgo(7200, PersianRelativeTimeFormatter.FormatStyle.FUZZY));
    }

    @Test
    @DisplayName("Should format future times correctly")
    void testFutureFormatting() {
        assertEquals("هم‌اکنون", PersianRelativeTimeFormatter.formatSecondsInFuture(5));
        assertEquals("چند لحظه دیگر", PersianRelativeTimeFormatter.formatSecondsInFuture(30));
        assertEquals("۱ دقیقه آینده", PersianRelativeTimeFormatter.formatSecondsInFuture(60));
        assertEquals("۲ ساعت آینده", PersianRelativeTimeFormatter.formatSecondsInFuture(7200));
        assertEquals("۳ روز دیگر", PersianRelativeTimeFormatter.formatSecondsInFuture(259200));
        assertEquals("۲ هفته دیگر", PersianRelativeTimeFormatter.formatSecondsInFuture(1209600));
    }

    @Test
    @DisplayName("Should handle LocalDateTime comparison")
    void testLocalDateTimeComparison() {
        LocalDateTime now = LocalDateTime.of(2023, 10, 15, 12, 0, 0);
        LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
        LocalDateTime twoHoursAgo = now.minusHours(2);
        LocalDateTime threeDaysAgo = now.minusDays(3);
        
        assertEquals("۵ دقیقه پیش", 
                    PersianRelativeTimeFormatter.formatRelativeTime(now, fiveMinutesAgo));
        assertEquals("۲ ساعت پیش", 
                    PersianRelativeTimeFormatter.formatRelativeTime(now, twoHoursAgo));
        assertEquals("۳ روز پیش", 
                    PersianRelativeTimeFormatter.formatRelativeTime(now, threeDaysAgo));
    }

    @Test
    @DisplayName("Should handle future LocalDateTime comparison")
    void testFutureLocalDateTimeComparison() {
        LocalDateTime now = LocalDateTime.of(2023, 10, 15, 12, 0, 0);
        LocalDateTime fiveMinutesLater = now.plusMinutes(5);
        LocalDateTime twoHoursLater = now.plusHours(2);
        LocalDateTime threeDaysLater = now.plusDays(3);
        
        assertEquals("۵ دقیقه آینده", 
                    PersianRelativeTimeFormatter.formatRelativeTime(now, fiveMinutesLater));
        assertEquals("۲ ساعت آینده", 
                    PersianRelativeTimeFormatter.formatRelativeTime(now, twoHoursLater));
        assertEquals("۳ روز دیگر", 
                    PersianRelativeTimeFormatter.formatRelativeTime(now, threeDaysLater));
    }

    @Test
    @DisplayName("Should provide common expressions")
    void testCommonExpressions() {
        // Past expressions
        assertEquals("هم‌اکنون", PersianRelativeTimeFormatter.Common.justNow());
        assertEquals("چند لحظه پیش", PersianRelativeTimeFormatter.Common.fewSecondsAgo());
        assertEquals("یک دقیقه پیش", PersianRelativeTimeFormatter.Common.oneMinuteAgo());
        assertEquals("چند دقیقه پیش", PersianRelativeTimeFormatter.Common.fewMinutesAgo());
        assertEquals("یک ساعت پیش", PersianRelativeTimeFormatter.Common.oneHourAgo());
        assertEquals("چند ساعت پیش", PersianRelativeTimeFormatter.Common.fewHoursAgo());
        assertEquals("دیروز", PersianRelativeTimeFormatter.Common.yesterday());
        assertEquals("چند روز پیش", PersianRelativeTimeFormatter.Common.fewDaysAgo());
        assertEquals("یک هفته پیش", PersianRelativeTimeFormatter.Common.oneWeekAgo());
        assertEquals("چند هفته پیش", PersianRelativeTimeFormatter.Common.fewWeeksAgo());
        assertEquals("یک ماه پیش", PersianRelativeTimeFormatter.Common.oneMonthAgo());
        assertEquals("چند ماه پیش", PersianRelativeTimeFormatter.Common.fewMonthsAgo());
        assertEquals("یک سال پیش", PersianRelativeTimeFormatter.Common.oneYearAgo());
        assertEquals("چند سال پیش", PersianRelativeTimeFormatter.Common.fewYearsAgo());
        
        // Future expressions
        assertEquals("چند لحظه دیگر", PersianRelativeTimeFormatter.Common.inFewSeconds());
        assertEquals("یک دقیقه دیگر", PersianRelativeTimeFormatter.Common.inOneMinute());
        assertEquals("چند دقیقه دیگر", PersianRelativeTimeFormatter.Common.inFewMinutes());
        assertEquals("یک ساعت دیگر", PersianRelativeTimeFormatter.Common.inOneHour());
        assertEquals("چند ساعت دیگر", PersianRelativeTimeFormatter.Common.inFewHours());
        assertEquals("فردا", PersianRelativeTimeFormatter.Common.tomorrow());
        assertEquals("چند روز دیگر", PersianRelativeTimeFormatter.Common.inFewDays());
        assertEquals("یک هفته دیگر", PersianRelativeTimeFormatter.Common.inOneWeek());
        assertEquals("چند هفته دیگر", PersianRelativeTimeFormatter.Common.inFewWeeks());
        assertEquals("یک ماه دیگر", PersianRelativeTimeFormatter.Common.inOneMonth());
        assertEquals("چند ماه دیگر", PersianRelativeTimeFormatter.Common.inFewMonths());
        assertEquals("یک سال دیگر", PersianRelativeTimeFormatter.Common.inOneYear());
        assertEquals("چند سال دیگر", PersianRelativeTimeFormatter.Common.inFewYears());
    }

    @Test
    @DisplayName("Should handle edge cases")
    void testEdgeCases() {
        // Boundary values
        assertEquals("هم‌اکنون", PersianRelativeTimeFormatter.formatSecondsAgo(0));
        assertEquals("چند لحظه پیش", PersianRelativeTimeFormatter.formatSecondsAgo(59));
        assertEquals("۱ دقیقه پیش", PersianRelativeTimeFormatter.formatSecondsAgo(60));
        assertEquals("۵۹ دقیقه پیش", PersianRelativeTimeFormatter.formatSecondsAgo(3540));
        assertEquals("۱ ساعت پیش", PersianRelativeTimeFormatter.formatSecondsAgo(3600));
    }

    @Test
    @DisplayName("Should format with JalaliDate integration")
    void testJalaliDateIntegration() {
        // Create a JalaliDate (this would need actual JalaliDate implementation)
        // For now, test that the method exists and can be called
        assertDoesNotThrow(() -> {
            JalaliDate date = JalaliDate.of(1404, 7, 1);
            String result = PersianRelativeTimeFormatter.formatRelativeTime(date);
            assertNotNull(result);
        });
    }

    @Test
    @DisplayName("Should format large time differences")
    void testLargeTimeDifferences() {
        long tenYears = 10L * 365 * 24 * 60 * 60;
        long hundredYears = 100L * 365 * 24 * 60 * 60;
        
        assertEquals("۱۰ سال پیش", PersianRelativeTimeFormatter.formatSecondsAgo(tenYears));
        assertEquals("۱۰۰ سال پیش", PersianRelativeTimeFormatter.formatSecondsAgo(hundredYears));
    }

    @Test
    @DisplayName("Should handle very small time differences consistently")
    void testVerySmallTimeDifferences() {
        for (int i = 0; i < 10; i++) {
            assertEquals("هم‌اکنون", PersianRelativeTimeFormatter.formatSecondsAgo(i));
        }
    }

    @Test
    @DisplayName("Should maintain consistency across different formatting styles")
    void testFormattingStyleConsistency() {
        long twoHours = 7200;
        
        String numeric = PersianRelativeTimeFormatter.formatSecondsAgo(twoHours, 
                                     PersianRelativeTimeFormatter.FormatStyle.NUMERIC);
        String words = PersianRelativeTimeFormatter.formatSecondsAgo(twoHours, 
                                   PersianRelativeTimeFormatter.FormatStyle.WORDS);
        String shortForm = PersianRelativeTimeFormatter.formatSecondsAgo(twoHours, 
                                       PersianRelativeTimeFormatter.FormatStyle.SHORT);
        String fuzzy = PersianRelativeTimeFormatter.formatSecondsAgo(twoHours, 
                                   PersianRelativeTimeFormatter.FormatStyle.FUZZY);
        
        assertTrue(numeric.contains("۲"));
        assertTrue(words.contains("دو"));
        assertTrue(shortForm.contains("۲س"));
        assertTrue(fuzzy.contains("چند") || fuzzy.contains("۲"));
        
        // All should end with "پیش"
        assertTrue(numeric.endsWith("پیش"));
        assertTrue(words.endsWith("پیش"));
        assertTrue(shortForm.endsWith("پیش"));
        assertTrue(fuzzy.endsWith("پیش"));
    }
}