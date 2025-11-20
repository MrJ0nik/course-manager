# Two-Server Architecture

## Overview

The project supports a two-server architecture for separation of concerns:

### Server 1: Course Management Service (port 8080)
**Responsibilities:**
- Course management
- Assignment management
- Exam management
- Grading formulas
- Exam variants

**Endpoints:**
- `/api/courses/**`
- `/api/assignments/**`
- `/api/exams/**`
- `/api/courses/{id}/formula`

### Server 2: Student & Grading Service (port 8081)
**Responsibilities:**
- Student management
- Grade management
- Gradebook

**Endpoints:**
- `/api/students/**`
- `/api/grades/**`
- `/api/courses/{id}/gradebook`

## Inter-Server Communication

Uses **WebClient** (Spring WebFlux) for asynchronous communication between servers.

### Usage Example

```java
@Service
public class StudentServiceClient {
    private final WebClient webClient;
    
    public List<StudentDTO> getStudentsByCourse(Long courseId) {
        return webClient.get()
            .uri("http://localhost:8081/api/students/course/{id}", courseId)
            .retrieve()
            .bodyToFlux(StudentDTO.class)
            .collectList()
            .block();
    }
}
```

## Configuration

In `application.properties`:
```properties
student.service.url=http://localhost:8081
```

## Running Two Servers

### Option 1: Two Separate Projects
1. Create two separate Maven projects
2. Configure different ports (8080 and 8081)
3. Run both servers

### Option 2: Spring Profiles
Create two profiles in `application.properties`:
- `application-server1.properties` (port 8080)
- `application-server2.properties` (port 8081)

Run:
```bash
# Server 1
mvn spring-boot:run -Dspring-boot.run.profiles=server1

# Server 2
mvn spring-boot:run -Dspring-boot.run.profiles=server2
```

## Architecture Benefits

1. **Separation of concerns** - each server handles its domain
2. **Scalability** - servers can scale independently
3. **Independent deployment** - changes in one server don't affect the other
4. **Technology flexibility** - can use different technologies

## Current Implementation

Current implementation includes:
- ✅ WebClient configuration
- ✅ StudentServiceClient for communication demo
- ✅ Fallback mechanism (if second server unavailable)

**Note:** For full implementation, create a separate project for the second server or use Spring profiles to run two instances.
