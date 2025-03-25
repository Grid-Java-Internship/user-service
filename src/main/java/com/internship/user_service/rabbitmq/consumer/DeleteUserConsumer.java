package com.internship.user_service.rabbitmq.consumer;

import com.internship.user_service.model.User;
import com.internship.user_service.rabbitmq.Message;
import com.internship.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteUserConsumer {

    private final UserRepository userRepository;

    /**
     * Deletes the user with the given ID from the database.
     *
     * @param message contains the ID of the user to be deleted
     */
    @RabbitListener(queues = "${configs.rabbitmq.queues.deleteUser}")
    public void consumeMessage(Message message) {
        log.info("Attempting to delete user with id: {}", message.getUserId());

        Optional<User> user = userRepository.findById(message.getUserId());

        if (user.isPresent()) {
            userRepository.delete(user.get());
        } else {
            log.error("User with id {} not found.", message.getUserId());
        }
    }
}
