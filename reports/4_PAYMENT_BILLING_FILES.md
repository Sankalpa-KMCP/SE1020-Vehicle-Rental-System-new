# Folder and File Breakdown: Payment & Billing (Student 4)

* **🎓 Student Identifier:** Student 4
* **🆔 University Registration No.:** IT25100385
* **🌿 Dedicated Git Branch:** `IT25100385---Payment-&-Billing`
* **📁 Package Directory:** `com.sliit.vehiclerentalsystem01.payment`

---

## 1. What the Folder Names Mean

Inside the `com.sliit.vehiclerentalsystem01.payment` component, classes are divided into modular operational MVC layers:

```text
com.sliit.vehiclerentalsystem01.payment/
├── controller/  <── Request Routers (handles card/cash forms and invoices)
├── entity/      <── Domain Entities (encapsulates payments and defines card/cash types)
├── repository/  <── Persistence Layer (handles payments.txt flat-file buffering and CSV parsing)
└── service/     <── Business Logic Layer (enforces billing engines and card validators)
```

* **`controller/`**: Houses controllers that handle payment checkouts (GET payment forms, POST details), process transactions, and render customer receipts or administrator ledger dashboards.
* **`entity/`**: Houses the base payment abstract entity and the concrete card/cash payment subclasses. Exposes transactional fields.
* **`repository/`**: Houses data access operations. Reads and serializes polymorphic card/cash payment records as structured, comma-delimited lines in `data/payments.txt`.
* **`service/`**: Houses billing engines. Automatically computes total rental costs based on booking duration and vehicle daily rate, verifies cards parameters, and updates ledger statuses.

---

## 2. What Exactly the Java Files Do

### A. Controller Tier (`controller/`)
* **[PaymentController.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/controller/PaymentController.java)**:
  Handles cash/card checkout forms, processes payments, displays printable breakdown invoices, and exposes administrator ledgers showing total checkout records.

### B. Entity Tier (`entity/`)
* **[Payment.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/entity/Payment.java)**:
  Abstract base class defining fields (booking ID, total amount, status) and declaring abstract methods `getPaymentMethod()` and `getPaymentType()`.
* **[CardPayment.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/entity/CardPayment.java)**:
  Concrete subclass representing credit/debit card transactions.
* **[CashPayment.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/entity/CashPayment.java)**:
  Concrete subclass representing cash register ledger entries.

### C. Repository Tier (`repository/`)
* **[PaymentRepository.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/repository/PaymentRepository.java)**:
  Encapsulates data access. Reads and parses lines from `data/payments.txt`. Recognizes dynamic subtypes using column indicators (`CARD` or `CASH`) and handles flat-file buffering.

### D. Service Tier (`service/`)
* **[PaymentService.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/service/PaymentService.java)**:
  Interface outlining billing calculations, checkout processes, and status overrides.
* **[PaymentServiceImpl.java](../src/main/java/com/sliit/vehiclerentalsystem01/payment/service/PaymentServiceImpl.java)**:
  Implements pricing calculations, card parameters checks, and transactional status modifications (e.g. converting cash status to `PAID` once verified).

---

## 3. What the Associated HTML Templates Do

These templates reside in the `templates/payment` directory:

* **`process-payment.html`**:
  Customer checkout form validating credit/debit card inputs (card number, cvv, and expiry dates) before sending processing request.
* **`customer-payment.html`**:
  Gateway selector page enabling customers to choose between Cash or Online Card payments.
* **`customer-billing.html`**:
  Breakdown invoice statement detailing daily rates, duration in days, tax calculations, and payment summaries in a printable layout.
* **`payment-dashboard.html`**:
  Audit logs table where administrators monitor checkout statuses, total transactions, and cash/card transaction methods.
* **`payment-detail.html`**:
  Details inspection card showing payment IDs and transactional status variables.
* **`edit-payment.html`**:
  Admin overrides panel enabling manual modifications to payment ledgers.
