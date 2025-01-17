package com.dan.sd.device_management.services;

import com.dan.sd.device_management.controllers.handlers.exceptions.model.DuplicateResourceException;
import com.dan.sd.device_management.controllers.handlers.exceptions.model.ResourceNotFoundException;
import com.dan.sd.device_management.dtos.UserDTO;
import com.dan.sd.device_management.entities.User;
import com.dan.sd.device_management.repositories.DeviceRepository;
import com.dan.sd.device_management.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, DeviceRepository deviceRepository) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
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
        deviceRepository.deleteAll(deviceRepository.findByUserId(user));
        userRepository.deleteById(id);
    }
}
