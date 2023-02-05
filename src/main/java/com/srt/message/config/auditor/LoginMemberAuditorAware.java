package com.srt.message.config.auditor;

import com.srt.message.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginMemberAuditorAware implements AuditorAware<Long> {
    private final LoginMember loginMember;
    private final HttpServletRequest request;

    @Override
    public Optional<Long> getCurrentAuditor() {
        if(request == null) // RabbitMQ로 consume 받는 상황일 때
            return Optional.empty();

        return Optional.ofNullable(loginMember.getId());
    }
}
