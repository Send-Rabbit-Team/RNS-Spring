package com.srt.message.repository;

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
//    @Query("select MR from MessageRule MR join Member M on MR.member = M where MR.member = :member ")
//    Optional<List<MessageRule>> getAll();
    //messageRule, Member, Broker

    Optional<List<MessageRule>> findByMemberId(Long memberId);

    // KT, SKT, LG 순으로 정렬해서 쿼리
    @Query("select m from MessageRule m left join fetch m.broker order by m.broker.name asc")
    List<MessageRule> findAllByMember(Member member);
}
