DROP DATABASE IF EXISTS todo_app;
CREATE DATABASE todo_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

DROP USER IF EXISTS 'todo_user'@'localhost';
CREATE USER 'todo_user'@'localhost' IDENTIFIED BY 'todo_pass';
GRANT ALL PRIVILEGES ON todo_app.* TO 'todo_user'@'localhost';
FLUSH PRIVILEGES;