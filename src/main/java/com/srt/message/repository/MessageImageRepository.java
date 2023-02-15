package com.srt.message.repository;

import com.srt.message.domain.MessageImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageImageRepository extends JpaRepository<MessageImage, Long> {
    List<MessageImage> findAllByMessageId(long messageId);
}
