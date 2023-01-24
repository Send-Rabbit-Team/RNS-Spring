package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.*;
import com.srt.message.service.dto.message.kakao.BrokerKakaoMessageDto;
import com.srt.message.service.dto.message.kakao.KakaoMessageDto;
import com.srt.message.service.dto.message.kakao.post.PostSendKakaoMessageReq;
import com.srt.message.service.dto.message.sms.BrokerMessageDto;
import com.srt.message.service.dto.message.sms.post.PostSendMessageReq;
import com.srt.message.repository.*;
import com.srt.message.service.rabbit.BrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.*;
import static com.srt.message.config.status.BaseStatus.ACTIVE;

@Log4j2
@Service
@RequiredArgsConstructor
public class MessageService {
    private final MemberRepository memberRepository;
    private final SenderNumberRepository senderNumberRepository;
    private final ContactRepository contactRepository;

    private final MessageRepository messageRepository;

    private final KakaoMessageRepository kakaoMessageRepository;

    private final BrokerService brokerService;


    public String sendMessageToBroker(PostSendMessageReq messageReq, long memberId){
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

        Message message = Message.builder()
                .member(member)
                .senderNumber(senderNumber)
                .content(messageReq.getMessage().getContent())
                .image(messageReq.getMessage().getImage())
                .messageType(messageReq.getMessage().getMessageType())
                .build();

        messageRepository.save(message);

        BrokerMessageDto brokerMessageDto = BrokerMessageDto.builder()
                .smsMessageDto(messageReq.getMessage())
                .message(message)
                .contacts(contacts)
                .member(member)
                .count(messageReq.getCount())
                .build();

        return brokerService.sendSmsMessage(brokerMessageDto);
    }

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
                .subject(messageReq.getMessage().getSubject())
                .subTitle(messageReq.getMessage().getSubtitle())
                .content(messageReq.getMessage().getContent())
                .image(messageReq.getMessage().getImage())
                .description(messageReq.getMessage().getDescription())
                .buttonUrl(messageReq.getMessage().getButtonUrl())
                .buttonTitle(messageReq.getMessage().getButtonTitle())
                .buttonType(messageReq.getMessage().getButtonType())
                .build();

        kakaoMessageRepository.save(message);

        BrokerKakaoMessageDto brokerMessageDto = BrokerKakaoMessageDto.builder()
                .kakaoMessageDto(messageReq.getMessage())
                .message(message)
                .contacts(contacts)
                .member(member)
                .count(messageReq.getCount())
                .build();

        return brokerService.sendKakaoMessage(brokerMessageDto);
    }

}
