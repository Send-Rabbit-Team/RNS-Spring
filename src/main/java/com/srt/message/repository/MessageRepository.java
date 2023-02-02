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

    // 예약된 메시지 필터 페이징 조회
    @Query(value = "select m from Message m join ReserveMessage rm where m.member.id = :memberId and m.id = rm.message.id " +
            "and rm.status = 'ACTIVE'",
            countQuery = "select count(m) from Message m where m.member.id = :memberId and m.messageType = :messageType")
    Page<Message> findReserveMessage(long memberId, Pageable pageable);


    // 수신자 검색 페이징 조회
    @Query(value = "select mr.message from MessageResult mr left join mr.message where mr.message.member.id = :memberId " +
            "and mr.contact.phoneNumber = :receiveNumber")
    Page<Message> findByReceiveNumber(String receiveNumber, long memberId, Pageable pageable);

    // 발신자 검색 페이징 조회
    @Query(value = "select mr.message from MessageResult mr left join mr.message m where mr.message.member.id = :memberId " +
            "and m.senderNumber.phoneNumber = :senderNumber")
    Page<Message> findBySenderNumber(String senderNumber, long memberId, Pageable pageable);

    // 메모 키워드 검색 페이징 조회
    @Query(value = "select mr.message from MessageResult mr left join mr.message where mr.message.member.id = :memberId " +
            "and mr.contact.memo like %:keyword%")
    Page<Message> findByMemo(String keyword, long memberId, Pageable pageable);

    // 메시지 내용 키워드 검색 페이징 조회
    @Query(value = "select mr.message from MessageResult mr join mr.message where mr.message.member.id = :memberId " +
            "and mr.message.content like %:keyword%")
    Page<Message> findByMessageContent(String keyword, long memberId, Pageable pageable);
}
