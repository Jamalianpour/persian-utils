package io.github.jamalianpour.date;

import io.github.jamalianpour.number.PersianNumberConverter;
import io.github.jamalianpour.number.NumberToWords;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats time differences in Persian relative time expressions.
 * Examples: "۵ دقیقه پیش", "۲ روز پیش", "چند لحظه پیش"
 */
public class PersianRelativeTimeFormatter {

    /**
     * Time formatting options
     */
    public enum FormatStyle {
        NUMERIC,    // "۵ دقیقه پیش"
        WORDS,      // "پنج دقیقه پیش"
        SHORT,      // "۵د پیش"
        FUZZY       // "چند دقیقه پیش"
    }

    /**
     * Time unit definitions with Persian translations
     */
    private enum TimeUnit {
        SECOND("ثانیه", "ث", 1),
        MINUTE("دقیقه", "د", 60),
        HOUR("ساعت", "س", 3600),
        DAY("روز", "ر", 86400),
        WEEK("هفته", "ه", 604800),
        MONTH("ماه", "م", 2592000), // 30 days
        YEAR("سال", "س", 31536000); // 365 days

        private final String persianName;
        private final String shortForm;
        private final long seconds;

        TimeUnit(String persianName, String shortForm, long seconds) {
            this.persianName = persianName;
            this.shortForm = shortForm;
            this.seconds = seconds;
        }

        public String getPersianName() {
            return persianName;
        }

        public String getShortForm() {
            return shortForm;
        }

        public long getSeconds() {
            return seconds;
        }
    }

    // Fuzzy time expressions
    private static final Map<TimeUnit, String> FUZZY_EXPRESSIONS = new HashMap<>();
    
    static {
        FUZZY_EXPRESSIONS.put(TimeUnit.SECOND, "چند لحظه");
        FUZZY_EXPRESSIONS.put(TimeUnit.MINUTE, "چند دقیقه");
        FUZZY_EXPRESSIONS.put(TimeUnit.HOUR, "چند ساعت");
        FUZZY_EXPRESSIONS.put(TimeUnit.DAY, "چند روز");
        FUZZY_EXPRESSIONS.put(TimeUnit.WEEK, "چند هفته");
        FUZZY_EXPRESSIONS.put(TimeUnit.MONTH, "چند ماه");
        FUZZY_EXPRESSIONS.put(TimeUnit.YEAR, "چند سال");
    }

    /**
     * Formats the time difference between now and the given JalaliDate as Persian relative time.
     * 
     * @param date the JalaliDate to compare with current time
     * @return Persian relative time string
     */
    public static String formatRelativeTime(JalaliDate date) {
        return formatRelativeTime(date, FormatStyle.NUMERIC);
    }

    /**
     * Formats the time difference between now and the given JalaliDate with specified style.
     * 
     * @param date the JalaliDate to compare with current time
     * @param style the formatting style
     * @return Persian relative time string
     */
    public static String formatRelativeTime(JalaliDate date, FormatStyle style) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target = date.toGregorian().atStartOfDay();
        
