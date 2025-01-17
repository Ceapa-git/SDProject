package com.dan.sd.user_management.services;

import com.dan.sd.user_management.constants.Role;
import com.dan.sd.user_management.controllers.handlers.exceptions.model.AuthIncorrectCredentialsException;
import com.dan.sd.user_management.controllers.handlers.exceptions.model.DuplicateResourceException;
import com.dan.sd.user_management.controllers.handlers.exceptions.model.EntityValidationException;
import com.dan.sd.user_management.controllers.handlers.exceptions.model.ResourceNotFoundException;
import com.dan.sd.user_management.dtos.*;
import com.dan.sd.user_management.dtos.builders.UserBuilder;
import com.dan.sd.user_management.entities.User;
import com.dan.sd.user_management.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final String syncDeviceServiceUrl;
    private final String syncChatServiceUrl;

    @Autowired
    public UserService(UserRepository userRepository, RestTemplate restTemplate,
                       @Value("${device.service.url}") String syncDeviceServiceUrl,
                       @Value("${chat.service.url}") String syncChatServiceUrl) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.syncDeviceServiceUrl = syncDeviceServiceUrl;
        this.syncChatServiceUrl = syncChatServiceUrl;
    }

    private void checkRole(UserDetailsDTO userDetailsDTO) {
        try {
            Role.valueOf(userDetailsDTO.getRole());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Role {} is not valid", userDetailsDTO.getRole());
            throw new EntityValidationException(User.class.getSimpleName() + " with username: " + userDetailsDTO.getUsername(),
                    Collections.singletonList("Role " + userDetailsDTO.getRole() + " is not valid"));
        }
    }

    private User getUserIfExists(UUID id, Optional<User> optionalUser) {
        if (optionalUser.isEmpty()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return optionalUser.get();
    }

    private User findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            LOGGER.error("Person with username {} was not found in db", username);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with username: " + username);
        }
        return optionalUser.get();
    }

    public List<UserDTO> findUsers() {
        List<User> personList = userRepository.findAll();
        return personList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO insert(UserDetailsDTO userDetailsDTO, String token) {
        checkRole(userDetailsDTO);

        if (userRepository.findByUsername(userDetailsDTO.getUsername()).isPresent()) {
            LOGGER.error("Person with username {} already exists in db", userDetailsDTO.getUsername());
            throw new DuplicateResourceException(User.class.getSimpleName() + " with username: " + userDetailsDTO.getUsername());
        }
        User user = UserBuilder.toEntity(userDetailsDTO);
        user = userRepository.save(user);
        LOGGER.debug("Person with id {} was inserted in db", user.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        restTemplate.postForEntity(syncDeviceServiceUrl + "/" + user.getId().toString(), requestEntity, Void.class);
        restTemplate.postForEntity(syncChatServiceUrl + "/" + user.getId().toString(), requestEntity, Void.class);

        return UserBuilder.toUserDTO(user);
    }

    public UserDTO findById(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return UserBuilder.toUserDTO(getUserIfExists(id, optionalUser));
    }

    public UserDTO update(UUID id, UserUpdateDTO userUpdateDTO) {
        Optional<User> optionalUser = userRepository.findById(id);
        User user = getUserIfExists(id, optionalUser);

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(user.getRole(), user.getUsername(), user.getPassword());
        if (userUpdateDTO.getRole() != null) {
            userDetailsDTO.setRole(userUpdateDTO.getRole());
        }
        if (userUpdateDTO.getUsername() != null) {
            userDetailsDTO.setUsername(userUpdateDTO.getUsername());
        }
        if (userUpdateDTO.getPassword() != null) {
            userDetailsDTO.setPassword(userUpdateDTO.getPassword());
        }

        checkRole(userDetailsDTO);

        user.setRole(userDetailsDTO.getRole());
        user.setUsername(userDetailsDTO.getUsername());
        user.setPassword(userDetailsDTO.getPassword());
        user = userRepository.save(user);
        LOGGER.debug("Person with id {} was updated in db", user.getId());
        return UserBuilder.toUserDTO(user);
    }

    public void deleteById(UUID id, String token) {
        userRepository.deleteById(id);
        LOGGER.debug("Person with id {} was deleted from db", id);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        restTemplate.exchange(
                syncDeviceServiceUrl + "/" + id,
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        restTemplate.exchange(
                syncChatServiceUrl + "/" + id,
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
    }

    public AuthDetailsDTO auth(AuthDTO authDTO) {
        User user = null;
        try {
            user = findByUsername(authDTO.getUsername());
        } catch (ResourceNotFoundException ignored) {
        }
        if (user == null || !user.getPassword().equals(authDTO.getPassword())) {
            LOGGER.error("Invalid credentials for user with username: {}", authDTO.getUsername());
            throw new AuthIncorrectCredentialsException("Invalid credentials for user with username: " + authDTO.getUsername());
        }
        return new AuthDetailsDTO(user.getId(), user.getUsername(), user.getRole());
    }
}
