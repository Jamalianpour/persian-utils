package io.github.jamalianpour.date;

import java.io.Serializable;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * JalaliDate - Full-featured Jalali (Persian/Shamsi) Date Utility
 *
 * Enhanced immutable value type with comprehensive features for working with Jalali dates.
 * Includes conversion, formatting, parsing, holidays, ranges, and temporal operations.
 */
public final class JalaliDate implements Comparable<JalaliDate>, Serializable {

    private static final long serialVersionUID = 1L;

    private final int year;
    private final int month; // 1..12
    private final int day;   // 1..31

    // Persian month names
    private static final String[] MONTH_NAMES_FA = {
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    };

    private static final String[] MONTH_NAMES_EN = {
            "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar",
            "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand"
    };

    // Persian weekday names
    private static final String[] WEEKDAY_NAMES_FA = {
            "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    };

    private static final String[] WEEKDAY_NAMES_EN = {
            "Shanbe", "Yekshanbe", "Doshanbe", "Seshanbe", "Chaharshanbe", "Panjshanbe", "Jomeh"
    };

    // Patterns for parsing
    private static final Pattern ISO_PATTERN = Pattern.compile("(\\d{4})[-/](\\d{1,2})[-/](\\d{1,2})");
    private static final Pattern PERSIAN_PATTERN = Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{2,4})");

    // ------------------------
    // Constructors and Factory Methods
    // ------------------------

    private JalaliDate(int year, int month, int day) {
        validate(year, month, day);
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * Creates a new JalaliDate instance with the specified year, month, and day.
     *
     * @param year  the year in Jalali calendar (e.g., 1400)
     * @param month the month of year (1-12), where 1 = Farvardin and 12 = Esfand
     * @param day   the day of month (1-31 depending on the month)
     * @return a new JalaliDate instance
     * @throws IllegalArgumentException if any parameter is out of valid range
     */
    public static JalaliDate of(int year, int month, int day) {
        return new JalaliDate(year, month, day);
    }

    /**
     * Creates a JalaliDate from a year and day-of-year.
     *
     * @param year      the year in Jalali calendar
     * @param dayOfYear the day-of-year (1-365 or 1-366 for leap years)
     * @return a new JalaliDate instance
     * @throws IllegalArgumentException if dayOfYear is out of valid range for the year
     */
    public static JalaliDate ofYearDay(int year, int dayOfYear) {
        if (dayOfYear < 1 || dayOfYear > (isLeapJalaliYear(year) ? 366 : 365)) {
            throw new IllegalArgumentException("Invalid day of year: " + dayOfYear);
        }

        int month, day;
        if (dayOfYear <= 186) {
            month = (dayOfYear - 1) / 31 + 1;
            day = (dayOfYear - 1) % 31 + 1;
        } else {
            dayOfYear -= 186;
            month = (dayOfYear - 1) / 30 + 7;
            day = (dayOfYear - 1) % 30 + 1;
        }
        return of(year, month, day);
    }

    // ------------------------
    // Parsing Methods
    // ------------------------

    /**
     * Parses a Jalali date string using automatic format detection.
     * Supports both ISO format (1400-01-01) and Persian format (01/01/1400).
     *
     * @param text the date string to parse
     * @return a JalaliDate instance
     * @throws IllegalArgumentException if the text cannot be parsed
     */
    public static JalaliDate parse(String text) {
        return parse(text, DateFormat.AUTO);
    }

    /**
     * Parses a Jalali date string using the specified format.
     * Supports both ISO format (1400-01-01) and Persian format (01/01/1400).
     *
     * @param text the date string to parse
     * @param format the format of the date string
     * @return a JalaliDate instance
     * @throws IllegalArgumentException if the text cannot be parsed
     */
    public static JalaliDate parse(String text, DateFormat format) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }

        text = text.trim();

        switch (format) {
            case ISO:
                return parseIso(text);
            case PERSIAN:
                return parsePersian(text);
            case AUTO:
                if (text.contains("-")) return parseIso(text);
                else return parsePersian(text);
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    /**
     * Parses a Jalali date string in ISO format (yyyy-mm-dd).
     *
     * @param s the Jalali date string to parse
     * @return a JalaliDate instance
     * @throws IllegalArgumentException if the date string is invalid
     */
    public static JalaliDate parseIso(String s) {
        var m = ISO_PATTERN.matcher(s.trim());
        if (!m.matches()) throw new IllegalArgumentException("Invalid ISO date format: " + s);
        int y = Integer.parseInt(m.group(1));
        int mo = Integer.parseInt(m.group(2));
        int d = Integer.parseInt(m.group(3));
        return of(y, mo, d);
    }

    /**
     * Parses a Jalali date string in Persian format (dd/mm/yyyy).
     *
     * @param s the Jalali date string to parse
     * @return a JalaliDate instance
     * @throws IllegalArgumentException if the date string is invalid
     *
     * <p>Example: "01/01/1400" parses to a JalaliDate representing the 1st of Farvardin, 1400.
     */
    public static JalaliDate parsePersian(String s) {
        var m = PERSIAN_PATTERN.matcher(s.trim());
        if (!m.matches()) throw new IllegalArgumentException("Invalid Persian date format: " + s);
        int d = Integer.parseInt(m.group(1));
        int mo = Integer.parseInt(m.group(2));
        int y = Integer.parseInt(m.group(3));
        if (y < 100) y += 1300; // Assume 13xx for 2-digit years
        return of(y, mo, d);
    }

    // ------------------------
    // Current Date Methods
    // ------------------------

    /**
     * Gets the current Jalali date from the system clock in the default time-zone.
     *
     * @return the current Jalali date
     */
    public static JalaliDate now() {
        return fromGregorian(LocalDate.now());
    }

    /**
     * Gets the current Jalali date from the system clock in the specified time-zone.
     *
     * @param zone the zone to use, not null
     * @return the current Jalali date
     * @throws NullPointerException if zone is null
     */
    public static JalaliDate now(ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        return fromGregorian(LocalDate.now(zone));
    }

    /**
     * Gets the current Jalali date from the system clock in the specified time-zone.
     *
     * @param clock the clock to use, not null
     * @return the current Jalali date
     * @throws NullPointerException if clock is null
     */
    public static JalaliDate now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        return fromGregorian(LocalDate.now(clock));
    }

