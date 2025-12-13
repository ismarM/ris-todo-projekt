package com.example.todo.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceUpdateTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @InjectMocks
    private TaskService service;

    // Pozitiven scenarij: update spremeni polja in shrani v bazo
    @Test
    void update_shouldUpdateFieldsAndSave() {
        // Arrange (staro stanje v bazi)
        Task existing = new Task();
        existing.setId(1L);
        existing.setTitle("Old");
        existing.setDescription("Old desc");
        existing.setDone(false);
        existing.setDueDate(LocalDate.of(2025, 12, 10));
        existing.setDifficulty("Low");
        existing.setEmail("old@mail.com");
        existing.setReminderEnabled(false);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // payload ki ga poslje frontend (novo stanje)
        Task payload = new Task();
        payload.setTitle("New title");
        payload.setDescription("New desc");
        payload.setDone(true);
        payload.setDueDate(LocalDate.of(2025, 12, 11));
        payload.setDifficulty("High");
        payload.setEmail("new@mail.com");
        payload.setReminderEnabled(true);

        // Act
        Task updated = service.update(1L, payload);

        // Assert (preverimo, da so polja posodobljena)
        assertEquals("New title", updated.getTitle());
        assertEquals("New desc", updated.getDescription());
        assertTrue(updated.isDone());
        assertEquals(LocalDate.of(2025, 12, 11), updated.getDueDate());
        assertEquals("High", updated.getDifficulty());
        assertEquals("High", updated.getDifficulty());
        assertEquals("new@mail.com", updated.getEmail());
        assertTrue(updated.isReminderEnabled());

        // Preverim da je save bil klican 1x
        verify(repository, times(1)).save(existing);
        System.out.println("[PASS] Update: task uspešno posodobljen in shranjen");
    }

    @Test
    void update_whenTaskNotFound_shouldThrowAndNotSave() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Task payload = new Task();
        payload.setTitle("Whatever");

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.update(999L, payload));

        assertTrue(ex.getMessage().toLowerCase().contains("task not found"));

        // Save se ne sme klicati
        verify(repository, never()).save(any());
        System.out.println("[PASS] Update: pravilno vržena izjema, ko task ne obstaja");

    }

}
