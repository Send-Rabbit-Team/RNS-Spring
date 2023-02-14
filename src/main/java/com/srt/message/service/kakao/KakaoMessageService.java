package com.srt.message.service.kakao;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.ReserveStatus;
import com.srt.message.domain.*;
import com.srt.message.dto.kakao_message.KakaoMessageDto;
import com.srt.message.repository.*;
import com.srt.message.dto.kakao_message.BrokerKakaoMessageDto;
import com.srt.message.dto.kakao_message.post.PostKakaoMessageReq;
import com.srt.message.service.PointService;
import com.srt.message.service.SchedulerService;
import com.srt.message.service.kakao.KakaoBrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoMessageService {
    private final ReserveMessageContactRepository reserveMessageContactRepository;
    private final MemberRepository memberRepository;
    private final ContactRepository contactRepository;
    private final KakaoMessageRepository kakaoMessageRepository;
    private final ReserveKakaoMessageRepository reserveKakaoMessageRepository;

    private final KakaoBrokerService kakaoBrokerService;
    private final KakaoMessageReserveService kakaoMessageReserveService;
    private final SchedulerService schedulerService;
    private final PointService pointService;

    public String sendKakaoMessageToBroker(PostKakaoMessageReq messageReq, long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));

        // Find Contacts
        List<Contact> contacts = contactRepository.findByPhoneNumberIn(messageReq.getReceivers());
        if (contacts.contains(null) || contacts.isEmpty())
            throw new BaseException(NOT_EXIST_CONTACT_NUMBER);

        // Pay Point
        pointService.payKakaoPoint(memberId, contacts.size());

        // Save KakaoMessage
        KakaoMessage kakaoMessage = KakaoMessageDto.toEntity(messageReq.getKakaoMessageDto(), member);
        kakaoMessageRepository.save(kakaoMessage);

        BrokerKakaoMessageDto brokerMessageDto = BrokerKakaoMessageDto.builder()
                .kakaoMessageDto(messageReq.getKakaoMessageDto())
                .kakaoMessage(kakaoMessage)
                .contacts(contacts)
                .member(member)
                .build();
        log.info("brokerMessageDto : " + brokerMessageDto);

        // 크론 표현식 있으면, 예약 발송으로 이동
        if (messageReq.getKakaoMessageDto().getCronExpression() != null)
            return kakaoMessageReserveService.reserveKakaoMessage(brokerMessageDto);

        return kakaoBrokerService.sendKakaoMessage(brokerMessageDto);
    }
}