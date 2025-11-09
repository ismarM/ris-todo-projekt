package com.example.todo.task;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

/*
Repository komunicira direktno z bazo.

ZAKAJ: vmesnik do baze.
KAKO: extends JpaRepository<Task, Long> že da: save, findAll, findById, deleteById, …
Zakaj čisto kratek: Spring Data JPA sama implementira te metode.
*/

public interface TaskRepository extends JpaRepository<Task, Long> {
    // vrne vse taske z točno tem dueDate
    List<Task> findByDueDate(LocalDate dueDate);                                // filtriranje: metoda, ki zna poiskati po datumu
}
