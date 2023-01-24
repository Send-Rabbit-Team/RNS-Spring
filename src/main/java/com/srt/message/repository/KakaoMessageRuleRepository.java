package com.srt.message.repository;

import com.srt.message.domain.Broker;
import com.srt.message.domain.KakaoMessageRule;
import com.srt.message.domain.Member;
import com.srt.message.domain.MessageRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KakaoMessageRuleRepository extends JpaRepository<KakaoMessageRule, Long> {
    @Query("select km from KakaoMessageRule km left join fetch km.broker order by km.broker.name asc")
    List<KakaoMessageRule> findAllByMember(Member member);

    Optional<KakaoMessageRule> findByBroker(Broker broker);
}
