# Course Management System

Web server for creating and managing student assignments and exams, implemented with Spring Boot.

## Functionality

### Main Features:

- ✅ Course creation
- ✅ Creating assignments and exams for courses
- ✅ Creating grading formulas for students
- ✅ Adding and changing assignment deadlines and penalty points
- ✅ Creating exam variants and exam questions
- ✅ Adding students to courses
- ✅ Viewing student performance as a gradebook
- ✅ Handling duplicate course creation
- ✅ Grading formula validation

### Additional Features:

- ✅ Input validation via Spring Validation
- ✅ Global ExceptionHandler
- ✅ Caching via @Cacheable
- ✅ JWT authorization
- ✅ Email reminder sending
- ✅ Task scheduler via @Scheduled

## Technologies

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **H2 Database** (in-memory)
- **JWT** for authorization
- **Caffeine Cache** for caching
- **JavaMail** for email sending
- **Lombok** for code simplification
- **Maven** for dependency management

## Project Structure

```
course-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/university/coursemanagement/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entity/           # JPA Entity classes
│   │   │   ├── exception/        # Exception handling
│   │   │   ├── repository/       # JPA Repository
│   │   │   ├── security/          # Security components
│   │   │   └── service/           # Business logic
│   │   └── resources/
│   │       └── application.properties
│   └── test/                      # Unit tests
└── pom.xml
```

## Entity Classes

1. **Course** - Course
2. **Assignment** - Laboratory work
3. **Exam** - Exam
4. **Student** - Student
5. **GradingFormula** - Grading formula
6. **ExamVariant** - Exam variant
7. **Grade** - Grade
8. **User** - User (for authorization)

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register
- `POST /api/auth/login` - Login

### Courses

- `POST /api/courses` - Create course
- `GET /api/courses` - Get all courses
- `GET /api/courses/{id}` - Get course by ID
- `PUT /api/courses/{id}` - Update course
- `DELETE /api/courses/{id}` - Delete course
- `GET /api/courses/{id}/gradebook` - Get gradebook

### Grading Formula

- `POST /api/courses/{courseId}/formula` - Create formula
- `GET /api/courses/{courseId}/formula` - Get formula
- `PUT /api/courses/{courseId}/formula` - Update formula

### Assignments

- `POST /api/courses/{courseId}/assignments` - Create assignment
- `GET /api/courses/{courseId}/assignments` - Get all course assignments
- `GET /api/assignments/{id}` - Get assignment by ID
- `PUT /api/assignments/{id}` - Update assignment
- `PUT /api/assignments/{id}/deadline` - Change deadline
- `PUT /api/assignments/{id}/penalty` - Change penalty
- `DELETE /api/assignments/{id}` - Delete assignment

### Exams

- `POST /api/courses/{courseId}/exams` - Create exam
- `GET /api/courses/{courseId}/exams` - Get all course exams
- `GET /api/exams/{id}` - Get exam by ID
- `PUT /api/exams/{id}` - Update exam
- `DELETE /api/exams/{id}` - Delete exam

### Exam Variants

- `POST /api/exams/{examId}/variants` - Create variant
- `GET /api/exams/{examId}/variants` - Get all variants
- `GET /api/variants/{id}` - Get variant by ID
- `PUT /api/exams/{examId}/variants/{variantId}` - Update variant
- `DELETE /api/variants/{id}` - Delete variant

### Students

- `POST /api/students` - Create student
- `GET /api/students` - Get all students
- `GET /api/students/{id}` - Get student by ID
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student
- `POST /api/courses/{courseId}/students/{studentId}` - Add student to course
- `DELETE /api/courses/{courseId}/students/{studentId}` - Remove student from course
- `GET /api/students/{id}/grades` - Get student grades

### Grades

- `POST /api/grades` - Create grade
- `GET /api/grades/{id}` - Get grade by ID
- `PUT /api/grades/{id}` - Update grade
- `DELETE /api/grades/{id}` - Delete grade
- `GET /api/grades/course/{courseId}` - Get all course grades

## Running the Project

### Requirements

- Java 17+
- Maven 3.6+

### Running Steps

1. Clone the repository or extract the archive
2. Open terminal in project directory
3. Run the project:

```bash
mvn spring-boot:run
```

4. Server will start on port 8080
5. H2 Console available at: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:coursedb`
   - Username: `sa`
   - Password: (empty)

## Configuration

### Email

To enable email functionality, configure in `application.properties`:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-password
```

### JWT

JWT secret is configured in `application.properties`:

```properties
jwt.secret=your-secret-key-at-least-256-bits-long
jwt.expiration=86400000
```

## Usage Examples

### 1. Registration and Login

```bash
# Registration
POST /api/auth/register
{
  "username": "instructor",
  "password": "password123",
  "email": "instructor@university.edu",
  "role": "INSTRUCTOR"
}

# Login
POST /api/auth/login
{
  "username": "instructor",
  "password": "password123"
}
```

### 2. Creating a Course

```bash
POST /api/courses
Authorization: Bearer <token>
{
  "name": "Applied Programming",
  "code": "PP123",
  "description": "Programming course",
  "instructor": "John Doe"
}
```

### 3. Creating Grading Formula

```bash
POST /api/courses/1/formula
Authorization: Bearer <token>
{
  "totalPoints": 100,
  "assignmentCount": 4,
  "pointsPerAssignment": 10,
  "examPoints": 60,
  "description": "4 labs × 10 + exam 60"
}
```

### 4. Creating an Assignment

```bash
POST /api/courses/1/assignments
Authorization: Bearer <token>
{
  "title": "Assignment 1",
  "description": "Spring Basics",
  "maxPoints": 10,
  "deadline": "2024-12-31T23:59:59",
  "latePenaltyPoints": 2,
  "orderNumber": 1
}
```

## Implementation Features

### Caching

- Uses `@Cacheable` for caching courses
- Cache automatically cleared on create/update/delete

### Task Scheduler

- `@Scheduled` method checks deadlines daily at 9:00
- Automatically sends reminders one day before deadline
- Automatically applies penalties for missed deadlines

### Validation

- All input data validated via `@Valid`
- Custom validators for courses, students, etc.

### Error Handling

- Global `@RestControllerAdvice` handles all exceptions
- Structured responses with error details

## Testing

To run tests:

```bash
mvn test
```

## Author

Created for Laboratory Work #4 in Applied Programming.
