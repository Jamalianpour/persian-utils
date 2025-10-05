---
layout: default
title: Code Examples
---

# Code Examples

Practical examples and common use cases for Persian Utils library.

[← Back to Documentation Home](index.md)

## Table of Contents

- [Date Examples](#date-examples)
- [Number Examples](#number-examples)
- [Text Examples](#text-examples)
- [Validation Examples](#validation-examples)
- [Real-World Use Cases](#real-world-use-cases)

---

## Date Examples

### Example 1: Display Current Jalali Date

```java
public String getCurrentPersianDate() {
    JalaliDate today = JalaliDate.now();
    return today.format(DateFormat.FULL, Locale.forLanguageTag("fa"));
    // Output: یک شنبه ۱۳ مهر ۱۴۰۴
}
```

### Example 2: Convert User's Birthday

```java
public JalaliDate convertBirthday(LocalDate gregorianBirthday) {
    return JalaliDate.fromGregorian(gregorianBirthday);
}

// Usage
LocalDate birthday = LocalDate.of(1990, 3, 21);
JalaliDate jalaliaBirthday = convertBirthday(birthday);
System.out.println("تاریخ تولد: " + jalaliaBirthday.format(DateFormat.LONG));
```

### Example 3: Calculate Age in Persian Calendar

```java
public int calculatePersianAge(JalaliDate birthDate) {
    JalaliDate today = JalaliDate.now();
    return (int) birthDate.yearsUntil(today);
}

// Usage
JalaliDate birthDate = JalaliDate.of(1370, 1, 15);
int age = calculatePersianAge(birthDate);
System.out.println("سن: " + age + " سال");
```

### Example 4: Check if Date is Holiday

```java
public String checkHoliday(JalaliDate date) {
    if (date.isWeekend()) {
        return "آخر هفته - " + date.getWeekdayName(true);
    } else if (date.isHoliday()) {
        return "تعطیل رسمی - " + date.getHolidayName();
    } else {
        return "روز کاری";
    }
}

// Usage
JalaliDate nowruz = JalaliDate.of(1400, 1, 1);
System.out.println(checkHoliday(nowruz)); // تعطیل رسمی - Nowruz
```

### Example 5: Generate Date Range for Calendar

```java
public List<JalaliDate> getMonthDates(int year, int month) {
    JalaliDate firstDay = JalaliDate.of(year, month, 1);
    JalaliDate lastDay = firstDay.lastDayOfMonth();
    
    return JalaliDate.between(firstDay, lastDay).toList();
}

// Usage
List<JalaliDate> aprilDates = getMonthDates(1404, 1);
aprilDates.forEach(date -> 
    System.out.println(date.format(DateFormat.PERSIAN))
);
```

### Example 6: Find Next Working Day

```java
public JalaliDate scheduleNextBusinessDay(JalaliDate requestedDate) {
    if (requestedDate.isWeekend() || requestedDate.isHoliday()) {
        return requestedDate.nextWorkingDay();
    }
    return requestedDate;
}

// Usage
JalaliDate friday = JalaliDate.of(1404, 1, 2); // Friday
JalaliDate nextWork = scheduleNextBusinessDay(friday);
System.out.println("روز کاری بعدی: " + nextWork.format(DateFormat.PERSIAN));
```

### Example 7: Calculate Due Dates

```java
public class InvoiceCalculator {
    public JalaliDate calculateDueDate(JalaliDate invoiceDate, int paymentTermDays) {
        return invoiceDate.plusDays(paymentTermDays);
    }
    
    public boolean isOverdue(JalaliDate dueDate) {
        return dueDate.isBefore(JalaliDate.now());
    }
    
    public long daysOverdue(JalaliDate dueDate) {
        if (!isOverdue(dueDate)) {
            return 0;
        }
        return dueDate.daysUntil(JalaliDate.now());
    }
}

// Usage
InvoiceCalculator calculator = new InvoiceCalculator();
JalaliDate invoiceDate = JalaliDate.of(1404, 1, 1);
JalaliDate dueDate = calculator.calculateDueDate(invoiceDate, 30);
System.out.println("تاریخ سررسید: " + dueDate.format(DateFormat.PERSIAN));
```

### Example 8: Display Relative Time

```java
public String getPostTime(JalaliDate postDate) {
    return PersianRelativeTimeFormatter.formatRelativeTime(postDate);
}

// Usage
JalaliDate twoHoursAgo = JalaliDate.now().minusHours(2);
System.out.println(getPostTime(twoHoursAgo)); // ۲ ساعت پیش

JalaliDate yesterday = JalaliDate.yesterday();
System.out.println(getPostTime(yesterday)); // ۱ روز پیش
```

---

## Number Examples

### Example 9: Format Price Display

```java
public String formatPrice(double price) {
    NumberFormatter.FormatConfig config = new NumberFormatter.FormatConfig()
        .withStyle(NumberFormatter.SeparatorStyle.PERSIAN)
        .withPersianDigits(true)
        .withDecimalPlaces(0)
        .withSuffix(" تومان");
    
    return NumberFormatter.format(price, config);
}

// Usage
System.out.println(formatPrice(1250000)); // ۱٬۲۵۰٬۰۰۰ تومان
```

### Example 10: Convert Amount to Words for Check

```java
public String generateCheckAmount(long amount) {
    String words = NumberToWords.toPersian(amount);
    String currency = "ریال";
    
    return words + " " + currency;
}

// Usage
System.out.println(generateCheckAmount(5500000));
// پنج میلیون و پانصد هزار ریال
```

### Example 11: Parse User Input with Persian Digits

```java
public double parseUserInput(String input) throws NumberFormatException {
    // Remove any currency symbols and separators
    String cleaned = NumberFormatter.removeSeparator(input);
    return NumberFormatter.parseDouble(cleaned);
}

// Usage
String userInput = "۱٬۲۳۴٬۵۶۷٫۸۹ تومان";
double amount = parseUserInput(userInput); // 1234567.89
```

### Example 12: Display Ordinal Position

```java
public String getPositionText(int position, boolean usePersian) {
    if (usePersian) {
        return OrdinalNumbers.toPersianOrdinal(position);
    } else {
        return OrdinalNumbers.toEnglishOrdinal(position);
    }
}

// Usage
System.out.println(getPositionText(1, true));  // اول
System.out.println(getPositionText(21, true)); // بیست و یکم
System.out.println(getPositionText(1, false)); // first
```

### Example 13: Format File Size

```java
public String formatFileSize(long bytes) {
    return PersianFileSizeFormatter.formatHumanReadable(bytes);
}

// Usage
System.out.println(formatFileSize(1536));      // ۱٫۵ کیلوبایت
System.out.println(formatFileSize(1048576));   // ۱ مگابایت
System.out.println(formatFileSize(5242880));   // ۵ مگابایت
```

---

## Text Examples

### Example 14: Validate and Normalize User Input

```java
public class UserInputProcessor {
    public String normalizeText(String input) {
        // Convert Arabic to Persian and clean up
        String normalized = PersianTextUtils.normalize(input);
        
        // Fix common typing mistakes
        normalized = PersianTextUtils.fixPersianTyping(normalized);
        
        return normalized;
    }
    
    public boolean isPersianContent(String text) {
        return PersianTextUtils.getPersianPercentage(text) > 50;
    }
}

// Usage
UserInputProcessor processor = new UserInputProcessor();
String input = "كتاب،من";
String clean = processor.normalizeText(input);
System.out.println(clean); // کتاب، من
```

### Example 15: Generate SEO-Friendly URL

```java
public String generateArticleUrl(String title) {
    String slug = PersianTextUtils.toPersianSlug(title);
    return "/articles/" + slug;
}

// Usage
String url = generateArticleUrl("آموزش برنامه‌نویسی جاوا");
System.out.println(url); // /articles/آموزش-برنامه-نویسی-جاوا
```

### Example 16: Detect Content Language

```java
public class ContentAnalyzer {
    public String detectLanguage(String content) {
        PersianTextUtils.PersianTextStats stats = 
            new PersianTextUtils.PersianTextStats(content);
        
        double persianPercent = stats.getPersianPercentage();
        
        if (persianPercent > 80) {
            return "Persian";
        } else if (persianPercent < 20) {
            return "English";
        } else {
            return "Mixed";
        }
    }
    
    public PersianTextUtils.TextDirection getDirection(String content) {
        return PersianTextUtils.getTextDirection(content);
    }
}

// Usage
ContentAnalyzer analyzer = new ContentAnalyzer();
System.out.println(analyzer.detectLanguage("سلام دنیا")); // Persian
System.out.println(analyzer.detectLanguage("Hello World")); // English
```

---

## Validation Examples

### Example 17: Validate User Registration

```java
public class UserValidator {
    public ValidationResult validateUser(String nationalId, String phone) {
        ValidationResult result = new ValidationResult();
        
        // Validate National ID
        if (!IranianNationalId.isValid(nationalId)) {
            result.addError("کد ملی نامعتبر است");
        }
        
        // Validate Phone
        if (!IranianPhoneValidator.isValidMobile(phone)) {
            result.addError("شماره موبایل نامعتبر است");
        }
        
        return result;
    }
}

// Usage
UserValidator validator = new UserValidator();
ValidationResult result = validator.validateUser("0499370899", "09123456789");
if (result.isValid()) {
    System.out.println("اطلاعات کاربر معتبر است");
}
```

### Example 18: Bank Account Information

```java
public class BankAccountService {
    public String getBankName(String iban) {
        IranianIban.IbanInfo info = new IranianIban.IbanInfo(iban);
        
        if (info.isValid()) {
            return info.getBankInfo().getPersianName();
        }
        return "بانک نامشخص";
    }
    
    public String formatIban(String iban) {
        return IranianIban.format(iban);
    }
}

// Usage
BankAccountService service = new BankAccountService();
String iban = "IR820540102680020817909002";
System.out.println(service.getBankName(iban));    // بانک پارسیان
System.out.println(service.formatIban(iban));     // IR82 0540 1026 8002 0817 9090 02
```

### Example 19: Phone Number Formatter

```java
public class ContactFormatter {
    public String formatForDisplay(String phone) {
        IranianPhoneValidator.PhoneInfo info = 
            new IranianPhoneValidator.PhoneInfo(phone);
        
        if (!info.isValid()) {
            return phone; // Return as-is if invalid
        }
        
        if (info.isMobile()) {
            return IranianPhoneValidator.PhoneFormatter.formatPersian(phone);
        } else {
            return IranianPhoneValidator.PhoneFormatter.formatDashed(phone);
        }
    }
    
    public String getOperatorName(String mobileNumber) {
        IranianPhoneValidator.PhoneInfo info = 
            new IranianPhoneValidator.PhoneInfo(mobileNumber);
        
        if (info.isMobile() && info.getOperator() != null) {
            return info.getOperator().getPersianName();
        }
        return "نامشخص";
    }
}

// Usage
ContactFormatter formatter = new ContactFormatter();
System.out.println(formatter.formatForDisplay("09123456789"));
// ۰۹۱۲ ۳۴۵ ۶۷۸۹

System.out.println(formatter.getOperatorName("09123456789"));
// همراه اول
```

### Example 20: Address Validator

```java
public class AddressValidator {
    public boolean validateAddress(String postalCode, String province) {
        if (!IranianPostalCode.isValid(postalCode)) {
            return false;
        }
        
        IranianPostalCode.PostalCodeInfo info = 
            new IranianPostalCode.PostalCodeInfo(postalCode);
        
        return info.getPostalCodeRange()
                   .getProvincePersian()
                   .equals(province);
    }
    
    public String getCityFromPostalCode(String postalCode) {
        IranianPostalCode.PostalCodeInfo info = 
            new IranianPostalCode.PostalCodeInfo(postalCode);
        
        if (info.isValid()) {
            return info.getPostalCodeRange().getCityPersian();
        }
        return null;
    }
}
```

### Example 21: Payment Card Processor

```java
public class PaymentProcessor {
    public CardDetails processCard(String cardNumber) {
        AtmCardValidator.CardInfo cardInfo = 
            new AtmCardValidator.CardInfo(cardNumber);
        
        CardDetails details = new CardDetails();
        details.setValid(cardInfo.isValid());
        
        if (cardInfo.isValid()) {
            details.setMaskedNumber(cardInfo.getMasked());
            details.setBankName(cardInfo.getIssuer().getPersianName());
            details.setIsIranian(AtmCardValidator.isIranianCard(cardNumber));
        }
        
        return details;
    }
    
    public boolean validateCardAndCvv(String cardNumber, String cvv2) {
        return AtmCardValidator.isValid(cardNumber) && 
               AtmCardValidator.isValidCvv2(cvv2);
    }
}

// Usage
PaymentProcessor processor = new PaymentProcessor();
CardDetails details = processor.processCard("6037701689095443");
System.out.println(details.getMaskedNumber()); // 6037-****-****-5443
System.out.println(details.getBankName());     // بانک کشاورزی
```

---

## Real-World Use Cases

### Example 22: Invoice Generator

```java
public class InvoiceGenerator {
    public Invoice generateInvoice(InvoiceData data) {
        Invoice invoice = new Invoice();
        
        // Set Persian date
        JalaliDate today = JalaliDate.now();
        invoice.setDate(today.format(DateFormat.FULL));
        
        // Set due date (30 days from now)
        JalaliDate dueDate = today.plusDays(30);
        invoice.setDueDate(dueDate.format(DateFormat.LONG));
        
        // Format amounts
        NumberFormatter.FormatConfig config = new NumberFormatter.FormatConfig()
            .withStyle(NumberFormatter.SeparatorStyle.PERSIAN)
            .withPersianDigits(true)
            .withSuffix(" ریال");
        
        invoice.setTotalAmount(NumberFormatter.format(data.getTotal(), config));
        invoice.setTotalInWords(NumberToWords.toPersianCurrency(
            data.getTotal(), NumberToWords.RIAL));
        
        return invoice;
    }
}
```

### Example 23: User Profile Manager

```java
public class UserProfileManager {
    public UserProfile createProfile(ProfileData data) {
        UserProfile profile = new UserProfile();
        
        // Validate and set national ID
        if (IranianNationalId.isValid(data.getNationalId())) {
            IranianNationalId.NationalIdInfo idInfo = 
                new IranianNationalId.NationalIdInfo(data.getNationalId());
            
            profile.setNationalId(idInfo.getFormattedPersian());
            profile.setProvince(idInfo.getProvinceName());
        }
        
        // Validate and set phone
        if (IranianPhoneValidator.isValidMobile(data.getPhone())) {
            IranianPhoneValidator.PhoneInfo phoneInfo = 
                new IranianPhoneValidator.PhoneInfo(data.getPhone());
            
            profile.setPhone(phoneInfo.getFormatted());
            profile.setOperator(phoneInfo.getOperator().getPersianName());
        }
        
        // Convert and set birthday
        JalaliDate birthday = JalaliDate.fromGregorian(data.getBirthday());
        profile.setBirthday(birthday.format(DateFormat.LONG));
        profile.setAge(calculateAge(birthday));
        
        return profile;
    }
    
    private int calculateAge(JalaliDate birthDate) {
        return (int) birthDate.yearsUntil(JalaliDate.now());
    }
}
```

### Example 24: E-commerce Product Display

```java
public class ProductDisplay {
    public ProductViewModel formatProduct(Product product) {
        ProductViewModel viewModel = new ProductViewModel();
        
        // Format price
        viewModel.setPrice(formatPrice(product.getPrice()));
        viewModel.setPriceInWords(formatPriceWords(product.getPrice()));
        
        // Format file size for digital products
        if (product.getFileSize() > 0) {
            viewModel.setFileSize(
                PersianFileSizeFormatter.formatHumanReadable(product.getFileSize())
            );
        }
        
        // Format dates
        JalaliDate createdDate = JalaliDate.fromGregorian(product.getCreatedAt());
        viewModel.setCreatedDate(createdDate.format(DateFormat.LONG));
        viewModel.setRelativeDate(
            PersianRelativeTimeFormatter.formatRelativeTime(createdDate)
        );
        
        return viewModel;
    }
    
    private String formatPrice(double price) {
        return NumberFormatter.formatCurrency(price, "IRT");
    }
    
    private String formatPriceWords(double price) {
        return NumberToWords.toPersianCurrency((long) price, NumberToWords.TOMAN);
    }
}
```

### Example 25: Appointment Scheduler

```java
public class AppointmentScheduler {
    public Appointment scheduleAppointment(LocalDateTime requestedTime, int durationMinutes) {
        // Convert to Jalali
        JalaliDate appointmentDate = JalaliDate.fromGregorian(requestedTime.toLocalDate());
        
        // Check if it's a working day
        if (appointmentDate.isWeekend() || appointmentDate.isHoliday()) {
            throw new IllegalArgumentException(
                "نمی‌توان در روز تعطیل وقت ملاقات تنظیم کرد"
            );
        }
        
        Appointment appointment = new Appointment();
        appointment.setDate(appointmentDate.format(DateFormat.FULL));
        appointment.setTime(requestedTime.toLocalTime().toString());
        
        // Calculate end time
        LocalDateTime endTime = requestedTime.plusMinutes(durationMinutes);
        appointment.setEndTime(endTime.toLocalTime().toString());
        
        // Set reminder time (30 minutes before)
        LocalDateTime reminderTime = requestedTime.minusMinutes(30);
        appointment.setReminderTime(reminderTime.toString());
        
        return appointment;
    }
    
    public List<JalaliDate> getAvailableDates(JalaliDate startDate, int numberOfDays) {
        return startDate.datesUntil(startDate.plusDays(numberOfDays))
                        .filter(date -> !date.isWeekend() && !date.isHoliday())
                        .collect(Collectors.toList());
    }
}
```

### Example 26: Report Generator

```java
public class ReportGenerator {
    public MonthlyReport generateMonthlyReport(int year, int month) {
        MonthlyReport report = new MonthlyReport();
        
        // Set period
        JalaliDate firstDay = JalaliDate.of(year, month, 1);
        JalaliDate lastDay = firstDay.lastDayOfMonth();
        
        report.setPeriod(String.format("%s تا %s",
            firstDay.format(DateFormat.LONG),
            lastDay.format(DateFormat.LONG)
        ));
        
        // Calculate working days
        long workingDays = JalaliDate.between(firstDay, lastDay)
            .stream()
            .filter(date -> !date.isWeekend() && !date.isHoliday())
            .count();
        
        report.setWorkingDays(
            NumberToWords.toPersian(workingDays) + " روز کاری"
        );
        
        // Set holidays
        List<String> holidays = JalaliDate.between(firstDay, lastDay)
            .stream()
            .filter(JalaliDate::isHoliday)
            .map(date -> date.format(DateFormat.LONG) + " - " + date.getHolidayName())
            .collect(Collectors.toList());
        
        report.setHolidays(holidays);
        
        return report;
    }
}
```

### Example 27: Form Validator

```java
public class FormValidator {
    private List<String> errors = new ArrayList<>();
    
    public boolean validateRegistrationForm(RegistrationForm form) {
        errors.clear();
        
        // Validate National ID
        if (!IranianNationalId.isValid(form.getNationalId())) {
            errors.add("کد ملی نامعتبر است");
        }
        
        // Validate Mobile
        if (!IranianPhoneValidator.isValidMobile(form.getMobile())) {
            errors.add("شماره موبایل نامعتبر است");
        }
        
        // Validate Postal Code
        if (!IranianPostalCode.isValid(form.getPostalCode())) {
            errors.add("کد پستی نامعتبر است");
        }
        
        // Validate IBAN if provided
        if (form.getIban() != null && !form.getIban().isEmpty()) {
            if (!IranianIban.isValid(form.getIban())) {
                errors.add("شماره شبا نامعتبر است");
            }
        }
        
        // Validate Card Number if provided
        if (form.getCardNumber() != null && !form.getCardNumber().isEmpty()) {
            if (!AtmCardValidator.isValid(form.getCardNumber())) {
                errors.add("شماره کارت نامعتبر است");
            }
        }
        
        return errors.isEmpty();
    }
    
    public List<String> getErrors() {
        return errors;
    }
}
```

---

[← Back to Documentation Home](index.md) | [Usage Guide →](usage.md) | [API Reference →](api.md)