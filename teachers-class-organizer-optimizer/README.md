# Teachers Class Organizer Optimizer

POC simples em Java 8 com Spring Boot para organizar professores, turmas, alunos, disciplinas, salas e sessões de aula, com validação de conflito e sugestão básica de horários.

## Stack

- Java 8
- Spring Boot
- Spring Data JPA
- H2 em memória
- Maven

## Regras da POC

- Uma sessão precisa de professor, turma, disciplina, sala, dia da semana, hora inicial e hora final.
- O sistema impede conflito de professor, turma e sala.
- Sessões `CANCELLED` são ignoradas nos conflitos.
- A sala precisa comportar a quantidade de alunos da turma.
- A sugestão procura slots em blocos de 30 minutos.
- Quando houver mais de uma possibilidade no mesmo dia, a sugestão prioriza horário colado em outra aula do professor.

## Estrutura

```text
src/main/java/com/example/teachersclassorganizeroptimizer
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
└── service
```

## Como rodar

```bash
mvn spring-boot:run
```

Aplicação sobe em `http://localhost:8080`.

H2 Console:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:teachersdb`
- User: `sa`
- Password: vazio

## Dados iniciais

Teachers:

- Maria
- João

Classes:

- Class A
- Class B

Subjects:

- Math
- Science
- History

Rooms:

- Room 101 / 30
- Room 102 / 40

Sessions:

- Maria / Class A / Math / Room 101 / MONDAY / 08:00-09:00
- Maria / Class A / Science / Room 101 / MONDAY / 10:00-11:00
- João / Class B / History / Room 102 / WEDNESDAY / 09:00-10:00

## Endpoints

### POST /teachers

Payload:

```json
{
  "name": "Maria",
  "subjectSpecialty": "Math"
}
```

```bash
curl -X POST http://localhost:8080/teachers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria",
    "subjectSpecialty": "Math"
  }'
```

### GET /teachers

```bash
curl http://localhost:8080/teachers
```

### POST /classes

Payload:

```json
{
  "name": "Class A",
  "gradeLevel": "5th Grade"
}
```

```bash
curl -X POST http://localhost:8080/classes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Class A",
    "gradeLevel": "5th Grade"
  }'
```

### GET /classes

```bash
curl http://localhost:8080/classes
```

### POST /students

Payload:

```json
{
  "name": "Ana",
  "schoolClassId": 1
}
```

```bash
curl -X POST http://localhost:8080/students \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ana",
    "schoolClassId": 1
  }'
```

### GET /students

```bash
curl http://localhost:8080/students
```

Filtro por turma:

```bash
curl "http://localhost:8080/students?classId=1"
```

### POST /subjects

Payload:

```json
{
  "name": "Math"
}
```

```bash
curl -X POST http://localhost:8080/subjects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Math"
  }'
```

### GET /subjects

```bash
curl http://localhost:8080/subjects
```

### POST /rooms

Payload:

```json
{
  "name": "Room 101",
  "capacity": 30
}
```

```bash
curl -X POST http://localhost:8080/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Room 101",
    "capacity": 30
  }'
```

### GET /rooms

```bash
curl http://localhost:8080/rooms
```

### POST /sessions

Payload:

```json
{
  "teacherId": 1,
  "schoolClassId": 1,
  "subjectId": 1,
  "roomId": 1,
  "dayOfWeek": "MONDAY",
  "startTime": "08:00",
  "endTime": "09:00"
}
```

```bash
curl -X POST http://localhost:8080/sessions \
  -H "Content-Type: application/json" \
  -d '{
    "teacherId": 1,
    "schoolClassId": 1,
    "subjectId": 1,
    "roomId": 1,
    "dayOfWeek": "MONDAY",
    "startTime": "08:00",
    "endTime": "09:00"
  }'
```

### GET /sessions

Filtros opcionais:

- `teacherId`
- `classId`
- `roomId`
- `dayOfWeek`
- `status`

```bash
curl http://localhost:8080/sessions
```

```bash
curl "http://localhost:8080/sessions?teacherId=1&dayOfWeek=MONDAY"
```

### GET /sessions/{id}

```bash
curl http://localhost:8080/sessions/1
```

### DELETE /sessions/{id}

```bash
curl -X DELETE http://localhost:8080/sessions/1
```

### POST /sessions/suggest

Payload:

```json
{
  "teacherId": 1,
  "schoolClassId": 1,
  "subjectId": 1,
  "roomId": 1,
  "durationMinutes": 60,
  "preferredDays": ["MONDAY", "WEDNESDAY"],
  "searchStartTime": "08:00",
  "searchEndTime": "18:00"
}
```

```bash
curl -X POST http://localhost:8080/sessions/suggest \
  -H "Content-Type: application/json" \
  -d '{
    "teacherId": 1,
    "schoolClassId": 1,
    "subjectId": 1,
    "roomId": 1,
    "durationMinutes": 60,
    "preferredDays": ["MONDAY", "WEDNESDAY"],
    "searchStartTime": "08:00",
    "searchEndTime": "18:00"
  }'
```

Resposta esperada:

```json
{
  "teacherId": 1,
  "schoolClassId": 1,
  "subjectId": 1,
  "roomId": 1,
  "suggestedDayOfWeek": "MONDAY",
  "suggestedStartTime": "09:00:00",
  "suggestedEndTime": "10:00:00",
  "message": "First valid optimized slot found"
}
```

## Testes

```bash
mvn test
```
