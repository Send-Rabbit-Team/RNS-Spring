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

import java.util.Optional;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class PointService {
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    // 포인트 조회
    public GetPointRes getPoint(long memberId) {
        Member member = getExistMember(memberId);
        Point point = getExistPointOrMakeDefault(member);
        return GetPointRes.toDto(point);
    }

    // 포인트 충전
    public GetPointRes chargePoint(long memberId, int smsPoint, int kakaoPoint) {
        Member member = getExistMember(memberId);
        Point point = getExistPointOrMakeDefault(member);
        point.addSmsPoint(smsPoint);
        point.addKakaoPoint(kakaoPoint);
        return GetPointRes.toDto(pointRepository.save(point));
    }

    // SMS 포인트 환불
//    public boolean refundPoint(long memberId, int amount, MessageType messageType)

    // 포인트 검증
    public GetPointRes validPoint(long memberId, int smsPoint, int kakaoPoint) {
        Member member = getExistMember(memberId);
        Point point = getExistPointOrMakeDefault(member);
        if (point.getSmsPoint() < smsPoint || point.getKakaoPoint() < kakaoPoint)
            throw new BaseException(INSUFFICIENT_POINT);
        return GetPointRes.toDto(point);
    }

    // 포인트 결제 (SMS)
    public GetPointRes paySmsPoint(long memberId, int smsPoint) {
        Member member = getExistMember(memberId);
        Point point = getExistPointOrMakeDefault(member);
        if (point.getSmsPoint() < smsPoint)
            throw new BaseException(INSUFFICIENT_POINT);
        else
            point.subSmsPoint(smsPoint);
        return GetPointRes.toDto(pointRepository.save(point));
    }

    // 포인트 결제 (KAKAO)
    public GetPointRes payKakaoPoint(long memberId, int kakaoPoint) {
        Member member = getExistMember(memberId);
        Point point = getExistPointOrMakeDefault(member);
        if (point.getKakaoPoint() < kakaoPoint)
            throw new BaseException(INSUFFICIENT_POINT);
        else
            point.subKakaoPoint(kakaoPoint);
        return GetPointRes.toDto(pointRepository.save(point));
    }

    private Member getExistMember(long memberId) {
        return memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
    }

    private Point getExistPointOrMakeDefault(Member member) {
        Optional<Point> byMemberId = pointRepository.findByMemberId(member.getId());
        if (byMemberId.isEmpty()) {
            return pointRepository.save(Point.builder()
                            .member(member)
                            .smsPoint(0)
                            .kakaoPoint(0)
                            .build());
        } else {
            return byMemberId.get();
        }
    }
}
