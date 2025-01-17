package com.dan.sd.chat.services;

import com.dan.sd.chat.controllers.handlers.exceptions.model.DuplicateResourceException;
import com.dan.sd.chat.controllers.handlers.exceptions.model.ResourceNotFoundException;
import com.dan.sd.chat.dtos.UserDTO;
import com.dan.sd.chat.entities.Message;
import com.dan.sd.chat.entities.User;
import com.dan.sd.chat.entities.Writing;
import com.dan.sd.chat.repositories.MessageRepository;
import com.dan.sd.chat.repositories.UserRepository;
import com.dan.sd.chat.repositories.WritingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final WritingRepository writingRepository;

    @Autowired
    public UserService(UserRepository userRepository, MessageRepository messageRepository, WritingRepository writingRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.writingRepository = writingRepository;
    }

    private User getUserIfExists(UUID id, Optional<User> userOptional) {
        if (userOptional.isEmpty()) {
            LOGGER.error("User with id {} not found", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return userOptional.get();
    }

    private void checkDuplicate(UUID id) {
        if (userRepository.findById(id).isPresent()) {
            LOGGER.error("User with id {} already exists in db", id);
            throw new DuplicateResourceException(User.class.getSimpleName() + " with id: " + id);
        }
    }

    public UserDTO insert(UUID id) {
        checkDuplicate(id);
        User user = new User(id);
        user = userRepository.save(user);
        return new UserDTO(user.getId());
    }

    public void deleteById(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        User user = getUserIfExists(id, userOptional);
        List<Message> messages = messageRepository.findAllBySenderIdOrReceiverId(user.getId(), user.getId());
        List<Writing> writings = writingRepository.findAllBySenderIdOrReceiverId(user.getId(), user.getId());
        messageRepository.deleteAll(messages);
        writingRepository.deleteAll(writings);
        userRepository.deleteById(id);
    }
}
