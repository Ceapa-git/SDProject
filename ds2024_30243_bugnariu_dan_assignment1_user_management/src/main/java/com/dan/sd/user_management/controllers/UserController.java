package com.dan.sd.user_management.controllers;

import com.dan.sd.user_management.dtos.UserDTO;
import com.dan.sd.user_management.dtos.UserDetailsDTO;
import com.dan.sd.user_management.dtos.UserUpdateDTO;
import com.dan.sd.user_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(userService.findUsers());
    }

    @PostMapping()
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDetailsDTO userDetailsDTO, @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(userService.insert(userDetailsDTO, token));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.update(id, userUpdateDTO));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        userService.deleteById(id, token);
        return ResponseEntity.noContent().build();
    }
}
