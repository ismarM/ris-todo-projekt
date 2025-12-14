package com.example.todo.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; //omogoči uporabo Mockito

import org.mockito.InjectMocks;
import org.mockito.Mock; //ustvari "lazno" verzijo razreda
import org.mockito.junit.jupiter.MockitoExtension; //poveze mockito z junit 5 anotacijami

import org.springframework.mail.javamail.JavaMailSender; //pri read testih nerabimo, ampak taskservice ga zahteva

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceReadTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private JavaMailSender mailSender; //Mock za pošiljanje e-pošte (da se v testu ne pošilja pravi mail)

    @InjectMocks
    private TaskService service;

    private Task task;

    @BeforeEach
    void setup() {
        task = new Task(); //testni task
        task.setId(1L);
        task.setTitle("Read test task");
        task.setDueDate(LocalDate.of(2025, 12, 25));
    }

    @AfterEach
    void tearDown() {
        System.out.println("READ test zaključen");
    }

    //FINDONE
    // POZITIVEN SCENARIJ: findOne vrne task, če obstaja
    @Test
    void findOne_shouldReturnTask_whenTaskExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        Task result = service.findOne(1L);

        assertNotNull(result); //preverim da rezultat ni null
        assertEquals("Read test task", result.getTitle()); //preverim da ima pravilen naziv
        verify(repository, times(1)).findById(1L); //preverim da je bla metoda poklicana natančno 1x

        System.out.println("READ TEST: task uspešno najden");
    }

    // NEGATIVEN SCENARIJ: findOne vrže izjemo, če task ne obstaja
    @Test
    void findOne_shouldThrowException_whenTaskDoesNotExist() { //testiranje obnašanja ko task ne obstaja
        when(repository.findById(99L)).thenReturn(Optional.empty()); //repozitorij ne najde zapisa

        RuntimeException exception = assertThrows( //pricakujemo da metoda vrže izjemo
                RuntimeException.class,
                () -> service.findOne(99L)
        );

        assertTrue(exception.getMessage().contains("Task not found"));
        verify(repository, times(1)).findById(99L);

        System.out.println("READ TEST: pravilno vržena izjema za neobstoječ task");
    }

    //FINDALL
    // POZITIVEN SCENARIJ: findAll brez filtra vrne vse taske
    @Test
    void findAll_shouldReturnAllTasks_whenNoDateFilter() {
       
        when(repository.findAll()).thenReturn(List.of(task)); //repozitorij vrne seznam z 1 taskom

        List<Task> result = service.findAll(null);

        assertEquals(1, result.size()); //preverim da je vrnjen pravilen seznam
        verify(repository, times(1)).findAll();

        System.out.println("READ ALL: vrnjeni vsi taski");
    }
}