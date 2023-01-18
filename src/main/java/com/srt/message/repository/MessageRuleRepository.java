package com.srt.message.repository;

import com.srt.message.domain.Broker;
import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import com.srt.message.domain.MessageRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRuleRepository extends JpaRepository<MessageRule, Long> {
    // KT, SKT, LG 순으로 정렬해서 쿼리
    @Query("select m from MessageRule m left join fetch m.broker order by m.broker.name asc")
    List<MessageRule> findAllByMember(Member member);

    Optional<MessageRule> findByBroker(Broker broker);
}
