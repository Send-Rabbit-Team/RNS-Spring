package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.*;
import com.srt.message.repository.*;
import com.srt.message.service.dto.message.kakao.BrokerKakaoMessageDto;
import com.srt.message.service.dto.message.kakao.post.PostSendKakaoMessageReq;
import com.srt.message.service.rabbit.KakaoBrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.*;
import static com.srt.message.config.status.BaseStatus.ACTIVE;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoMessageService {
    private final MemberRepository memberRepository;
    private final SenderNumberRepository senderNumberRepository;
    private final ContactRepository contactRepository;

    private final KakaoMessageRepository kakaoMessageRepository;

    private final KakaoBrokerService kakaoBrokerService;

    public String sendKakaoMessageToBroker(PostSendKakaoMessageReq messageReq, long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));

        List<Contact> contacts = contactRepository.findByPhoneNumberIn(messageReq.getReceivers());
        // 연락처 예외 처리
        if(contacts.contains(null) || contacts.isEmpty())
            throw new BaseException(NOT_EXIST_CONTACT_NUMBER);

        // 발신자 번호 예외 처리
        SenderNumber senderNumber = senderNumberRepository.findByPhoneNumberAndStatus(messageReq.getSenderNumber(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_SENDER_NUMBER));

        log.info("senderNumber - memberId {}", senderNumber.getMember().getId());
        log.info("member - memberId {}", member.getId());

        if(senderNumber.getMember().getId() != member.getId())
            throw new BaseException(NOT_MATCH_SENDER_NUMBER);

        KakaoMessage message = KakaoMessage.builder()
                .member(member)
                .senderNumber(senderNumber)
                .subject(messageReq.getKakaoMessageDto().getSubject())
                .subTitle(messageReq.getKakaoMessageDto().getSubtitle())
                .content(messageReq.getKakaoMessageDto().getContent())
                .image(messageReq.getKakaoMessageDto().getImage())
                .description(messageReq.getKakaoMessageDto().getDescription())
                .build();

        kakaoMessageRepository.save(message);

        BrokerKakaoMessageDto brokerMessageDto = BrokerKakaoMessageDto.builder()
                .kakaoMessageDto(messageReq.getKakaoMessageDto())
                .kakaoMessage(message)
                .contacts(contacts)
                .member(member)
                .count(messageReq.getCount())
                .build();

        return kakaoBrokerService.sendKakaoMessage(brokerMessageDto);
    }

}
