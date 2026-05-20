# DriveAway: Premium Vehicle Rental & Fleet Management System — Project Breakdown & Class Ownership

This document contains a comprehensive visual file tree of the project. Every class, controller, service, storage repository, entity, and template is labeled with its respective student owner in brackets `[Student 1 - 6]`, or marked as `[Shared/Common]` if it forms the joint core framework of the Spring Boot MVC vehicle rental system.

---

## 👥 Student ID & Component Index

| Student Identifier | University Reg No. | Dedicated Git Branch | Assigned Code Component | Key Functional Focus |
| :--- | :--- | :--- | :--- | :--- |
| **Student 1** | **IT25100142** | `IT25100142---User-Management` | **User Management** | Profile handling, user model subclasses, authentication portals, and admin dashboards. |
| **Student 2** | **IT25100445** | `IT25100445---Reservations-&-Bookings` | **Reservations & Bookings** | Transaction models, booking wizard flows, and rental calendar date conflict validations. |
| **Student 3** | **IT24100879** | `IT24100879---Vehicle-Fleet-Inventory` | **Vehicle Fleet Inventory** | Polymorphic vehicle structures, catalog browsers, and administrative fleet management panels. |
| **Student 4** | **IT25100385** | `IT25100385---Payment-&-Billing` | **Payment & Billing** | Polymorphic payment subclasses (Card/Cash), invoicing engines, and transaction ledger audits. |
| **Student 5** | **IT25100565** | `IT25100565--Customer-Reviews` | **Customer Reviews** | Polymorphic reviews (CustomerReview/Feedback), star ratings, and support staff response trails. |
| **Student 6** | **IT25100429** | `IT25100429---Maintenance-&_Vehicle-Availability` | **Maintenance & Vehicle Availability** | Maintenance log records, service dates tracking, and fleet availability status controls. |

---

## 📂 Project Directory File Tree

