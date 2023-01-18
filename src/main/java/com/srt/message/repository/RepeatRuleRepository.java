package com.srt.message.repository;

import com.srt.message.domain.RepeatRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatRuleRepository extends JpaRepository<RepeatRule, Long> {
}
