package com.srt.message.repository;

import com.srt.message.config.type.ButtonType;
import com.srt.message.config.type.MessageType;
import com.srt.message.domain.KakaoMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface KakaoMessageRepository extends JpaRepository<KakaoMessage, Long> {
    @EntityGraph(value = "KakaoMessage.with.Member.SenderNumber.RepeatRule")
    Optional<KakaoMessage> findKakaoMessageById(long messageId);

    @Query(value = "select km from KakaoMessage km where km.member.id = :memberId",
            countQuery = "select count(km) from KakaoMessage km where km.member.id = :memberId")
    Page<KakaoMessage> findAllKakaoMessage(Pageable pageable, Long memberId);

    @Query(value = "select km from KakaoMessage km where km.member.id = :memberId and km.buttonType = :buttonType",
            countQuery = "select count(km) from KakaoMessage km where km.member.id = :memberId and km.buttonType = :buttonType")
    Page<KakaoMessage> findKakaoMessageByButtonType(Pageable pageable, Long memberId, ButtonType buttonType);

    @Query(value = "select kmr from KakaoMessageResult kmr where kmr.kakaoMessage.member.id = :memberId and kmr.contact.phoneNumber = :contactNumber",
            countQuery = "select count(kmr) from KakaoMessageResult kmr where kmr.kakaoMessage.member.id = :memberId and kmr.contact.phoneNumber = :contactNumber")
    Page<KakaoMessage> findKakaoMessageByContactNumber(Pageable pageable, Long memberId, String contactNumber);

    @Query(value = "select kmr from KakaoMessageResult kmr where kmr.kakaoMessage.member.id = :memberId and kmr.contact.memo = :contactMemo",
            countQuery = "select count(kmr) from KakaoMessageResult kmr where kmr.kakaoMessage.member.id = :memberId and kmr.contact.memo = :contactMemo")
    Page<KakaoMessage> findKakaoMessageByContactMemo(Pageable pageable, Long memberId, String contactMemo);

    @Query(value = "select km from KakaoMessage km where km.member.id = :memberId and km.title like %:messageContent% or km.subTitle like %:messageContent% or " +
            "km.content like %:messageContent% or km.description like %:messageContent%",
            countQuery = "select count(km) from KakaoMessage km where km.member.id = :memberId and km.title like %:messageContent% or km.subTitle like %:messageContent% or " +
            "km.content like %:messageContent% or km.description like %:messageContent%")
    Page<KakaoMessage> findKakaoMessageByMessageContent(Pageable pageable, Long memberId, String messageContent);
}
