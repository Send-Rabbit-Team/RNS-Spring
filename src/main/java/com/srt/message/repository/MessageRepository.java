package com.srt.message.repository;

import com.srt.message.domain.Message;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 캐시용
    @EntityGraph(value = "Message.with.Member.SenderNumber.RepeatRule")
    Optional<Message> findMessageById(long messageId);
}
