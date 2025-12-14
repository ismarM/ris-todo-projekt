package com.example.todo.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceDeleteTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private TaskService service;

    @BeforeEach
    void setup() {
        System.out.println("Začetek DELETE testa");
    }

    @AfterEach
    void tearDown() {
        System.out.println("DELETE test zaključen");
    }

    // POZITIVEN SCENARIJ: delete kliče deleteById
    @Test
    void delete_shouldCallRepositoryDeleteById() {
        Long taskId = 1L;
        doNothing().when(repository).deleteById(taskId);

        service.delete(taskId);

        verify(repository, times(1)).deleteById(taskId);

        System.out.println("DELETE: deleteById uspešno poklican");
    }

    // NEGATIVEN SCENARIJ: brisanje neobstoječega ID-ja (repository ne vrže izjeme)
    @Test
    void delete_shouldNotThrowException_whenTaskDoesNotExist() {
        Long nonExistingId = 999L;
        doNothing().when(repository).deleteById(nonExistingId);

        assertDoesNotThrow(() -> service.delete(nonExistingId)); //preverim da metoda ne vrže izjeme tudi če ID ne obstaja
        verify(repository, times(1)).deleteById(nonExistingId);

        System.out.println("DELETE: brez izjeme za neobstoječ task");
    }
}