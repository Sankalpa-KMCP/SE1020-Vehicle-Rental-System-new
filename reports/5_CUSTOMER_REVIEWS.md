# Component Documentation: Customer Reviews

* **🎓 Student Identifier:** Student 5
* **🆔 University Registration No.:** IT25100565
* **🌿 Dedicated Git Branch:** `IT25100565--Customer-Reviews`

---

## 1. Subsystem Overview & Purpose
The **Customer Reviews** component manages customer feedback, star ratings, and staff moderation panels. It handles:
* Collecting customer star ratings and written reviews.
* Verifying ratings constraints (ensuring star values are between 1 and 5).
* Displaying vehicle feedback listings on rental catalogs.
* Providing a staff moderation console to approve or reject pending reviews.
* Enabling support staff to reply to customer comments, tracking the replier's username and response timestamp.

---

## 2. Component Directory & File Mapping
This component's responsibilities are divided across standard Spring Boot MVC tiers:

* **Entities (Domain Entities):**
  * [Feedback.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/entity/Feedback.java): Abstract base class defining common metadata and declaring the abstract method `getReviewType()`.
  * [CustomerReview.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/entity/CustomerReview.java): Concrete subclass representing reviews. It captures reviewer name, star rating (1-5), comments, approval status, staff reply fields, and an optional **review image URL** (`imageUrl`) when present in the CSV.
* **Repositories (Data Access Layer):**
  * [ReviewRepository.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/repository/ReviewRepository.java): Coordinates CSV file operations, parsing, and serialization of rating comments.
* **Services (Business Logic):**
  * [ReviewService.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/service/ReviewService.java): Coordinates moderation, approval states, and reply tracking contract.
  * [ReviewServiceImpl.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/service/ReviewServiceImpl.java): Implements moderation transition rules, rating boundary controls, and staff reply trail tracking.
* **Controllers (Request Routers):**
  * [ReviewController.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/controller/ReviewController.java): Coordinates review submissions, the star rating dashboard, and staff response editors.
* **Thymeleaf HTML Templates (`templates/review` folder):**
  * `review/customer-review-form.html`: Customer submit vehicle feedback rating view.
  * `review/customer-reviews-list.html`: Vehicle profile reviews list view.
  * `review/edit-review.html`: Add admin feedback reply form view.
  * `review/review-dashboard.html`: Staff reviews moderation and replies list view.
  * `review/review-detail.html`: Review comment and ratings inspect card view.

---

## 3. Academic OOP Rubric Implementation

### A. Abstraction
Declares [Feedback.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/entity/Feedback.java) as `public abstract class Feedback` to establish a standard interface for comments and ratings, mandating the abstract `getReviewType()` method:
```java
public abstract class Feedback {
    private String id;
    private String comments;

    public abstract String getReviewType();
}
```

### B. Encapsulation
Star ratings are validated to ensure values remain within the valid [1 - 5] range:
```java
public void setRating(int rating) {
    if (rating < 1 || rating > 5) {
        throw new IllegalArgumentException("Star rating must be between 1 and 5");
    }
    this.rating = rating;
}
```

### C. Inheritance
The review module implements a standard class hierarchy:

```mermaid
classDiagram
    class Feedback {
        <<abstract>>
        -String id
        -String comments
        +getReviewType()* String
    }
    class CustomerReview {
        -String reviewerName
        -int rating
        -String status
        -String bookingId
        -String vehicleId
        -String staffReply
        -String repliedBy
        -LocalDateTime repliedAt
        -String imageUrl
        +getReviewType() String
    }
    Feedback <|-- CustomerReview
```

### D. Polymorphism
Polymorphism is demonstrated through dynamic dispatch of `getReviewType()` and by dynamically rendering UI star sliders and average rating counters based on ratings scores at runtime.

---

## 4. Flat-File Persistence Schema (`data/reviews.txt`)
Reviews are serialized in a comma-delimited format inside `data/reviews.txt`:
* **Record Schema (10 columns, minimum):**
  `reviewId,reviewerName,rating,comments,status,bookingId,vehicleId,staffReply,repliedBy,repliedAt`
* **Optional 11th column:** `imageUrl` (parsed by `CustomerReview.fromCsvLine` when present).
* **Sample Entry (with optional image URL):**
  `REV-001,customer,5,Smooth ride,PUBLISHED,TXN-101,VEH-101,Thank you!,support,2026-05-21T10:00:00,/uploads/reviews/sample.jpg`

### Persistence & concurrency notes
[ReviewRepository.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/repository/ReviewRepository.java) reads and writes `data/reviews.txt` with `java.nio.file.Files` (full rewrite on `saveAll`). Unlike some other repositories in this project, it **does not** currently use `ReentrantReadWriteLock`; concurrent updates should be treated as best-effort at the file level.

* **Decoupled architecture (Single Responsibility Principle):** [ReviewServiceImpl.java](../src/main/java/com/sliit/vehiclerentalsystem01/review/service/ReviewServiceImpl.java) validates ratings and orchestrates saves; the repository handles CSV encoding, optional legacy pipe rows, and the header comment line documented in code.
* **Fault tolerance:** Malformed lines are skipped during load so one bad row does not break the reviews UI.

---

## 5. Component Workflows

### A. Customer feedback submission sequence
```text
[Customer submits review] ──(HTTP POST /customer/reviews)──> [CustomerPortalController]
                                                                    │
                                                         (validate booking / limits, optional image upload)
                                                                    ▼
       [ReviewServiceImpl.save] ──> [ReviewRepository.saveAll] ──> [Truncate + rewrite reviews.txt]
```
New customer reviews are created with status **`PUBLISHED`** in [CustomerPortalController](../src/main/java/com/sliit/vehiclerentalsystem01/user/controller/CustomerPortalController.java). Staff use [ReviewController](../src/main/java/com/sliit/vehiclerentalsystem01/review/controller/ReviewController.java) under `/reviews` for listing/detail and staff-reply updates.

---

## 6. Verification and Integration
The component is verified by:
* Testing review submissions on completed vehicle bookings, asserting that a customer cannot write a review without an active/completed booking.
* Validating moderation transitions via the review dashboard, checking that staff replies append and update the CSV record in `data/reviews.txt`.
