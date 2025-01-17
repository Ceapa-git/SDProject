package com.dan.sd.chat.controllers;

import com.dan.sd.chat.dtos.WritingDTO;
import com.dan.sd.chat.services.WritingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/writing")
public class WritingController {
    private final WritingService writingService;

    @Autowired
    public WritingController(WritingService writingService) {
        this.writingService = writingService;
    }

    @PostMapping(value = "/start")
    public void startWriting(@RequestBody WritingDTO writingDTO) {
        writingService.startWriting(writingDTO);
    }

    @PostMapping(value = "/stop")
    public void stopWriting(@RequestBody WritingDTO writingDTO) {
        writingService.stopWriting(writingDTO);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<List<WritingDTO>> getWritingById(@PathVariable UUID id) {
        return ResponseEntity.ok(writingService.findAllForUser(id));
    }
}
