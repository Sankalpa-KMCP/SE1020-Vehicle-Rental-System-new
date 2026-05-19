# Folder and File Breakdown: Vehicle Fleet Inventory (Student 3)

* **🎓 Student Identifier:** Student 3
* **🆔 University Registration No.:** IT24100879
* **🌿 Dedicated Git Branch:** `IT24100879---Vehicle-Fleet-Inventory`
* **📁 Package Directory:** `com.sliit.vehiclerentalsystem01.vehicle`

---

## 1. What the Folder Names Mean

Inside the `com.sliit.vehiclerentalsystem01.vehicle` component, classes are structured into standard modular MVC patterns:

```text
com.sliit.vehiclerentalsystem01.vehicle/
├── controller/  <── Request Routers (handles vehicle listings and catalog edits)
├── entity/      <── Domain Entities (encapsulates fleet assets and polymorphic pricing)
├── repository/  <── Persistence Layer (handles vehicles.txt flat-file buffering and CSV parsing)
└── service/     <── Business Logic Layer (enforces inventory checks and deletion safety locks)
```

* **`controller/`**: Houses controllers that handle administrative request routing for inventory controls (registering cars/motorcycles) and render the public catalog search browser page.
* **`entity/`**: Houses the base vehicle abstract entity and the concrete car/motorcycle subclasses. Exposes specific getters and overrides calculation methods.
* **`repository/`**: Houses file database operations, converting Java objects to serialized comma-separated strings inside `data/vehicles.txt`.
* **`service/`**: Houses business rule validators. Checks that a vehicle is not active under an ongoing customer booking before allowing administrators to delete it from the system.

---

## 2. What Exactly the Java Files Do

### A. Controller Tier (`controller/`)
* **[VehicleController.java](../src/main/java/com/sliit/vehiclerentalsystem01/vehicle/controller/VehicleController.java)**:
  Exposes endpoints to view the vehicle catalog, search vehicles, details view, and process administration forms to add, update, or remove fleet inventory assets.

### B. Entity Tier (`entity/`)
* **[Vehicle.java](../src/main/java/com/sliit/vehiclerentalsystem01/vehicle/entity/Vehicle.java)**:
  Abstract base class defining fields (brand, model, daily rate) and declaring abstract methods `getVehicleType()`, `calculateRentalRate()`, `getSpecificDetail()`, and `toCSV()`.
* **[Car.java](../src/main/java/com/sliit/vehiclerentalsystem01/vehicle/entity/Car.java)**:
  Concrete subclass representing cars. Encapsulates `seatingCapacity` and overrides pricing rate structures.
* **[Motorcycle.java](../src/main/java/com/sliit/vehiclerentalsystem01/vehicle/entity/Motorcycle.java)**:
  Concrete subclass representing motorcycles. Encapsulates `helmetIncluded` flag and overrides pricing rate calculations.

### C. Repository Tier (`repository/`)
* **[VehicleRepository.java](../src/main/java/com/sliit/vehiclerentalsystem01/vehicle/repository/VehicleRepository.java)**:
  Encapsulates data access. Reads and parses lines from `data/vehicles.txt`. Detects polymorphic subclass types via the line discriminator prefix (`Car` or `Motorcycle`) and serializes list items.

### D. Service Tier (`service/`)
* **[VehicleService.java](../src/main/java/com/sliit/vehiclerentalsystem01/vehicle/service/VehicleService.java)**:
  Interface outlining vehicle inventory signatures (adding, updating, listing, and deleting vehicles).
* **[VehicleServiceImpl.java](../src/main/java/com/sliit/vehiclerentalsystem01/vehicle/service/VehicleServiceImpl.java)**:
  Implements fleet inventory behaviors: brand listings catalog lookups, and checking active customer reservation bounds to prevent deleting occupied vehicles.

---

## 3. What the Associated HTML Templates Do

These templates reside in the `templates/vehicle` directory:

* **`vehicle-browse.html`**:
  Customer-facing public catalog browsing grid. Features brand, model, and availability filters, displaying pricing details dynamically.
* **`vehicle-dashboard.html`**:
  Admin dashboard listing the system's entire fleet with action buttons to modify or remove specific vehicle assets.
* **`vehicle-form.html`**:
  Form layout mapping vehicle entries and daily rates properties.
* **`vehicle-detail.html`**:
  Details inspection panel displaying dynamic properties depending on whether the vehicle is a Car or Motorcycle.
* **`vehicle-entity-form.html`**:
  HTML segment injecting subtype form inputs dynamically.
* **`vehicle-list.html`**:
  Query list displaying matching inventory search indexes.
* **`add-vehicle.html`**:
  Admin onboarding form to register new vehicles.
* **`edit-vehicle.html`**:
  Admin spec sheet editor to modify existing fleet vehicle configurations.
