package com.example.todo.task;

/*
--> “posrednik” med spletno plastjo (controllerjem) in bazo podatkov (repositoryjem)
Service sloj ni nujen, a je dobra praksa: controller samo “sprejme HTTP”, service pa ve, kaj narediti (branje, posodabljanje, validacije, …).
Tukaj pripravimo čist CRUD tok: create(t), findAll(), findOne(id), update(id, payload), delete(id).
*/

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import com.example.todo.task.dto.AnalitikaOpravil;

import java.time.LocalDate;
import java.util.List;

@EnableScheduling
@Service
public class TaskService {

    private final TaskRepository repository;
    private final JavaMailSender mailSender;

// konstruktor
    public TaskService(TaskRepository repository, JavaMailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
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
        t.setDifficulty(updatedTask.getDifficulty());
        t.setEmail(updatedTask.getEmail());
        t.setReminderEnabled(updatedTask.isReminderEnabled());
        return repository.save(t);                          // shranijo spremembe v bazo
    }

    // DELETE
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Scheduled(cron = "0 * * * * *")
    public void sendTomorrowReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Task> tasks = repository.findByReminderEnabledTrueAndReminderSentFalseAndDoneFalseAndDueDate(tomorrow);

        for (Task t : tasks) {
            String email = t.getEmail();
            if (email == null || email.isBlank()) {
                continue;
            }

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Opomnik na opravilo: " + t.getTitle());
            msg.setText("""
                    Pozdravljen/a,

                    Opomnik: jutri (%s) imaš rok za opravilo:

                    %s

                    Lep pozdrav,
                    ToDo aplikacija
                    """.formatted(t.getDueDate(), t.getTitle()));

            mailSender.send(msg);

            t.setReminderSent(true);          // da ne pošljemo ponovno
            repository.save(t);

            System.out.println("[REMINDER] Poslan opomnik na " + email + " za task " + t.getId());
        }
    }

    // Analitika produktivnosti
    public AnalitikaOpravil getAnalitika() {
        List<Task> tasks = repository.findAll();

        long skupno = tasks.size();

        long dokoncanih = tasks.stream()
                .filter(Task::isDone)
                .count();

        long nedokoncanih = skupno - dokoncanih;

        LocalDate danes = LocalDate.now();

        long zapadlih = tasks.stream()
                .filter(t -> !t.isDone())
                .filter(t -> t.getDueDate() != null)
                .filter(t -> t.getDueDate().isBefore(danes))
                .count();

        double odstotekDokoncanih = (skupno == 0)
                ? 0.0
                : (dokoncanih * 100.0) / skupno;

        // ZAČASNO PREVERJANJE
        System.out.println(
                "ANALITIKA -> skupno=" + skupno +
                        ", dokoncanih=" + dokoncanih +
                        ", nedokoncanih=" + nedokoncanih +
                        ", zapadlih=" + zapadlih +
                        ", odstotek=" + odstotekDokoncanih
        );

        return new AnalitikaOpravil(
                skupno,
                dokoncanih,
                nedokoncanih,
                zapadlih,
                odstotekDokoncanih
        );
    }

}
