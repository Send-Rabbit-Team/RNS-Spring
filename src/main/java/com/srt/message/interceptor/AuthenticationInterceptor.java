package com.srt.message.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.auditor.LoginMember;
import com.srt.message.config.auditor.LoginMemberAuditorAware;
import com.srt.message.dto.jwt.JwtInfo;
import com.srt.message.jwt.JwtService;
import com.srt.message.jwt.NoIntercept;
import com.srt.message.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;

@Log4j2
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("url : {}", request.getRequestURI());
        // @NoIntercept -> not apply intercept
        boolean check = checkNoIntercept(handler, NoIntercept.class);
        if (check) return true;

        getJwtToken(request, handler);

        return true;
    }

    private boolean checkNoIntercept(Object handler, Class c) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (handlerMethod.getMethodAnnotation(c) != null) {
            return true;
        }
        return false;
    }

    private void getJwtToken(HttpServletRequest request, Object handler) {
        LinkedHashMap jwtInfo;

        // ResponseIntercept는 Jwt가 null이여도 받아옴
        jwtInfo = jwtService.getJwtInfo();

        if (jwtInfo == null)
            return;

        JwtInfo convertJwtInfo = objectMapper.convertValue(jwtInfo, JwtInfo.class);
        request.setAttribute("jwtInfo", convertJwtInfo);

        // auditor
        authService.updateLoginMember(convertJwtInfo.getMemberId());
    }
}
