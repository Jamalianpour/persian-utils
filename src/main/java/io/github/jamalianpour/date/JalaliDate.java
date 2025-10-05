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

    public static JalaliDate of(int year, int month, int day) {
        return new JalaliDate(year, month, day);
    }

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

    public static JalaliDate parse(String text) {
        return parse(text, DateFormat.AUTO);
    }

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

    public static JalaliDate parseIso(String s) {
        var m = ISO_PATTERN.matcher(s.trim());
        if (!m.matches()) throw new IllegalArgumentException("Invalid ISO date format: " + s);
        int y = Integer.parseInt(m.group(1));
        int mo = Integer.parseInt(m.group(2));
        int d = Integer.parseInt(m.group(3));
        return of(y, mo, d);
    }

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

    public static JalaliDate now() {
        return fromGregorian(LocalDate.now());
    }

    public static JalaliDate now(ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        return fromGregorian(LocalDate.now(zone));
    }

    public static JalaliDate now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        return fromGregorian(LocalDate.now(clock));
    }

    public static JalaliDate today() {
        return now();
    }

    public static JalaliDate yesterday() {
        return now().minusDays(1);
    }

    public static JalaliDate tomorrow() {
        return now().plusDays(1);
    }

    // ------------------------
    // Conversion Methods
    // ------------------------

    public static JalaliDate fromGregorian(LocalDate date) {
        Objects.requireNonNull(date, "date");
        int[] j = g2j(g2d(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        return new JalaliDate(j[0], j[1], j[2]);
    }

    public static JalaliDate fromGregorian(int year, int month, int day) {
        return fromGregorian(LocalDate.of(year, month, day));
    }

    public LocalDate toGregorian() {
        int jdn = j2d(this.year, this.month, this.day);
        int[] g = d2g(jdn);
        return LocalDate.of(g[0], g[1], g[2]);
    }

    public LocalDateTime atStartOfDay() {
        return toGregorian().atStartOfDay();
    }

    public LocalDateTime atTime(int hour, int minute) {
        return toGregorian().atTime(hour, minute);
    }

    public LocalDateTime atTime(int hour, int minute, int second) {
        return toGregorian().atTime(hour, minute, second);
    }

    public LocalDateTime atTime(LocalTime time) {
        return toGregorian().atTime(time);
    }

    public ZonedDateTime atStartOfDay(ZoneId zone) {
        return toGregorian().atStartOfDay(zone);
    }

    // ------------------------
    // Epoch Methods
    // ------------------------

    public long toEpochDay() {
        return toGregorian().toEpochDay();
    }

    public static JalaliDate ofEpochDay(long epochDay) {
        return fromGregorian(LocalDate.ofEpochDay(epochDay));
    }

    public long toEpochSecond(LocalTime time, ZoneOffset offset) {
        return toGregorian().toEpochSecond(time, offset);
    }

    // ------------------------
    // Arithmetic Operations
    // ------------------------

    public JalaliDate plusDays(long days) {
        if (days == 0) return this;
        LocalDate g = toGregorian().plusDays(days);
        return fromGregorian(g);
    }

    public JalaliDate minusDays(long days) {
        return plusDays(-days);
    }

    public JalaliDate plusWeeks(long weeks) {
        return plusDays(weeks * 7);
    }

    public JalaliDate minusWeeks(long weeks) {
        return plusWeeks(-weeks);
    }

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

    public JalaliDate minusMonths(long months) {
        return plusMonths(-months);
    }

    public JalaliDate plusYears(long years) {
        if (years == 0) return this;
        int newYear = year + (int) years;
        int maxDay = jalaaliMonthLength(newYear, month);
        int newDay = Math.min(day, maxDay);
        return of(newYear, month, newDay);
    }

    public JalaliDate minusYears(long years) {
        return plusYears(-years);
    }

    // ------------------------
    // Temporal Adjusters
    // ------------------------

    public JalaliDate with(TemporalAdjuster adjuster) {
        LocalDate adjusted = toGregorian().with(adjuster);
        return fromGregorian(adjusted);
    }

    public JalaliDate withDayOfMonth(int dayOfMonth) {
        if (day == dayOfMonth) return this;
        return of(year, month, dayOfMonth);
    }

    public JalaliDate withMonth(int month) {
        if (this.month == month) return this;
        int maxDay = jalaaliMonthLength(year, month);
        return of(year, month, Math.min(day, maxDay));
    }

    public JalaliDate withYear(int year) {
        if (this.year == year) return this;
        int maxDay = jalaaliMonthLength(year, month);
        return of(year, month, Math.min(day, maxDay));
    }

    public JalaliDate firstDayOfMonth() {
        return day == 1 ? this : of(year, month, 1);
    }

    public JalaliDate lastDayOfMonth() {
        int lastDay = lengthOfMonth();
        return day == lastDay ? this : of(year, month, lastDay);
    }

    public JalaliDate firstDayOfYear() {
        return (month == 1 && day == 1) ? this : of(year, 1, 1);
    }

    public JalaliDate lastDayOfYear() {
        int lastDay = isLeapYear() ? 30 : 29;
        return (month == 12 && day == lastDay) ? this : of(year, 12, lastDay);
    }

    public JalaliDate firstDayOfNextMonth() {
        return plusMonths(1).firstDayOfMonth();
    }

    public JalaliDate firstDayOfNextYear() {
        return of(year + 1, 1, 1);
    }

    public JalaliDate nextWorkingDay() {
        JalaliDate next = plusDays(1);
        while (next.isWeekend() || next.isHoliday()) {
            next = next.plusDays(1);
        }
        return next;
    }

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

    public int lengthOfMonth() {
        return jalaaliMonthLength(year, month);
    }

    public int lengthOfYear() {
        return isLeapYear() ? 366 : 365;
    }

    public boolean isLeapYear() {
        return isLeapJalaliYear(year);
    }

    public DayOfWeek dayOfWeek() {
        return toGregorian().getDayOfWeek();
    }

    public int getDayOfWeek() {
        // Returns 0=Saturday, 1=Sunday, ..., 6=Friday (Persian week)
        DayOfWeek dow = dayOfWeek();
        return (dow.getValue() + 1) % 7;
    }

    public int getDayOfYear() {
        int dayOfYear = 0;
        for (int m = 1; m < month; m++) {
            dayOfYear += jalaaliMonthLength(year, m);
        }
        return dayOfYear + day;
    }

    public int getWeekOfYear() {
        JalaliDate firstDay = of(year, 1, 1);
        int firstDayOfWeek = firstDay.getDayOfWeek();
        int dayOfYear = getDayOfYear();
        return (dayOfYear + firstDayOfWeek - 1) / 7 + 1;
    }

    public int getQuarter() {
        return (month - 1) / 3 + 1;
    }

    public boolean isWeekend() {
        DayOfWeek dow = dayOfWeek();
        return dow == DayOfWeek.FRIDAY || dow == DayOfWeek.THURSDAY;
    }

    public boolean isWeekday() {
        return !isWeekend();
    }

    // ------------------------
    // Holiday Support
    // ------------------------

    public boolean isHoliday() {
        return IranianHolidays.isHoliday(this);
    }

    public String getHolidayName() {
        return IranianHolidays.getHolidayName(this);
    }

    public static List<JalaliDate> getHolidaysInYear(int year) {
        return IranianHolidays.getHolidaysForYear(year);
    }

    // ------------------------
    // Comparison and Interval Methods
    // ------------------------

    public boolean isBefore(JalaliDate other) {
        return compareTo(other) < 0;
    }

    public boolean isAfter(JalaliDate other) {
        return compareTo(other) > 0;
    }

    public boolean isBeforeOrEqual(JalaliDate other) {
        return compareTo(other) <= 0;
    }

    public boolean isAfterOrEqual(JalaliDate other) {
        return compareTo(other) >= 0;
    }

    public long daysUntil(JalaliDate other) {
        Objects.requireNonNull(other, "other");
        return ChronoUnit.DAYS.between(this.toGregorian(), other.toGregorian());
    }

    public long weeksUntil(JalaliDate other) {
        return daysUntil(other) / 7;
    }

    public long monthsUntil(JalaliDate other) {
        Objects.requireNonNull(other, "other");
        long months = (other.year - this.year) * 12L + (other.month - this.month);
        if (months > 0 && other.day < this.day) months--;
        else if (months < 0 && other.day > this.day) months++;
        return months;
    }

    public long yearsUntil(JalaliDate other) {
        Objects.requireNonNull(other, "other");
        long years = other.year - this.year;
        if (years > 0 && (other.month < this.month ||
                (other.month == this.month && other.day < this.day))) years--;
        else if (years < 0 && (other.month > this.month ||
                (other.month == this.month && other.day > this.day))) years++;
        return years;
    }

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

    public static JalaliDateRange between(JalaliDate start, JalaliDate end) {
        return new JalaliDateRange(start, end);
    }

    public Stream<JalaliDate> datesUntil(JalaliDate endExclusive) {
        return datesUntil(endExclusive, Period.ofDays(1));
    }

    public Stream<JalaliDate> datesUntil(JalaliDate endExclusive, Period step) {
        long days = step.getDays() + step.getMonths() * 30L + step.getYears() * 365L;
        if (days <= 0) {
            throw new IllegalArgumentException("Step must be positive");
        }

        return Stream.iterate(this,
                date -> date.isBefore(endExclusive),
                date -> date.plus(step));
    }

    public JalaliDate plus(Period period) {
        return plusYears(period.getYears())
                .plusMonths(period.getMonths())
                .plusDays(period.getDays());
    }

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

    public String toIso() {
        return toString();
    }

    public String format(DateFormat format) {
        return format(format, Locale.forLanguageTag("fa"));
    }

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

    public String getMonthName() {
        return getMonthName(false);
    }

    public String getMonthName(boolean persian) {
        return persian ? MONTH_NAMES_FA[month - 1] : MONTH_NAMES_EN[month - 1];
    }

    public String getWeekdayName() {
        return getWeekdayName(false);
    }

    public String getWeekdayName(boolean persian) {
        int dow = getDayOfWeek();
        return persian ? WEEKDAY_NAMES_FA[dow] : WEEKDAY_NAMES_EN[dow];
    }

    // ------------------------
    // Utility Methods
    // ------------------------

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

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

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder month(int month) {
            this.month = month;
            return this;
        }

        public Builder day(int day) {
            this.day = day;
            return this;
        }

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

        public boolean contains(JalaliDate date) {
            return !date.isBefore(start) && !date.isAfter(end);
        }

        public long getDays() {
            return start.daysUntil(end) + 1;
        }

        public Stream<JalaliDate> stream() {
            return start.datesUntil(end.plusDays(1));
        }

        public List<JalaliDate> toList() {
            return stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        public JalaliDate getStart() {
            return start;
        }

        public JalaliDate getEnd() {
            return end;
        }
    }

    // ------------------------
    // Format Enum
    // ------------------------

    public enum DateFormat {
        ISO,      // 1400-01-01
        FULL,     // Yekshanbe 1 Farvardin 1400
        LONG,     // 1 Farvardin 1400
        MEDIUM,   // 1 Far 1400
        SHORT,    // 01/01/00
        PERSIAN,  // 01/01/1400
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

        public static boolean isHoliday(JalaliDate date) {
            String key = date.month + "-" + date.day;
            return FIXED_HOLIDAYS.containsKey(key) || date.isWeekend();
        }

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

    public static int jalaaliMonthLength(int jy, int jm) {
        if (jm <= 6) return 31;
        if (jm <= 11) return 30;
        return isLeapJalaliYear(jy) ? 30 : 29;
    }

    public static boolean isLeapJalaliYear(int jy) {
        return jalCal(jy).leap;
    }

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