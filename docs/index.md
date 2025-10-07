---
layout: default
title: Persian Utils Documentation
---

# Persian Utils Documentation

Welcome to the Persian Utils library documentation. This comprehensive Java library provides robust tools for Persian/Farsi language processing, including date conversion, number formatting, text processing, and validation of Iranian standards.

## Quick Links

- [Installation & Getting Started](#installation)
- [Usage Guide](usage.md)
- [API Reference](api.md)
- [Code Examples](examples.md)
- [GitHub Repository](https://github.com/jamalianpour/persian-utils)

## Installation

### Maven
```xml
<dependency>
    <groupId>io.github.jamalianpour</groupId>
    <artifactId>persian-utils</artifactId>
    <version>0.4.0</version>
</dependency>
```

## Features Overview

### 📅 Date & Time
Complete Jalali (Persian/Shamsi) calendar implementation with conversion, formatting, and holiday support.

[Learn more →](usage.md#jalali-date)

### 🔢 Number Utilities
Convert between Persian, Arabic, and English digits. Format numbers with separators and convert to words.

[Learn more →](usage.md#number-utilities)

### 📝 Text Processing
Detect, normalize, and process Persian text with support for direction detection and URL slug generation.

[Learn more →](usage.md#text-processing)

### ✅ Validation
Validate Iranian national IDs, IBANs, phone numbers, postal codes, and ATM cards.

[Learn more →](usage.md#validation)

## Quick Start

```java
// Jalali Date
JalaliDate today = JalaliDate.now();
System.out.println(today.format(DateFormat.FULL));
// Output: شنبه ۱ فروردین ۱۴۰۰

// Number Conversion
String persian = PersianNumberConverter.toPersianDigits("1234");
// Output: ۱۲۳۴

String words = NumberToWords.toPersian(123);
// Output: یکصد و بیست و سه

// Validation
boolean valid = IranianNationalId.isValid("0499370899");
```

## Documentation Sections

### [Usage Guide](usage.md)
Comprehensive guide covering all features with detailed explanations and examples.

### [API Reference](api.md)
Complete API documentation for all classes and methods.

### [Code Examples](examples.md)
Practical code examples and common use cases.

## Support

- **Issues**: [GitHub Issues](https://github.com/jamalianpour/persian-utils/issues)
- **Email**: jamalian.mjp@gmail.com

## License

Apache License - see [LICENSE](https://github.com/jamalianpour/persian-utils/blob/main/LICENSE) for details.