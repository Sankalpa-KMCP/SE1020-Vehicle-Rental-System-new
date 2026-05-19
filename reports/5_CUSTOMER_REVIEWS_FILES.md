# Folder and File Breakdown: Customer Reviews (Student 5)

* **🎓 Student Identifier:** Student 5
* **🆔 University Registration No.:** IT25100565
* **🌿 Dedicated Git Branch:** `IT25100565--Customer-Reviews`
* **📁 Package Directory:** `com.sliit.vehiclerentalsystem01.review`

---

## 1. What the Folder Names Mean

Inside the `com.sliit.vehiclerentalsystem01.review` component, classes are structured into standard modular MVC patterns:

```text
com.sliit.vehiclerentalsystem01.review/
├── controller/  <── Request Routers (handles rating forms and moderation replies)
├── entity/      <── Domain Entities (encapsulates comments and defines feedback structures)
├── repository/  <── Persistence Layer (handles reviews.txt flat-file buffering and CSV parsing)
└── service/     <── Business Logic Layer (enforces rating checks and approval transitions)
```

* **`controller/`**: Houses controllers that handle client review forms (GET forms, POST ratings), star dashboard interactions, and staff response editor paths.
* **`entity/`**: Houses the base feedback abstract entity and the concrete customer review subclass. Exposes state fields and validation rules.
* **`repository/`**: Houses file database operations, converting Java objects to serialized comma-separated strings inside `data/reviews.txt`.
* **`service/`**: Houses the business logic for moderating feedback. Validates star ratings ranges, handles approval transitions, and appends support staff responses.

---

## 2. What Exactly the Java Files Do

### A. Controller Tier (`controller/`)
* **[ReviewController.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/controller/ReviewController.java)**:
  Handles client feedback submissions, displays stars feedback listings, and exposes moderation screens enabling support agents to approve/reject comments and add responses.

### B. Entity Tier (`entity/`)
* **[Feedback.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/entity/Feedback.java)**:
  Abstract base class defining fields (ID, comments text) and declaring the abstract method `getReviewType()`.
* **[CustomerReview.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/entity/CustomerReview.java)**:
  Concrete subclass representing review feedback. It captures reviewer name, star rating (1-5), booking ID, vehicle ID, approval status, and staff reply fields.

### C. Repository Tier (`repository/`)
* **[ReviewRepository.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/repository/ReviewRepository.java)**:
  Encapsulates data access. Reads and parses lines from `data/reviews.txt`, and serializes customer review logs as structured, comma-delimited lines.

### D. Service Tier (`service/`)
* **[ReviewService.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/service/ReviewService.java)**:
  Interface outlining review moderation signatures (registering, modifying, approving, and replying to reviews).
* **[ReviewServiceImpl.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/service/ReviewServiceImpl.java)**:
  Implements reviews moderation behaviors: validating star rating bounds (must be between 1 and 5), handling approval states (`PENDING` -> `APPROVED`), and tracking replying staff usernames and timestamps.

---

## 3. What the Associated HTML Templates Do

These templates reside in the `templates/review` directory:

* **`customer-review-form.html`**:
  Customer-facing form to write comments and choose star ratings (1 to 5) for a completed vehicle rental booking.
* **`customer-reviews-list.html`**:
  Displays verified, approved reviews and ratings stars under specific vehicle listings in the catalog.
* **`review-dashboard.html`**:
  Staff-facing moderation console showing all submitted reviews, allowing quick approvals or loading editors to add staff responses.
* **`review-detail.html`**:
  Details card displaying review comments and metadata.
* **`edit-review.html`**:
  Form page for support staff to write and attach response replies to customer comments.
