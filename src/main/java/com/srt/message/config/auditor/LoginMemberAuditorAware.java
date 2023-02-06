package com.srt.message.config.auditor;

import com.srt.message.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginMemberAuditorAware implements AuditorAware<Long> {
    private final LoginMember loginMember;
    private final HttpServletRequest request;

    @Override
    public Optional<Long> getCurrentAuditor() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes)
            return Optional.ofNullable(loginMember.getId());

        return Optional.empty(); // RabbitMQ로 consume 받는 상황일 때
    }
}
