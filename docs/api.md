---
layout: default
title: API Reference
---

# API Reference

Complete API documentation for Persian Utils library.

[← Back to Documentation Home](index.md)

## Table of Contents

- [Date Package](#date-package)
- [Number Package](#number-package)
- [Text Package](#text-package)
- [Validation Package](#validation-package)

---

## Date Package

### JalaliDate

`io.github.jamalianpour.date.JalaliDate`

Immutable Jalali (Persian/Shamsi) date implementation.

#### Factory Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `now()` | `JalaliDate` | Current date |
| `now(ZoneId zone)` | `JalaliDate` | Current date in zone |
| `now(Clock clock)` | `JalaliDate` | Current date from clock |
| `today()` | `JalaliDate` | Current date (alias) |
| `yesterday()` | `JalaliDate` | Yesterday's date |
| `tomorrow()` | `JalaliDate` | Tomorrow's date |
| `of(int year, int month, int day)` | `JalaliDate` | Create from components |
| `ofYearDay(int year, int dayOfYear)` | `JalaliDate` | Create from day of year |
| `ofEpochDay(long epochDay)` | `JalaliDate` | Create from epoch day |
| `fromGregorian(LocalDate date)` | `JalaliDate` | Convert from Gregorian |
| `fromGregorian(int y, int m, int d)` | `JalaliDate` | Convert from Gregorian |
| `builder()` | `Builder` | Get builder instance |

#### Parsing Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `parse(String text)` | `JalaliDate` | Auto-detect format |
| `parse(String text, DateFormat format)` | `JalaliDate` | Parse with format |
| `parseIso(String s)` | `JalaliDate` | Parse ISO format |
| `parsePersian(String s)` | `JalaliDate` | Parse Persian format |

#### Conversion Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `toGregorian()` | `LocalDate` | Convert to Gregorian |
| `atStartOfDay()` | `LocalDateTime` | To LocalDateTime |
| `atTime(int hour, int minute)` | `LocalDateTime` | To LocalDateTime |
| `atTime(int hour, int minute, int second)` | `LocalDateTime` | To LocalDateTime |
| `atTime(LocalTime time)` | `LocalDateTime` | To LocalDateTime |
| `atStartOfDay(ZoneId zone)` | `ZonedDateTime` | To ZonedDateTime |
| `toEpochDay()` | `long` | To epoch day |
| `toEpochSecond(LocalTime, ZoneOffset)` | `long` | To epoch second |

#### Arithmetic Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `plusDays(long days)` | `JalaliDate` | Add days |
| `minusDays(long days)` | `JalaliDate` | Subtract days |
| `plusWeeks(long weeks)` | `JalaliDate` | Add weeks |
| `minusWeeks(long weeks)` | `JalaliDate` | Subtract weeks |
| `plusMonths(long months)` | `JalaliDate` | Add months |
| `minusMonths(long months)` | `JalaliDate` | Subtract months |
| `plusYears(long years)` | `JalaliDate` | Add years |
| `minusYears(long years)` | `JalaliDate` | Subtract years |
| `plus(Period period)` | `JalaliDate` | Add period |
| `minus(Period period)` | `JalaliDate` | Subtract period |

#### Adjustment Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `with(TemporalAdjuster adjuster)` | `JalaliDate` | Apply adjuster |
| `withDayOfMonth(int day)` | `JalaliDate` | Set day of month |
| `withMonth(int month)` | `JalaliDate` | Set month |
| `withYear(int year)` | `JalaliDate` | Set year |
| `firstDayOfMonth()` | `JalaliDate` | First day of month |
| `lastDayOfMonth()` | `JalaliDate` | Last day of month |
| `firstDayOfYear()` | `JalaliDate` | First day of year |
| `lastDayOfYear()` | `JalaliDate` | Last day of year |
| `firstDayOfNextMonth()` | `JalaliDate` | First day of next month |
| `firstDayOfNextYear()` | `JalaliDate` | First day of next year |
| `nextWorkingDay()` | `JalaliDate` | Next working day |
| `previousWorkingDay()` | `JalaliDate` | Previous working day |

#### Query Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getYear()` | `int` | Year component |
| `getMonth()` | `int` | Month component (1-12) |
| `getDay()` | `int` | Day component |
| `getDayOfMonth()` | `int` | Day of month |
| `getDayOfYear()` | `int` | Day of year (1-366) |
| `getDayOfWeek()` | `int` | Day of week (0-6) |
| `getWeekOfYear()` | `int` | Week of year |
| `getQuarter()` | `int` | Quarter (1-4) |
| `lengthOfMonth()` | `int` | Days in month |
| `lengthOfYear()` | `int` | Days in year |
| `isLeapYear()` | `boolean` | Is leap year |
| `dayOfWeek()` | `DayOfWeek` | Day of week |
| `isWeekend()` | `boolean` | Is weekend |
| `isWeekday()` | `boolean` | Is weekday |
| `isHoliday()` | `boolean` | Is holiday |
| `getHolidayName()` | `String` | Holiday name |
| `getMonthName()` | `String` | Month name (English) |
| `getMonthName(boolean persian)` | `String` | Month name |
| `getWeekdayName()` | `String` | Weekday name (English) |
| `getWeekdayName(boolean persian)` | `String` | Weekday name |

#### Comparison Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `isBefore(JalaliDate other)` | `boolean` | Is before |
| `isAfter(JalaliDate other)` | `boolean` | Is after |
| `isBeforeOrEqual(JalaliDate other)` | `boolean` | Is before or equal |
| `isAfterOrEqual(JalaliDate other)` | `boolean` | Is after or equal |
| `compareTo(JalaliDate o)` | `int` | Compare to |
| `daysUntil(JalaliDate other)` | `long` | Days until |
| `weeksUntil(JalaliDate other)` | `long` | Weeks until |
| `monthsUntil(JalaliDate other)` | `long` | Months until |
| `yearsUntil(JalaliDate other)` | `long` | Years until |
| `periodUntil(JalaliDate other)` | `Period` | Period until |

#### Formatting Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `toString()` | `String` | ISO format |
| `toIso()` | `String` | ISO format |
| `format(DateFormat format)` | `String` | Format with style |
| `format(DateFormat format, Locale locale)` | `String` | Format with locale |

#### Stream Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `datesUntil(JalaliDate end)` | `Stream<JalaliDate>` | Date stream |
| `datesUntil(JalaliDate end, Period step)` | `Stream<JalaliDate>` | Date stream with step |

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `between(JalaliDate start, JalaliDate end)` | `JalaliDateRange` | Create range |
| `getHolidaysInYear(int year)` | `List<JalaliDate>` | Get holidays |
| `isValid(int year, int month, int day)` | `boolean` | Validate date |
| `jalaaliMonthLength(int year, int month)` | `int` | Month length |
| `isLeapJalaliYear(int year)` | `boolean` | Is leap year |

### PersianRelativeTimeFormatter

`io.github.jamalianpour.date.PersianRelativeTimeFormatter`

Format time differences in Persian.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `formatRelativeTime(JalaliDate date)` | `String` | Format from date |
| `formatRelativeTime(JalaliDate date, FormatStyle style)` | `String` | Format with style |
| `formatRelativeTime(LocalDateTime from, LocalDateTime to)` | `String` | Format between times |
| `formatRelativeTime(LocalDateTime from, LocalDateTime to, FormatStyle style)` | `String` | Format with style |
| `formatSecondsAgo(long secondsAgo)` | `String` | Format seconds ago |
| `formatSecondsAgo(long secondsAgo, FormatStyle style)` | `String` | Format with style |
| `formatSecondsInFuture(long seconds)` | `String` | Format future time |
| `formatSecondsInFuture(long seconds, FormatStyle style)` | `String` | Format with style |

#### Enums

**FormatStyle**: `NUMERIC`, `WORDS`, `SHORT`, `FUZZY`

---

## Number Package

### PersianNumberConverter

`io.github.jamalianpour.number.PersianNumberConverter`

Convert between Persian, Arabic, and English digits.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `toPersianDigits(String input)` | `String` | To Persian digits |
| `toEnglishDigits(String input)` | `String` | To English digits |
| `toArabicDigits(String input)` | `String` | To Arabic digits |
| `convertAllToPersian(String input)` | `String` | All to Persian |
| `convertAllToEnglish(String input)` | `String` | All to English |
| `toPersianDigit(char digit)` | `char` | Single to Persian |
| `toEnglishDigit(char digit)` | `char` | Single to English |
| `toArabicDigit(char digit)` | `char` | Single to Arabic |
| `toPersianNumber(int number)` | `String` | Number to Persian |
| `toPersianNumber(long number)` | `String` | Number to Persian |
| `toPersianNumber(double number)` | `String` | Number to Persian |
| `parseInteger(String persianNumber)` | `int` | Parse to int |
| `parseLong(String persianNumber)` | `long` | Parse to long |
| `parseDouble(String persianNumber)` | `double` | Parse to double |
| `isPersianDigit(char c)` | `boolean` | Is Persian digit |
| `isArabicDigit(char c)` | `boolean` | Is Arabic digit |
| `isEnglishDigit(char c)` | `boolean` | Is English digit |
| `isDigit(char c)` | `boolean` | Is any digit |
| `getPersianDigitValue(char c)` | `int` | Get digit value |
| `getArabicDigitValue(char c)` | `int` | Get digit value |
| `getDigitValue(char c)` | `int` | Get digit value |
| `countPersianDigits(String input)` | `int` | Count Persian digits |
| `containsPersianDigits(String input)` | `boolean` | Contains Persian |
| `isAllPersianDigits(String input)` | `boolean` | All Persian |

### NumberToWords

`io.github.jamalianpour.number.NumberToWords`

Convert numbers to words.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `toPersian(long number)` | `String` | To Persian words |
| `toPersian(double number)` | `String` | To Persian words |
| `toPersian(BigDecimal number)` | `String` | To Persian words |
| `toEnglish(long number)` | `String` | To English words |
| `toEnglish(double number)` | `String` | To English words |
| `toPersianCurrency(long amount, String currency)` | `String` | Persian currency |
| `toPersianCurrency(double amount, String currency)` | `String` | Persian currency |
| `toEnglishCurrency(long amount, String currency)` | `String` | English currency |
| `toEnglishCurrency(double amount, String currency)` | `String` | English currency |

#### Constants

- `RIAL`, `TOMAN`, `DOLLAR`, `EURO`, `POUND`

### NumberFormatter

`io.github.jamalianpour.number.NumberFormatter`

Format numbers with separators and styles.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `addSeparator(long number)` | `String` | Add comma separator |
| `addSeparator(long number, SeparatorStyle style)` | `String` | Add separator |
| `addSeparator(double number)` | `String` | Add separator |
| `addSeparator(double number, SeparatorStyle style)` | `String` | Add separator |
| `format(long number, FormatConfig config)` | `String` | Format with config |
| `format(double number, FormatConfig config)` | `String` | Format with config |
| `format(BigDecimal number, FormatConfig config)` | `String` | Format with config |
| `addPersianSeparator(long number)` | `String` | Persian separator |
| `addPersianSeparator(double number)` | `String` | Persian separator |
| `removeSeparator(String formattedNumber)` | `String` | Remove separators |
| `parseLong(String formattedNumber)` | `long` | Parse to long |
| `parseDouble(String formattedNumber)` | `double` | Parse to double |
| `parseBigDecimal(String formattedNumber)` | `BigDecimal` | Parse to BigDecimal |
| `formatCurrency(double amount, String currencyCode)` | `String` | Format currency |
| `formatPercentage(double value, int decimalPlaces)` | `String` | Format percentage |
| `formatPhoneNumber(String phone, String pattern)` | `String` | Format phone |
| `formatCreditCard(String cardNumber)` | `String` | Format card |
| `isValidFormattedNumber(String number)` | `boolean` | Validate format |
| `getDecimalPlaces(String formattedNumber)` | `int` | Get decimal places |

#### Enums

**SeparatorStyle**: `COMMA`, `PERSIAN`, `SPACE`, `UNDERSCORE`, `APOSTROPHE`, `NONE`

#### Configuration

**FormatConfig** class with fluent API:
- `withStyle(SeparatorStyle)`
- `withPersianDigits(boolean)`
- `withDecimalPlaces(int)`
- `withPositiveSign(boolean)`
- `withPrefix(String)`
- `withSuffix(String)`
- `withGrouping(boolean)`
- `withGroupingSize(int)`

### OrdinalNumbers

`io.github.jamalianpour.number.OrdinalNumbers`

Convert to ordinal numbers.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `toPersianOrdinal(int number)` | `String` | Persian ordinal words |
| `toEnglishOrdinal(int number)` | `String` | English ordinal words |
| `getEnglishOrdinalSuffix(int number)` | `String` | Get suffix (st/nd/rd/th) |
| `formatEnglishOrdinal(int number)` | `String` | Format with suffix |
| `formatPersianOrdinal(int number)` | `String` | Format Persian |
| `hasSpecialPersianOrdinal(int number)` | `boolean` | Has special form |

---

## Text Package

### PersianTextUtils

`io.github.jamalianpour.text.PersianTextUtils`

Persian text processing utilities.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `containsPersian(String text)` | `boolean` | Contains Persian |
| `isPersian(String text)` | `boolean` | Is Persian |
| `isPersianStrict(String text, boolean allowDigits, boolean allowPunctuation)` | `boolean` | Is strict Persian |
| `arabicToPersian(String text)` | `String` | Arabic to Persian |
| `normalize(String text)` | `String` | Normalize text |
| `removeDiacritics(String text)` | `String` | Remove diacritics |
| `removeZeroWidthChars(String text)` | `String` | Remove zero-width |
| `normalizeWhitespace(String text)` | `String` | Normalize whitespace |
| `isPersianLetter(char c)` | `boolean` | Is Persian letter |
| `isPersianDigit(char c)` | `boolean` | Is Persian digit |
| `isPersianPunctuation(char c)` | `boolean` | Is Persian punctuation |
| `isPersianChar(char c)` | `boolean` | Is Persian char |
| `countPersianChars(String text)` | `int` | Count Persian chars |
| `extractPersianWords(String text)` | `List<String>` | Extract words |
| `getPersianPercentage(String text)` | `double` | Get percentage |
| `getTextDirection(String text)` | `TextDirection` | Get direction |
| `isArabicChar(char c)` | `boolean` | Is Arabic char |
| `isMixedPersianEnglish(String text)` | `boolean` | Is mixed |
| `addRLM(String text)` | `String` | Add RLM mark |
| `addLRM(String text)` | `String` | Add LRM mark |
| `addZWNJ(String text, int position)` | `String` | Add ZWNJ |
| `toPersianSlug(String text)` | `String` | To URL slug |
| `englishToPersianDigits(String text)` | `String` | English to Persian |
| `fixPersianTyping(String text)` | `String` | Fix typing issues |

#### Enums

**TextDirection**: `RTL`, `LTR`, `NEUTRAL`

#### Classes

**PersianTextStats**: Analyze Persian content
- `getTotalChars()` - Total characters
- `getPersianChars()` - Persian characters
- `getEnglishChars()` - English characters
- `getPersianWords()` - Persian words count
- `getPersianPercentage()` - Percentage
- `getDirection()` - Text direction

### PersianFileSizeFormatter

`io.github.jamalianpour.text.PersianFileSizeFormatter`

Format file sizes in Persian.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `format(long bytes)` | `String` | Format file size |
| `format(long bytes, SizeMode mode)` | `String` | Format with mode |
| `format(long bytes, SizeMode mode, NumberStyle numberStyle)` | `String` | Format with style |
| `format(long bytes, SizeMode mode, NumberStyle numberStyle, UnitStyle unitStyle, int decimalPlaces)` | `String` | Full formatting |
| `formatHumanReadable(long bytes)` | `String` | Auto precision |
| `formatWithWords(long bytes)` | `String` | With words |
| `formatMultiple(long[] sizes)` | `String[]` | Multiple sizes |
| `parse(String sizeString)` | `long` | Parse to bytes |

#### Enums

**SizeMode**: `BINARY`, `DECIMAL`  
**NumberStyle**: `NUMERIC`, `WORDS`  
**UnitStyle**: `FULL`, `SHORT`, `ENGLISH`

---

## Validation Package

### IranianNationalId

`io.github.jamalianpour.validation.IranianNationalId`

Validate Iranian national IDs.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `isValid(String nationalId)` | `boolean` | Validate ID |
| `format(String nationalId)` | `String` | Format ID |
| `formatPersian(String nationalId)` | `String` | Persian format |
| `getProvinceCode(String nationalId)` | `String` | Get province code |
| `getProvinceName(String nationalId)` | `String` | Get province name |
| `normalize(String nationalId)` | `String` | Normalize ID |
| `validateBatch(List<String> nationalIds)` | `Map<String, Boolean>` | Batch validation |
| `generateTestId(String provinceCode, String uniqueNumber)` | `String` | Generate test ID |
| `getAllProvinceCodes()` | `Map<String, String>` | Get all provinces |

#### Classes

**NationalIdInfo**: Detailed information
- `getNationalId()` - Normalized ID
- `isValid()` - Is valid
- `getProvinceCode()` - Province code
- `getProvinceName()` - Province name
- `getFormatted()` - Formatted ID
- `getFormattedPersian()` - Persian format

### IranianIban

`io.github.jamalianpour.validation.IranianIban`

Validate Iranian IBANs (SHEBA).

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `isValid(String iban)` | `boolean` | Validate IBAN |
| `format(String iban)` | `String` | Format IBAN |
| `formatCompact(String iban)` | `String` | Compact format |
| `getBankCode(String iban)` | `String` | Get bank code |
| `getBankInfo(String iban)` | `BankInfo` | Get bank info |
| `getAccountNumber(String iban)` | `String` | Get account number |
| `calculateCheckDigits(String bankCode, String accountNumber)` | `String` | Calculate check digits |
| `generateIban(String bankCode, String accountNumber)` | `String` | Generate IBAN |
| `validateBatch(List<String> ibans)` | `Map<String, Boolean>` | Batch validation |
| `getAllBankCodes()` | `Map<String, BankInfo>` | Get all banks |
| `searchBanks(String query)` | `List<BankInfo>` | Search banks |
| `fromPersian(String persianIban)` | `String` | From Persian |
| `toPersian(String iban)` | `String` | To Persian |

### IranianPhoneValidator

`io.github.jamalianpour.validation.IranianPhoneValidator`

Validate Iranian phone numbers.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `isValid(String phoneNumber)` | `boolean` | Validate phone |
| `isValidMobile(String phoneNumber)` | `boolean` | Validate mobile |
| `isValidLandline(String phoneNumber)` | `boolean` | Validate landline |
| `isEmergencyNumber(String number)` | `boolean` | Is emergency |
| `normalizePhoneNumber(String phoneNumber)` | `String` | Normalize |
| `getOperator(String phoneNumber)` | `OperatorInfo` | Get operator |
| `getAreaCode(String phoneNumber)` | `String` | Get area code |
| `getCityName(String phoneNumber)` | `String` | Get city |
| `validateBatch(List<String> phoneNumbers)` | `Map<String, Boolean>` | Batch validation |
| `getAllMobileOperators()` | `Map<String, OperatorInfo>` | All operators |
| `getAllAreaCodes()` | `Map<String, String>` | All area codes |
| `getEmergencyNumbers()` | `Set<String>` | Emergency numbers |

### IranianPostalCode

`io.github.jamalianpour.validation.IranianPostalCode`

Validate Iranian postal codes.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `isValid(String postalCode)` | `boolean` | Validate code |
| `format(String postalCode)` | `String` | Format code |
| `formatPersian(String postalCode)` | `String` | Persian format |
| `getPostalCodeRange(String postalCode)` | `PostalCodeRange` | Get range info |
| `getProvinceName(String postalCode)` | `String` | Get province |
| `getCityName(String postalCode)` | `String` | Get city |
| `getRegionCode(String postalCode)` | `String` | Get region code |
| `getLocalCode(String postalCode)` | `String` | Get local code |
| `validateBatch(List<String> postalCodes)` | `Map<String, Boolean>` | Batch validation |
| `getAllProvinces()` | `List<String>` | All provinces |
| `getCitiesInProvince(String provinceName)` | `List<String>` | Cities in province |
| `search(String query)` | `List<PostalCodeRange>` | Search |

### AtmCardValidator

`io.github.jamalianpour.validation.AtmCardValidator`

Validate ATM/debit cards.

#### Static Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `isValid(String cardNumber)` | `boolean` | Validate card |
| `format(String cardNumber)` | `String` | Format card |
| `formatWithSpaces(String cardNumber)` | `String` | Format with spaces |
| `mask(String cardNumber)` | `String` | Mask card |
| `getBin(String cardNumber)` | `String` | Get BIN |
| `getCardIssuer(String cardNumber)` | `CardIssuerInfo` | Get issuer |
| `isValidCvv2(String cvv2)` | `boolean` | Validate CVV2 |
| `isValidExpiry(String expiry)` | `boolean` | Validate expiry |
| `isExpired(int month, int year)` | `boolean` | Is expired |
| `validateBatch(List<String> cardNumbers)` | `Map<String, Boolean>` | Batch validation |
| `generateTestCard(String bin, String accountNumber)` | `String` | Generate test card |
| `getAllCardIssuers()` | `Map<String, CardIssuerInfo>` | All issuers |
| `searchIssuers(String query)` | `List<CardIssuerInfo>` | Search issuers |
| `isIranianCard(String cardNumber)` | `boolean` | Is Iranian card |
| `toPersianFormat(String cardNumber)` | `String` | Persian format |

---

[← Back to Documentation Home](index.md) | [Usage Guide →](usage.md) | [Examples →](examples.md)