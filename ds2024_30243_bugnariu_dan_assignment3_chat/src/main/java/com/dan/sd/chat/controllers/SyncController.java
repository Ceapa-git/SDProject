package com.dan.sd.chat.controllers;

import com.dan.sd.chat.dtos.UserDTO;
import com.dan.sd.chat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/sync")
public class SyncController {
    private final UserService userService;

    @Autowired
    public SyncController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserDTO> createUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.insert(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
