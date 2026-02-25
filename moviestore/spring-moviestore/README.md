# MovieStore Spring Boot + Thymeleaf

This folder contains a Spring Boot migration of the original MERN MovieStore app.

## Stack
- Java 17
- Spring Boot 3 (Web, Security, Data JPA, Validation, Mail)
- Thymeleaf (server-rendered HTML/CSS/JS)
- MySQL database
- ZXing for QR generation

## Implemented features
- Authentication (register/login/logout)
- Roles: `GUEST`, `ADMIN`, `SUPERADMIN`
- Public pages: home, movie details, visual seat-grid booking, my dashboard, public check-in
- Admin pages: dashboard, movies, cinemas, showtimes, reservations
- Superadmin page: user role management
- Image upload for users, movies, and cinemas
- QR code generation per reservation
- Invitation emails with reservation details + QR/check-in link

## Run
1. Open terminal in `spring-moviestore`
2. Run:
   ```bash
   mvn spring-boot:run
   ```
3. Open: `http://localhost:8080`

## Seed users
- Superadmin: `superadmin` / `password123`
- Admin: `admin` / `password123`
- Guest: `guest` / `password123`

## Mail configuration (required for invitations)
Set environment variables before running:
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- optional: `MAIL_HOST`, `MAIL_PORT`, `MAIL_FROM`, `APP_BASE_URL`

Example (PowerShell):
```powershell
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="app-password"
$env:MAIL_FROM="your-email@gmail.com"
$env:APP_BASE_URL="http://localhost:8080"
mvn spring-boot:run
```

## MySQL configuration (required)
Set environment variables before running:
- `DB_HOST` (default `localhost`)
- `DB_PORT` (default `3306`)
- `DB_NAME` (default `moviestore`)
- `DB_USERNAME` (default `root`)
- `DB_PASSWORD` (default `root`)

Example (PowerShell):
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="moviestore"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
mvn spring-boot:run
```

## Notes
- Uploads are stored under `spring-moviestore/uploads/` and served via `/uploads/**`.
- This migration keeps your original MERN project unchanged.


