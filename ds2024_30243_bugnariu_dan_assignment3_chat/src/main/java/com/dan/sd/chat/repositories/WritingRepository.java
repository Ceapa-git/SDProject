package com.dan.sd.chat.repositories;

import com.dan.sd.chat.entities.Writing;
import com.dan.sd.chat.entities.keys.WritingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WritingRepository extends JpaRepository<Writing, WritingId> {
    List<Writing> findAllByReceiverId(UUID receiverId);
    List<Writing> findAllBySenderIdOrReceiverId(UUID senderId, UUID receiverId);
    Writing findBySenderIdAndReceiverId(UUID senderId, UUID receiverId);
}
