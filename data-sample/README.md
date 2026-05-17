# Sample data for DriveAway

The live `data/` folder is **not** committed to Git (see `.gitignore`). Copy these files into a new `data/` folder at the project root before running the app.

## Setup

**Windows (PowerShell)** — from the project root:

```powershell
New-Item -ItemType Directory -Force -Path data
Copy-Item -Path data-sample\*.txt -Destination data\ -Force
```

**macOS / Linux:**

```bash
mkdir -p data
cp data-sample/*.txt data/
```

Then start the app (`mvn spring-boot:run`) and open `http://localhost:8081`.

## Sample logins

| Role        | Username | Password   |
|-------------|----------|------------|
| Admin staff | `admin`  | `admin123` |
| Master admin| `master1`| `master123`|
| Customer    | `ada`    | `pass123`  |

Staff login: `/auth/staff-login` — Customer login: `/auth/login`

## Files

| File | Purpose |
|------|---------|
| `users.txt` | Customers and staff accounts |
| `vehicles.txt` | Cars and motorcycles |
| `bookings.txt` | Sample reservation |
| `payments.txt` | Sample payment |
| `reviews.txt` | Sample review |
| `maintenance.txt` | Maintenance records |
| `staff_access_requests.txt` | Pending access requests (empty by default) |

Your local `data/` edits stay private and are not pushed to the remote repository.
