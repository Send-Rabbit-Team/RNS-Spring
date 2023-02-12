package com.srt.message.dto.point.get;

import com.srt.message.domain.Point;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetPointRes {
    private long pointId;
    private long memberId;
    private int smsPoint;
    private int kakaoPoint;

    public static GetPointRes toDto(Point point) {
        return GetPointRes.builder()
                .pointId(point.getId())
                .memberId(point.getMember().getId())
                .smsPoint(point.getSmsPoint())
                .kakaoPoint(point.getKakaoPoint())
                .build();
    }
}
