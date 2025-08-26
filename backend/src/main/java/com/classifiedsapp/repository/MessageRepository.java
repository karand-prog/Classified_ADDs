package com.classifiedsapp.repository;

import com.classifiedsapp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverId(Long receiverId);
    List<Message> findBySenderId(Long senderId);
    List<Message> findByAdId(int adId);
    List<Message> findByReceiverIdAndReadFalse(Long receiverId);
    List<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<Message> findBySenderIdAndReceiverIdAndAdId(Long senderId, Long receiverId, int adId);
} 