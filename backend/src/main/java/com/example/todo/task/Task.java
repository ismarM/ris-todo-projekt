package com.example.todo.task;

/*
ZAKAJ: opisuje ENO vrstico v tabeli tasks. Hibernate jo iz tega razreda ustvari.
KLJUČNE STVARI:
    @Entity + @Table(name="tasks") → pove, da gre za tabelo
    @Id @GeneratedValue(...) → primarni ključ id se generira sam
    @NotBlank na title → enostavna validacija (ne prazen naslov)
    createdAt, updatedAt + @PreUpdate → preprosta časovna žiga
*/

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity                                                     // to pove, da je ta razred tabela
@Table(name = "tasks")                                      // ime tabele (če nič nena napišem --> default: task - kot ime razreda)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // v bazi je to AUTO_INCREMENT
    private Long id;

    @Column(nullable = false)                               // NOT NULL
    private String title;

    @Column(nullable = false)
    private boolean done = false;

    @Column(length = 2000)
    private String description;

    private LocalDate dueDate;

    @Column(length = 20)
    private String difficulty = "Medium";

    @PrePersist
    public void onCreate() {
        if (difficulty == null || difficulty.isBlank()) {
            difficulty = "Medium";
        }
    }

//    private LocalDateTime createdAt = LocalDateTime.now();
//
//    private LocalDateTime updatedAt = LocalDateTime.now();


//    // preden Hibernate posodobi vrstico v tabeli, pokliči to metodo - Torej vedno, ko spremeniš opravilo, bo samodejno nastavil updatedAt na trenutni čas
//    @PreUpdate
//    public void touch() {
//        this.updatedAt = LocalDateTime.now();
//    }


    // get/set (potrebno za JPA in JSON)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }


//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }

}
