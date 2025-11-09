package com.example.todo.task;

/*
--> “posrednik” med spletno plastjo (controllerjem) in bazo podatkov (repositoryjem)
Service sloj ni nujen, a je dobra praksa: controller samo “sprejme HTTP”, service pa ve, kaj narediti (branje, posodabljanje, validacije, …).
Tukaj pripravimo čist CRUD tok: create(t), findAll(), findOne(id), update(id, payload), delete(id).
*/

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

// konstruktor
    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }


// CRUD METODE
    // CREATE
    public Task create(Task task) {
        return repository.save(task);
    }

    // READ all: preberi vse, opcijsko filtrirano po datumu
    public List<Task> findAll(LocalDate dueDate) {
        if (dueDate != null) {                                      // filtriranje: metoda preveri, ali je poslan filter
            return repository.findByDueDate(dueDate);
        }
        return repository.findAll();
    }
    // READ one
    public Task findOne(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Task not found " + id));
    }

    // UPDATE
    public Task update(Long id, Task updatedTask) {
        Task t = findOne(id);                               // stari task v bazi - najdemo ga po id
        t.setTitle(updatedTask.getTitle());
        t.setDone(updatedTask.isDone());
        t.setDescription(updatedTask.getDescription());
        t.setDueDate(updatedTask.getDueDate());
        return repository.save(t);                          // shranijo spremembe v bazo
    }

    // DELETE
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
