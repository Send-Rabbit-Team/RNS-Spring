package com.srt.message.repository;

import com.srt.message.domain.KakaoMessage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KakaoMessageRepository extends JpaRepository<KakaoMessage, Long> {
    // 캐시용
    @EntityGraph(value = "KakaoMessage.with.Member.SenderNumber")
    Optional<KakaoMessage> findKakaoMessageById(long messageId);
}
