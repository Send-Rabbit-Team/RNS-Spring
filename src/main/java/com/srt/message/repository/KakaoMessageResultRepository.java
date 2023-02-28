package com.srt.message.repository;

import com.srt.message.domain.KakaoMessageResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KakaoMessageResultRepository extends JpaRepository<KakaoMessageResult, Long> {

    List<KakaoMessageResult> findKakaoMessageResultByKakaoMessageId(Long kakaoMessageId);

    @EntityGraph(value = "Contact.KakaoBroker")
    List<KakaoMessageResult> findAllByKakaoMessageIdOrderByIdDesc(long kakaoMessageId);
}
