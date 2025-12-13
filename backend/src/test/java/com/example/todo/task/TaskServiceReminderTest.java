package com.example.todo.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceReminderTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private TaskService service;

    // POZITIVEN: mail se poslje
    @Test
    void sendTomorrowReminders_shouldSendEmailAndMarkReminderSent() {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        Task t = new Task();
        t.setId(1L);
        t.setTitle("Test task");
        t.setDueDate(tomorrow);
        t.setDone(false);
        t.setReminderEnabled(true);
        t.setReminderSent(false);
        t.setEmail("test@mail.com");

        when(repository.findByReminderEnabledTrueAndReminderSentFalseAndDoneFalseAndDueDate(tomorrow))
        .thenReturn(List.of(t));

        // Act
        service.sendTomorrowReminders();

        // Assert: mail send je bil klican
        ArgumentCaptor<SimpleMailMessage> msgCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(msgCaptor.capture());

        SimpleMailMessage sentMsg = msgCaptor.getValue();
        assertArrayEquals(new String[]{"test@mail.com"}, sentMsg.getTo());
        assertTrue(sentMsg.getSubject().contains("Opomnik na opravilo"));
        assertTrue(sentMsg.getText().contains("Test task"));

        assertTrue(t.isReminderSent());
        verify(repository, times(1)).save(t);
        System.out.println("[PASS] Reminder: e-pošta poslana in reminderSent nastavljen");

    }

    // NEGATIVE: če email manjka/prazen ne poslje
    @Test
    void sendTomorrowReminders_whenEmailMissing_shouldSkipSendingAndNotSave() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        Task t = new Task();
        t.setId(2L);
        t.setTitle("No email task");
        t.setDueDate(tomorrow);
        t.setDone(false);
        t.setReminderEnabled(true);
        t.setReminderSent(false);
        t.setEmail(""); // kljuc: prazen email

        when(repository.findByReminderEnabledTrueAndReminderSentFalseAndDoneFalseAndDueDate(tomorrow))
        .thenReturn(List.of(t));

        service.sendTomorrowReminders();

        // mail se ne sme poslati
        verify(mailSender, never()).send(any(SimpleMailMessage.class));

        // task se ne sme shraniti, ker se reminderSent ne spremeni
        verify(repository, never()).save(any(Task.class));

        // reminderSent ostane false
        assertFalse(t.isReminderSent());
        System.out.println("[PASS] Reminder: brez e-pošte – mail ni bil poslan");

    }
}
