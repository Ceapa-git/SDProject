package com.dan.sd.chat.services;

import com.dan.sd.chat.constants.WebSocketEndpoints;
import com.dan.sd.chat.dtos.MessageDTO;
import com.dan.sd.chat.dtos.MessageDetailsDTO;
import com.dan.sd.chat.entities.Message;
import com.dan.sd.chat.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate template;

    @Autowired
    public MessageService(MessageRepository messageRepository, SimpMessagingTemplate template) {
        this.messageRepository = messageRepository;
        this.template = template;
    }

    public void seen(MessageDTO messageDTO) {
        Message messages = messageRepository.findBySenderIdAndReceiverIdAndTextAndTimestamp(
                messageDTO.getSenderId(),
                messageDTO.getReceiverId(),
                messageDTO.getText(),
                messageDTO.getTimestamp());
        messages.setSeen(true);
        messageRepository.save(messages);
        template.convertAndSend(
                WebSocketEndpoints.CHAT_SEEN + "user/" + messageDTO.getSenderId(),
                messageDTO.getReceiverId()
        );
    }

    public void insert(MessageDetailsDTO messageDTO) {
        Message message = new Message();
        message.setText(messageDTO.getText());
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        message.setTimestamp(new Date());
        message.setSeen(false);
        message = messageRepository.save(message);

        template.convertAndSend(
                WebSocketEndpoints.CHAT_RECEIVE + "user/" + message.getReceiverId(),
                new MessageDTO(message.getText(), message.getSenderId(), message.getReceiverId(), message.getTimestamp(), message.getSeen())
        );
    }

    public List<MessageDTO> findAllForUser(UUID userId) {
        List<Message> messages = messageRepository.findAllBySenderIdOrReceiverId(userId, userId);
        messages.forEach(message -> {
            if (message.getReceiverId().equals(userId)) {
                if (!message.getSeen()) {
                    message.setSeen(true);
                    messageRepository.save(message);
                    template.convertAndSend(
                            WebSocketEndpoints.CHAT_SEEN + "user/" + message.getSenderId(),
                            message.getReceiverId()
                    );
                }
            }
        });
        return messages.stream()
                .map(message -> new MessageDTO(message.getText(), message.getSenderId(), message.getReceiverId(), message.getTimestamp(), message.getSeen()))
                .toList();
    }
}