        return formatRelativeTime(now, target, style);
    }

    /**
     * Formats the time difference between two LocalDateTime objects.
     * 
     * @param from the reference time (usually now)
     * @param to the target time
     * @return Persian relative time string
     */
    public static String formatRelativeTime(LocalDateTime from, LocalDateTime to) {
        return formatRelativeTime(from, to, FormatStyle.NUMERIC);
    }

    /**
     * Formats the time difference between two LocalDateTime objects with specified style.
     * 
     * @param from the reference time (usually now)
     * @param to the target time
     * @param style the formatting style
     * @return Persian relative time string
     */
    public static String formatRelativeTime(LocalDateTime from, LocalDateTime to, FormatStyle style) {
        long diffInSeconds = Math.abs(ChronoUnit.SECONDS.between(from, to));
        boolean isPast = to.isBefore(from);

        // Handle "now" case
        if (diffInSeconds < 10) {
            return "هم‌اکنون";
        }

        // Find the appropriate time unit
        TimeUnit bestUnit = findBestTimeUnit(diffInSeconds);
        long value = diffInSeconds / bestUnit.getSeconds();

        // Handle special cases for better readability
        if (bestUnit == TimeUnit.SECOND && diffInSeconds < 60) {
            return "چند لحظه " + (isPast ? "پیش" : "آینده");
        }

        return formatWithStyle(value, bestUnit, isPast, style);
    }

    /**
     * Formats a time difference from seconds ago.
     * 
     * @param secondsAgo seconds in the past
     * @return Persian relative time string
     */
    public static String formatSecondsAgo(long secondsAgo) {
        return formatSecondsAgo(secondsAgo, FormatStyle.NUMERIC);
    }

    /**
     * Formats a time difference from seconds ago with specified style.
     * 
     * @param secondsAgo seconds in the past
     * @param style the formatting style
     * @return Persian relative time string
     */
    public static String formatSecondsAgo(long secondsAgo, FormatStyle style) {
        if (secondsAgo < 10) {
            return "هم‌اکنون";
        }

        TimeUnit bestUnit = findBestTimeUnit(secondsAgo);
        long value = secondsAgo / bestUnit.getSeconds();

        if (bestUnit == TimeUnit.SECOND && secondsAgo < 60) {
            return "چند لحظه پیش";
        }

        return formatWithStyle(value, bestUnit, true, style);
    }

    /**
     * Formats a time difference in the future.
     * 
     * @param secondsInFuture seconds in the future
     * @return Persian relative time string
     */
    public static String formatSecondsInFuture(long secondsInFuture) {
        return formatSecondsInFuture(secondsInFuture, FormatStyle.NUMERIC);
    }

    /**
     * Formats a time difference in the future with specified style.
     * 
     * @param secondsInFuture seconds in the future
     * @param style the formatting style
     * @return Persian relative time string
     */
    public static String formatSecondsInFuture(long secondsInFuture, FormatStyle style) {
        if (secondsInFuture < 10) {
            return "هم‌اکنون";
        }

        TimeUnit bestUnit = findBestTimeUnit(secondsInFuture);
        long value = secondsInFuture / bestUnit.getSeconds();

        if (bestUnit == TimeUnit.SECOND && secondsInFuture < 60) {
            return "چند لحظه دیگر";
        }

        return formatWithStyle(value, bestUnit, false, style);
    }

    /**
     * Finds the best time unit for the given duration.
     */
    private static TimeUnit findBestTimeUnit(long seconds) {
        if (seconds < TimeUnit.MINUTE.getSeconds()) {
            return TimeUnit.SECOND;
        } else if (seconds < TimeUnit.HOUR.getSeconds()) {
            return TimeUnit.MINUTE;
        } else if (seconds < TimeUnit.DAY.getSeconds()) {
            return TimeUnit.HOUR;
        } else if (seconds < TimeUnit.WEEK.getSeconds()) {
            return TimeUnit.DAY;
        } else if (seconds < TimeUnit.MONTH.getSeconds()) {
            return TimeUnit.WEEK;
        } else if (seconds < TimeUnit.YEAR.getSeconds()) {
            return TimeUnit.MONTH;
        } else {
            return TimeUnit.YEAR;
        }
    }

    /**
     * Formats the time value with the specified style.
     */
    private static String formatWithStyle(long value, TimeUnit unit, boolean isPast, FormatStyle style) {
        String timeExpression;
        
        switch (style) {
            case NUMERIC:
                timeExpression = formatNumeric(value, unit);
                break;
            case WORDS:
                timeExpression = formatWords(value, unit);
                break;
            case SHORT:
                timeExpression = formatShort(value, unit);
                break;
            case FUZZY:
                timeExpression = formatFuzzy(value, unit);
                break;
            default:
                timeExpression = formatNumeric(value, unit);
        }

        // Add past/future suffix
        String suffix = isPast ? "پیش" : (unit == TimeUnit.DAY || unit == TimeUnit.WEEK ? "دیگر" : "آینده");
        
        return timeExpression + " " + suffix;
    }

    /**
     * Formats with Persian numerals.
     */
    private static String formatNumeric(long value, TimeUnit unit) {
        String persianNumber = PersianNumberConverter.toPersianDigits(String.valueOf(value));
        return persianNumber + " " + unit.getPersianName();
    }

    /**
     * Formats with Persian number words.
     */
    private static String formatWords(long value, TimeUnit unit) {
        String persianWords = NumberToWords.toPersian((int) value);
        return persianWords + " " + unit.getPersianName();
    }

    /**
     * Formats with short forms.
     */
    private static String formatShort(long value, TimeUnit unit) {
        String persianNumber = PersianNumberConverter.toPersianDigits(String.valueOf(value));
        return persianNumber + unit.getShortForm();
    }

    /**
     * Formats with fuzzy expressions.
     */
    private static String formatFuzzy(long value, TimeUnit unit) {
        if (value == 1) {
            // For single units, use specific expressions
            switch (unit) {
                case MINUTE:
                    return "یک دقیقه";
                case HOUR:
                    return "یک ساعت";
                case DAY:
                    return "یک روز";
                case WEEK:
                    return "یک هفته";
                case MONTH:
                    return "یک ماه";
                case YEAR:
                    return "یک سال";
                default:
                    return FUZZY_EXPRESSIONS.get(unit);
            }
        } else if (value < 5) {
            return FUZZY_EXPRESSIONS.get(unit);
        } else {
            // Fall back to numeric for larger values
            return formatNumeric(value, unit);
        }
    }

    /**
     * Convenience method for common time periods.
     */
    public static class Common {
        public static String justNow() {
            return "هم‌اکنون";
        }

        public static String fewSecondsAgo() {
            return "چند لحظه پیش";
        }

        public static String oneMinuteAgo() {
            return "یک دقیقه پیش";
        }

        public static String fewMinutesAgo() {
            return "چند دقیقه پیش";
        }

        public static String oneHourAgo() {
            return "یک ساعت پیش";
        }

        public static String fewHoursAgo() {
            return "چند ساعت پیش";
        }

        public static String yesterday() {
            return "دیروز";
        }

        public static String fewDaysAgo() {
            return "چند روز پیش";
        }

        public static String oneWeekAgo() {
            return "یک هفته پیش";
        }

        public static String fewWeeksAgo() {
            return "چند هفته پیش";
        }

        public static String oneMonthAgo() {
            return "یک ماه پیش";
        }

        public static String fewMonthsAgo() {
            return "چند ماه پیش";
        }

        public static String oneYearAgo() {
            return "یک سال پیش";
        }

        public static String fewYearsAgo() {
            return "چند سال پیش";
        }

        // Future expressions
        public static String inFewSeconds() {
            return "چند لحظه دیگر";
        }

        public static String inOneMinute() {
            return "یک دقیقه دیگر";
        }

        public static String inFewMinutes() {
            return "چند دقیقه دیگر";
        }

        public static String inOneHour() {
            return "یک ساعت دیگر";
        }

        public static String inFewHours() {
            return "چند ساعت دیگر";
        }

        public static String tomorrow() {
            return "فردا";
        }

        public static String inFewDays() {
            return "چند روز دیگر";
        }

        public static String inOneWeek() {
            return "یک هفته دیگر";
        }

        public static String inFewWeeks() {
            return "چند هفته دیگر";
        }

        public static String inOneMonth() {
            return "یک ماه دیگر";
        }

        public static String inFewMonths() {
            return "چند ماه دیگر";
        }

        public static String inOneYear() {
            return "یک سال دیگر";
        }

        public static String inFewYears() {
            return "چند سال دیگر";
        }
    }
}