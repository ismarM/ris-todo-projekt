package com.example.todo.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceFilterTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private TaskService service;

    private Task task;

    @BeforeEach
    void setup() {
        task = new Task();
        task.setTitle("Filtered task");
        task.setDueDate(LocalDate.of(2025, 12, 20));
    }

    // POZITIVEN SCENARIJ: filtriranje po datumu
    @Test
    void findAll_withDate_shouldFilterByDueDate() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 12, 20);
        when(repository.findByDueDate(date)).thenReturn(List.of(task));

        // Act
        List<Task> result = service.findAll(date);

        // Assert
        assertEquals(1, result.size());
        verify(repository, times(1)).findByDueDate(date);
        verify(repository, never()).findAll();

        System.out.println("[PASS] FILTER: filtriranje po datumu deluje");
    }

    // NEGATIVEN SCENARIJ: brez datuma vrne vse taske
    @Test
    void findAll_withoutDate_shouldReturnAllTasks() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(task));

        // Act
        List<Task> result = service.findAll(null);

        // Assert
        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
        verify(repository, never()).findByDueDate(any());

        System.out.println("[PASS] FILTER: brez datuma vrne vse taske");
    }
}
