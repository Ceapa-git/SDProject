package com.dan.sd.user_management.controllers;

import com.dan.sd.user_management.dtos.AuthDTO;
import com.dan.sd.user_management.dtos.AuthDetailsDTO;
import com.dan.sd.user_management.security.JwtGenerator;
import com.dan.sd.user_management.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class AuthController {
    private final JwtGenerator jwtGenerator;
    private final UserService userService;

    public AuthController(JwtGenerator jwtGenerator, UserService userService) {
        this.jwtGenerator = jwtGenerator;
        this.userService = userService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO authDTO)
    {
        AuthDetailsDTO authDetailsDTO = userService.auth(authDTO);
        Long jwtExpiration = 3600000L;
        String jwt = jwtGenerator.generateToken(authDetailsDTO.getUsername(), jwtExpiration);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("loginData", authDetailsDTO);
        responseBody.put("jwt", jwt);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(responseBody);
    }

    @PostMapping(value = "/checkToken")
    public ResponseEntity<?> checkToken()
    {
        return ResponseEntity.status(HttpStatus.OK).body("Token is valid");
    }
}
