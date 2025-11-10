# 📝 ToDo App – RIS projekt

Projekt **ToDo App** je preprosta spletna aplikacija za upravljanje opravil, razvita v okviru predmeta **Razvoj informacijskih sistemov (RIS)** na UM FERI.  
Sestavljena je iz **zalednega (backend)** dela v **Spring Boot** in **odjemalnega (frontend)** dela v **React (Vite)**, povezana pa je z **MySQL/MariaDB** podatkovno bazo.

---

## 1) Dokumentacija za razvijalce

### Struktura projekta
```
todo_app/
│
├─ backend/                        # Spring Boot (REST API + JPA)
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ java/com/example/todo/
│  │  │  │  ├─ task/
│  │  │  │  │  ├─ Task.java
│  │  │  │  │  ├─ TaskController.java
│  │  │  │  │  ├─ TaskRepository.java
│  │  │  │  │  └─ TaskService.java
│  │  │  │  └─ TodoApplication.java
│  │  │  └─ resources/
│  │  │     └─ application.properties
│  │  └─ test/java/com/example/todo/TodoApplicationTests.java
│  ├─ pom.xml
│  ├─ mvnw / mvnw.cmd              # Maven wrapper
│  └─ .mvn/
│
├─ frontend/                       # React (Vite)
│  ├─ public/vite.svg
│  ├─ src/
│  │  ├─ assets/react.svg
│  │  ├─ api.js
│  │  ├─ App.jsx
│  │  ├─ index.css
│  │  └─ main.jsx
│  ├─ package.json
│  ├─ package-lock.json
│  └─ vite.config.js
│
├─ db/
│  └─ todo.sql                     # Ustvari bazo + uporabnika
│
├─ .gitignore
└─ README.md
```

### Arhitektura & standardi
- **Backend:** Java 17+ (deluje tudi na novejših), Spring Boot 3, REST konvencije, JPA (Repository pattern).
- **Frontend:** React 18+, Vite, JSX, Hooks (`useState`, `useEffect`).
- **Baza:** MySQL 8+ ali MariaDB 10.6+.
- **Stil kode:** angleška imena razredov/spremenljivk/metod; jedrnati komentarji; formatiranje po privzetih formatterjih (IntelliJ/Prettier).

### 🔌 API povzetek
- `GET  /api/tasks` – vrne seznam opravkov  
- `POST /api/tasks` – doda opravilo  
- `GET  /api/tasks/{id}` – vrne eno opravilo  
- `PUT  /api/tasks/{id}` – posodobi opravilo  
- `DELETE /api/tasks/{id}` – izbriše opravilo  

> CORS: Controller ima `@CrossOrigin(origins = "http://localhost:5173")` za lokalni razvoj.

---

## 2) Navodila za nameščanje

### 🗄️ Baza podatkov
1. Zaženi MySQL/MariaDB (lokalno ali v Dockerju).
2. Ustvari bazo in uporabnika z datoteko `db/todo.sql`:
   ```bash
   sudo mariadb < db/todo.sql
   ```
3. Nastavitve v `backend/src/main/resources/application.properties`:
   ```properties
   spring.application.name=todo

   # Povezava na bazo
   spring.datasource.url=jdbc:mysql://127.0.0.1:3306/todo_app?useSSL=false&allowPublicKeyRetrieval=true
   spring.datasource.username=todo_user
   spring.datasource.password=todo_pass

   # JPA/Hibernate
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true

   # MariaDB dialekt (če uporabljaš MariaDB)
   spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
   ```

> Opomba: Če uporabljaš **MySQL 8**, nastavitev `spring.jpa.database-platform` navadno ni potrebna.

---

### Backend (Spring Boot)
**IntelliJ IDEA:**
1. Odpri mapo `backend`.
2. Počakaj, da Maven naloži odvisnosti.
3. Zaženi `TodoApplication.java` (Run).  
   Aplikacija posluša na: `http://localhost:8080`.

---

### Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```
Aplikacija: `http://localhost:5173`

---

## 3) Navodila za razvijalce (Git)

**Repozitorij:** https://github.com/ismarM/ris-todo-projekt

### Prvič:
```bash
git clone https://github.com/ismarM/ris-todo-projekt.git
cd ris-todo-projekt
git config user.name "Ime Priimek"
git config user.email "email@example.com"
```

### Vedno pred delom:
```bash
git pull origin main
```

### Po spremembah:
```bash
git add .
git commit -m "Kratek opis spremembe"
git push origin main
```

> Delamo na `main`. Ne dodajaj `node_modules/`, `.idea/`, `.DS_Store` (že v `.gitignore`).

---

## Avtorji ekipe
- Ismar Mujezinović  
- Ana Cvetko  
- Živa Šumak

---
© 2025 FERI – Razvoj informacijskih sistemov
