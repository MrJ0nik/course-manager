# Портування exam-manager на Java

Цей проєкт було переписано з NestJS (TypeScript) на Java Spring Boot, базуючись на структурі з репозиторію [exam-manager](https://github.com/mykhailo-fedyna/exam-manager).

## Основні зміни

### Entity класи
- **Course** - курс з вбудованою формулою оцінювання (CourseFormula)
- **Assignment** - завдання з типом (LAB/EXAM)
- **Student** - студент (email, name)
- **Enrollment** - запис студента на курс
- **Submission** - подання завдання студентом
- **ExamVariant** - варіант екзамену (для завдань типу EXAM)
- **ExamTask** - завдання в варіанті екзамену

### API Endpoints

#### Courses
- `POST /courses` - створити курс
- `GET /courses` - отримати всі курси
- `GET /courses/{id}/journal` - отримати журнал курсу
- `POST /courses/{id}/formula` - встановити формулу оцінювання

#### Assignments
- `POST /assignments/{courseId}` - створити завдання
- `PATCH /assignments/{id}` - оновити завдання
- `POST /assignments/{id}/variants` - додати варіант екзамену
- `POST /assignments/submit` - подати завдання

#### Students
- `POST /students/enroll/{courseId}` - записати студента на курс

#### Health
- `GET /` - перевірка статусу
- `GET /routes` - список доступних маршрутів

### Особливості реалізації

1. **Валідація завдань** - перевірка відповідності формулі курсу:
   - Кількість лабораторних робіт не перевищує встановлену
   - Бали за лабораторні відповідають формулі
   - Може бути лише один екзамен на курс
   - Бали за екзамен відповідають формулі

2. **Автоматичне застосування пенальті** - SchedulerService перевіряє завдання щохвилини та застосовує пенальті за прострочені подання

3. **Формула оцінювання** - вбудована в Course як embedded entity

## Технології

- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (in-memory)
- Lombok
- Maven

## Запуск

```bash
mvn spring-boot:run
```

Сервер запускається на порту 8080.

