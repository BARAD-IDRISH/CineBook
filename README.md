# CineBook - Cinema Booking System

A modern, full-featured cinema booking application built with Spring Boot 3, Thymeleaf, and MySQL. CineBook provides a complete solution for managing movie theaters, showtimes, user bookings, and cinema administration.

**Repository**: [https://github.com/BARAD-IDRISH/CineBook](https://github.com/BARAD-IDRISH/CineBook)

## 🎬 Features

### For Users
- **Movie Browsing**: Browse available movies with detailed information and categories
- **Cinema Locations**: View all available cinema locations with details
- **Advanced Booking**: Book movie tickets with seat selection and visual seat grid
- **Payment Processing**: Secure payment integration for ticket purchases
- **QR Code Check-in**: Generate and scan QR codes for entry verification
- **User Dashboard**: Manage bookings and view booking history
- **Profile Management**: Update user account information
- **Invitation Emails**: Receive reservation details and QR codes via email

### For Administrators
- **Dashboard**: Overview of all system activities and statistics
- **Movie Management**: Add, edit, and manage movies with poster uploads
- **Cinema Management**: Manage cinema locations and details
- **Screen Management**: Configure cinema screens and seating arrangements
- **Showtime Management**: Create and manage movie showtimes
- **Reservation Management**: View and manage all user reservations
- **User Management**: Manage user accounts, roles, and permissions
- **Account Management**: Administrative account settings

### Core Features
- **User Authentication**: Secure login and registration system
- **Role-Based Access Control**: Superadmin, Admin, and Guest roles
- **Database Persistence**: MySQL database for reliable data storage
- **File Uploads**: Support for movie posters, user profile pictures, and QR codes
- **Responsive Design**: Mobile-friendly user interface
- **Email Integration**: Email notifications with invitation links and QR codes
- **QR Code Generation**: Automatic QR code creation for check-ins using ZXing

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.3.2
- **Template Engine**: Thymeleaf (server-rendered HTML/CSS/JS)
- **Database**: MySQL 8.0+
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security
- **Build Tool**: Maven
- **Java Version**: JDK 17
- **QR Code Generation**: Google Zxing Library (core & javase)
- **Email**: Spring Boot Mail Starter

### Key Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Thymeleaf
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Validation
- Spring Boot Starter Mail
- MySQL Connector/J
- Google Zxing Library (QR Codes)

## 📋 Project Structure

```
spring-moviestore/
├── src/
│   ├── main/
│   │   ├── java/com/moviestore/app/
│   │   │   ├── MovieStoreApplication.java (Main entry point)
│   │   │   ├── config/ (Configuration classes)
│   │   │   ├── controller/ (REST controllers)
│   │   │   ├── dto/ (Data Transfer Objects)
│   │   │   ├── model/ (Entity models)
│   │   │   ├── repository/ (Data access layer)
│   │   │   └── service/ (Business logic)
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/ (CSS, JavaScript)
│   │       └── templates/ (Thymeleaf templates)
├── pom.xml (Maven configuration)
└── README.md (This file)
```

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Apache Maven 3.6+
- MySQL Server 8.0+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/BARAD-IDRISH/CineBook.git
   cd CineBook/spring-moviestore
   ```

2. **Configure Database**
   Edit `src/main/resources/application.properties` and update MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/moviestore
   spring.datasource.username=your_db_username
   spring.datasource.password=your_db_password
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   The application will start at `http://localhost:8080`

## 🔐 Default Credentials

Initial admin account (configure in `DataInitializer.java`):
- **Username**: admin@cinebook.com
- **Password**: admin123

*Note: Change default credentials in production*

## 📁 Directory Structure

### Key Directories

- **config/**: Spring configuration classes
  - `DataInitializer.java` - Initialize sample data
  - `GlobalModelAttributes.java` - Global model attributes setup
  - `SecurityConfig.java` - Spring Security configuration
  - `WebConfig.java` - Web configuration

- **controller/**: Request handlers
  - `AdminController.java` - Admin panel endpoints
  - `AuthController.java` - Authentication endpoints
  - `PublicController.java` - Public page endpoints

- **service/**: Business logic layer
  - Service classes handling core business operations

- **repository/**: Data access layer
  - JPA repository interfaces

- **model/**: Entity models
  - Database entity classes

- **dto/**: Data Transfer Objects
  - Form objects and API DTOs

- **templates/**: Thymeleaf HTML templates
  - `admin/` - Admin panel pages
  - `public/` - Public-facing pages
  - `fragments/` - Reusable template fragments

- **static/**: Front-end resources
  - `css/` - Stylesheets
  - `js/` - JavaScript files

- **uploads/**: Dynamic file storage
  - `movies/` - Movie poster images
  - `users/` - User profile pictures
  - `qrcodes/` - Generated QR codes
  - `cinemas/` - Cinema images

## 🌐 API Endpoints

### Public Routes
- `GET /` - Home page
- `GET /login` - Login page
- `GET /register` - Registration page
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /movies` - View all movies
- `GET /cinemas` - View all cinemas
- `GET /movie/{id}` - Movie details

### Admin Routes (Requires Authentication)
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/movies` - Manage movies
- `GET /admin/cinemas` - Manage cinemas
- `GET /admin/screens` - Manage screens
- `GET /admin/showtimes` - Manage showtimes
- `GET /admin/reservations` - View reservations
- `GET /admin/users` - Manage users

### User Routes (Requires Authentication)
- `GET /user/dashboard` - User dashboard
- `GET /booking` - Booking page
- `POST /api/booking/reserve` - Make reservation
- `GET /payment` - Payment page
- `GET /checkin` - Check-in page

## 🔑 Seed Users

Default users available for testing (configure in `DataInitializer.java`):
- **Superadmin**: `superadmin` / `password123` - Full system access
- **Admin**: `admin` / `password123` - Admin panel access
- **Guest**: `guest` / `password123` - Limited guest access

*Note: Change these credentials before deploying to production*

## 🔧 Configuration

### Application Properties
Configure the application by editing `src/main/resources/application.properties`:

```properties
# Spring Boot configuration
spring.application.name=CineBook
server.port=8080

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/moviestore
spring.jpa.hibernate.ddl-auto=update

# Security
spring.security.user.name=admin
spring.security.user.password=admin

# Thymeleaf
spring.thymeleaf.mode=HTML
spring.thymeleaf.cache=false
```

### Mail Configuration (Required for Invitations)

Email functionality is essential for sending reservation invitations with QR codes and check-in links. Set the following environment variables before running:

**Required:**
- `MAIL_USERNAME` - Email account username
- `MAIL_PASSWORD` - Email account password

**Optional:**
- `MAIL_HOST` - SMTP host (default: smtp.gmail.com)
- `MAIL_PORT` - SMTP port (default: 587)
- `MAIL_FROM` - Sender email address
- `APP_BASE_URL` - Application base URL for check-in links

**Example using Gmail:**
```bash
set MAIL_USERNAME=your-email@gmail.com
set MAIL_PASSWORD=your-app-password
set MAIL_FROM=noreply@cinebook.com
set APP_BASE_URL=http://localhost:8080
```

*Note: For Gmail, use an App Password, not your regular password. Enable 2FA and generate an App Password in Google Account settings.*

## 🧪 Testing

Run tests with Maven:
```bash
mvn test
```

## 📦 Building for Production

Create a production-ready JAR file:
```bash
mvn clean package -DskipTests
```

The built JAR will be located at: `target/moviestore-thymeleaf-1.0.0.jar`

## 🚢 Deployment

### Using Docker (Optional)
1. Build Docker image: `docker build -t cinebook .`
2. Run container: `docker run -p 8080:8080 cinebook`

### Direct Deployment
1. Ensure MySQL is running and configured
2. Run: `java -jar moviestore-thymeleaf-1.0.0.jar`

## 📊 Database Schema

The application uses JPA to manage the following main entities:
- **User** - User accounts with roles
- **Movie** - Movie information
- **Cinema** - Cinema locations
- **Screen** - Cinema screens with seat capacity
- **Showtime** - Movie showtimes
- **Reservation** - User movie reservations
- **Booking** - Booking records with payment status

## 🔒 Security Features

- **Spring Security**: Authentication and authorization
- **Password Encoding**: BCrypt password hashing
- **CSRF Protection**: Cross-site request forgery prevention
- **Role-Based Access Control**: Admin and User roles
- **Session Management**: Secure session handling

## 📝 Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is open source and available under the MIT License.

## 👨‍💻 Author

**BARAD IDRISH**
- GitHub: [@BARAD-IDRISH](https://github.com/BARAD-IDRISH)
- Email: baradidrish@38gmail.com

## 🤝 Support

For support, email support@cinebook.com or open an issue on the GitHub repository.

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Documentation](https://www.thymeleaf.org/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---

**CineBook** - Making Movie Booking Simple and Seamless! 🎟️

