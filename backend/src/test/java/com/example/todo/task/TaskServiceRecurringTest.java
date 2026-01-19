package com.example.todo.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceRecurringTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private TaskService service;

    @Test
    void update_whenRecurringDailyAndMarkedDone_shouldDeleteOldAndCreateNextTask() {
        // Arrange (obstoječa naloga v bazi)
        Task existing = new Task();
        existing.setId(1L);
        existing.setTitle("Telovadba");
        existing.setDescription("Vsak dan");
        existing.setDone(false);
        existing.setDueDate(LocalDate.of(2026, 1, 10));
        existing.setRecurrenceType(RecurrenceType.DAILY);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // payload iz frontenda (označena kot done)
        Task payload = new Task();
        payload.setTitle("Telovadba");
        payload.setDescription("Vsak dan");
        payload.setDone(true);
        payload.setDueDate(existing.getDueDate());
        payload.setRecurrenceType(RecurrenceType.DAILY);

        // Act
        Task result = service.update(1L, payload);

        // Assert: ker recurring + done -> metoda vrne NOVO nalogo (next), ki je done=false
        assertNotNull(result);
        assertFalse(result.isDone(), "Vrnjena mora biti nova ponovitev (done=false)");
        assertEquals("Telovadba", result.getTitle());
        assertEquals(RecurrenceType.DAILY, result.getRecurrenceType());

        // catch-up: datum mora biti v prihodnosti glede na danes
        assertTrue(
                result.getDueDate().isAfter(LocalDate.now()),
                "Catch-up: dueDate nove naloge mora biti po današnjem datumu"
        );

        // Preveri interakcije: save (stara posodobitev) + delete old + save new
        verify(repository, times(2)).save(any(Task.class));
        verify(repository, times(1)).deleteById(1L);

        // Ujamemo shranjene objekte (1. save je stari task, 2. save je next)
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(repository, times(2)).save(captor.capture());
        List<Task> saved = captor.getAllValues();

        Task savedOld = saved.get(0);
        Task savedNext = saved.get(1);

        assertTrue(savedOld.isDone(), "Stari task mora biti shranjen kot done=true");
        assertFalse(savedNext.isDone(), "Nova ponovitev mora biti done=false");
        assertEquals(RecurrenceType.DAILY, savedNext.getRecurrenceType());
    }

    @Test
    void update_whenNoRecurrence_shouldNotCreateNextTask() {
        // Arrange
        Task existing = new Task();
        existing.setId(2L);
        existing.setTitle("Enkratna naloga");
        existing.setDone(false);
        existing.setDueDate(LocalDate.of(2026, 1, 10));
        existing.setRecurrenceType(RecurrenceType.NONE);

        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task payload = new Task();
        payload.setTitle("Enkratna naloga");
        payload.setDone(true);
        payload.setDueDate(existing.getDueDate());
        payload.setRecurrenceType(RecurrenceType.NONE);

        // Act
        Task result = service.update(2L, payload);

        // Assert
        assertNotNull(result);
        assertTrue(result.isDone(), "Pri NONE mora update vrniti isti task kot done=true");

        verify(repository, times(1)).save(any(Task.class));
        verify(repository, never()).deleteById(anyLong());
    }
}
