# ToDo App – RIS projekt

Projekt **ToDo App** je preprosta spletna aplikacija za upravljanje opravil, razvita v okviru predmeta **Razvoj informacijskih sistemov (RIS)** na UM FERI. 

Sestavljena je iz **zalednega (backend)** dela v **Spring Boot** in **odjemalnega (frontend)** dela v **React.js**, povezana pa je z **MySQL** podatkovno bazo.

## 1. Dokumentacija za razvijalce

### Struktura projekta
todo_app/
│
├─ backend/ # Spring Boot (REST API + JPA)
│ ├─ src/
│ │ ├─ main/
│ │ │ ├─ java/com/example/todo/
│ │ │ │ ├─ task/
│ │ │ │ │ ├─ Task.java
│ │ │ │ │ ├─ TaskController.java
│ │ │ │ │ ├─ TaskRepository.java
│ │ │ │ │ └─ TaskService.java
│ │ │ │ └─ TodoApplication.java
│ │ │ └─ resources/
│ │ │ └─ application.properties
│ │ └─ test/java/com/example/todo/TodoApplicationTests.java
│ ├─ pom.xml
│ ├─ mvnw / mvnw.cmd # Maven wrapper
│ └─ .mvn/
│
├─ frontend/ # React (Vite)
│ ├─ public/vite.svg
│ ├─ src/
│ │ ├─ assets/react.svg
│ │ ├─ api.js
│ │ ├─ App.jsx
│ │ ├─ index.css
│ │ └─ main.jsx
│ ├─ package.json
│ ├─ package-lock.json
│ └─ vite.config.js
│
├─ db/
│ └─ todo.sql # Ustvari bazo + uporabnika
│
├─ .gitignore
└─ README.md

### Standardi kodiranja
- **Backend:** Java 17+, Spring Boot 3, REST konvencije, JPA repository pattern.  
- **Frontend:** React 18+, Vite, JSX komponente, Hooks (`useState`, `useEffect`).  
- **Baza:** MySQL 8+ z uporabnikom `todo_user` in bazo `todo_app`.  
- Koda uporablja angleška imena razredov, spremenljivk in metod.

### Uporabljena orodja in verzije
| Orodje / Framework | Verzija | Namen |
|--------------------|----------|--------|
| Java | 17 ali več | Backend |
| Spring Boot | 3.5.6 | REST API + JPA |
| React | 18+ | Frontend |
| Vite | najnovejša | React build orodje |
| MySQL / MariaDB | 8+ | Podatkovna baza |
| Maven | 3.9+ | Upravljanje odvisnosti |

---

## 2. Navodila za nameščanje

### Baza podatkov
1. Zaženi MySQL/MariaDB (lahko lokalno ali z Dockerjem).  
2. Ustvari bazo z datoteko `db/todo.sql`:
   ```bash
   sudo mariadb < db/todo.sql
3. V backend/src/main/resources/application.properties so privzete nastavitve:
  spring.datasource.url=jdbc:mysql://127.0.0.1:3306/todo_app
  spring.datasource.username=todo_user
  spring.datasource.password=todo_pass

### Backend (Spring Boot)
1. Odpri mapo backend v IntelliJ IDEA.
2. Počakaj, da Maven naloži vse odvisnosti.
3. Zaženi aplikacijo preko TodoApplication.java.
4. Strežnik bo deloval na:
http://localhost:8080/api/tasks

### Frontend (React)
1. Odpri terminal v mapi frontend
2. Namesti odvisnosti:
   npm install
3. Zaženi razvojni strežnik:
   npm run dev
4. Aplikacija bo na naslovu:
   http://localhost:5173

## 3. Navodila za razvijalce

### Delo z GitHub repozitorijem
Povezava do repozitorija:
https://github.com/ismarM/ris-todo-projekt

### Prvič:
git clone https://github.com/ismarM/ris-todo-projekt.git
cd ris-todo-projekt
git config user.name "Ime Priimek"
git config user.email "email@example.com"

### Preden začneš delati:
git pull origin main

### Ko zaključiš delo:
git add .
git commit -m "Opis spremembe"
git push origin main

### Priporočila:
Vedno pred delom pokliči git pull, da imaš najnovejšo kodo.
Ne dodajaj datotek, kot so node_modules/, .idea/, .DS_Store – so v .gitignore.
Komentarji v kodi naj bodo kratki in razumljivi.
Backend in frontend imata ločene okolje (npm run dev in IntelliJ).

## Avtorji ekipe
Ismar Mujezinović
Ana Cvetko
Živa Šumak
