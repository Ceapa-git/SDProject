package com.dan.sd.chat.services;

import com.dan.sd.chat.constants.WebSocketEndpoints;
import com.dan.sd.chat.dtos.WritingDTO;
import com.dan.sd.chat.entities.Writing;
import com.dan.sd.chat.repositories.WritingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WritingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WritingService.class);
    private final WritingRepository writingRepository;
    private final SimpMessagingTemplate template;

    @Autowired
    public WritingService(WritingRepository writingRepository, SimpMessagingTemplate template) {
        this.writingRepository = writingRepository;
        this.template = template;
    }

    public void startWriting(WritingDTO writingDTO) {
        Writing writing = new Writing();
        writing.setSenderId(writingDTO.getSenderId());
        writing.setReceiverId(writingDTO.getReceiverId());
        writingRepository.save(writing);
        template.convertAndSend(
                WebSocketEndpoints.CHAT_WRITING_START + "user/" + writingDTO.getReceiverId(),
                writingDTO.getSenderId()
        );
        LOGGER.error("User with id {} started writing to user with id {}", writingDTO.getSenderId(), writingDTO.getReceiverId());
        LOGGER.error(WebSocketEndpoints.CHAT_WRITING_START + "user/{}", writingDTO.getReceiverId());
    }

    public void stopWriting(WritingDTO writingDTO) {
        Writing writing = writingRepository.findBySenderIdAndReceiverId(writingDTO.getSenderId(), writingDTO.getReceiverId());
        writingRepository.delete(writing);
        template.convertAndSend(
                WebSocketEndpoints.CHAT_WRITING_STOP + "user/" + writingDTO.getReceiverId(),
                writingDTO.getSenderId()
        );
        LOGGER.error("User with id {} stopped writing to user with id {}", writingDTO.getSenderId(), writingDTO.getReceiverId());
        LOGGER.error(WebSocketEndpoints.CHAT_WRITING_STOP + "user/{}", writingDTO.getReceiverId());
    }

    public List<WritingDTO> findAllForUser(UUID userId) {
        List<Writing> writings = writingRepository.findAllByReceiverId(userId);
        return writings.stream()
                .map(writing -> new WritingDTO(writing.getSenderId(), writing.getReceiverId()))
                .toList();
    }
}
