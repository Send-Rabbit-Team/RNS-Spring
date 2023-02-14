package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.KakaoTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KakaoTemplateRepository extends JpaRepository<KakaoTemplate, Long> {

    Optional<KakaoTemplate> findByIdAndStatus(Long templateId, BaseStatus status);

    List<KakaoTemplate> findByMemberIdAndStatusOrderByUpdatedAtDesc(Long memberId, BaseStatus status);

    @Query(value = "select kt from KakaoTemplate kt where kt.member.id = :memberId and kt.status = :status",
    countQuery = "select count(kt) from KakaoTemplate kt where kt.member.id = :memberId and kt.status = :status")
    Page<KakaoTemplate> findAllTemplate(Long memberId, BaseStatus status, Pageable pageable);


}
