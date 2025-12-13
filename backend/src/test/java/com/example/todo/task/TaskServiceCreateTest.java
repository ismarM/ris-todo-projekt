package com.example.todo.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceCreateTest {

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
        task.setTitle("Test task");
        task.setDueDate(LocalDate.of(2025, 12, 20));
    }

    // POZITIVEN SCENARIJ: create shrani task in ga vrne
    @Test
    void create_shouldSaveTask() {
        // Arrange
        when(repository.save(task)).thenReturn(task);

        // Act
        Task result = service.create(task);

        // Assert
        assertNotNull(result);
        assertEquals("Test task", result.getTitle());
        verify(repository, times(1)).save(task);

        System.out.println("[PASS] CREATE: task uspešno shranjen");
    }
}
