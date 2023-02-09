package com.srt.message.dto.point.get;

import com.srt.message.domain.Point;
import lombok.Builder;

@Builder
public class GetPointRes {
    private long pointId;
    private long memberId;
    private int point;

    public static GetPointRes toDto(Point point) {
        return GetPointRes.builder()
                .pointId(point.getId())
                .memberId(point.getMember().getId())
                .point(point.getPoint())
                .build();
    }
}
