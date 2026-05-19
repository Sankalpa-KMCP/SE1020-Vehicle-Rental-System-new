# Folder and File Breakdown: Maintenance & Vehicle Availability (Student 6)

* **🎓 Student Identifier:** Student 6
* **🆔 University Registration No.:** IT25100429
* **🌿 Dedicated Git Branch:** `IT25100429---Maintenance-&_Vehicle-Availability`
* **📁 Package Directory:** `com.sliit.vehiclerentalsystem01.maintenance`

---

## 1. What the Folder Names Mean

Inside the `com.sliit.vehiclerentalsystem01.maintenance` component, classes are grouped into standard MVC tiers to support modular decoupling:

```text
com.sliit.vehiclerentalsystem01.maintenance/
├── controller/  <── Request Routers (handles service logs forms and state edits)
├── entity/      <── Domain Entities (defines maintenance records and status constants)
├── repository/  <── Persistence Layer (handles maintenance.txt flat-file buffering and pipe parsing)
└── service/     <── Business Logic Layer (enforces service rules and availability updates)
```

* **`controller/`**: Houses controllers that handle administrative request routing for logging vehicle checkups (GET forms, POST maintenance updates) and render staff-level fleet health tables.
* **`entity/`**: Houses entities representing physical service records and standard status helper states.
* **`repository/`**: Houses file database operations. Reads and serializes maintenance logs inside `data/maintenance.txt` using a specialized pipe-separated (`|`) format.
* **`service/`**: Houses core operational availability logic. Enforces service schedules, updates statuses, and exposes status-checking APIs to block booking operations if a vehicle is under inspection.

---

## 2. What Exactly the Java Files Do

### A. Controller Tier (`controller/`)
* **[MaintenanceController.java](../src/main/java/com/sliit/vehiclerentalsystem01/maintenance/controller/MaintenanceController.java)**:
  Exposes endpoints enabling front desk staff to monitor fleet health, view logs, schedule repair checkups, and submit new maintenance logs.

### B. Entity Tier (`entity/`)
* **[MaintenanceRecord.java](../src/main/java/com/sliit/vehiclerentalsystem01/maintenance/entity/MaintenanceRecord.java)**:
  Domain model containing fields of physical fleet service operations (record ID, vehicle ID, status, last service date, next service date, and inspection comments).
* **[MaintenanceStatus.java](../src/main/java/com/sliit/vehiclerentalsystem01/maintenance/entity/MaintenanceStatus.java)**:
  Utility class defining standard system availability constants (`AVAILABLE`, `IN_USE`, `MAINTENANCE`).

### C. Repository Tier (`repository/`)
* **[MaintenanceRepository.java](../src/main/java/com/sliit/vehiclerentalsystem01/maintenance/repository/MaintenanceRepository.java)**:
  Encapsulates data access. Reads and parses lines from `data/maintenance.txt`. Handles pipe-separated conversions and serializes maintenance record list items.

### D. Service Tier (`service/`)
* **[MaintenanceService.java](../src/main/java/com/sliit/vehiclerentalsystem01/maintenance/service/MaintenanceService.java)**:
  Interface contract declaring maintenance tracking and availability validation signatures.
* **[MaintenanceServiceImpl.java](../src/main/java/com/sliit/vehiclerentalsystem01/maintenance/service/MaintenanceServiceImpl.java)**:
  Implements fleet service rules: validates service dates (last service must precede next service), updates vehicle states, and checks availability (blocking bookings if status is `MAINTENANCE`).

---

## 3. What the Associated HTML Templates Do

These templates reside in the `templates/user` directory:

* **`user/maintenance-dashboard.html`**:
  Staff-facing dashboard showing all fleet assets and active maintenance status markers. Features simple forms to register new inspection entries, update service dates, and change vehicle states.
