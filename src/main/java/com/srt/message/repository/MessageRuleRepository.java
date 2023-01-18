package com.srt.message.repository;

import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.MessageRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRuleRepository extends JpaRepository<MessageRule, Long> {
//    @Query("select MR from MessageRule MR join Member M on MR.member = M where MR.member = :member ")
//    Optional<List<MessageRule>> getAll();
    //messageRule, Member, Broker

    Optional<List<MessageRule>> findByMemberId(Long memberId);

}
