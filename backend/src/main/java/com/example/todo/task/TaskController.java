package com.example.todo.task;

import com.example.todo.task.dto.AnalitikaOpravil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/*
Controller sprejema HTTP zahtevke (npr. iz Postmana ali iz React frontenda).
*/

@CrossOrigin(origins = {"http://localhost:5173","http://localhost:3000"})           // dovoli da frontend teče na _ ali _
@RestController                                                                     // To pove Springu, da ta razred obravnava REST zahteve
@RequestMapping("/api/tasks")                                                    // Nastavi “osnovni URL” za vse metode spodaj
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {                                    // zdaj lahko kliče metode iz TaskService
        this.service = service;
    }

// CRUD  -  Te metode so povezane z HTTP zahtevki, ki jih pošiljaš s Postmana ali frontenda
    // CREATE
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(service.create(task));
    }

    //ANALITIKA
    @GetMapping("/analytics")
    public ResponseEntity<AnalitikaOpravil> analytics() {
        return ResponseEntity.ok(service.getAnalitika());
    }

    // READ (all)
    @GetMapping
    public ResponseEntity<?> all(@RequestParam(required = false) String dueDate) {          // filtriranje: sprejme query parameter ?dueDate= in ga pretvori v LocalDate
        LocalDate parsed = null;
        if (dueDate != null && !dueDate.isBlank()) {
            try {
                parsed = LocalDate.parse(dueDate); // pričakuje ž: yyyy-MM-dd
            } catch (DateTimeParseException ex) {
                return ResponseEntity.badRequest().body("Invalid dueDate format. Use yyyy-MM-dd.");
            }
        }
        List<Task> result = service.findAll(parsed);
        return ResponseEntity.ok(result);
    }

    // READ (one)
    @GetMapping("/{id}")
    public Task one(@PathVariable Long id) {
        return service.findOne(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @RequestBody Task task) {
        return service.update(id, task);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }



}
