package com.example.todo.task;

import com.example.todo.task.dto.AnalitikaOpravil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceAnalyticsTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private TaskService service;

    // POZITIVEN: pravilno izračuna statistiko za mešan seznam opravil
    @Test
    void getAnalitika_shouldReturnCorrectStats() {
        LocalDate danes = LocalDate.now();

        Task t1 = new Task(); // DONE
        t1.setDone(true);
        t1.setDueDate(danes.minusDays(5));

        Task t2 = new Task(); // NOT DONE + overdue
        t2.setDone(false);
        t2.setDueDate(danes.minusDays(1));

        Task t3 = new Task(); // NOT DONE + rok v prihodnosti
        t3.setDone(false);
        t3.setDueDate(danes.plusDays(2));

        Task t4 = new Task(); // NOT DONE + brez roka (ne šteje kot overdue)
        t4.setDone(false);
        t4.setDueDate(null);

        when(repository.findAll()).thenReturn(List.of(t1, t2, t3, t4));

        AnalitikaOpravil a = service.getAnalitika();

        assertEquals(4, a.getSkupnoStevilo());
        assertEquals(1, a.getSteviloDokoncanih());
        assertEquals(3, a.getSteviloNedokoncanih());
        assertEquals(1, a.getSteviloZapadlih()); // samo t2
        assertEquals(25.0, a.getOdstotekDokoncanih(), 0.0001); // 1/4 * 100
    }

    // NEGATIVEN: prazen seznam opravil
    @Test
    void getAnalitika_whenNoTasks_shouldReturnZeros() {
        when(repository.findAll()).thenReturn(List.of());

        AnalitikaOpravil a = service.getAnalitika();

        assertEquals(0, a.getSkupnoStevilo());
        assertEquals(0, a.getSteviloDokoncanih());
        assertEquals(0, a.getSteviloNedokoncanih());
        assertEquals(0, a.getSteviloZapadlih());
        assertEquals(0.0, a.getOdstotekDokoncanih(), 0.0001);
    }
}
