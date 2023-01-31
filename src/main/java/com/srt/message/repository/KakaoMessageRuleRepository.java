package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.KakaoMessageRule;
import com.srt.message.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KakaoMessageRuleRepository extends JpaRepository<KakaoMessageRule, Long> {
    @Query("select km from KakaoMessageRule km left join fetch km.kakaoBroker order by km.kakaoBroker.name asc")
    List<KakaoMessageRule> findAllByMember(Member member);

    List<KakaoMessageRule> findByMemberIdAndStatus(Long memberId, BaseStatus status);
}
