package com.srt.message.repository;

import com.srt.message.config.type.MessageType;
import com.srt.message.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // 캐시용
    @EntityGraph(value = "Message.with.Member.SenderNumber.RepeatRule")
    Optional<Message> findMessageById(long messageId);

    // 사용자 보낸 메시지 페이징 조회
    @Query(value = "select m from Message m where m.member.id = :memberId",
            countQuery = "select count(m) from Message m where m.member.id = :memberId")
    Page<Message> findAllMessage(long memberId, Pageable pageable);

    // 유형별 메시지 필터 페이징 조회
    @Query(value = "select m from Message m where m.member.id = :memberId and m.messageType = :messageType",
            countQuery = "select count(m) from Message m where m.member.id = :memberId and m.messageType = :messageType")
    Page<Message> findMessagesByMessageType(MessageType messageType, long memberId, Pageable pageable);
}
