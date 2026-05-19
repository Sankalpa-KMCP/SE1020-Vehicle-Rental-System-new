# Folder and File Breakdown: Reservations & Bookings (Student 2)

* **🎓 Student Identifier:** Student 2
* **🆔 University Registration No.:** IT25100445
* **🌿 Dedicated Git Branch:** `IT25100445---Reservations-&-Bookings`
* **📁 Package Directory:** `com.sliit.vehiclerentalsystem01.reservation`

---

## 1. What the Folder Names Mean

Inside the `com.sliit.vehiclerentalsystem01.reservation` component, classes are organized into decoupled operational MVC layers:

```text
com.sliit.vehiclerentalsystem01.reservation/
├── controller/  <── Request Routers (handles booking wizards and staff controls)
├── entity/      <── Domain Entities (defines transaction types and booking states)
├── repository/  <── Persistence Layer (handles bookings.txt flat-file buffering and CSV parsing)
└── service/     <── Business Logic Layer (enforces calendar date validations and availability locks)
```

* **`controller/`**: Houses controllers that receive HTTP requests from customers (initiating bookings) or administrators (managing rental schedules), processing forms, and dispatching views.
* **`entity/`**: Houses the abstract and concrete blueprints for transactions and bookings, holding details like vehicle selections, customer associations, duration, and status indicators.
* **`repository/`**: Houses data access components. Restricts raw file streaming (`BufferedReader`, `BufferedWriter`) to this layer, writing records as structured, comma-delimited lines.
* **`service/`**: Houses core transactional validation logic, date checks, double-booking calculations, and reservation updates.

---

## 2. What Exactly the Java Files Do

### A. Controller Tier (`controller/`)
* **[BookingController.java](../src/main/java/com/sliit/vehiclerentalsystem01/reservation/controller/BookingController.java)**:
  Coordinates the customer booking wizard. It handles GET requests to load booking forms, computes daily rental costs dynamically, and receives booking submissions to register reservations.
* **[ReservationAdminController.java](../src/main/java/com/sliit/vehiclerentalsystem01/reservation/controller/ReservationAdminController.java)**:
  Coordinates staff-level reservation views. Exposes dashboards listing all bookings, processes approvals or rejections, handles cancellations, and appends support messages.

### B. Entity Tier (`entity/`)
* **[Transaction.java](../src/main/java/com/sliit/vehiclerentalsystem01/reservation/entity/Transaction.java)**:
  Polymorphic abstract parent model defining transactional metadata (transactionId, customerName, rentalDate) and declaring the abstract method `getTransactionType()`.
* **[Booking.java](../src/main/java/com/sliit/vehiclerentalsystem01/reservation/entity/Booking.java)**:
  Concrete subclass representing a vehicle rental reservation. It encapsulates vehicle ID, duration in days, status (e.g. `CONFIRMED`, `CANCELLED`), and staff messages.

### C. Repository Tier (`repository/`)
* **[ReservationRepository.java](../src/main/java/com/sliit/vehiclerentalsystem01/reservation/repository/ReservationRepository.java)**:
  Manages file access for reservations. Reads and parses lines from `data/bookings.txt`, serializes `Booking` entities to comma-separated CSV rows, and supports legacy pipe-delimited format parsing for backward compatibility.

### D. Service Tier (`service/`)
* **[ReservationService.java](../src/main/java/com/sliit/vehiclerentalsystem01/reservation/service/ReservationService.java)**:
  Interface outlining reservation signatures (making, updating, cancelling, finding, and auditing bookings).
* **[ReservationServiceImpl.java](../src/main/java/com/sliit/vehiclerentalsystem01/reservation/service/ReservationServiceImpl.java)**:
  Implements reservation rules: prevents back-dated rentals, calculates durations, and executes calendar checks to prevent vehicle double bookings.

---

## 3. What the Associated HTML Templates Do

These templates reside in the `templates/booking` directory:

* **`booking-form.html`**:
  Customer-facing multi-step reservation booking wizard. Displays daily rates, accepts rental dates and durations, and gathers driver details.
* **`booking-dashboard.html`**:
  Front desk staff operations dashboard showing all bookings, pending actions, and quick approval/rejection triggers.
* **`booking-detail.html`**:
  Inspected booking card displaying transaction ID, associated customer, vehicle, duration, status, and feedback trails.
* **`booking-list.html`**:
  Standard query results listing records.
* **`cancel-booking.html`**:
  Customer-facing form to request reservation cancellations.
* **`edit-booking.html`**:
  Form for customers to modify their active booking dates or daily durations.
* **`my-bookings.html`**:
  Customer's portal list showing their current, past, and pending reservation history.
* **`search-booking.html`**:
  Staff booking filters panel.
* **`view-all-bookings.html`**:
  Corporate ledger list displaying the system's global reservation indexes.
* **`booking-admin-form.html`**:
  Overriding editor panel for administrators to manually shift reservation parameters.