```text
VehicleRentalSystem01/
├── pom.xml [Shared/Common] (Maven configuration & central Spring Boot dependencies)
├── .gitignore [Shared/Common] (Git exclusion rules for compiler targets)
├── data/ [Shared/Common] (Central Flat-File Database folder)
│   ├── users.txt [Shared/Common] (Unified User Database CSV File)
│   ├── vehicles.txt [Shared/Common] (Polymorphic Vehicle Database CSV File)
│   ├── bookings.txt [Shared/Common] (Rental Transactions Database CSV File)
│   ├── payments.txt [Shared/Common] (Financial Ledger Billing Database CSV File)
│   ├── reviews.txt [Shared/Common] (Star Ratings & Customer Feedback CSV File)
│   ├── maintenance.txt [Shared/Common] (Vehicle Service logs pipe-delimited flat file)
│   └── staff_access_requests.txt [Shared/Common] (Registration approval ledger CSV file)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── sliit/
│   │   │           └── vehiclerentalsystem01/
│   │   │               ├── VehicleRentalSystemApplication.java [Shared/Common] (Spring Boot system bootstrapper)
│   │   │               │
│   │   │               ├── core/ [Shared/Common] (Config, presentation, security helpers, image storage, exceptions)
│   │   │               │   ├── exceptions/
│   │   │               │   │   ├── StorageException.java [Shared/Common] (Custom I/O storage operation failure exception)
│   │   │               │   │   └── ValidationException.java [Shared/Common] (Custom user/domain inputs validation exception)
│   │   │               │   ├── config/
│   │   │               │   │   ├── WebConfig.java [Shared/Common] (Resource handlers, e.g. /uploads/**)
│   │   │               │   │   └── WebMvcConfig.java [Shared/Common] (Resource handlers, e.g. /avatars/** from data)
│   │   │               │   ├── presentation/
│   │   │               │   │   ├── FleetPreviewItem.java [Shared/Common] (Landing fleet preview row DTO)
│   │   │               │   │   └── WebUIController.java [Shared/Common] (Public landing, about, contact, legal routes)
│   │   │               │   ├── security/
│   │   │               │   │   ├── UserSessionKeys.java [Shared/Common] (HttpSession attribute key constants)
│   │   │               │   │   └── SecurityUtils.java [Shared/Common] (Auth/session helper utilities)
│   │   │               │   └── service/
│   │   │               │       └── ImageStorageService.java [Shared/Common] (Stores uploaded images, e.g. profile pictures)
│   │   │               │
│   │   │               ├── user/ [Student 1] (User Management Feature Domain)
│   │   │               │   ├── controller/
│   │   │               │   │   ├── UserAuthController.java [Student 1] (User sign-in, session auth, register, and logout routes)
│   │   │               │   │   ├── UserController.java [Student 1] (Account profile console routes)
│   │   │               │   │   ├── CustomerAdminController.java [Student 1] (Admin registered customers listing view)
│   │   │               │   │   ├── CustomerPortalController.java [Student 1] (Customer profile, dashboard statistics views)
│   │   │               │   │   ├── StaffAdminController.java [Student 1] (Staff listings and registration manager view)
│   │   │               │   │   ├── UserAdminHomeController.java [Student 1] (Admin central route redirection coordinator)
│   │   │               │   │   ├── UserManagementController.java [Student 1] (Admin direct user records REST views)
│   │   │               │   │   └── MasterConsoleController.java [Student 1] (System master setup and overriding controller)
│   │   │               │   ├── entity/
│   │   │               │   │   ├── User.java [Student 1] (Polymorphic abstract parent for all system users)
│   │   │               │   │   ├── Customer.java [Student 1] (Concrete user guest subclass with driving license details)
│   │   │               │   │   ├── Staff.java [Student 1] (Polymorphic abstract parent for operations personnel)
│   │   │               │   │   ├── AdminStaff.java [Student 1] (Concrete manager staff subclass)
│   │   │               │   │   └── SupportStaff.java [Student 1] (Concrete support agent subclass)
│   │   │               │   ├── repository/
│   │   │               │   │   └── UserRepository.java [Student 1] (CSV User Database Flat-File Repository persistence layer)
│   │   │               │   └── service/
│   │   │               │       ├── UserService.java [Student 1] (Core user manager operations abstraction)
│   │   │               │       ├── UserServiceImpl.java [Student 1] (UserService interface implementation)
│   │   │               │       └── MasterAdminPendingService.java [Student 1] (Support staff access activation service)
│   │   │               │
│   │   │               ├── reservation/ [Student 2] (Reservations & Bookings Feature Domain)
│   │   │               │   ├── controller/
│   │   │               │   │   ├── BookingController.java [Student 2] (Customer booking wizard and listing routes)
│   │   │               │   │   └── ReservationAdminController.java [Student 2] (Staff reservation list and status handlers)
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Transaction.java [Student 2] (Polymorphic abstract parent for transactional records)
│   │   │               │   │   └── Booking.java [Student 2] (Concrete vehicle rental reservation subclass)
│   │   │               │   ├── repository/
│   │   │               │   │   └── ReservationRepository.java [Student 2] (CSV Bookings Flat-File Repository persistence layer)
│   │   │               │   └── service/
│   │   │               │       ├── ReservationService.java [Student 2] (Date boundary controls and scheduler logic contract)
│   │   │               │       └── ReservationServiceImpl.java [Student 2] (ReservationService interface implementation)
│   │   │               │
│   │   │               ├── vehicle/ [Student 3] (Vehicle Fleet Inventory Feature Domain)
│   │   │               │   ├── controller/
│   │   │               │   │   └── VehicleController.java [Student 3] (Admin fleet list, addition and editor views routes)
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Vehicle.java [Student 3] (Polymorphic abstract parent: id, brand, model, dailyRate, imagePath)
│   │   │               │   │   ├── Car.java [Student 3] (Concrete passenger car subclass with seating capacity)
│   │   │               │   │   └── Motorcycle.java [Student 3] (Concrete motorcycle subclass with helmet included flag)
│   │   │               │   ├── repository/
│   │   │               │   │   └── VehicleRepository.java [Student 3] (CSV Vehicles Flat-File Repository persistence layer)
│   │   │               │   └── service/
│   │   │               │       ├── VehicleService.java [Student 3] (Inventory control and rates calculations contract)
│   │   │               │       └── VehicleServiceImpl.java [Student 3] (VehicleService interface implementation)
│   │   │               │
│   │   │               ├── payment/ [Student 4] (Payment & Billing Feature Domain)
│   │   │               │   ├── controller/
│   │   │               │   │   └── PaymentController.java [Student 4] (Card/Cash billing input forms and receipt view routes)
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Payment.java [Student 4] (Polymorphic abstract parent for transaction payment records)
│   │   │               │   │   ├── CardPayment.java [Student 4] (Polymorphic credit/debit card transaction subclass)
│   │   │               │   │   ├── CashPayment.java [Student 4] (Polymorphic cash ledger subclass)
│   │   │               │   │   └── PaymentStatus.java [Shared/Common] (Canonical payment lifecycle enum and string parsing)
│   │   │               │   ├── repository/
│   │   │               │   │   └── PaymentRepository.java [Student 4] (CSV Payments Flat-File Repository persistence layer)
│   │   │               │   └── service/
│   │   │               │       ├── PaymentService.java [Student 4] (Ledger audit routines and billing engine contract)
│   │   │               │       └── PaymentServiceImpl.java [Student 4] (PaymentService interface implementation)
│   │   │               │
│   │   │               ├── review/ [Student 5] (Customer Reviews Feature Domain)
│   │   │               │   ├── controller/
│   │   │               │   │   └── ReviewController.java [Student 5] (Reviews writer form and star rating dashboard routes)
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Feedback.java [Student 5] (Polymorphic abstract parent for customer comments)
│   │   │               │   │   └── CustomerReview.java [Student 5] (Concrete star rating feedback subclass with reply trails)
│   │   │               │   ├── repository/
│   │   │               │   │   └── ReviewRepository.java [Student 5] (CSV Reviews Flat-File Repository persistence layer)
│   │   │               │   └── service/
│   │   │               │       ├── ReviewService.java [Student 5] (Public rating displays and staff reply contract)
│   │   │               │       └── ReviewServiceImpl.java [Student 5] (ReviewService interface implementation)
│   │   │               │
│   │   │               └── maintenance/ [Student 6] (Maintenance & Vehicle Availability Domain)
│   │   │                   ├── controller/
│   │   │                   │   └── MaintenanceController.java [Student 6] (Vehicle service status logs and entries logging routes)
│   │   │                   ├── entity/
│   │   │                   │   ├── MaintenanceRecord.java [Student 6] (Vehicle maintenance log history data structure)
│   │   │                   │   └── MaintenanceStatus.java [Student 6] (AVAILABILITY status constants record helper)
│   │   │                   ├── repository/
│   │   │                   │   └── MaintenanceRepository.java [Student 6] (Maintenance file persistence mapping layer)
│   │   │                   └── service/
│   │   │                       ├── MaintenanceService.java [Student 6] (Maintenance status controls and record updates)
│   │   │                       └── MaintenanceServiceImpl.java [Student 6] (MaintenanceService interface implementation)
│   │   │
│   │   └── resources/
│   │       ├── application.properties [Shared/Common] (Server port configs, Thymeleaf parameters, persistent directories)
│   │       │
│   │       ├── static/ [Shared/Common] (Public CSS, JS, images; app and marketing bundles)
│   │       │   ├── css/ [Shared/Common] (Shared and admin styles, e.g. style.css)
│   │       │   ├── js/ [Shared/Common] (Shared scripts, e.g. main.js)
│   │       │   └── landing/ [Shared/Common] (DriveAway marketing UI: css/, js/, tokens, sections — many files; see repo)
│   │       │
│   │       └── templates/ (Thymeleaf HTML templates)
│   │           ├── fragments/ [Shared/Common]
│   │           │   ├── footer.html [Shared/Common] (Dynamic footer signature elements)
│   │           │   └── layout.html [Shared/Common] (Global responsive UI framework skeleton wrapper)
│   │           │
│   │           ├── landing/ [Shared/Common] (Central system promotional landing page templates)
│   │           │   ├── index.html [Shared/Common] (Active marketing portal dashboard home)
│   │           │   └── fragments/
│   │           │       ├── footer.html [Shared/Common] (Landing page footer)
│   │           │       ├── nav.html [Shared/Common] (Landing page menu)
│   │           │       ├── overlays/
│   │           │       │   ├── loader.html [Shared/Common] (Responsive transition load screens)
│   │           │       │   └── welcome.html [Shared/Common] (First-time user welcome pop-up dialog)
│   │           │       └── sections/
│   │           │           ├── advantage.html [Shared/Common] (Marketing perks presentation grid)
│   │           │           ├── experience.html [Shared/Common] (Experience summary numbers blocks)
│   │           │           ├── faq.html [Shared/Common] (Interactive support accordions)
│   │           │           ├── fleet.html [Shared/Common] (Premium catalog browse section)
│   │           │           ├── hero.html [Shared/Common] (Main eye-catching brand introduction)
│   │           │           ├── how-it-works.html [Shared/Common] (Workflow onboarding guide panels)
│   │           │           ├── locations.html [Shared/Common] (Pickup points locator catalog)
│   │           │           ├── partners.html [Shared/Common] (Collaborating brands logos slider)
│   │           │           ├── stats.html [Shared/Common] (Fleet size & customer base counter)
│   │           │           ├── team.html [Shared/Common] (Student developers profiles panel segment)
│   │           │           └── testimonials.html [Shared/Common] (Ratings previews showcase segment)
│   │           │
│   │           ├── public/ [Shared/Common] (Legal & Static catalogs)
│   │           │   ├── about.html [Shared/Common] (DriveAway brand history catalog view)
│   │           │   ├── contact.html [Shared/Common] (Location maps and contact sheet view)
│   │           │   ├── privacy.html [Shared/Common] (Academic compliance privacy clauses)
│   │           │   └── terms.html [Shared/Common] (Rental lease rules statement layout)
│   │           │
│   │           ├── auth/ [Student 1] (Authentication portal layouts)
│   │           │   └── register.html [Student 1] (Unified customer registration form)
│   │           │
│   │           ├── user/ [Student 1] (Guest Management thymeleaf templates)
│   │           │   ├── login.html [Student 1] (Unified customer sign-in panel view)
│   │           │   ├── staff-login.html [Student 1] (Staff sign-in credentials validation view)
│   │           │   ├── customer-dashboard.html [Student 1] (Personal active rentals and settings card view)
│   │           │   ├── staff-dashboard.html [Student 1] (Front desk booking approvals table view)
│   │           │   ├── admin-dashboard.html [Student 1] (System revenue, inventory, and stats view)
│   │           │   ├── admin-home.html [Student 1] (Admin home dashboard entry point)
│   │           │   ├── maintenance-dashboard.html [Student 1] (Operational fleet service status boards view)
│   │           │   ├── customer-directory.html [Student 1] (Admin customer accounts listing table view)
│   │           │   ├── staff-directory.html [Student 1] (Admin operating team roster table view)
│   │           │   ├── user-form.html [Student 1] (Admin onboard new personnel console view)
│   │           │   ├── user-detail.html [Student 1] (Detailed user profiles inspection panel view)
│   │           │   ├── user-list.html [Student 1] (Unified admin database query wrapper view)
│   │           │   ├── user-management.html [Student 1] (Admin role privileges allocation view)
│   │           │   ├── edit-customer.html [Student 1] (Customer profile registration details edit form view)
│   │           │   ├── edit-staff.html [Student 1] (Staff contact updates edit form view)
│   │           │   ├── customer-support.html [Student 1] (Customer inquiries submissions inbox view)
│   │           │   ├── master-console.html [Student 1] (System overrides master dashboard view)
│   │           │   └── error.html [Shared/Common] (Unified fallback request failure handler view)
│   │           │
│   │           ├── booking/ [Student 2] (Reservations & Bookings thymeleaf templates)
│   │           │   ├── booking-admin-form.html [Student 2] (Admin booking modification panel)
│   │           │   ├── booking-dashboard.html [Student 2] (Staff central reservations dashboard)
│   │           │   ├── booking-detail.html [Student 2] (Detailed reservation metadata review card)
│   │           │   ├── booking-form.html [Student 2] (Multi-step customer reservation wizard form)
│   │           │   ├── booking-list.html [Student 2] (Central booking index view list)
│   │           │   ├── cancel-booking.html [Student 2] (Customer reservation cancellation form)
│   │           │   ├── edit-booking.html [Student 2] (Customer booking date/duration update wizard)
│   │           │   ├── my-bookings.html [Student 2] (Customer's personal reservations listing panel)
│   │           │   ├── search-booking.html [Student 2] (Interactive reservations query panel)
│   │           │   └── view-all-bookings.html [Student 2] (Support/Admin total reservations overview)
│   │           │
│   │           ├── vehicle/ [Student 3] (Vehicle Inventory thymeleaf templates)
│   │           │   ├── vehicle-browse.html [Student 3] (Dynamic vehicle catalog display & search card view)
│   │           │   ├── vehicle-dashboard.html [Student 3] (Admin catalog overview and deletion panels view)
│   │           │   ├── vehicle-form.html [Student 3] (Admin add/edit vehicle details entry view)
│   │           │   ├── vehicle-detail.html [Student 3] (Polymorphic vehicle details inspection panel view)
│   │           │   ├── vehicle-entity-form.html [Student 3] (Subclass custom properties layout block view)
│   │           │   ├── vehicle-list.html [Student 3] (Inline fleet catalog lists view)
│   │           │   ├── add-vehicle.html [Student 3] (Admin new fleet registration console view)
│   │           │   └── edit-vehicle.html [Student 3] (Admin edit existing vehicle specification console view)
│   │           │
│   │           ├── payment/ [Student 4] (Payment Processing thymeleaf templates)
│   │           │   ├── process-payment.html [Student 4] (Card details validator and billing payment form view)
│   │           │   ├── customer-payment.html [Student 4] (Customer-facing checkout gateway form)
│   │           │   ├── customer-billing.html [Student 4] (Invoice breakdown billing details printable format view)
│   │           │   ├── payment-dashboard.html [Student 4] (Admin checkout transactional records table view)
│   │           │   ├── payment-detail.html [Student 4] (Tenders ledgers details inspection card view)
│   │           │   └── edit-payment.html [Student 4] (Update invoice status administrative control view)
│   │           │
│   │           └── review/ [Student 5] (Customer Review thymeleaf templates)
│   │               ├── customer-review-form.html [Student 5] (Customer submit vehicle feedback rating view)
│   │               ├── customer-reviews-list.html [Student 5] (Vehicle profile reviews list view)
│   │               ├── review-dashboard.html [Student 5] (Staff reviews moderation and replies list view)
│   │               ├── review-detail.html [Student 5] (Review comment and ratings inspect card view)
│   │               └── edit-review.html [Student 5] (Add admin feedback reply form view)
```
