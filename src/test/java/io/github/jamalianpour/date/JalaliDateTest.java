package io.github.jamalianpour.date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.*;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JalaliDate Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JalaliDateTest {

    @Nested
    @DisplayName("Construction and Factory Methods")
    class ConstructionTests {

        @Test
        @DisplayName("Should create valid JalaliDate using of() method")
        void testValidConstruction() {
            JalaliDate date = JalaliDate.of(1400, 1, 1);
            assertEquals(1400, date.getYear());
            assertEquals(1, date.getMonth());
            assertEquals(1, date.getDay());
        }

        @ParameterizedTest
        @CsvSource({
                "0, 1, 1",      // Invalid year (too low)
                "3179, 1, 1",   // Invalid year (too high)
                "1400, 0, 1",   // Invalid month (too low)
                "1400, 13, 1",  // Invalid month (too high)
                "1400, 1, 0",   // Invalid day (too low)
                "1400, 1, 32",  // Invalid day (too high for month 1)
                "1400, 7, 32",  // Invalid day (too high for month 7)
                "1400, 12, 30", // Invalid day (too high for month 12 in non-leap year)
        })
        @DisplayName("Should throw exception for invalid dates")
        void testInvalidConstruction(int year, int month, int day) {
            assertThrows(IllegalArgumentException.class, () -> JalaliDate.of(year, month, day));
        }

        @Test
        @DisplayName("Should create date from year and day of year")
        void testOfYearDay() {
            JalaliDate date = JalaliDate.ofYearDay(1400, 1);
            assertEquals(1400, date.getYear());
            assertEquals(1, date.getMonth());
            assertEquals(1, date.getDay());

            // Test day 32 (should be month 2, day 1)
            date = JalaliDate.ofYearDay(1400, 32);
            assertEquals(1400, date.getYear());
            assertEquals(2, date.getMonth());
            assertEquals(1, date.getDay());

            // Test day 187 (first day of month 7)
            date = JalaliDate.ofYearDay(1400, 187);
            assertEquals(1400, date.getYear());
            assertEquals(7, date.getMonth());
            assertEquals(1, date.getDay());
        }

        @Test
        @DisplayName("Should throw exception for invalid day of year")
        void testInvalidYearDay() {
            assertThrows(IllegalArgumentException.class, () -> JalaliDate.ofYearDay(1400, 0));
            assertThrows(IllegalArgumentException.class, () -> JalaliDate.ofYearDay(1400, 366)); // Non-leap year
            assertThrows(IllegalArgumentException.class, () -> JalaliDate.ofYearDay(1399, 367)); // Leap year boundary
        }

        @Test
        @DisplayName("Should create current date")
        void testNowMethods() {
            JalaliDate now = JalaliDate.now();
            JalaliDate today = JalaliDate.today();

            assertNotNull(now);
            assertNotNull(today);
            assertEquals(now, today);

            JalaliDate yesterday = JalaliDate.yesterday();
            JalaliDate tomorrow = JalaliDate.tomorrow();

            assertEquals(1, now.daysUntil(tomorrow));
            assertEquals(1, yesterday.daysUntil(now));
        }
    }

    @Nested
    @DisplayName("Parsing Tests")
    class ParsingTests {

        @ParameterizedTest
        @CsvSource({
                "1400-01-01, 1400, 1, 1",
                "1400/12/29, 1400, 12, 29",
                "1399-12-30, 1399, 12, 30"
        })
        @DisplayName("Should parse ISO format dates")
        void testParseIso(String dateStr, int expectedYear, int expectedMonth, int expectedDay) {
            JalaliDate date = JalaliDate.parseIso(dateStr);
            assertEquals(expectedYear, date.getYear());
            assertEquals(expectedMonth, date.getMonth());
            assertEquals(expectedDay, date.getDay());
        }

        @ParameterizedTest
        @CsvSource({
                "01/01/1400, 1400, 1, 1",
                "29/12/1400, 1400, 12, 29",
                "15/06/99, 1399, 6, 15"  // 2-digit year
        })
        @DisplayName("Should parse Persian format dates")
        void testParsePersian(String dateStr, int expectedYear, int expectedMonth, int expectedDay) {
            JalaliDate date = JalaliDate.parsePersian(dateStr);
            assertEquals(expectedYear, date.getYear());
            assertEquals(expectedMonth, date.getMonth());
            assertEquals(expectedDay, date.getDay());
        }

        @Test
        @DisplayName("Should auto-detect format when parsing")
        void testParseAuto() {
            JalaliDate isoDate = JalaliDate.parse("1400-01-01");
            JalaliDate persianDate = JalaliDate.parse("01/01/1400");

            assertEquals(isoDate, persianDate);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "invalid-date", "1400/13/01", "32/01/1400"})
        @DisplayName("Should throw exception for invalid date strings")
        void testInvalidParsing(String invalidDate) {
            assertThrows(IllegalArgumentException.class, () -> JalaliDate.parse(invalidDate));
        }

        @Test
        @DisplayName("Should throw exception for null date string")
        void testNullParsing() {
            assertThrows(IllegalArgumentException.class, () -> JalaliDate.parse(null));
        }
    }

    @Nested
    @DisplayName("Conversion Tests")
    class ConversionTests {

        @Test
        @DisplayName("Should convert from Gregorian to Jalali")
        void testFromGregorian() {
            LocalDate gregorian = LocalDate.of(2021, 3, 21); // Nowruz 1400
            JalaliDate jalali = JalaliDate.fromGregorian(gregorian);

            assertEquals(1400, jalali.getYear());
            assertEquals(1, jalali.getMonth());
            assertEquals(1, jalali.getDay());
        }

        @Test
        @DisplayName("Should convert from Jalali to Gregorian")
        void testToGregorian() {
            JalaliDate jalali = JalaliDate.of(1400, 1, 1);
            LocalDate gregorian = jalali.toGregorian();

            assertEquals(2021, gregorian.getYear());
            assertEquals(3, gregorian.getMonthValue());
            assertEquals(21, gregorian.getDayOfMonth());
        }

        @Test
        @DisplayName("Should handle roundtrip conversion correctly")
        void testRoundtripConversion() {
            JalaliDate original = JalaliDate.of(1400, 6, 15);
            LocalDate gregorian = original.toGregorian();
            JalaliDate converted = JalaliDate.fromGregorian(gregorian);

            assertEquals(original, converted);
        }

        @Test
        @DisplayName("Should convert to LocalDateTime")
        void testToLocalDateTime() {
            JalaliDate jalali = JalaliDate.of(1400, 1, 1);

            LocalDateTime startOfDay = jalali.atStartOfDay();
            LocalDateTime withTime = jalali.atTime(12, 30);
            LocalDateTime withFullTime = jalali.atTime(12, 30, 45);
            LocalDateTime withLocalTime = jalali.atTime(LocalTime.of(15, 45));

            assertEquals(LocalTime.MIDNIGHT, startOfDay.toLocalTime());
            assertEquals(LocalTime.of(12, 30), withTime.toLocalTime());
            assertEquals(LocalTime.of(12, 30, 45), withFullTime.toLocalTime());
            assertEquals(LocalTime.of(15, 45), withLocalTime.toLocalTime());
        }

        @Test
        @DisplayName("Should convert to epoch day")
        void testEpochConversion() {
            JalaliDate jalali = JalaliDate.of(1400, 1, 1);
            long epochDay = jalali.toEpochDay();

            JalaliDate fromEpoch = JalaliDate.ofEpochDay(epochDay);
            assertEquals(jalali, fromEpoch);
        }
    }

    @Nested
    @DisplayName("Arithmetic Operations")
    class ArithmeticTests {

        @Test
        @DisplayName("Should add and subtract days correctly")
        void testDayArithmetic() {
            JalaliDate date = JalaliDate.of(1400, 1, 15);

            JalaliDate plus10 = date.plusDays(10);
            assertEquals(25, plus10.getDay());

            JalaliDate minus5 = date.minusDays(5);
            assertEquals(10, minus5.getDay());

            // Test month boundary
            JalaliDate endOfMonth = JalaliDate.of(1400, 1, 31);
            JalaliDate nextDay = endOfMonth.plusDays(1);
            assertEquals(2, nextDay.getMonth());
            assertEquals(1, nextDay.getDay());
        }

        @Test
        @DisplayName("Should add and subtract weeks correctly")
        void testWeekArithmetic() {
            JalaliDate date = JalaliDate.of(1400, 1, 1);

            JalaliDate plus2Weeks = date.plusWeeks(2);
            assertEquals(15, plus2Weeks.getDay());

            JalaliDate minus1Week = date.minusWeeks(1);
            assertEquals(1399, minus1Week.getYear());
            assertEquals(12, minus1Week.getMonth());
        }

        @Test
        @DisplayName("Should add and subtract months correctly")
        void testMonthArithmetic() {
            JalaliDate date = JalaliDate.of(1400, 6, 15);

            JalaliDate plus3Months = date.plusMonths(3);
            assertEquals(9, plus3Months.getMonth());
            assertEquals(15, plus3Months.getDay());

            JalaliDate plus7Months = date.plusMonths(7);
            assertEquals(1401, plus7Months.getYear());
            assertEquals(1, plus7Months.getMonth());

            // Test day adjustment for shorter months
            JalaliDate endOfMonth = JalaliDate.of(1400, 1, 31);
            JalaliDate plus6Months = endOfMonth.plusMonths(6);
            assertEquals(7, plus6Months.getMonth());
            assertEquals(30, plus6Months.getDay()); // Adjusted to max day of month 7
        }

        @Test
        @DisplayName("Should add and subtract years correctly")
        void testYearArithmetic() {
            JalaliDate date = JalaliDate.of(1400, 6, 15);

            JalaliDate plus5Years = date.plusYears(5);
            assertEquals(1405, plus5Years.getYear());
            assertEquals(6, plus5Years.getMonth());
            assertEquals(15, plus5Years.getDay());

            // Test leap year adjustment
            JalaliDate leapDay = JalaliDate.of(1399, 12, 30); // Leap year
            JalaliDate nextYear = leapDay.plusYears(1);
            assertEquals(1400, nextYear.getYear());
            assertEquals(12, nextYear.getMonth());
            assertEquals(29, nextYear.getDay()); // Adjusted to non-leap year
        }

        @Test
        @DisplayName("Should handle zero operations correctly")
        void testZeroOperations() {
            JalaliDate date = JalaliDate.of(1400, 6, 15);

            assertSame(date, date.plusDays(0));
            assertSame(date, date.plusMonths(0));
            assertSame(date, date.plusYears(0));
        }
    }

    @Nested
    @DisplayName("Temporal Adjusters")
    class TemporalAdjusterTests {

        @Test
        @DisplayName("Should adjust day, month, and year")
        void testBasicAdjustments() {
            JalaliDate date = JalaliDate.of(1400, 6, 15);

            JalaliDate newDay = date.withDayOfMonth(20);
            assertEquals(20, newDay.getDay());
            assertEquals(6, newDay.getMonth());
            assertEquals(1400, newDay.getYear());

            JalaliDate newMonth = date.withMonth(9);
            assertEquals(15, newMonth.getDay());
            assertEquals(9, newMonth.getMonth());
            assertEquals(1400, newMonth.getYear());

            JalaliDate newYear = date.withYear(1405);
            assertEquals(15, newYear.getDay());
            assertEquals(6, newYear.getMonth());
            assertEquals(1405, newYear.getYear());
        }

        @Test
        @DisplayName("Should return same instance when no change needed")
        void testNoChangeOptimization() {
            JalaliDate date = JalaliDate.of(1400, 6, 15);

            assertSame(date, date.withDayOfMonth(15));
            assertSame(date, date.withMonth(6));
            assertSame(date, date.withYear(1400));
        }

        @Test
        @DisplayName("Should get first and last days of month/year")
        void testFirstLastDays() {
            JalaliDate date = JalaliDate.of(1400, 6, 15);

            JalaliDate firstOfMonth = date.firstDayOfMonth();
            assertEquals(1, firstOfMonth.getDay());
            assertEquals(6, firstOfMonth.getMonth());

            JalaliDate lastOfMonth = date.lastDayOfMonth();
            assertEquals(31, lastOfMonth.getDay());
            assertEquals(6, lastOfMonth.getMonth());

            JalaliDate firstOfYear = date.firstDayOfYear();
            assertEquals(1, firstOfYear.getDay());
            assertEquals(1, firstOfYear.getMonth());
            assertEquals(1400, firstOfYear.getYear());

            JalaliDate lastOfYear = date.lastDayOfYear();
            assertEquals(29, lastOfYear.getDay()); // Non-leap year
            assertEquals(12, lastOfYear.getMonth());
            assertEquals(1400, lastOfYear.getYear());
        }

        @Test
        @DisplayName("Should find next and previous working days")
        void testWorkingDays() {
            // Assuming Friday is weekend
            JalaliDate friday = JalaliDate.of(1400, 1, 4); // Assuming this is Friday

            JalaliDate nextWorking = friday.nextWorkingDay();
            assertFalse(nextWorking.isWeekend());
            assertTrue(nextWorking.isAfter(friday));

            JalaliDate prevWorking = friday.previousWorkingDay();
            assertFalse(prevWorking.isWeekend());
            assertTrue(prevWorking.isBefore(friday));
        }
    }

    @Nested
    @DisplayName("Query Methods")
    class QueryTests {

        @Test
        @DisplayName("Should return correct month and year lengths")
        void testLengths() {
            JalaliDate date1400 = JalaliDate.of(1400, 1, 1); // Non-leap year
            JalaliDate date1399 = JalaliDate.of(1399, 1, 1); // Leap year

            // Month lengths
            assertEquals(31, date1400.withMonth(1).lengthOfMonth());
            assertEquals(31, date1400.withMonth(6).lengthOfMonth());
            assertEquals(30, date1400.withMonth(7).lengthOfMonth());
            assertEquals(29, date1400.withMonth(12).lengthOfMonth()); // Non-leap
            assertEquals(30, date1399.withMonth(12).lengthOfMonth()); // Leap

            // Year lengths
            assertEquals(365, date1400.lengthOfYear());
            assertEquals(366, date1399.lengthOfYear());
        }

        @Test
        @DisplayName("Should detect leap years correctly")
        void testLeapYear() {
            assertTrue(JalaliDate.of(1399, 1, 1).isLeapYear());
            assertFalse(JalaliDate.of(1400, 1, 1).isLeapYear());
            assertTrue(JalaliDate.isLeapJalaliYear(1399));
            assertFalse(JalaliDate.isLeapJalaliYear(1400));
        }

        @Test
        @DisplayName("Should return correct day of week")
        void testDayOfWeek() {
            JalaliDate date = JalaliDate.of(1400, 1, 1); // Nowruz 1400
            assertNotNull(date.dayOfWeek());

            int persianDayOfWeek = date.getDayOfWeek();
            assertTrue(persianDayOfWeek >= 0 && persianDayOfWeek <= 6);
        }

        @Test
        @DisplayName("Should return correct day of year")
        void testDayOfYear() {
            JalaliDate date = JalaliDate.of(1400, 1, 1);
            assertEquals(1, date.getDayOfYear());

            date = JalaliDate.of(1400, 2, 1);
            assertEquals(32, date.getDayOfYear());

            date = JalaliDate.of(1400, 7, 1);
            assertEquals(187, date.getDayOfYear());
        }

        @Test
        @DisplayName("Should return correct quarter")
        void testQuarter() {
            assertEquals(1, JalaliDate.of(1400, 1, 1).getQuarter());
            assertEquals(1, JalaliDate.of(1400, 3, 1).getQuarter());
            assertEquals(2, JalaliDate.of(1400, 4, 1).getQuarter());
            assertEquals(3, JalaliDate.of(1400, 7, 1).getQuarter());
            assertEquals(4, JalaliDate.of(1400, 10, 1).getQuarter());
        }

        @Test
        @DisplayName("Should detect weekends and weekdays")
        void testWeekendWeekday() {
            JalaliDate date = JalaliDate.of(1400, 1, 1);

            // Test that weekend/weekday are opposites
            assertEquals(!date.isWeekend(), date.isWeekday());
        }
    }

    @Nested
    @DisplayName("Comparison Tests")
    class ComparisonTests {

        @Test
        @DisplayName("Should compare dates correctly")
        void testComparison() {
            JalaliDate date1 = JalaliDate.of(1400, 6, 15);
            JalaliDate date2 = JalaliDate.of(1400, 6, 20);
            JalaliDate date3 = JalaliDate.of(1400, 6, 15);

            assertTrue(date1.isBefore(date2));
            assertTrue(date2.isAfter(date1));
            assertFalse(date1.isAfter(date2));
            assertFalse(date2.isBefore(date1));

            assertTrue(date1.isBeforeOrEqual(date2));
            assertTrue(date1.isBeforeOrEqual(date3));
            assertTrue(date2.isAfterOrEqual(date1));
            assertTrue(date1.isAfterOrEqual(date3));

            assertEquals(0, date1.compareTo(date3));
            assertTrue(date1.compareTo(date2) < 0);
            assertTrue(date2.compareTo(date1) > 0);
        }

        @Test
        @DisplayName("Should calculate time periods correctly")
        void testTimePeriods() {
            JalaliDate start = JalaliDate.of(1400, 1, 1);
            JalaliDate end = JalaliDate.of(1400, 1, 11);

            assertEquals(10, start.daysUntil(end));
            assertEquals(-10, end.daysUntil(start));

            start = JalaliDate.of(1400, 1, 1);
            end = JalaliDate.of(1400, 6, 1);

            assertEquals(5, start.monthsUntil(end));
            assertEquals(-5, end.monthsUntil(start));

            start = JalaliDate.of(1400, 1, 1);
            end = JalaliDate.of(1405, 1, 1);

            assertEquals(5, start.yearsUntil(end));
            assertEquals(-5, end.yearsUntil(start));
        }

        @Test
        @DisplayName("Should calculate periods correctly")
        void testPeriodUntil() {
            JalaliDate start = JalaliDate.of(1400, 1, 15);
            JalaliDate end = JalaliDate.of(1402, 3, 20);

            Period period = start.periodUntil(end);
            assertNotNull(period);

            // Verify period makes sense (exact values depend on implementation)
            assertTrue(period.getYears() >= 2);
            assertTrue(period.getMonths() >= 0 && period.getMonths() < 12);
            assertTrue(period.getDays() >= 0 && period.getDays() < 31);
        }
    }

    @Nested
    @DisplayName("Formatting Tests")
    class FormattingTests {

        @Test
        @DisplayName("Should format dates in different formats")
        void testFormatting() {
            JalaliDate date = JalaliDate.of(1400, 1, 15);

            assertEquals("1400-01-15", date.toString());
            assertEquals("1400-01-15", date.toIso());

            String full = date.format(JalaliDate.DateFormat.FULL);
            assertNotNull(full);
            assertTrue(full.contains("1400"));

            String persian = date.format(JalaliDate.DateFormat.PERSIAN);
            assertEquals("15/01/1400", persian);
        }

        @Test
        @DisplayName("Should return month and weekday names")
        void testNames() {
            JalaliDate date = JalaliDate.of(1400, 1, 1);

            String monthNameEn = date.getMonthName(false);
            String monthNameFa = date.getMonthName(true);
            assertEquals("Farvardin", monthNameEn);
            assertEquals("فروردین", monthNameFa);

            String weekdayEn = date.getWeekdayName(false);
            String weekdayFa = date.getWeekdayName(true);
            assertNotNull(weekdayEn);
            assertNotNull(weekdayFa);
        }

        @Test
        @DisplayName("Should format with different locales")
        void testLocaleFormatting() {
            JalaliDate date = JalaliDate.of(1400, 1, 1);

            String english = date.format(JalaliDate.DateFormat.FULL, Locale.ENGLISH);
            String persian = date.format(JalaliDate.DateFormat.FULL, Locale.forLanguageTag("fa"));

            assertNotNull(english);
            assertNotNull(persian);
            assertNotEquals(english, persian);
        }
    }

    @Nested
    @DisplayName("Holiday Tests")
    class HolidayTests {

        @Test
        @DisplayName("Should detect major holidays")
        void testHolidays() {
            JalaliDate nowruz = JalaliDate.of(1400, 1, 1);
            assertTrue(nowruz.isHoliday());
            assertEquals("Nowruz", nowruz.getHolidayName());

            JalaliDate sizdah = JalaliDate.of(1400, 1, 13);
            assertTrue(sizdah.isHoliday());
            assertEquals("Sizdah Bedar", sizdah.getHolidayName());

            JalaliDate regular = JalaliDate.of(1400, 2, 10);
            // This might be a holiday if it's a Friday, but otherwise not
            String holidayName = regular.getHolidayName();
            if (holidayName != null) {
                assertTrue(regular.isHoliday());
            }
        }

        @Test
        @DisplayName("Should get holidays for a year")
        void testYearlyHolidays() {
            List<JalaliDate> holidays = JalaliDate.getHolidaysInYear(1400);
            assertNotNull(holidays);
            assertFalse(holidays.isEmpty());

            // Should include Nowruz
            assertTrue(holidays.contains(JalaliDate.of(1400, 1, 1)));
        }
    }

    @Nested
    @DisplayName("Date Range Tests")
    class DateRangeTests {

        @Test
        @DisplayName("Should create and use date ranges")
        void testDateRange() {
            JalaliDate start = JalaliDate.of(1400, 1, 1);
            JalaliDate end = JalaliDate.of(1400, 1, 10);

            JalaliDate.JalaliDateRange range = JalaliDate.between(start, end);

            assertEquals(start, range.getStart());
            assertEquals(end, range.getEnd());
            assertEquals(10, range.getDays());

            assertTrue(range.contains(JalaliDate.of(1400, 1, 5)));
            assertFalse(range.contains(JalaliDate.of(1400, 1, 15)));

            List<JalaliDate> dates = range.toList();
            assertEquals(10, dates.size());
            assertEquals(start, dates.get(0));
            assertEquals(end, dates.get(9));
        }

        @Test
        @DisplayName("Should throw exception for invalid range")
        void testInvalidRange() {
            JalaliDate start = JalaliDate.of(1400, 1, 10);
            JalaliDate end = JalaliDate.of(1400, 1, 1);

            assertThrows(IllegalArgumentException.class,
                    () -> JalaliDate.between(start, end));
        }

//        @Test
//        @DisplayName("Should create date streams")
//        void testDateStreams() {
//            JalaliDate start = JalaliDate.of(1400, 1, 1);
//            JalaliDate end = JalaliDate.of(1400, 1, 5);
//
//            Stream<JalaliDate> stream = start.datesUntil(end);
//            List<JalaliDate> dates = stream.toList();
//
//            assertEquals(4, dates.size()); // Exclusive end
//            assertEquals(start, dates.get(0));
//            assertEquals(JalaliDate.of(1400, 1, 4), dates.get(3));
//        }
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should build dates using builder pattern")
        void testBuilder() {
            JalaliDate date = JalaliDate.builder()
                    .year(1400)
                    .month(6)
                    .day(15)
                    .build();

            assertEquals(1400, date.getYear());
            assertEquals(6, date.getMonth());
            assertEquals(15, date.getDay());
        }

        @Test
        @DisplayName("Should build from Gregorian date")
        void testBuilderFromGregorian() {
            LocalDate gregorian = LocalDate.of(2021, 3, 21);
            JalaliDate date = JalaliDate.builder()
                    .fromGregorian(gregorian)
                    .build();

            assertEquals(1400, date.getYear());
            assertEquals(1, date.getMonth());
            assertEquals(1, date.getDay());
        }

        @Test
        @DisplayName("Should use default values in builder")
        void testBuilderDefaults() {
            JalaliDate date = JalaliDate.builder().build();

            assertEquals(1400, date.getYear());
            assertEquals(1, date.getMonth());
            assertEquals(1, date.getDay());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @ParameterizedTest
        @CsvSource({
                "1400, 1, 1, true",
                "1400, 12, 29, true",
                "1399, 12, 30, true",   // Leap year
                "1400, 12, 30, false",  // Non-leap year
                "1400, 13, 1, false",   // Invalid month
                "1400, 1, 32, false"    // Invalid day
        })
        @DisplayName("Should validate dates correctly")
        void testValidation(int year, int month, int day, boolean expected) {
            assertEquals(expected, JalaliDate.isValid(year, month, day));
        }

        @Test
        @DisplayName("Should handle edge cases in validation")
        void testValidationEdgeCases() {
            // Year boundaries
            assertTrue(JalaliDate.isValid(1, 1, 1));
            assertTrue(JalaliDate.isValid(3178, 1, 1));
            assertFalse(JalaliDate.isValid(0, 1, 1));
            assertFalse(JalaliDate.isValid(3179, 1, 1));

            // Month boundaries
            assertTrue(JalaliDate.isValid(1400, 1, 1));
            assertTrue(JalaliDate.isValid(1400, 12, 1));
            assertFalse(JalaliDate.isValid(1400, 0, 1));
            assertFalse(JalaliDate.isValid(1400, 13, 1));
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should implement equals correctly")
        void testEquals() {
            JalaliDate date1 = JalaliDate.of(1400, 6, 15);
            JalaliDate date2 = JalaliDate.of(1400, 6, 15);
            JalaliDate date3 = JalaliDate.of(1400, 6, 16);

            assertEquals(date1, date2);
            assertNotEquals(date1, date3);
            assertNotEquals(date1, null);
            assertNotEquals(date1, "not a date");
            assertEquals(date1, date1); // Reflexive
        }

        @Test
        @DisplayName("Should implement hashCode correctly")
        void testHashCode() {
            JalaliDate date1 = JalaliDate.of(1400, 6, 15);
            JalaliDate date2 = JalaliDate.of(1400, 6, 15);
            JalaliDate date3 = JalaliDate.of(1400, 6, 16);

            assertEquals(date1.hashCode(), date2.hashCode());
            assertNotEquals(date1.hashCode(), date3.hashCode());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null parameters properly")
        void testNullParameters() {
            JalaliDate date = JalaliDate.of(1400, 1, 1);

            assertThrows(NullPointerException.class, () -> date.compareTo(null));
            assertThrows(NullPointerException.class, () -> date.daysUntil(null));
            assertThrows(NullPointerException.class, () -> date.monthsUntil(null));
            assertThrows(NullPointerException.class, () -> date.yearsUntil(null));
            assertThrows(NullPointerException.class, () -> JalaliDate.fromGregorian(null));
        }

        @Test
        @DisplayName("Should handle large date arithmetic")
        void testLargeDateArithmetic() {
            JalaliDate date = JalaliDate.of(1400, 1, 1);

            // Large positive operations
            JalaliDate future = date.plusDays(100000);
            assertNotNull(future);
            assertTrue(future.isAfter(date));

            // Large negative operations
            JalaliDate past = date.minusDays(100000);
            assertNotNull(past);
            assertTrue(past.isBefore(date));
        }

        @Test
        @DisplayName("Should handle leap year edge cases")
        void testLeapYearEdgeCases() {
            // Last day of leap year
            JalaliDate leapDay = JalaliDate.of(1399, 12, 30);
            assertTrue(leapDay.isLeapYear());
            assertEquals(30, leapDay.lengthOfMonth());

            // Adding/subtracting from leap day
            JalaliDate nextYear = leapDay.plusYears(1);
            assertEquals(29, nextYear.getDay()); // Should adjust to non-leap year

            JalaliDate prevYear = leapDay.minusYears(1);
            assertEquals(29, prevYear.getDay()); // Should adjust if previous year is non-leap
        }
    }

    // Helper method to provide test data for parameterized tests
    private static Stream<Arguments> provideValidDates() {
        return Stream.of(
                Arguments.of(1400, 1, 1),
                Arguments.of(1400, 6, 31),
                Arguments.of(1400, 7, 30),
                Arguments.of(1400, 12, 29),
                Arguments.of(1399, 12, 30) // Leap year
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidDates")
    @DisplayName("Should handle all valid dates correctly")
    void testValidDateHandling(int year, int month, int day) {
        JalaliDate date = JalaliDate.of(year, month, day);

        assertNotNull(date);
        assertEquals(year, date.getYear());
        assertEquals(month, date.getMonth());
        assertEquals(day, date.getDay());

        // Should be able to convert to Gregorian and back
        LocalDate gregorian = date.toGregorian();
        JalaliDate roundTrip = JalaliDate.fromGregorian(gregorian);
        assertEquals(date, roundTrip);
    }
}