package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.config.type.LoginType;
import com.srt.message.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByIdAndStatus(long memberId, BaseStatus baseStatus);
    Optional<Member> findByEmailIgnoreCase(String email);

    Optional<Member> findByEmailIgnoreCaseAndLoginType(String email, LoginType loginType);
}
