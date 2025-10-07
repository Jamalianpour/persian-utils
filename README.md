# Persian Utils

![Language](https://img.shields.io/badge/language-Java-blue)
[![Coverage Status](https://coveralls.io/repos/github/jamalianpour/persian-utils/badge.svg?branch=main)](https://coveralls.io/github/jamalianpour/persian-utils?branch=main)
![License](https://img.shields.io/github/license/jamalianpour/persian-utils)
[![Docs](https://img.shields.io/badge/docs-available-brightgreen.svg)](https://jamalianpour.github.io/persian-utils/)

[English](README.md) | [فارسی 🇮🇷](README_FA.md)

A comprehensive Java library for Persian/Farsi language utilities including date conversion, text processing, number formatting, and validation tools.

# Overview
Persian Utils is a production-ready Java library designed to handle common Persian/Farsi language processing tasks. It provides a comprehensive set of utilities for working with Persian dates, numbers, text, and Iranian-specific validations, making it easier to build applications that support Persian language and Iranian standards.

## Features

### 📅 Date & Time
- **Jalali Calendar**: Complete implementation of the Persian (Shamsi/Jalali) calendar system with full conversion support
- **Date Conversion**: Bidirectional conversion between Jalali and Gregorian calendars
- **Date Formatting**: Multiple output formats including ISO, Persian, full text, and custom patterns
- **Date Arithmetic**: Add/subtract days, months, years with automatic calendar adjustments
- **Holiday Detection**: Built-in Iranian national and religious holiday calendar
- **Relative Time**: Format time differences in Persian (e.g., "۵ دقیقه پیش", "چند ساعت دیگر")
- **Date Ranges**: Create and iterate over date ranges with stream support

### 🔢 Number Utilities
- **Digit Conversion**: Convert between Persian (۰-۹), Arabic (٠-٩), and English (0-9) numeral systems
- **Number to Words**: Convert numeric values to their Persian and English word equivalents
- **Ordinal Numbers**: Generate ordinal representations (first, second → اول، دوم)
- **Number Formatting**: Format numbers with a thousand separators in various styles (comma, Persian, space)
- **Currency Formatting**: Format monetary values with proper Persian currency notation (Rial, Toman)
- **Decimal Handling**: Support for decimal numbers with configurable precision

### 📝 Text Processing
- **Character Detection**: Identify Persian, Arabic, and mixed-language content
- **Text Normalization**: Convert Arabic characters to their Persian equivalents (ي → ی, ك → ک)
- **Diacritics Management**: Remove or process Persian diacritical marks (اعراب)
- **Direction Detection**: Automatically determine text direction (RTL/LTR)
- **Text Statistics**: Analyze Persian content ratio, word count, and character distribution
- **URL Slug Generation**: Create SEO-friendly Persian URL slugs
- **Zero-Width Character Handling**: Manage ZWNJ and other invisible characters

### ✅ Validation
- **National ID (کد ملی)**: Validate Iranian national identification numbers with check digit verification and province detection
- **IBAN/SHEBA**: Validate Iranian bank account numbers with bank identification and proper formatting
- **Phone Numbers**: Validate mobile and landline numbers with operator/area code detection
- **Postal Codes**: Validate Iranian 10-digit postal codes with province and city lookup
- **ATM Cards**: Validate card numbers using Luhn algorithm with BIN (Bank Identification Number) detection

### 🛠️ Additional Utilities
- **File Size Formatting**: Display file sizes in Persian with appropriate units (بایت، کیلوبایت، مگابایت)
- **Batch Processing**: Validate multiple entries simultaneously for improved performance

## Installation

### Maven
```xml
<dependency>
    <groupId>io.github.jamalianpour</groupId>
    <artifactId>persian-utils</artifactId>
    <version>0.4.0</version>
</dependency>
```

## Quick Example

```java
// Jalali Date
JalaliDate today = JalaliDate.now();
System.out.println(today.format(DateFormat.FULL));
// Output: یک شنبه ۱۳ مهر ۱۴۰۴

// Number Conversion
String persian = PersianNumberConverter.toPersianDigits("1234");
// Output: ۱۲۳۴

String words = NumberToWords.toPersian(123);
// Output: یکصد و بیست و سه

// Validation
boolean validNationalId = IranianNationalId.isValid("0499370899");
boolean validIban = IranianIban.isValid("IR820540102680020817909002");
boolean validPhone = IranianPhoneValidator.isValid("09123456789");
boolean validPostalCode = IranianPostalCode.isValid("1234567890");
```

## Documentation

For detailed usage instructions, examples, and API reference, please see **[Documents](https://jamalianpour.github.io/persian-utils/)**

### Requirements
- Java 17 or higher
- Maven 3.6+

## API Documentation

### Date Package (`io.github.jamalianpour.date`)
- **JalaliDate**: Full-featured Jalali date implementation with conversion and formatting
- **PersianRelativeTimeFormatter**: Format time differences in Persian relative expressions

### Text Package (`io.github.jamalianpour.text`)
- **PersianTextUtils**: Text processing utilities for Persian language
- **PersianFileSizeFormatter**: Format file sizes in Persian with appropriate units

### Number Package (`io.github.jamalianpour.number`)
- **NumberToWords**: Convert numbers to word representation
- **NumberFormatter**: Format numbers with various separator styles
- **PersianNumberConverter**: Convert between Persian and English digits
- **OrdinalNumbers**: Generate ordinal numbers

### Validation Package (`io.github.jamalianpour.validation`)
- **IranianNationalId**: National ID validation and utilities
- **IranianIban**: IBAN validation for Iranian banks
- **IranianPhoneValidator**: Phone number validation
- **IranianPostalCode**: Postal code validation
- **AtmCardValidator**: ATM card number validation

## Testing

The project includes comprehensive unit tests for all functionality:

```bash
mvn test
```

## License

This project is licensed under the Apache License - see the [LICENSE](https://github.com/Jamalianpour/persian-utils/blob/master/LICENSE) file for details.

## Author

**Mohammad Jamalianpour**
- Email: jamalian.mjp@gmail.com
- GitHub: [jamalianpour](https://github.com/jamalianpour)

## Support

If you find this library helpful, please give it a ⭐️ on GitHub!

For bug reports and feature requests, please use the [GitHub Issues](https://github.com/jamalianpour/persian-utils/issues) page.