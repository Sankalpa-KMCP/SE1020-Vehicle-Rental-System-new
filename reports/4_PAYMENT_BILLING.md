# Component Documentation: Payment & Billing

* **🎓 Student Identifier:** Student 4
* **🆔 University Registration No.:** IT25100385
* **🌿 Dedicated Git Branch:** `IT25100385---Payment-&-Billing`

---

## 1. Subsystem Overview & Purpose
The **Payment & Billing** component manages all financial workflows, invoicing, and transactional logs of the platform. It handles:
* Calculating total rental costs based on rental durations and vehicle daily rates.
* Supporting multiple payment options (Credit/Debit Card and Cash payments) through a polymorphic payment model hierarchy.
* Capturing payment data, including amount, transaction status, and booking identifiers.
* Auditing financial checkout logs through an administrative dashboard.
* Generating and displaying printable customer receipts and invoice sheets.

---

## 2. Component Directory & File Mapping
This component's responsibilities are divided across standard Spring Boot MVC tiers:

* **Entities (Domain Entities):**
  * [Payment.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/entity/Payment.java): Abstract base class defining common fields (`id`, `bookingId`, `amount`, `status`) and declaring abstract methods `getPaymentMethod()` and `getPaymentType()`.
  * [CardPayment.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/entity/CardPayment.java): Concrete subclass representing credit/debit card transactions.
  * [CashPayment.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/entity/CashPayment.java): Concrete subclass representing cash ledger entries.
* **Repositories (Data Access Layer):**
  * [PaymentRepository.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/repository/PaymentRepository.java): Manages file storage serialization, legacy pipe parsing, and CSV operations for card and cash payments.
* **Services (Business Logic):**
  * [PaymentService.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/service/PaymentService.java): Core billing calculation and ledger verification interface.
  * [PaymentServiceImpl.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/service/PaymentServiceImpl.java): Implements duration billing formulas, card parameters validation, and active status ledger controls.
* **Controllers (Request Routers):**
  * [PaymentController.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/controller/PaymentController.java): Coordinates card/cash payment forms, customer receipt page layouts, and the admin ledgers monitor dashboard.
* **Thymeleaf HTML Templates (`templates/payment` folder):**
  * `payment/process-payment.html`: Card details validator and billing payment form view.
  * `payment/customer-payment.html`: Customer-facing checkout gateway form.
  * `payment/customer-billing.html`: Invoice breakdown billing details printable format view.
  * `payment/payment-dashboard.html`: Admin checkout transactional records table view.
  * `payment/payment-detail.html`: Tenders ledgers details inspection card view.
  * `payment/edit-payment.html`: Update invoice status administrative control view.

---

## 3. Academic OOP Rubric Implementation

### A. Abstraction
Declares [Payment.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/entity/Payment.java) as `public abstract class Payment` to define a uniform interface for all monetary settlements, mandating the `getPaymentMethod()` contract:
```java
public abstract class Payment {
    private String id;
    private String bookingId;
    private double amount;
    private String status;

    public abstract String getPaymentMethod();
}
```

### B. Encapsulation
Keep transactional properties `private`. Setters validate data (e.g. checking that the invoice amount is positive) to preserve ledger integrity:
```java
public void setAmount(double amount) {
    if (amount < 0.0) {
        throw new IllegalArgumentException("Payment amount cannot be negative");
    }
    this.amount = amount;
}
```

### C. Inheritance
The payment module adopts a simple, modular class hierarchy:
```text
          Payment (Abstract)
          ├── CardPayment (Concrete - implements Credit/Debit cards)
          └── CashPayment (Concrete - implements Cash register)
```

### D. Polymorphism
Polymorphism is demonstrated by overriding the `getPaymentMethod()` method, which dynamically dispatches the correct payment type label at runtime:
* **Dynamic View Rendering:** In customer billing views, Thymeleaf displays the appropriate payment type dynamically by calling `payment.getPaymentMethod()`.
* **Polymorphic File Serialization:** [PaymentRepository.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/repository/PaymentRepository.java) processes list elements as base `Payment` objects, dynamically writing their subtype indicators (`CARD` or `CASH`) to the flat-file database.

---

## 4. Flat-File Persistence Schema (`data/payments.txt`)
Payment logs are serialized in a comma-delimited format inside `data/payments.txt`. The first column acts as the type discriminator:
* **Record Schema:**
  `CARD/CASH,id,bookingId,amount,status`
* **Sample Entries:**
  `CARD,PAY-001,TXN-101,250.0,PAID`
  `CASH,PAY-002,TXN-102,120.0,PENDING`

---

## 5. Component Workflows

### A. Dynamic Billing Calculation Workflow
```text
[Customer clicks checkout] ──> [PaymentController]
                                       │
                          (calculates duration & total cost)
                                       ▼
    [VehicleService] <──(query daily rate)── [PaymentServiceImpl]
           │                                      │
    (returns rate)                                │
           ▼                                      ▼
  (total = rate * days) ──(confirm tender choice)──> [Save Card/Cash Payment subclass]
                                                           │
                                                [Save via PaymentRepository]
```

---

## 6. Verification and Integration
The component is verified by:
* End-to-end checkout and receipt generation routines.
* Invoicing ledger transitions testing (changing cash status from `PENDING` to `PAID` via the admin dashboard and checking the output in `data/payments.txt`).
