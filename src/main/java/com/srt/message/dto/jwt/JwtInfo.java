package com.srt.message.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtInfo {
    private Long memberId;

    static public Long getMemberId(HttpServletRequest request){
        System.out.println("request = " + request);
        JwtInfo jwtInfo = (JwtInfo) request.getAttribute("jwtInfo");
        System.out.println("jwtInfo = " + jwtInfo);

        if(jwtInfo == null)
            return null;

        return jwtInfo.getMemberId();
    }
}
