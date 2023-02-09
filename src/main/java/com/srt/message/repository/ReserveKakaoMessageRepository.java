package com.srt.message.repository;

import com.srt.message.config.status.ReserveStatus;
import com.srt.message.domain.ReserveKakaoMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ReserveKakaoMessageRepository extends CrudRepository<ReserveKakaoMessage, Long> {

    Optional<ReserveKakaoMessage> findByKakaoMessageId(long messageId);

    @Query(value = "select rkm from ReserveKakaoMessage rkm where rkm.kakaoMessage.member.id = :memberId")
    Page<ReserveKakaoMessage> findByMemberId(PageRequest pageRequest, long memberId);

    @EntityGraph(value = "ReserveKakaoMessage.with.KakaoMessage.Member")
    List<ReserveKakaoMessage> findAllByReserveStatus(ReserveStatus status);
}
