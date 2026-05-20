# Folder and File Breakdown: User Management (Student 1)

* **🎓 Student Identifier:** Student 1
* **🆔 University Registration No.:** IT25100142
* **🌿 Dedicated Git Branch:** `IT25100142---User-Management`
* **📁 Package Directory:** `com.sliit.vehiclerentalsystem01.user`

---

## 1. What the Folder Names Mean

Inside the `com.sliit.vehiclerentalsystem01.user` component, classes are grouped into four distinct operational directories to support decoupling and MVC design:

```text
com.sliit.vehiclerentalsystem01.user/
├── controller/  <── Request Routers (handles HTTP endpoints, session attributes)
├── entity/      <── Domain Entities (encapsulates state and defines user inheritance)
├── repository/  <── Persistence Layer (manages users.txt flat-file buffering and CSV parsing)
└── service/     <── Business Logic Layer (implements login hashing, role checks, and user lifecycle rules)
```

* **`controller/`**: Houses controllers that receive HTTP requests (GET, POST), validate request parameters, bind session data (e.g. tracking logged-in user names), and return Thymeleaf template paths or redirect URLs.
* **`entity/`**: Houses polymorphic model structures that define the blueprint of users. Contains inheritance branches separating customers from operational staff.
* **`repository/`**: Houses file access storage components. It isolates raw I/O (`BufferedReader`, `BufferedWriter`, file streams) from services and controllers, providing clean database interfaces.
* **`service/`**: Houses the heavy operational business logic. It handles credential security (password matching), session-related rules, and validation. Initial user data lives in `data/users.txt` (maintained by the team), not auto-generated here on empty file.

---

## 2. What Exactly the Java Files Do

### A. Controller Tier (`controller/`)
* **[UserAuthController.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/controller/UserAuthController.java)**:
  Handles user session logins, login screens, customer registrations, and session terminations. It tracks active session states by saving authenticated user objects inside HTTP sessions.
* **[UserController.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/controller/UserController.java)**:
  Coordinates user profile views, contact modifications, and personal settings changes.
* **[CustomerAdminController.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/controller/CustomerAdminController.java)**:
  Exposes administrative views listing all registered customer profiles. Enables support staff to view customer records.
* **[CustomerPortalController.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/controller/CustomerPortalController.java)**:
  Coordinates personal customer dashboards, display summaries of active leases, account balances, and personal navigation links.
* **[StaffAdminController.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/controller/StaffAdminController.java)**:
  Admin console managing staff rosters, onboarding staff and allocating operational support roles.
* **[UserAdminHomeController.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/controller/UserAdminHomeController.java)**:
  A central administrator home entry dispatcher, redirecting personnel to specialized staff or manager dashboards.
* **[UserManagementController.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/controller/UserManagementController.java)**:
  A REST/MVC controller exposing CRUD endpoints to directly manage raw user profiles, permissions, and direct exclusions.
* **[MasterConsoleController.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/controller/MasterConsoleController.java)**:
  Provides master-console routes for privileged configuration and diagnostics as implemented in the controller.

### B. Entity Tier (`entity/`)
* **[User.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/entity/User.java)**:
  Polymorphic abstract parent model defining credentials (id, name, email, username, password) and dynamic role interfaces via `getUserRole()`.
* **[Customer.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/entity/Customer.java)**:
  Concrete subclass representing a customer. Encapsulates NIC, driver's license number, and personal phone properties. Exposes serializing CSV methods.
* **[Staff.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/entity/Staff.java)**:
  Abstract subclass representing operating personnel. Exposes operational metrics.
* **[AdminStaff.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/entity/AdminStaff.java)**:
  Concrete subclass representing administrators with managers' privileges. Returns `ADMIN` role.
* **[SupportStaff.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/entity/SupportStaff.java)**:
  Concrete subclass representing front desk and support workers. Returns `SUPPORT` role.

### C. Repository Tier (`repository/`)
* **[UserRepository.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/repository/UserRepository.java)**:
  Encapsulates flat-file storage manipulation. Implements thread-safe buffering, parses polymorphic CSV columns, performs customer/staff profile reads, and serializes user records to `data/users.txt`.

### D. Service Tier (`service/`)
* **[UserService.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/service/UserService.java)**:
  Interface outlining user manager signatures (login, register, update, find, delete).
* **[UserServiceImpl.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/service/UserServiceImpl.java)**:
  Implements user behaviors: password hashing, login verification, duplicate checks, and CRUD orchestration against `UserRepository`.
* **[MasterAdminPendingService.java](../src/main/java/com/sliit/vehiclerentalsystem01/user/service/MasterAdminPendingService.java)**:
  Handles operating staff registrations pending administrator approval. It reads and writes entries in `data/staff_access_requests.txt`.

---

## 3. What the Associated HTML Templates Do

* **`templates/auth/register.html`**:
  Customer-facing sign-up wizard capturing new usernames, passwords, licenses, NICs, and contact details.
* **`templates/user/login.html`**:
  Customer login card validating credentials.
* **`templates/user/staff-login.html`**:
  Operational personnel gateway card separating public traffic from corporate backend dashboards.
* **`templates/user/customer-dashboard.html`**:
  Personal account dashboard displaying current rentals, feedback links, billing invoices, and statistics.
* **`templates/user/staff-dashboard.html`**:
  Renders the front-office table where staff view active reservations, verify customers, and moderate reviews.
* **`templates/user/admin-dashboard.html`**:
  Visualizes metrics, total system revenue, registered users, and active fleet capacities.
* **`templates/user/admin-home.html`**:
  Welcome dashboard router redirecting users to specialized operational consoles.
* **`templates/user/customer-directory.html`**:
  Admin customer index search grid.
* **`templates/user/staff-directory.html`**:
  Renders staff directory showing contact information for administrators.
* **`templates/user/user-form.html`**:
  Form for administrators to onboard new personnel.
* **`templates/user/user-detail.html`**:
  Detailed customer or staff profile card panel view.
* **`templates/user/user-list.html`**:
  Dynamic index wrappers checking raw system registries.
* **`templates/user/user-management.html`**:
  Coordinates direct user deletes or manual authorization upgrades.
* **`templates/user/edit-customer.html`**:
  Customer profile updates console.
* **`templates/user/edit-staff.html`**:
  Staff profiles specifications editor.
* **`templates/user/customer-support.html`**:
  Renders client feedback and communication interfaces.
* **`templates/user/master-console.html`**:
  System administrator diagnostics overrides screen.
