package com.srt.message.repository;

import com.srt.message.domain.ReserveMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ReserveMessageRepository extends CrudRepository<ReserveMessage, Long> {
    Optional<ReserveMessage> findByMessageId(long messageId);
}
