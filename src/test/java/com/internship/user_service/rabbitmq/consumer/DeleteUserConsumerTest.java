package com.internship.user_service.rabbitmq.consumer;

import com.internship.user_service.model.User;
import com.internship.user_service.rabbitmq.Message;
import com.internship.user_service.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.Logger;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserConsumerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteUserConsumer deleteUserConsumer;

    private ListAppender<ILoggingEvent> logWatcher;

    private Message message;
    private User user;

    @BeforeEach
    void beforeEach() {
        message = new Message(1L);

        user = User.builder()
                .id(1L)
                .name("Name")
                .surname("Surname")
                .email("email@email.com")
                .build();

        logWatcher = new ListAppender<>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(DeleteUserConsumer.class)).addAppender(logWatcher);
    }

    @Test
    void consumeMessage_shouldDeleteUser_whenUserExists() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        deleteUserConsumer.consumeMessage(message);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void consumeMessage_shouldNotDeleteUser_whenUserDoesNotExist() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        deleteUserConsumer.consumeMessage(message);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).delete(any());
        assertEquals("User with id 1 not found.", logWatcher.list.get(1).getFormattedMessage());
    }

    @AfterEach
    void afterEach() {
        assertEquals("Attempting to delete user with id: 1", logWatcher.list.get(0).getFormattedMessage());
    }
}