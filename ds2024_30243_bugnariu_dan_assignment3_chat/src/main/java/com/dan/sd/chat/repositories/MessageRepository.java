package com.dan.sd.chat.repositories;

import com.dan.sd.chat.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllBySenderIdOrReceiverId(UUID senderId, UUID receiverId);
    Message findBySenderIdAndReceiverIdAndTextAndTimestamp(UUID senderId, UUID receiverId, String text, Date timestamp);
}