    /**
     * Gets the current Jalali date from the system clock in the default time-zone.
     * This is a convenience method for {@link #now()}.
     *
     * @return the current Jalali date
     */
    public static JalaliDate today() {
        return now();
    }

    /**
     * Gets the Jalali date for yesterday from the system clock in the default time-zone.
     *
     * @return the Jalali date for yesterday
     */
    public static JalaliDate yesterday() {
        return now().minusDays(1);
    }

    /**
     * Gets the Jalali date for tomorrow from the system clock in the default time-zone.
     *
     * @return the Jalali date for tomorrow
     */
    public static JalaliDate tomorrow() {
        return now().plusDays(1);
    }

    // ------------------------
    // Conversion Methods
    // ------------------------

    /**
     * Converts a Gregorian LocalDate to JalaliDate.
     *
     * @param date the Gregorian date to convert, not null
     * @return the equivalent JalaliDate
     * @throws NullPointerException if date is null
     */
    public static JalaliDate fromGregorian(LocalDate date) {
        Objects.requireNonNull(date, "date");
        int[] j = g2j(g2d(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        return new JalaliDate(j[0], j[1], j[2]);
    }

    /**
     * Converts a Gregorian date to JalaliDate.
     *
     * @param year  the year in Gregorian calendar
     * @param month the month of year (1-12), where 1 = January and 12 = December
     * @param day   the day of month (1-31 depending on the month)
     * @return the equivalent JalaliDate
     */
    public static JalaliDate fromGregorian(int year, int month, int day) {
        return fromGregorian(LocalDate.of(year, month, day));
    }

    /**
     * Converts this JalaliDate to a Gregorian LocalDate.
     *
     * @return the equivalent Gregorian LocalDate
     */
    public LocalDate toGregorian() {
        int jdn = j2d(this.year, this.month, this.day);
        int[] g = d2g(jdn);
        return LocalDate.of(g[0], g[1], g[2]);
    }

    /**
     * Returns a LocalDateTime formed from this date at the start of day.
     *
     * @return a LocalDateTime formed from this date at the start of day
     */
    public LocalDateTime atStartOfDay() {
        return toGregorian().atStartOfDay();
    }

    /**
     * Returns a LocalDateTime formed from this date at the specified time.
     *
     * @param hour   the hour-of-day (0-23)
     * @param minute the minute-of-hour (0-59)
     * @return a LocalDateTime formed from this date at the specified time
     */
    public LocalDateTime atTime(int hour, int minute) {
        return toGregorian().atTime(hour, minute);
    }

    /**
     * Returns a LocalDateTime formed from this date at the specified time.
     *
     * @param hour   the hour-of-day (0-23)
     * @param minute the minute-of-hour (0-59)
     * @param second the second-of-minute (0-59)
     * @return a LocalDateTime formed from this date at the specified time
     * @throws DateTimeException if the specified time is invalid
     */
    public LocalDateTime atTime(int hour, int minute, int second) {
        return toGregorian().atTime(hour, minute, second);
    }

    /**
     * Returns a LocalDateTime formed from this date at the specified time, using the system zone.
     *
     * @param time the time of day, not null
     * @return a LocalDateTime formed from this date at the specified time, not null
     * @throws NullPointerException if time is null
     */
    public LocalDateTime atTime(LocalTime time) {
        return toGregorian().atTime(time);
    }

    /**
     * Returns a ZonedDateTime formed from this date at the start of the day, using the specified zone.
     *
     * @param zone the zone to use, not null
     * @return a ZonedDateTime formed from this date at the start of the day, not null
     * @throws NullPointerException if zone is null
     */
    public ZonedDateTime atStartOfDay(ZoneId zone) {
        return toGregorian().atStartOfDay(zone);
    }

    // ------------------------
    // Epoch Methods
    // ------------------------

    /**
     * Returns the number of days from the epoch (1970-01-01) to this date.
     *
     * @return the number of days from the epoch to this date
     */
    public long toEpochDay() {
        return toGregorian().toEpochDay();
    }

    /**
     * Creates a JalaliDate from the number of days from the epoch (1970-01-01).
     *
     * @param epochDay the number of days from the epoch (1970-01-01)
     * @return a JalaliDate representing the specified epoch day
     */
    public static JalaliDate ofEpochDay(long epochDay) {
        return fromGregorian(LocalDate.ofEpochDay(epochDay));
    }

    /**
     * Converts this Jalali date to the number of seconds from the epoch (1970-01-01 00:00:00Z) to the specified time, using the specified offset.
     *
     * @param time the time of day, not null
     * @param offset the zone offset, not null
     * @return the number of seconds from the epoch to the specified time, not null
     * @throws NullPointerException if time or offset is null
     */
    public long toEpochSecond(LocalTime time, ZoneOffset offset) {
        return toGregorian().toEpochSecond(time, offset);
    }

    // ------------------------
    // Arithmetic Operations
    // ------------------------

    /**
     * Returns a copy of this date with the specified number of days added.
     *
     * @param days the days to add, may be negative
     * @return a JalaliDate based on this date with the days added, not null
     */
    public JalaliDate plusDays(long days) {
        if (days == 0) return this;
        LocalDate g = toGregorian().plusDays(days);
        return fromGregorian(g);
    }

    /**
     * Returns a copy of this date with the specified number of days subtracted.
     *
     * <p>
     * This is equivalent to calling {@link #plusDays(long) plusDays} with the negative of the specified number of days.
     *
     * @param days the days to subtract, may be negative
     * @return a JalaliDate based on this date with the days subtracted, not null
     */
    public JalaliDate minusDays(long days) {
        return plusDays(-days);
    }

    /**
     * Returns a copy of this date with the specified number of weeks added.
     * <p>
     * This is equivalent to calling {@link #plusDays(long) plusDays} with the number of days equal to the number of weeks multiplied by 7.
     *
     * @param weeks the weeks to add, may be negative
     * @return a JalaliDate based on this date with the weeks added, not null
     */
    public JalaliDate plusWeeks(long weeks) {
        return plusDays(weeks * 7);
    }

    /**
     * Returns a copy of this date with the specified number of weeks subtracted.
     * <p>
     * This is equivalent to calling {@link #plusWeeks(long) plusWeeks} with the negative of the specified number of weeks.
     *
     * @param weeks the weeks to subtract, may be negative
     * @return a JalaliDate based on this date with the weeks subtracted, not null
     */
    public JalaliDate minusWeeks(long weeks) {
        return plusWeeks(-weeks);
    }

    /**
     * Returns a copy of this date with the specified number of months added.
     *
     * The calculation for the resulting date is as follows:
     * <ul>
     * <li>The total months is calculated as the current year multiplied by 12, plus the current month minus 1, plus the months to add.</li>
     * <li>The new year is calculated as the total months divided by 12.</li>
     * <li>The new month is calculated as the total months modulo 12, plus 1.</li>
     * <li>If the new month is less than 1, then the new month is set to 12 and the new year is decremented by 1.</li>
     * <li>The new day is the minimum of the current day and the maximum day of the new month in the new year.</li>
     * </ul>
     *
     * @param months the months to add, may be negative
     * @return a JalaliDate based on this date with the months added, not null
     */
    public JalaliDate plusMonths(long months) {
        if (months == 0) return this;

        long totalMonths = year * 12L + (month - 1) + months;
        int newYear = (int) (totalMonths / 12);
        int newMonth = (int) (totalMonths % 12) + 1;

        if (newMonth <= 0) {
            newMonth += 12;
            newYear--;
        }

        int maxDay = jalaaliMonthLength(newYear, newMonth);
        int newDay = Math.min(day, maxDay);

        return of(newYear, newMonth, newDay);
    }

    /**
     * Returns a copy of this JalaliDate with the specified number of months subtracted.
     *
     * @param months the months to subtract, may be negative
     * @return a JalaliDate based on this date with the months subtracted, not null
     */
    public JalaliDate minusMonths(long months) {
        return plusMonths(-months);
    }

    /**
     * Returns a copy of this JalaliDate with the year adjusted by the specified number of years.
     * <p>
     * The adjustment is made in the order of years, months, and days.
     * <p>
     * For example, 2008-06-15 plus one year would result in 2009-06-15.
     * <p>
     * Note that it is possible for the result to be in a different calendar system than the input.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years the years to add, may be negative
     * @return a JalaliDate based on this date with the years added, not null
     */
    public JalaliDate plusYears(long years) {
        if (years == 0) return this;
        int newYear = year + (int) years;
        int maxDay = jalaaliMonthLength(newYear, month);
        int newDay = Math.min(day, maxDay);
        return of(newYear, month, newDay);
    }

    /**
     * Returns a copy of this JalaliDate with the year adjusted by the specified number of years.
     * If the number of years is negative, the year is decreased.
     *
     * @param years the number of years to adjust, may be negative
     * @return a JalaliDate based on this date with the year adjusted, not null
     */
    public JalaliDate minusYears(long years) {
        return plusYears(-years);
    }

    // ------------------------
    // Temporal Adjusters
    // ------------------------

    /**
     * Returns a new JalaliDate with the specified adjuster applied.
     *
     * <p>This returns a new JalaliDate, based on this one, with the adjustment made.
     *
     * <p>This is equivalent to:<br>
     * {@code JalaliDate date = this.toGregorian().with(adjuster);
     * return JalaliDate.fromGregorian(date);}
     *
     * @param adjuster the adjuster to apply
     * @return a new JalaliDate based on this date with the specified adjustment made, not null
     */
    public JalaliDate with(TemporalAdjuster adjuster) {
        LocalDate adjusted = toGregorian().with(adjuster);
        return fromGregorian(adjusted);
    }

    /**
     * Returns a JalaliDate representing the given day of month, with the year and month remaining the same.
     *
     * If the given day of month is the same as the current day of month, then this date is returned.
     * Otherwise, a new JalaliDate is returned with the year remaining the same, the month remaining the same, and
     * the day of month set to the given day of month.
     *
     * @param dayOfMonth the day of month to set in the returned JalaliDate
     * @return a JalaliDate representing the given day of month, with the year and month remaining the same, not null
     */
    public JalaliDate withDayOfMonth(int dayOfMonth) {
        if (day == dayOfMonth) return this;
        return of(year, month, dayOfMonth);
    }

    /**
     * Returns a JalaliDate representing the given month, with the year and day remaining the same.
     *
     * If the given month is the same as the current month, then this date is returned.
     * Otherwise, a new JalaliDate is returned with the year remaining the same, the month set to the given month,
     * and the day set to the minimum of the current day and the maximum day of the month in the given year.
     *
     * @param month the month to set in the returned JalaliDate
     * @return a JalaliDate representing the given month, with the year and day remaining the same, not null
     */
    public JalaliDate withMonth(int month) {
        if (this.month == month) return this;
        int maxDay = jalaaliMonthLength(year, month);
        return of(year, month, Math.min(day, maxDay));
    }

    /**
     * Returns a JalaliDate representing the given year, with the month and day remaining the same.
     * If the given year is the same as the current year, then this date is returned.
     * Otherwise, a new JalaliDate is returned with the year set to the given year,
     * the month remaining the same, and the day set to the minimum of the current day and
     * the maximum day of the month in the given year.
     *
     * @param year the year to set in the returned JalaliDate
     * @return a JalaliDate representing the given year, with the month and day remaining the same, not null
     */
    public JalaliDate withYear(int year) {
        if (this.year == year) return this;
        int maxDay = jalaaliMonthLength(year, month);
        return of(year, month, Math.min(day, maxDay));
    }

    /**
     * Returns a JalaliDate representing the first day of the month.
     *
     * If this date is already the first day of the month, then this date is returned.
     * Otherwise, a new JalaliDate is returned with the year, month and day set to the first day of the month.
     *
     * @return a JalaliDate representing the first day of the month, not null
     */
    public JalaliDate firstDayOfMonth() {
        return day == 1 ? this : of(year, month, 1);
    }

    /**
     * Returns a JalaliDate representing the last day of the month.
     *
     * If this date is already the last day of the month, then this date is returned.
     * Otherwise, a new JalaliDate is returned with the year, month and day set to
     * the last day of the month.
     *
     * @return a JalaliDate representing the last day of the month, not null
     */
    public JalaliDate lastDayOfMonth() {
        int lastDay = lengthOfMonth();
        return day == lastDay ? this : of(year, month, lastDay);
    }

    /**
     * Returns a JalaliDate representing the first day of the year.
     *
     * If this date is already the first day of the year, then this date is returned.
     * Otherwise, a new JalaliDate is returned with the year, month and day set to 1.
     *
     * @return a JalaliDate representing the first day of the year, not null
     */
    public JalaliDate firstDayOfYear() {
        return (month == 1 && day == 1) ? this : of(year, 1, 1);
    }

    /**
     * Returns a JalaliDate representing the last day of the year.
     *
     * If this date is already the last day of the year, then this date is returned.
     * Otherwise, a new JalaliDate is returned with the year, month and day set to
     * the last day of the year.
     *
     * @return a JalaliDate representing the last day of the year, not null
     */
    public JalaliDate lastDayOfYear() {
        int lastDay = isLeapYear() ? 30 : 29;
        return (month == 12 && day == lastDay) ? this : of(year, 12, lastDay);
    }

    /**
     * Returns a JalaliDate representing the first day of the next month.
     *
     * If this date is already the last day of the month, then a new JalaliDate is returned
     * with the year and month set to the next month and the day set to 1.
     *
     * @return a JalaliDate representing the first day of the next month, not null
     */
    public JalaliDate firstDayOfNextMonth() {
        return plusMonths(1).firstDayOfMonth();
    }

    /**
     * Returns a JalaliDate representing the first day of the next year.
     *
     * If this date is already the last day of the year, then a new JalaliDate is returned
     * with the year, month and day set to the first day of the next year.
     *
     * @return a JalaliDate representing the first day of the next year, not null
     */
    public JalaliDate firstDayOfNextYear() {
        return of(year + 1, 1, 1);
    }

    /**
     * Returns a JalaliDate representing the next working day.
     *
     * The next working day is the first day after this date that is not a weekend or a holiday.
     *
     * @return a JalaliDate representing the next working day, not null
     */
    public JalaliDate nextWorkingDay() {
        JalaliDate next = plusDays(1);
        while (next.isWeekend() || next.isHoliday()) {
            next = next.plusDays(1);
        }
        return next;
    }

    /**
     * Returns a JalaliDate representing the previous working day.
     *
     * The previous working day is the first day before this date that is not a weekend or a holiday.
     *
     * @return a JalaliDate representing the previous working day, not null
     */
    public JalaliDate previousWorkingDay() {
        JalaliDate prev = minusDays(1);
        while (prev.isWeekend() || prev.isHoliday()) {
            prev = prev.minusDays(1);
        }
        return prev;
    }

    // ------------------------
    // Query Methods
    // ------------------------

    /**
     * Returns the length of this month in days.
     *
     * @return the number of days in this month
     */
    public int lengthOfMonth() {
        return jalaaliMonthLength(year, month);
    }

    /**
     * Returns the length of this year in days.
     *
     * The length of the year is either 365 or 366, depending on whether the year is a leap year or not.
     *
     * @return the number of days in this year
     */
    public int lengthOfYear() {
        return isLeapYear() ? 366 : 365;
    }

    /**
     * Checks if this date's year is a leap year in the Jalali calendar.
     *
     * @return true if the year is a leap year
     */
    public boolean isLeapYear() {
        return isLeapJalaliYear(year);
    }

    /**
     * Gets the day-of-week field, which is an enum DayOfWeek.
     * This method returns the enum {@link java.time.DayOfWeek} for the day-of-week.
     *
     * @return the day-of-week for this date
     */
    public DayOfWeek dayOfWeek() {
        return toGregorian().getDayOfWeek();
    }

    /**
     * Gets the day-of-week as an integer value, using Persian week numbering.
     * Returns 0=Saturday, 1=Sunday, ..., 6=Friday (Persian week)
     *
     * @return the day-of-week as an integer (0-6)
     */
    public int getDayOfWeek() {
        // Returns 0=Saturday, 1=Sunday, ..., 6=Friday (Persian week)
        DayOfWeek dow = dayOfWeek();
        return (dow.getValue() + 1) % 7;
    }

    /**
     * Gets the day-of-year field.
     * This returns the day of year from 1 to 365 (or 366 in leap years).
     *
     * @return the day-of-year (1-366)
     */
    public int getDayOfYear() {
        int dayOfYear = 0;
        for (int m = 1; m < month; m++) {
            dayOfYear += jalaaliMonthLength(year, m);
        }
        return dayOfYear + day;
    }

    /**
     * Gets the week-of-year field.
     * This calculates the week number within the year.
     *
     * @return the week-of-year
     */
    public int getWeekOfYear() {
        JalaliDate firstDay = of(year, 1, 1);
        int firstDayOfWeek = firstDay.getDayOfWeek();
        int dayOfYear = getDayOfYear();
        return (dayOfYear + firstDayOfWeek - 1) / 7 + 1;
    }

    /**
     * Gets the quarter-of-year field from 1 to 4.
     * This returns the quarter that this date falls in.
     *
     * @return the quarter-of-year (1-4)
     */
    public int getQuarter() {
        return (month - 1) / 3 + 1;
    }

    /**
     * Checks if this date falls on a weekend (Thursday or Friday in Iran).
     *
     * @return true if this date is a weekend
     */
    public boolean isWeekend() {
        DayOfWeek dow = dayOfWeek();
        return dow == DayOfWeek.FRIDAY || dow == DayOfWeek.THURSDAY;
    }

    /**
     * Checks if this date falls on a weekday (not weekend).
     * A weekday is any day that is not Thursday or Friday in Iran.
     *
     * @return true if this date is a weekday (not weekend)
     */
    public boolean isWeekday() {
        return !isWeekend();
    }

    // ------------------------
    // Holiday Support
    // ------------------------

    /**
     * Checks if this date is a public holiday in Iran.
     * This includes both fixed holidays and weekends.
     *
     * @return true if this date is a holiday
     */
    public boolean isHoliday() {
        return IranianHolidays.isHoliday(this);
    }

    /**
     * Gets the name of the holiday if this date is a holiday.
     *
     * @return the holiday name, or null if this date is not a holiday
     */
    public String getHolidayName() {
        return IranianHolidays.getHolidayName(this);
    }

    /**
     * Gets all holidays for the specified Jalali year.
     * This includes both fixed holidays and all Fridays in the year.
     *
     * @param year the Jalali year
     * @return a list of all holidays in the specified year, sorted by date
     */
    public static List<JalaliDate> getHolidaysInYear(int year) {
        return IranianHolidays.getHolidaysForYear(year);
    }

    // ------------------------
    // Comparison and Interval Methods
    // ------------------------

    /**
     * Checks if this date is before the specified date.
     *
     * @param other the other date to compare to, not null
     * @return true if this date is before the specified date
     * @throws NullPointerException if other is null
     */
    public boolean isBefore(JalaliDate other) {
        return compareTo(other) < 0;
    }

    /**
     * Checks if this date is after the specified date.
     *
     * @param other the other date to compare to, not null
     * @return true if this date is after the specified date
     * @throws NullPointerException if other is null
     */
    public boolean isAfter(JalaliDate other) {
        return compareTo(other) > 0;
    }

    /**
     * Checks if this date is before or equal to the specified date.
     *
     * @param other the other date to compare to, not null
     * @return true if this date is before or equal to the specified date
     * @throws NullPointerException if other is null
     */
    public boolean isBeforeOrEqual(JalaliDate other) {
        return compareTo(other) <= 0;
    }

    /**
     * Checks if this date is after or equal to the specified date.
     *
     * @param other the other date to compare to, not null
     * @return true if this date is after or equal to the specified date
     * @throws NullPointerException if other is null
     */
    public boolean isAfterOrEqual(JalaliDate other) {
        return compareTo(other) >= 0;
    }

    /**
     * Calculates the number of days from this date to the specified date.
     * The calculation returns a positive value if the target date is after this date.
     *
     * @param other the other date, not null
     * @return the number of days from this date to the other date
     * @throws NullPointerException if other is null
     */
    public long daysUntil(JalaliDate other) {
        Objects.requireNonNull(other, "other");
        return ChronoUnit.DAYS.between(this.toGregorian(), other.toGregorian());
    }

    /**
     * Calculates the number of weeks from this date to the specified date.
     * This is equivalent to {@code daysUntil(other) / 7}.
     *
     * @param other the other date, not null
     * @return the number of weeks from this date to the other date
     * @throws NullPointerException if other is null
     */
    public long weeksUntil(JalaliDate other) {
        return daysUntil(other) / 7;
    }

    /**
     * Calculates the number of months from this date to the specified date.
     * The calculation takes into account the day of month for accurate results.
     *
     * @param other the other date, not null
     * @return the number of months from this date to the other date
     * @throws NullPointerException if other is null
     */
    public long monthsUntil(JalaliDate other) {
        Objects.requireNonNull(other, "other");
        long months = (other.year - this.year) * 12L + (other.month - this.month);
        if (months > 0 && other.day < this.day) months--;
        else if (months < 0 && other.day > this.day) months++;
        return months;
    }

    /**
     * Calculates the number of years from this date to the specified date.
     * The calculation takes into account the month and day for accurate results.
     *
     * @param other the other date, not null
     * @return the number of years from this date to the other date
     * @throws NullPointerException if other is null
     */
    public long yearsUntil(JalaliDate other) {
        Objects.requireNonNull(other, "other");
        long years = other.year - this.year;
        if (years > 0 && (other.month < this.month ||
                (other.month == this.month && other.day < this.day))) years--;
        else if (years < 0 && (other.month > this.month ||
                (other.month == this.month && other.day > this.day))) years++;
        return years;
    }

    /**
     * Calculates the period between this date and the specified date.
     * Returns a Period representing the years, months, and days between the dates.
     *
     * @param other the other date, not null
     * @return the Period between this date and the other date
     * @throws NullPointerException if other is null
     */
    public Period periodUntil(JalaliDate other) {
        long years = yearsUntil(other);
        JalaliDate temp = plusYears(years);
        long months = temp.monthsUntil(other);
        temp = temp.plusMonths(months);
        long days = temp.daysUntil(other);
        return Period.of((int) years, (int) months, (int) days);
    }

    // ------------------------
    // Range and Stream Operations
    // ------------------------

    /**
     * Creates a date range between two JalaliDate instances.
     *
     * @param start the start date of the range, not null
     * @param end the end date of the range, not null
     * @return a JalaliDateRange representing the period between the dates
     * @throws NullPointerException if start or end is null
     * @throws IllegalArgumentException if start is after end
     */
    public static JalaliDateRange between(JalaliDate start, JalaliDate end) {
        return new JalaliDateRange(start, end);
    }

    /**
     * Returns a sequential stream of dates from this date until the specified end date (exclusive).
     * Uses a step of 1 day.
     *
     * @param endExclusive the end date (exclusive), not null
     * @return a stream of dates from this date until the end date
     * @throws NullPointerException if endExclusive is null
     */
    public Stream<JalaliDate> datesUntil(JalaliDate endExclusive) {
        return datesUntil(endExclusive, Period.ofDays(1));
    }

    /**
     * Returns a sequential stream of dates from this date until the specified end date (exclusive)
     * with the specified step period.
     *
     * @param endExclusive the end date (exclusive), not null
     * @param step the step period between dates, must be positive
     * @return a stream of dates from this date until the end date with the specified step
     * @throws NullPointerException if endExclusive or step is null
     * @throws IllegalArgumentException if step is not positive
     */
    public Stream<JalaliDate> datesUntil(JalaliDate endExclusive, Period step) {
        long days = step.getDays() + step.getMonths() * 30L + step.getYears() * 365L;
        if (days <= 0) {
            throw new IllegalArgumentException("Step must be positive");
        }

        return Stream.iterate(this,
                date -> date.isBefore(endExclusive),
                date -> date.plus(step));
    }

    /**
     * Returns a copy of this date with the specified period added.
     * The period is added in the order of years, months, then days.
     *
     * @param period the period to add, not null
     * @return a JalaliDate based on this date with the period added, not null
     * @throws NullPointerException if period is null
     */
    public JalaliDate plus(Period period) {
        return plusYears(period.getYears())
                .plusMonths(period.getMonths())
                .plusDays(period.getDays());
    }

    /**
     * Returns a copy of this date with the specified period subtracted.
     * The period is subtracted in the order of years, months, then days.
     *
     * @param period the period to subtract, not null
     * @return a JalaliDate based on this date with the period subtracted, not null
     * @throws NullPointerException if period is null
     */
    public JalaliDate minus(Period period) {
        return minusYears(period.getYears())
                .minusMonths(period.getMonths())
                .minusDays(period.getDays());
    }

    // ------------------------
    // Formatting Methods
    // ------------------------

    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    /**
     * Returns this date formatted as an ISO-8601 string (yyyy-MM-dd).
     * This is equivalent to calling {@link #toString()}.
     *
     * @return the ISO-8601 formatted date string
     */
    public String toIso() {
        return toString();
    }

    /**
     * Formats this date using the specified format with Persian locale.
     *
     * @param format the format to use
     * @return the formatted date string
     */
    public String format(DateFormat format) {
        return format(format, Locale.forLanguageTag("fa"));
    }

    /**
     * Formats this date using the specified format with the given locale.
     *
     * @param format the format to use, one of the following:
     * <ul>
     * <li>{@link DateFormat#ISO}</li>
     * <li>{@link DateFormat#FULL}</li>
     * <li>{@link DateFormat#LONG}</li>
     * <li>{@link DateFormat#MEDIUM}</li>
     * <li>{@link DateFormat#SHORT}</li>
     * <li>{@link DateFormat#PERSIAN}</li>
     * </ul>
     * @param locale the locale to use, which can affect the weekday and month names
     * @return the formatted date string
     */
    public String format(DateFormat format, Locale locale) {
        boolean isPersian = locale.getLanguage().equals("fa");

        switch (format) {
            case ISO:
                return toIso();
            case FULL:
                return String.format("%s %d %s %d",
                        getWeekdayName(isPersian),
                        day,
                        getMonthName(isPersian),
                        year);
            case LONG:
                return String.format("%d %s %d",
                        day,
                        getMonthName(isPersian),
                        year);
            case MEDIUM:
                return String.format("%d %s %d",
                        day,
                        getMonthName(isPersian).substring(0, 3),
                        year);
            case SHORT:
                return String.format("%02d/%02d/%02d",
                        day, month, year % 100);
            case PERSIAN:
                return String.format("%02d/%02d/%04d", day, month, year);
            default:
                return toIso();
        }
    }

    /**
     * Gets the month name in English.
     * This is equivalent to calling {@code getMonthName(false)}.
     *
     * @return the English month name
     */
    public String getMonthName() {
        return getMonthName(false);
    }

    /**
     * Gets the month name in the specified language.
     *
     * @param persian true to return Persian month name, false for English
     * @return the month name in the requested language
     */
    public String getMonthName(boolean persian) {
        return persian ? MONTH_NAMES_FA[month - 1] : MONTH_NAMES_EN[month - 1];
    }

    /**
     * Gets the weekday name in English.
     * This is equivalent to calling {@code getWeekdayName(false)}.
     *
     * @return the English weekday name
     */
    public String getWeekdayName() {
        return getWeekdayName(false);
    }

    /**
     * Gets the weekday name in the specified language.
     *
     * @param persian true to return Persian weekday name, false for English
     * @return the weekday name in the requested language
     */
    public String getWeekdayName(boolean persian) {
        int dow = getDayOfWeek();
        return persian ? WEEKDAY_NAMES_FA[dow] : WEEKDAY_NAMES_EN[dow];
    }

    // ------------------------
    // Utility Methods
    // ------------------------

    /**
     * Gets the year field.
     *
     * @return the year, from 1 to 3178
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the month-of-year field from 1 to 12.
     * January is 1, February is 2, and so on up to December which is 12.
     *
     * @return the month-of-year, from 1 to 12
     */
    public int getMonth() {
        return month;
    }

    /**
     * Gets the day-of-month field.
     * The return value will be in the valid range of days for the month-year.
     *
     * @return the day-of-month, from 1 to 31
     */
    public int getDay() {
        return day;
    }

    /**
     * Gets the day-of-month field.
     * This is an alias for {@link #getDay()}.
     *
     * @return the day-of-month, from 1 to 31
     */
    public int getDayOfMonth() {
        return day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JalaliDate)) return false;
        JalaliDate other = (JalaliDate) o;
        return year == other.year && month == other.month && day == other.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day);
    }

    @Override
    public int compareTo(JalaliDate o) {
        if (o == null) throw new NullPointerException();
        if (this.year != o.year) return Integer.compare(this.year, o.year);
        if (this.month != o.month) return Integer.compare(this.month, o.month);
        return Integer.compare(this.day, o.day);
    }

    // ------------------------
    // Validation
    // ------------------------

    private static void validate(int y, int m, int d) {
        if (y < 1 || y > 3178) {
            throw new IllegalArgumentException("Year must be between 1 and 3178");
        }
        if (m < 1 || m > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        int maxDay = jalaaliMonthLength(y, m);
        if (d < 1 || d > maxDay) {
            throw new IllegalArgumentException(
                    String.format("Day must be between 1 and %d for month %d", maxDay, m));
        }
    }

    /**
     * Checks if the year, month, day values are valid for a JalaliDate.
     * This validates that the date would be valid if used to construct a JalaliDate.
     *
     * @param year the year to check, from 1 to 3178
     * @param month the month to check, from 1 to 12
     * @param day the day to check, from 1 to 31
     * @return true if the date values are valid
     */
    public static boolean isValid(int year, int month, int day) {
        try {
            validate(year, month, day);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ------------------------
    // Builder Pattern
    // ------------------------

    public static class Builder {
        private int year = 1400;
        private int month = 1;
        private int day = 1;

        /**
         * Sets the year for the JalaliDate being built.
         * The year is from 1 to 3178.
         *
         * @param year the year for the JalaliDate, from 1 to 3178
         * @return this, for method chaining
         */
        public Builder year(int year) {
            this.year = year;
            return this;
        }

        /**
         * Sets the month of the year for the JalaliDate being built.
         * The month of the year is the month of the year (1-12) for the specified year.
         *
         * @param month the month of the year, from 1 to 12
         * @return this, for method chaining
         */
        public Builder month(int month) {
            this.month = month;
            return this;
        }

        /**
         * Sets the day of the month for the JalaliDate being built.
         *
         * The day of the month is the day of the month (1-31) for the specified month.
         *
         * @param day the day of the month, from 1 to 31
         * @return this, for method chaining
         */
        public Builder day(int day) {
            this.day = day;
            return this;
        }

        /**
         * Sets the year, month, and day of the JalaliDate being built from the given Gregorian date.
         * The given date is converted to a JalaliDate and the year, month, and day of the JalaliDate
         * are used to set the year, month, and day of the JalaliDate being built.
         *
         * @param date the Gregorian date to convert and use to set the year, month, and day of the JalaliDate
         * @return this, for method chaining
         **/
        public Builder fromGregorian(LocalDate date) {
            JalaliDate jd = JalaliDate.fromGregorian(date);
            this.year = jd.year;
            this.month = jd.month;
            this.day = jd.day;
            return this;
        }

        public JalaliDate build() {
            return JalaliDate.of(year, month, day);
        }
    }

    /**
     * Creates a new builder to construct a JalaliDate.
     * The builder starts with default values of 1400-01-01.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    // ------------------------
    // Date Range Class
    // ------------------------

    public static class JalaliDateRange {
        private final JalaliDate start;
        private final JalaliDate end;

        public JalaliDateRange(JalaliDate start, JalaliDate end) {
            this.start = Objects.requireNonNull(start);
            this.end = Objects.requireNonNull(end);
            if (start.isAfter(end)) {
                throw new IllegalArgumentException("Start date must be before or equal to end date");
            }
        }

        /**
         * Checks if the given JalaliDate is within the date range.
         *
         * This method returns true if the given JalaliDate is on or after the start date of the range and
         * on or before the end date of the range. Otherwise, false is returned.
         *
         * @param date the JalaliDate to check
         * @return true if the given JalaliDate is within the date range
         */
        public boolean contains(JalaliDate date) {
            return !date.isBefore(start) && !date.isAfter(end);
        }

        /**
         * Returns the number of days in the date range.
         *
         * This method returns the number of days from the start date to the end date, inclusive.
         * The return value is 1 plus the number of days from the start date to the end date.
         *
         * @return the number of days in the date range
         */
        public long getDays() {
            return start.daysUntil(end) + 1;
        }

        /**
         * Converts the JalaliDateRange to a stream of dates.
         * The stream will contain all dates in the range, inclusive of the start and end dates.
         *
         * @return a stream of all dates in the range
         */
        public Stream<JalaliDate> stream() {
            return start.datesUntil(end.plusDays(1));
        }

        /**
         * Converts the JalaliDateRange to a list of dates.
         * The list will contain all dates in the range, inclusive of the start and end dates.
         *
         * @return a list of all dates in the range
         */
        public List<JalaliDate> toList() {
            return stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        /**
         * Returns the start date of the JalaliDateRange.
         *
         * @return the start date of the JalaliDateRange
         */
        public JalaliDate getStart() {
            return start;
        }

        /**
         * Gets the end date of the JalaliDateRange.
         *
         * @return the end date of the JalaliDateRange
         */
        public JalaliDate getEnd() {
            return end;
        }
    }

    // ------------------------
    // Format Enum
    // ------------------------

    public enum DateFormat {
        /**
         * ISO format: 1400-01-01
         */
        ISO,      // 1400-01-01
        /**
         * Full format: Yekshanbe 1 Farvardin 1400
         */
        FULL,     // Yekshanbe 1 Farvardin 1400
        /**
         * Long format: 1 Farvardin 1400
         */
        LONG,     // 1 Farvardin 1400
        /**
         * Medium format: 1 Far 1400
         */
        MEDIUM,   // 1 Far 1400
        /**
         * Short format: 01/01/00
         */
        SHORT,    // 01/01/00
        /**
         * Persian format: 01/01/1400
         */
        PERSIAN,  // 01/01/1400
        /**
         * Auto-detect format: 1400-01-01
         */
        AUTO      // Auto-detect
    }

    // ------------------------
    // Holiday Support Class
    // ------------------------

    public static class IranianHolidays {
        private static final Map<String, String> FIXED_HOLIDAYS = new HashMap<>();

        static {
            // Nowruz holidays
            FIXED_HOLIDAYS.put("1-1", "Nowruz");
            FIXED_HOLIDAYS.put("1-2", "Nowruz Holiday");
            FIXED_HOLIDAYS.put("1-3", "Nowruz Holiday");
            FIXED_HOLIDAYS.put("1-4", "Nowruz Holiday");
            FIXED_HOLIDAYS.put("1-12", "Islamic Republic Day");
            FIXED_HOLIDAYS.put("1-13", "Sizdah Bedar");

            // Other fixed holidays
            FIXED_HOLIDAYS.put("3-14", "Death of Khomeini");
            FIXED_HOLIDAYS.put("3-15", "Revolt of Khordad 15");
            FIXED_HOLIDAYS.put("11-22", "Victory of Islamic Revolution");
            FIXED_HOLIDAYS.put("12-29", "Oil Nationalization Day");
        }

        /**
         * Checks if the given JalaliDate is a holiday in Iran.
         * This method returns true if the given JalaliDate is a fixed holiday or a weekend (Thursday or Friday).
         *
         * @param date the JalaliDate to check
         * @return true if the given JalaliDate is a holiday
         */
        public static boolean isHoliday(JalaliDate date) {
            String key = date.month + "-" + date.day;
            return FIXED_HOLIDAYS.containsKey(key) || date.isWeekend();
        }

        /**
         * Gets the name of the holiday if the given JalaliDate is a holiday.
         * If the given JalaliDate is not a holiday, then this method returns null.
         * Otherwise, it returns the name of the holiday.
         *
         * @param date the JalaliDate to check
         * @return the name of the holiday if the given JalaliDate is a holiday, or null if it is not
         */
        public static String getHolidayName(JalaliDate date) {
            String key = date.month + "-" + date.day;
            if (FIXED_HOLIDAYS.containsKey(key)) {
                return FIXED_HOLIDAYS.get(key);
            }
            if (date.dayOfWeek() == DayOfWeek.FRIDAY) {
                return "Friday (Weekend)";
            }
            return null;
        }

        /**
         * Gets all holidays for the specified Jalali year.
         * This includes both fixed holidays and all Fridays in the year.
         *
         * @param year the Jalali year
         * @return a list of all holidays in the specified year, sorted by date
         */
        public static List<JalaliDate> getHolidaysForYear(int year) {
            List<JalaliDate> holidays = new ArrayList<>();
            for (String key : FIXED_HOLIDAYS.keySet()) {
                String[] parts = key.split("-");
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                if (JalaliDate.isValid(year, month, day)) {
                    holidays.add(JalaliDate.of(year, month, day));
                }
            }
            // Add all Fridays
            JalaliDate date = JalaliDate.of(year, 1, 1);
            while (date.year == year) {
                if (date.dayOfWeek() == DayOfWeek.FRIDAY) {
                    holidays.add(date);
                }
                date = date.plusDays(1);
            }
            holidays.sort(Comparator.naturalOrder());
            return holidays;
        }
    }

    // ------------------------
    // Core Conversion Helpers (unchanged from original)
    // ------------------------

    /**
     * Gets the length of the specified month in the specified Jalali year.
     * Takes into account leap years for the 12th month (Esfand).
     *
     * @param jy the Jalali year
     * @param jm the Jalali month (1-12)
     * @return the length of the month in days
     */
    public static int jalaaliMonthLength(int jy, int jm) {
        if (jm <= 6) return 31;
        if (jm <= 11) return 30;
        return isLeapJalaliYear(jy) ? 30 : 29;
    }

    /**
     * Checks if the specified Jalali year is a leap year.
     * Uses the accurate Jalali calendar leap year calculation algorithm.
     *
     * @param jy the Jalali year to check
     * @return true if the year is a leap year
     */
    public static boolean isLeapJalaliYear(int jy) {
        return jalCal(jy).leap;
    }

    /**
     * Converts a Jalali date to a Julian Day Number (JDN).
     *
     * @param jy the Jalali year
     * @param jm the Jalali month (1-12)
     * @param jd the Jalali day (1-29 or 30 or 31 depending on the month)
     * @return the corresponding Julian Day Number (JDN)
     */
    private static int j2d(int jy, int jm, int jd) {
        JalCalResult r = jalCal(jy);
        int gy = r.gy;
        int march = r.march;
        int jDayOfYear;
        if (jm <= 7) {
            jDayOfYear = (jm - 1) * 31 + (jd - 1);
        } else {
            jDayOfYear = 6 * 31 + (jm - 7) * 30 + (jd - 1);
        }
        int jdnFarvardin1 = g2d(gy, 3, march);
        return jdnFarvardin1 + jDayOfYear;
    }

    private static int[] g2j(int jdn) {
        int[] g = d2g(jdn);
        int gy = g[0];
        int gm = g[1];
        int gd = g[2];
        int jy = gy - 621;
        JalCalResult r = jalCal(jy);
        int march = r.march;
        int jdnFarvardin1 = g2d(gy, 3, march);
        int k = jdn - jdnFarvardin1;
        if (k >= 0) {
            if (k <= 185) {
                int jm = 1 + k / 31;
                int jd = (k % 31) + 1;
                return new int[]{jy, jm, jd};
            } else {
                k -= 186;
                int jm = 7 + k / 30;
                int jd = (k % 30) + 1;
                return new int[]{jy, jm, jd};
            }
        } else {
            jy = jy - 1;
            r = jalCal(jy);
            march = r.march;
            jdnFarvardin1 = g2d(gy - 1, 3, march);
            k = jdn - jdnFarvardin1;
            if (k <= 185) {
                int jm = 1 + k / 31;
                int jd = (k % 31) + 1;
                return new int[]{jy, jm, jd};
            } else {
                k -= 186;
                int jm = 7 + k / 30;
                int jd = (k % 30) + 1;
                return new int[]{jy, jm, jd};
            }
        }
    }

    private static int g2d(int gy, int gm, int gd) {
        int a = (14 - gm) / 12;
        int y = gy + 4800 - a;
        int m = gm + 12 * a - 3;
        return gd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
    }

    private static int[] d2g(int jdn) {
        int a = jdn + 32044;
        int b = (4 * a + 3) / 146097;
        int c = a - (146097 * b) / 4;
        int d = (4 * c + 3) / 1461;
        int e = c - (1461 * d) / 4;
        int m = (5 * e + 2) / 153;
        int day = e - (153 * m + 2) / 5 + 1;
        int month = m + 3 - 12 * (m / 10);
        int year = 100 * b + d - 4800 + (m / 10);
        return new int[]{year, month, day};
    }

    private static JalCalResult jalCal(int jy) {
        final int[] breaks = {
                -61, 9, 38, 199, 426, 686, 756, 818,
                1111, 1181, 1210, 1635, 2060, 2097, 2192,
                2262, 2324, 2394, 2456, 3178
        };
        int bl = breaks.length;
        int gy = jy + 621;
        int leapJ = -14;
        int jp = breaks[0];
        int jump = 0;
        int j = 1;
        for (; j < bl; j++) {
            int jm = breaks[j];
            jump = jm - jp;
            if (jy < jm) break;
            leapJ += (jump / 33) * 8 + (jump % 33) / 4;
            jp = jm;
        }
        int n = jy - jp;
        leapJ += (n / 33) * 8 + ((n % 33) + 3) / 4;
        if (jump % 33 == 4 && jump - n == 4) {
            leapJ += 1;
        }
        int leapG = gy / 4 - ((gy / 100) + 1) * 3 / 4 - 150;
        int march = 20 + leapJ - leapG;
        int diff = jy - jp;
        if (jump - diff < 6) {
            diff = diff - jump + ((jump + 4) / 33) * 33;
        }
        int leap = (((diff + 1) % 33) - 1) % 4;
        if (leap == -1) leap = 4;
        boolean isLeap = (leap == 0);
        return new JalCalResult(isLeap, gy, march);
    }

    private static final class JalCalResult {
        final boolean leap;
        final int gy;
        final int march;

        JalCalResult(boolean leap, int gy, int march) {
            this.leap = leap;
            this.gy = gy;
            this.march = march;
        }
    }
}