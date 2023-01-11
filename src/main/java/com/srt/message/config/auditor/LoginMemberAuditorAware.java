package com.srt.message.config.auditor;

import com.srt.message.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginMemberAuditorAware implements AuditorAware<Member> {
    private final LoginMember loginMember;

    @Override
    public Optional<Member> getCurrentAuditor() {
        return Optional.of(loginMember.toEntity());
    }
}
