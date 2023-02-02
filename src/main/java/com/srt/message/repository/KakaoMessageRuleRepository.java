package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.KakaoMessageRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KakaoMessageRuleRepository extends JpaRepository<KakaoMessageRule, Long> {
    List<KakaoMessageRule> findByMemberIdAndStatus(Long memberId, BaseStatus status);
}
