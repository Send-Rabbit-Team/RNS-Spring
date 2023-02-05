package com.srt.message.repository;

import com.srt.message.domain.MessageImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageImageRepository extends JpaRepository<MessageImage, Long> {
}
