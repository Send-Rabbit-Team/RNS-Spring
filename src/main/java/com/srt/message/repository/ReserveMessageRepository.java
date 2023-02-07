package com.srt.message.repository;

import com.srt.message.config.status.ReserveStatus;
import com.srt.message.domain.ReserveMessage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface ReserveMessageRepository extends CrudRepository<ReserveMessage, Long> {
    Optional<ReserveMessage> findByMessageId(long messageId);

    @EntityGraph(value = "ReserveMessage.with.Message.SenderNumber.Member")
    List<ReserveMessage> findAllByReserveStatus(ReserveStatus reserveStatus);
}
