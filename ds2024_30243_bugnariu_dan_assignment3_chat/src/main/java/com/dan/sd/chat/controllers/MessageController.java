package com.dan.sd.chat.controllers;

import com.dan.sd.chat.dtos.MessageDTO;
import com.dan.sd.chat.dtos.MessageDetailsDTO;
import com.dan.sd.chat.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/message")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping(value = "/socket/user/{userId}")
    public void sendMessage(@Payload MessageDetailsDTO messageDTO, @DestinationVariable String userId) {
        Logger logger = LoggerFactory.getLogger(MessageController.class.getName());
        logger.error("Received message: {} from {}", messageDTO, userId);
        messageService.insert(messageDTO);
    }

    @PostMapping(value = "/seen")
    public void seenMessage(@RequestBody MessageDTO messageDTO) {
        Logger logger = LoggerFactory.getLogger(MessageController.class.getName());
        logger.error("Seen message: {}", messageDTO);
        messageService.seen(messageDTO);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<List<MessageDTO>> getMessageById(@PathVariable UUID id) {
        return ResponseEntity.ok(messageService.findAllForUser(id));
    }
}
