---
layout: default
title: Usage Guide
---

# Usage Guide

Complete guide to using Persian Utils library.

[← Back to Documentation Home](index.md)

## Table of Contents

- [Jalali Date](#jalali-date)
- [Number Utilities](#number-utilities)
- [Text Processing](#text-processing)
- [Validation](#validation)
- [File Size Formatting](#file-size-formatting)
- [Relative Time](#relative-time)

---

## Jalali Date

The `JalaliDate` class provides a complete implementation of the Persian (Jalali/Shamsi) calendar system.

Includes conversion, formatting, parsing, holidays, ranges, and temporal operations.
### Creating Dates

```java
// Current date
JalaliDate today = JalaliDate.now();
JalaliDate todayInZone = JalaliDate.now(ZoneId.of("Asia/Tehran"));

// From year, month, day
JalaliDate date = JalaliDate.of(1404, 1, 1);

// From day of year
JalaliDate date = JalaliDate.ofYearDay(1404, 1); // First day of year

// Convenience methods
JalaliDate yesterday = JalaliDate.yesterday();
JalaliDate tomorrow = JalaliDate.tomorrow();
```

### Parsing Dates

```java
// Auto-detect format
JalaliDate date = JalaliDate.parse("1404/01/01");

// Specific format
JalaliDate date = JalaliDate.parse("1404-01-01", DateFormat.ISO);
JalaliDate date = JalaliDate.parseIso("1404-01-01");
JalaliDate date = JalaliDate.parsePersian("01/01/1404");
```

### Conversion Between Calendars

```java
// Gregorian to Jalali
LocalDate gregorian = LocalDate.of(2025, 9, 21);
JalaliDate jalali = JalaliDate.fromGregorian(gregorian);

// Or directly
JalaliDate jalali = JalaliDate.fromGregorian(2025, 9, 21);

// Jalali to Gregorian
LocalDate gregorian = jalali.toGregorian();

// To LocalDateTime
LocalDateTime dateTime = jalali.atStartOfDay();
LocalDateTime dateTime = jalali.atTime(14, 30);
LocalDateTime dateTime = jalali.atTime(14, 30, 0);
```

### Date Arithmetic

```java
JalaliDate date = JalaliDate.of(1404, 1, 1);

// Add/subtract days
JalaliDate tomorrow = date.plusDays(1);
JalaliDate yesterday = date.minusDays(1);

// Add/subtract weeks
JalaliDate nextWeek = date.plusWeeks(1);
JalaliDate lastWeek = date.minusWeeks(1);

// Add/subtract months
JalaliDate nextMonth = date.plusMonths(1);
JalaliDate lastMonth = date.minusMonths(1);

// Add/subtract years
JalaliDate nextYear = date.plusYears(1);
JalaliDate lastYear = date.minusYears(1);

// Using Period
JalaliDate future = date.plus(Period.of(1, 2, 15)); // 1 year, 2 months, 15 days
```

### Date Comparison

```java
JalaliDate date1 = JalaliDate.of(1404, 1, 1);
JalaliDate date2 = JalaliDate.of(1404, 6, 15);

// Comparison
boolean before = date1.isBefore(date2);
boolean after = date1.isAfter(date2);
boolean beforeOrEqual = date1.isBeforeOrEqual(date2);
boolean afterOrEqual = date1.isAfterOrEqual(date2);

// Calculate differences
long days = date1.daysUntil(date2);
long weeks = date1.weeksUntil(date2);
long months = date1.monthsUntil(date2);
long years = date1.yearsUntil(date2);

// Get complete period
Period period = date1.periodUntil(date2);
```

### Date Formatting

```java
JalaliDate date = JalaliDate.of(1404, 1, 1);

// ISO format
String iso = date.toIso(); // 1404-01-01

// Different formats
String full = date.format(DateFormat.FULL);     // شنبه ۱ فروردین ۱۴۰۴
String long_ = date.format(DateFormat.LONG);    // ۱ فروردین ۱۴۰۴
String medium = date.format(DateFormat.MEDIUM); // ۱ فرو ۱۴۰۴
String short_ = date.format(DateFormat.SHORT);  // 01/01/00
String persian = date.format(DateFormat.PERSIAN); // 01/01/1404

// With locale
String formatted = date.format(DateFormat.FULL, Locale.forLanguageTag("fa"));
```

### Date Properties

```java
JalaliDate date = JalaliDate.of(1404, 1, 15);

// Get components
int year = date.getYear();
int month = date.getMonth();
int day = date.getDay();

// Get names
String monthName = date.getMonthName();        // Farvardin
String monthNamePersian = date.getMonthName(true); // فروردین
String weekday = date.getWeekdayName();        // Yekshanbe
String weekdayPersian = date.getWeekdayName(true); // یکشنبه

// Other properties
int dayOfYear = date.getDayOfYear();
int weekOfYear = date.getWeekOfYear();
int quarter = date.getQuarter();
DayOfWeek dayOfWeek = date.dayOfWeek();
int lengthOfMonth = date.lengthOfMonth();
int lengthOfYear = date.lengthOfYear();
boolean isLeap = date.isLeapYear();
```

### Date Adjusters

```java
JalaliDate date = JalaliDate.of(1404, 6, 15);

// Change components
JalaliDate newDate = date.withYear(1404);
JalaliDate newDate = date.withMonth(1);
JalaliDate newDate = date.withDayOfMonth(1);

// First and last days
JalaliDate firstDay = date.firstDayOfMonth();
JalaliDate lastDay = date.lastDayOfMonth();
JalaliDate firstDayOfYear = date.firstDayOfYear();
JalaliDate lastDayOfYear = date.lastDayOfYear();

// Next month/year
JalaliDate nextMonth = date.firstDayOfNextMonth();
JalaliDate nextYear = date.firstDayOfNextYear();
```

### Holidays

```java
JalaliDate date = JalaliDate.of(1404, 1, 1);

// Check if holiday
boolean isHoliday = date.isHoliday();

// Get holiday name
String holidayName = date.getHolidayName(); // "Nowruz"

// Get all holidays in a year
List<JalaliDate> holidays = JalaliDate.getHolidaysInYear(1400);

// Weekend checking
boolean isWeekend = date.isWeekend(); // Friday or Thursday
boolean isWeekday = date.isWeekday();
```

### Working Days

```java
JalaliDate date = JalaliDate.of(1404, 1, 13); // Sizdah Bedar (holiday)

// Next working day (skip weekends and holidays)
JalaliDate nextWork = date.nextWorkingDay();

// Previous working day
JalaliDate prevWork = date.previousWorkingDay();
```

### Date Ranges

```java
JalaliDate start = JalaliDate.of(1404, 1, 1);
JalaliDate end = JalaliDate.of(1404, 1, 31);

// Create range
JalaliDate.JalaliDateRange range = JalaliDate.between(start, end);

// Check if contains
boolean contains = range.contains(JalaliDate.of(1404, 1, 15));

// Get number of days
long days = range.getDays();

// Iterate
List<JalaliDate> dates = range.toList();
range.stream().forEach(date -> System.out.println(date));
```

### Date Streams

```java
JalaliDate start = JalaliDate.of(1404, 1, 1);
JalaliDate end = JalaliDate.of(1404, 12, 29);

// Stream of dates (daily)
start.datesUntil(end)
     .forEach(date -> System.out.println(date));

// Stream with custom step
start.datesUntil(end, Period.ofDays(7))  // Weekly
     .forEach(date -> System.out.println(date));

start.datesUntil(end, Period.ofMonths(1)) // Monthly
     .forEach(date -> System.out.println(date));
```

### Builder Pattern

```java
JalaliDate date = JalaliDate.builder()
    .year(1400)
    .month(1)
    .day(1)
    .build();

// From Gregorian
JalaliDate date = JalaliDate.builder()
    .fromGregorian(LocalDate.of(2025, 3, 21))
    .build();
```

---

## Number Utilities

### Digit Conversion

Convert between Persian (۰-۹), Arabic (٠-٩), and English (0-9) numerals. Converts numbers and digits between Persian/Farsi, Arabic, and English numeral systems.

```java
// English to Persian
String persian = PersianNumberConverter.toPersianDigits("1234");
// Output: ۱۲۳۴

// Persian to English
String english = PersianNumberConverter.toEnglishDigits("۱۲۳۴");
// Output: 1234

// Arabic to Persian
String persian = PersianNumberConverter.toArabicDigits("1234");
// Output: ١٢٣٤

// Convert all to Persian (handles mixed)
String persian = PersianNumberConverter.convertAllToPersian("123 و ۴۵۶");
// Output: ۱۲۳ و ۴۵۶

// Single digit conversion
char persianDigit = PersianNumberConverter.toPersianDigit('5');
// Output: ۵
```

### Number to Persian Digits

```java
// Convert numbers to Persian
String persian = PersianNumberConverter.toPersianNumber(1234);
// Output: ۱۲۳۴

String persian = PersianNumberConverter.toPersianNumber(1234567L);
// Output: ۱۲۳۴۵۶۷

String persian = PersianNumberConverter.toPersianNumber(123.45);
// Output: ۱۲۳٫۴۵
```

### Parsing Persian Numbers

```java
// Parse to integers
int number = PersianNumberConverter.parseInteger("۱۲۳۴");
// Output: 1234

long number = PersianNumberConverter.parseLong("۱۲۳۴۵۶۷");
// Output: 1234567

double number = PersianNumberConverter.parseDouble("۱۲۳٫۴۵");
// Output: 123.45
```

### Number to Words
Converts numbers to their word representation in Persian (Farsi) and English. Supports whole numbers, decimals, negative numbers, and currency formatting.

```java
// Persian words
String words = NumberToWords.toPersian(123);
// Output: یکصد و بیست و سه

String words = NumberToWords.toPersian(1000);
// Output: یک هزار

String words = NumberToWords.toPersian(1234567);
// Output: یک میلیون و دویست و سی و چهار هزار و پانصد و شصت و هفت

// Decimal numbers
String words = NumberToWords.toPersian(123.45);
// Output: یکصد و بیست و سه ممیز چهار پنج

// Negative numbers
String words = NumberToWords.toPersian(-123);
// Output: منفی یکصد و بیست و سه

// BigDecimal support
BigDecimal number = new BigDecimal("123456789.12");
String words = NumberToWords.toPersian(number);
```

### English Words

```java
String words = NumberToWords.toEnglish(123);
// Output: one hundred twenty three

String words = NumberToWords.toEnglish(1000000);
// Output: one million
```

### Currency Formatting

```java
// Persian currency
String currency = NumberToWords.toPersianCurrency(50000, NumberToWords.TOMAN);
// Output: پنجاه هزار تومان

String currency = NumberToWords.toPersianCurrency(100000, NumberToWords.RIAL);
// Output: یکصد هزار ریال

// With decimals
String currency = NumberToWords.toPersianCurrency(50000.50, NumberToWords.TOMAN);
// Output: پنجاه هزار تومان و پنجاه ریال

// English currency
String currency = NumberToWords.toEnglishCurrency(100, NumberToWords.DOLLAR);
// Output: one hundred dollars

String currency = NumberToWords.toEnglishCurrency(99.99, NumberToWords.DOLLAR);
// Output: ninety nine dollars and ninety nine cents
```

### Number Formatting
Utility class for formatting numbers with separators and removing separators from formatted numbers. Supports various separator styles including English comma, Persian comma, space, and custom separators.

```java
// Add thousand separators
String formatted = NumberFormatter.addSeparator(1234567);
// Output: 1,234,567

// Persian separator
String formatted = NumberFormatter.addPersianSeparator(1234567);
// Output: ۱٬۲۳۴٬۵۶۷

// With different separator styles
String formatted = NumberFormatter.addSeparator(1234567, 
    NumberFormatter.SeparatorStyle.SPACE);
// Output: 1 234 567
```

### Advanced Formatting

```java
// Create custom configuration
NumberFormatter.FormatConfig config = new NumberFormatter.FormatConfig()
    .withStyle(NumberFormatter.SeparatorStyle.PERSIAN)
    .withPersianDigits(true)
    .withDecimalPlaces(2)
    .withPrefix("قیمت: ")
    .withSuffix(" ریال")
    .withPositiveSign(false);

String formatted = NumberFormatter.format(1234567.89, config);
// Output: قیمت: ۱٬۲۳۴٬۵۶۷٫۸۹ ریال

// Pre-configured formats
String formatted = NumberFormatter.format(1234, NumberFormatter.PERSIAN_FORMAT);
String formatted = NumberFormatter.format(1234.56, NumberFormatter.CURRENCY_FORMAT);
```

### Currency Formatting

```java
// Format as currency
String price = NumberFormatter.formatCurrency(1500.50, "USD");
// Output: $1,500.50

String price = NumberFormatter.formatCurrency(1500.50, "IRR");
// Output: ۱٬۵۰۱ ریال

String price = NumberFormatter.formatCurrency(1500000, "IRT");
// Output: ۱٬۵۰۰٬۰۰۰ تومان
```

### Percentage Formatting

```java
String percent = NumberFormatter.formatPercentage(75.5, 1);
// Output: 75.5%

String percent = NumberFormatter.formatPercentage(0.755, 2);
// Output: 0.76%
```

### Remove Separators and Parse

```java
// Remove separators
String clean = NumberFormatter.removeSeparator("1,234,567");
// Output: 1234567

String clean = NumberFormatter.removeSeparator("۱٬۲۳۴٬۵۶۷");
// Output: 1234567

// Parse formatted numbers
long number = NumberFormatter.parseLong("1,234,567");
// Output: 1234567

double number = NumberFormatter.parseDouble("۱٬۲۳۴٫۵۶");
// Output: 1234.56

BigDecimal number = NumberFormatter.parseBigDecimal("1,234,567.89");
// Output: 1234567.89
```

### Ordinal Numbers
Converts numbers to their ordinal representation in Persian and English. Examples: 1st, 2nd, 3rd in English | اول، دوم، سوم in Persian

```java
// Persian ordinals
String first = OrdinalNumbers.toPersianOrdinal(1);
// Output: اول

String second = OrdinalNumbers.toPersianOrdinal(2);
// Output: دوم

String twentyFirst = OrdinalNumbers.toPersianOrdinal(21);
// Output: بیست و یکم

// Format with digits
String formatted = OrdinalNumbers.formatPersianOrdinal(21);
// Output: ۲۱م

// English ordinals
String first = OrdinalNumbers.toEnglishOrdinal(1);
// Output: first

String twentyFirst = OrdinalNumbers.toEnglishOrdinal(21);
// Output: twenty-first

// Ordinal suffix
String suffix = OrdinalNumbers.getEnglishOrdinalSuffix(21);
// Output: st

String formatted = OrdinalNumbers.formatEnglishOrdinal(21);
// Output: 21st
```

---

## Text Processing

### Persian Text Detection
Comprehensive utilities for Persian/Farsi text processing, validation, and manipulation. Handles Persian character detection, Arabic to Persian conversion, text normalization, and more.

```java
// Check if contains Persian
boolean hasPersian = PersianTextUtils.containsPersian("سلام");
// Output: true

boolean hasPersian = PersianTextUtils.containsPersian("Hello");
// Output: false

// Check if entirely Persian
boolean isPersian = PersianTextUtils.isPersian("سلام دنیا");
// Output: true

// Strict Persian check
boolean isPersian = PersianTextUtils.isPersianStrict("سلام", true, true);
```

### Text Normalization

```java
// Convert Arabic characters to Persian
String normalized = PersianTextUtils.arabicToPersian("كتاب");
// Output: کتاب

// Full normalization (Arabic to Persian + remove diacritics + cleanup)
String normalized = PersianTextUtils.normalize("كِتابٌ");
// Output: کتاب

// Remove diacritics only
String clean = PersianTextUtils.removeDiacritics("کِتاب");
// Output: کتاب

// Remove zero-width characters
String clean = PersianTextUtils.removeZeroWidthChars("سلام‌دنیا");
```

### Character Type Detection

```java
// Check character types
boolean isLetter = PersianTextUtils.isPersianLetter('س');
boolean isDigit = PersianTextUtils.isPersianDigit('۵');
boolean isPunctuation = PersianTextUtils.isPersianPunctuation('،');
boolean isPersianChar = PersianTextUtils.isPersianChar('س');
```

### Text Statistics

```java
String text = "این یک متن نمونه است با کمی English mixed in.";

PersianTextUtils.PersianTextStats stats = 
    new PersianTextUtils.PersianTextStats(text);

int totalChars = stats.getTotalChars();
int persianChars = stats.getPersianChars();
int englishChars = stats.getEnglishChars();
int persianWords = stats.getPersianWords();
double persianPercent = stats.getPersianPercentage();
PersianTextUtils.TextDirection direction = stats.getDirection();

System.out.println(stats.toString());
// Output: Total: X chars, Persian: Y (Z%), English: W, Persian words: V, Direction: RTL
```

### Text Direction

```java
// Detect text direction
PersianTextUtils.TextDirection dir = 
    PersianTextUtils.getTextDirection("سلام");
// Output: RTL

PersianTextUtils.TextDirection dir = 
    PersianTextUtils.getTextDirection("Hello");
// Output: LTR

// Check for mixed content
boolean isMixed = PersianTextUtils.isMixedPersianEnglish("سلام World");
// Output: true
```

### Extract Persian Content

```java
// Extract Persian words
List<String> words = PersianTextUtils.extractPersianWords("سلام World دنیا");
// Output: ["سلام", "دنیا"]

// Count Persian characters
int count = PersianTextUtils.countPersianChars("سلام World");
// Output: 4

// Get Persian percentage
double percent = PersianTextUtils.getPersianPercentage("سلام World");
// Output: ~50.0
```

### URL Slug Generation

```java
// Create Persian-friendly URL slug
String slug = PersianTextUtils.toPersianSlug("سلام دنیا");
// Output: سلام-دنیا

String slug = PersianTextUtils.toPersianSlug("عنوان مقاله من!");
// Output: عنوان-مقاله-من
```

### Directional Marks

```java
// Add RTL mark
String rtl = PersianTextUtils.addRLM("متن فارسی");

// Add LRM mark
String ltr = PersianTextUtils.addLRM("English text");

// Add ZWNJ
String text = PersianTextUtils.addZWNJ("می خواهم", 2);
```

### Fix Persian Typing

```java
// Fix common Persian typing issues
String fixed = PersianTextUtils.fixPersianTyping("سلام،چطوري؟");
// Output: سلام، چطوری؟

// Handles:
// - Arabic to Persian conversion
// - Spacing around punctuation
// - Quotation marks
```

---

## Validation

### National ID (کد ملی)
Validator and utilities for Iranian National ID (کد ملی). Implements the official check digit algorithm for 10-digit national IDs.

```java
// Simple validation
boolean isValid = IranianNationalId.isValid("0499370899");

// Get detailed information
IranianNationalId.NationalIdInfo info = 
    new IranianNationalId.NationalIdInfo("0499370899");

if (info.isValid()) {
    String provinceCode = info.getProvinceCode(); // 049
    String provinceName = info.getProvinceName(); // تهران
    String formatted = info.getFormatted();       // 049-937089-9
    String persianFormat = info.getFormattedPersian(); // ۰۴۹-۹۳۷۰۸۹-۹
}

// Format national ID
String formatted = IranianNationalId.format("0499370899");
// Output: 049-937089-9

// Normalize (clean up)
String normalized = IranianNationalId.normalize("049-937089-9");
// Output: 0499370899

// Batch validation
List<String> ids = Arrays.asList("0499370899", "1234567890");
Map<String, Boolean> results = IranianNationalId.validateBatch(ids);

// Generate test ID (for development only)
String testId = IranianNationalId.generateTestId("049", "937089");
// Output: 0499370899
```

### IBAN/SHEBA
Validator and utilities for Iranian IBAN (SHEBA - شماره شبا). Implements the IBAN mod-97 check digit algorithm for Iranian bank accounts.

```java
// Simple validation
boolean isValid = IranianIban.isValid("IR820540102680020817909002");

// Get detailed information
IranianIban.IbanInfo info = 
    new IranianIban.IbanInfo("IR820540102680020817909002");

if (info.isValid()) {
    String bankCode = info.getBankCode();
    IranianIban.BankInfo bank = info.getBankInfo();
    String bankName = bank.getPersianName();      // بانک پارسیان
    String bankNameEn = bank.getEnglishName();    // Bank Parsian
    String accountNumber = info.getAccountNumber();
    String formatted = info.getFormatted();
    // Output: IR82 0540 1026 8002 0817 9090 02
}

// Format IBAN
String formatted = IranianIban.format("IR820540102680020817909002");
String compact = IranianIban.formatCompact("IR82 0540 1026 8002 0817 9090 02");

// Normalize
String normalized = IranianIban.normalize("IR82 0540 1026 8002 0817 9090 02");

// Get bank info
IranianIban.BankInfo bank = IranianIban.getBankInfo("IR820540102680020817909002");

// Generate IBAN
String iban = IranianIban.generateIban("054", "0102680020817909002");

// Calculate check digits
String checkDigits = IranianIban.calculateCheckDigits("054", "0102680020817909002");

// Search banks
List<IranianIban.BankInfo> banks = IranianIban.searchBanks("پارسیان");

// Persian format
String persianIban = IranianIban.toPersian("IR820540102680020817909002");
```

### Phone Numbers
Validator and utilities for Iranian phone numbers (mobile and landline). Supports operator identification, area codes, and various formatting options.

```java
// Simple validation
boolean isValid = IranianPhoneValidator.isValid("09123456789");
boolean isMobile = IranianPhoneValidator.isValidMobile("09123456789");
boolean isLandline = IranianPhoneValidator.isValidLandline("02188776655");

// Get detailed information
IranianPhoneValidator.PhoneInfo info = 
    new IranianPhoneValidator.PhoneInfo("09123456789");

if (info.isValid()) {
    boolean isMobile = info.isMobile();
    IranianPhoneValidator.OperatorInfo operator = info.getOperator();
    String operatorName = operator.getPersianName(); // همراه اول
    String formatted = info.getFormatted();          // 0912-345-6789
    String international = info.getInternational();  // +98 912 345 6789
}

// Format phone numbers
String dashed = IranianPhoneValidator.PhoneFormatter.formatDashed("09123456789");
// Output: 0912-345-6789

String spaced = IranianPhoneValidator.PhoneFormatter.formatSpaced("09123456789");
// Output: 0912 345 6789

String international = IranianPhoneValidator.PhoneFormatter.formatInternational("09123456789");
// Output: +98 912 345 6789

String persian = IranianPhoneValidator.PhoneFormatter.formatPersian("09123456789");
// Output: ۰۹۱۲ ۳۴۵ ۶۷۸۹

// Get operator
IranianPhoneValidator.OperatorInfo operator = 
    IranianPhoneValidator.getOperator("09123456789");

// Get city for landline
String city = IranianPhoneValidator.getCityName("02188776655");

// Emergency numbers
boolean isEmergency = IranianPhoneValidator.isEmergencyNumber("110");

// Normalize
String normalized = IranianPhoneValidator.normalizePhoneNumber("+989123456789");
```

### Postal Codes
Validator and utilities for Iranian postal codes (کد پستی). Iranian postal codes are 10 digits where the first 5 digits represent the region/city and the last 5 digits represent the specific area/street/building.

```java
// Simple validation
boolean isValid = IranianPostalCode.isValid("1234567890");

// Get detailed information
IranianPostalCode.PostalCodeInfo info = 
    new IranianPostalCode.PostalCodeInfo("1234567890");

if (info.isValid()) {
    String regionCode = info.getRegionCode();      // 12345
    String localCode = info.getLocalCode();        // 67890
    String provinceName = info.getPostalCodeRange().getProvincePersian();
    String cityName = info.getPostalCodeRange().getCityPersian();
    String formatted = info.getFormatted();        // 12345-67890
    String persianFormat = info.getFormattedPersian(); // ۱۲۳۴۵-۶۷۸۹۰
}

// Format postal code
String formatted = IranianPostalCode.format("1234567890");
// Output: 12345-67890

// Normalize
String normalized = IranianPostalCode.normalize("12345-67890");

// Get province/city
String province = IranianPostalCode.getProvinceName("1234567890");
String city = IranianPostalCode.getCityName("1234567890");

// Search
List<IranianPostalCode.PostalCodeRange> results = 
    IranianPostalCode.search("تهران");

// Get all provinces
List<String> provinces = IranianPostalCode.getAllProvinces();

// Get cities in province
List<String> cities = IranianPostalCode.getCitiesInProvince("تهران");

// Statistics
Map<String, Integer> distribution = 
    IranianPostalCode.getProvinceDistribution(postalCodes);
```

### ATM Cards
Validator and utilities for ATM/Debit cards with Iranian bank card support. Implements the Luhn algorithm for card number validation and provides BIN identification.

```java
// Simple validation
boolean isValid = AtmCardValidator.isValid("6037701689095443");

// Get detailed information
AtmCardValidator.CardInfo info = 
    new AtmCardValidator.CardInfo("6037701689095443");

if (info.isValid()) {
    String bin = info.getBin();                    // 603770
    AtmCardValidator.CardIssuerInfo issuer = info.getIssuer();
    String bankName = issuer.getPersianName();     // بانک کشاورزی
    String formatted = info.getFormatted();        // 6037-7016-8909-5443
    String masked = info.getMasked();              // 6037-****-****-5443
}

// Format card number
String formatted = AtmCardValidator.format("6037701689095443");
// Output: 6037-7016-8909-5443

String spaced = AtmCardValidator.formatWithSpaces("6037701689095443");
// Output: 6037 7016 8909 5443

// Mask card number
String masked = AtmCardValidator.mask("6037701689095443");
// Output: 6037-****-****-5443

// Get BIN
String bin = AtmCardValidator.getBin("6037701689095443");

// Get issuer
AtmCardValidator.CardIssuerInfo issuer = 
    AtmCardValidator.getCardIssuer("6037701689095443");

// Check if Iranian card
boolean isIranian = AtmCardValidator.isIranianCard("6037701689095443");

// Validate CVV2
boolean validCvv = AtmCardValidator.isValidCvv2("123");

// Validate expiry
boolean validExpiry = AtmCardValidator.isValidExpiry("12/25");

// Check if expired
boolean expired = AtmCardValidator.isExpired(12, 2020);

// Generate test card (for development only)
String testCard = AtmCardValidator.generateTestCard("603770", "168909544");

// Search issuers
List<AtmCardValidator.CardIssuerInfo> issuers = 
    AtmCardValidator.searchIssuers("کشاورزی");

// Persian format
String persian = AtmCardValidator.toPersianFormat("6037701689095443");
```

---

## File Size Formatting
Formats file sizes in Persian with appropriate units. Supports both binary (1024) and decimal (1000) modes with Persian translations.
> Examples: 1024 → "۱ کیلوبایت", 1048576 → "۱ مگابایت"

```java
// Simple formatting
String size = PersianFileSizeFormatter.format(1536);
// Output: ۱٫۵ کیلوبایت

String size = PersianFileSizeFormatter.format(1048576);
// Output: ۱ مگابایت

// Human-readable (automatic precision)
String size = PersianFileSizeFormatter.formatHumanReadable(1536000);
// Output: ۱٫۵ مگابایت

// With different modes
String binary = PersianFileSizeFormatter.format(1024, 
    PersianFileSizeFormatter.SizeMode.BINARY);
// Output: ۱ کیلوبایت

String decimal = PersianFileSizeFormatter.format(1000,
    PersianFileSizeFormatter.SizeMode.DECIMAL);
// Output: ۱ کیلوبایت

// With words
String words = PersianFileSizeFormatter.formatWithWords(1024);
// Output: یک کیلوبایت

// Custom formatting
String formatted = PersianFileSizeFormatter.format(
    1536,
    PersianFileSizeFormatter.SizeMode.BINARY,
    PersianFileSizeFormatter.NumberStyle.NUMERIC,
    PersianFileSizeFormatter.UnitStyle.FULL,
    2  // decimal places
);

// Parse
long bytes = PersianFileSizeFormatter.parse("۱٫۵ کیلوبایت");

// Common conversions
String kb = PersianFileSizeFormatter.Common.formatKilobytes(1024);
String mb = PersianFileSizeFormatter.Common.formatMegabytes(1);
String gb = PersianFileSizeFormatter.Common.formatGigabytes(1);
```

---

## Relative Time

Format time differences in Persian relative expressions.
> Examples: "۵ دقیقه پیش", "۲ روز پیش", "چند لحظه پیش"
> 
```java
// Format from JalaliDate
JalaliDate yesterday = JalaliDate.yesterday();
String relative = PersianRelativeTimeFormatter.formatRelativeTime(yesterday);
// Output: یک روز پیش

// Format from LocalDateTime
LocalDateTime now = LocalDateTime.now();
LocalDateTime past = now.minusHours(2);
String relative = PersianRelativeTimeFormatter.formatRelativeTime(now, past);
// Output: دو ساعت پیش

// Format from seconds
String relative = PersianRelativeTimeFormatter.formatSecondsAgo(3600);
// Output: یک ساعت پیش

// Future time
String relative = PersianRelativeTimeFormatter.formatSecondsInFuture(7200);
// Output: دو ساعت دیگر

// Different styles
String numeric = PersianRelativeTimeFormatter.formatSecondsAgo(300,
    PersianRelativeTimeFormatter.FormatStyle.NUMERIC);
// Output: ۵ دقیقه پیش

String words = PersianRelativeTimeFormatter.formatSecondsAgo(300,
    PersianRelativeTimeFormatter.FormatStyle.WORDS);
// Output: پنج دقیقه پیش

String fuzzy = PersianRelativeTimeFormatter.formatSecondsAgo(300,
    PersianRelativeTimeFormatter.FormatStyle.FUZZY);
// Output: چند دقیقه پیش

String short_ = PersianRelativeTimeFormatter.formatSecondsAgo(300,
    PersianRelativeTimeFormatter.FormatStyle.SHORT);
// Output: ۵د پیش

// Common expressions
String now = PersianRelativeTimeFormatter.Common.justNow();
String fewSeconds = PersianRelativeTimeFormatter.Common.fewSecondsAgo();
String oneMinute = PersianRelativeTimeFormatter.Common.oneMinuteAgo();
String yesterday = PersianRelativeTimeFormatter.Common.yesterday();
String tomorrow = PersianRelativeTimeFormatter.Common.tomorrow();
```

---

[← Back to Documentation Home](index.md) | [API Reference →](api.md)