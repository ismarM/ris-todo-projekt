package com.example.todo.task;

import com.example.todo.task.dto.CalendarEventDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceCalendarSyncTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private JavaMailSender mailSender; // TaskService ga rabi v konstruktorju

    @InjectMocks
    private TaskService service;

    /**
     * POZITIVEN SCENARIJ:
     * - task obstaja
     * - ima dueDate
     * - pričakujemo status: IN_PROGRESS -> SUCCESS
     * - in da metoda vrne CalendarEventDTO
     */
    @Test
    void syncTaskToCalendar_whenTaskValid_shouldSetInProgressThenSuccess_andReturnDTO() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Naloga A");
        task.setDescription("Opis");
        task.setDueDate(LocalDate.of(2026, 1, 20));

        when(repository.findById(1L)).thenReturn(Optional.of(task));

        // Tukaj ujamemo statuse ob posameznem save klicu
        List<CalendarSyncStatus> statusesOnSave = new java.util.ArrayList<>();

        when(repository.save(any(Task.class))).thenAnswer(inv -> {
            Task saved = inv.getArgument(0);
            statusesOnSave.add(saved.getCalendarSyncStatus());
            return saved;
        });

        // Act
        CalendarEventDTO dto = service.syncTaskToCalendar(1L);

        // Assert: DTO vsebuje prave podatke
        assertNotNull(dto);
        assertEquals("Naloga A", dto.getTitle());
        assertEquals(LocalDate.of(2026, 1, 20), dto.getStartDate());
        assertEquals(LocalDate.of(2026, 1, 20), dto.getEndDate());
        assertEquals("Opis", dto.getDescription());

        // Assert: končni status
        assertEquals(CalendarSyncStatus.SUCCESS, task.getCalendarSyncStatus());

        // Assert: status flow med shranjevanji
        assertEquals(
                List.of(CalendarSyncStatus.IN_PROGRESS, CalendarSyncStatus.SUCCESS),
                statusesOnSave,
                "Pričakovan status flow: IN_PROGRESS -> SUCCESS"
        );

        // Bonus: preverimo št. shranjevanje
        verify(repository, times(2)).save(any(Task.class));
    }


    /**
     * NEGATIVEN SCENARIJ (neveljavna naloga):
     * - task obstaja, ampak nima dueDate
     * - pričakujemo status ERROR in exception
     */
    @Test
    void syncTaskToCalendar_whenTaskHasNoDueDate_shouldSetError_andThrow() {
        // Arrange
        Task task = new Task();
        task.setId(2L);
        task.setTitle("Naloga brez roka");
        task.setDueDate(null);

        when(repository.findById(2L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.syncTaskToCalendar(2L));

        assertTrue(ex.getMessage().toLowerCase().contains("cannot sync"));

        // Status mora biti ERROR
        assertEquals(CalendarSyncStatus.ERROR, task.getCalendarSyncStatus());

        // Save mora biti klican vsaj 1x (ker nastavi ERROR in shrani)
        verify(repository, atLeastOnce()).save(any(Task.class));
        verify(repository, never()).deleteById(anyLong());
    }

    /**
     * DODATNI NEGATIVEN SCENARIJ (naloga ne obstaja):
     * - findById vrne empty
     * - metoda vrže exception
     * - repository.save se sploh ne kliče, ker ni taska za shranit
     */
    @Test
    void syncTaskToCalendar_whenTaskNotFound_shouldThrow_andNotSave() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.syncTaskToCalendar(999L));
        assertTrue(ex.getMessage().toLowerCase().contains("task not found"));

        verify(repository, never()).save(any(Task.class));
    }
}
