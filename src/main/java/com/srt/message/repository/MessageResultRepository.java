package com.srt.message.repository;

import com.srt.message.domain.MessageResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageResultRepository extends JpaRepository<MessageResult, Long> {
    List<MessageResult> findAllByMessageId(long messageId);
}
