package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Member;
import com.srt.message.domain.Point;
import com.srt.message.dto.point.get.GetPointRes;
import com.srt.message.repository.MemberRepository;
import com.srt.message.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class PointService {
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    // 포인트 조회
    public GetPointRes getPoint(long memberId) {
        getExistMember(memberId);
        Point point = pointRepository.findByMemberId(memberId).orElseThrow(() -> new BaseException(NOT_EXIST_POINT));
        return GetPointRes.toDto(point);
    }

    // 포인트 충전
    public GetPointRes chargePoint(long memberId, int addPoint) {
        getExistMember(memberId);
        Point point = pointRepository.findByMemberId(memberId).orElseThrow(() -> new BaseException(NOT_EXIST_POINT));
        point.addPoint(addPoint);
        return GetPointRes.toDto(pointRepository.save(point));
    }

    // 포인트 결제
    public GetPointRes payPoint(long memberId, int subPoint) {
        getExistMember(memberId);
        Point point = pointRepository.findByMemberId(memberId).orElseThrow(() -> new BaseException(NOT_EXIST_POINT));
        if (point.getPoint() < subPoint)
            throw new BaseException(INSUFFICIENT_POINT);
        else
            point.subPoint(subPoint);
        return GetPointRes.toDto(pointRepository.save(point));
    }

    private Member getExistMember(long memberId) {
        return memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
    }
}
