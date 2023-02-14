package com.srt.message.repository;

import com.srt.message.domain.Contact;
import com.srt.message.domain.Message;
import com.srt.message.domain.MessageResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageResultRepository extends JpaRepository<MessageResult, Long> {
    List<MessageResult> findAllByMessageIdOrderByIdDesc(long messageId);

    Optional<MessageResult> findByContactAndMessage(Contact contact, Message message);

    List<MessageResult> findAllByDescriptionLike(String description);
}
