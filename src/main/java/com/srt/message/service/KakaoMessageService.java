package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.*;
import com.srt.message.repository.*;
import com.srt.message.dto.message.kakao.BrokerKakaoMessageDto;
import com.srt.message.dto.message.kakao.post.PostSendKakaoMessageReq;
import com.srt.message.service.rabbit.KakaoBrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoMessageService {
    private final MemberRepository memberRepository;
    private final ContactRepository contactRepository;

    private final KakaoMessageRepository kakaoMessageRepository;

    private final KakaoBrokerService kakaoBrokerService;

    public String sendKakaoMessageToBroker(PostSendKakaoMessageReq messageReq, long memberId){
        // Find Member
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
        log.info("member : " + member);

        // Find Contacts
        List<Contact> contacts = contactRepository.findByPhoneNumberIn(messageReq.getReceivers());
        if(contacts.contains(null) || contacts.isEmpty())
            throw new BaseException(NOT_EXIST_CONTACT_NUMBER);
        log.info("contacts : " + contacts);

        // Save Kakao Message
        KakaoMessage kakaoMessage = KakaoMessage.builder()
                .member(member)
                .sender(messageReq.getKakaoMessageDto().getFrom())
                .title(messageReq.getKakaoMessageDto().getTitle())
                .subTitle(messageReq.getKakaoMessageDto().getSubtitle())
                .content(messageReq.getKakaoMessageDto().getContent())
                .image(messageReq.getKakaoMessageDto().getImage())
                .description(messageReq.getKakaoMessageDto().getDescription())
                .buttonTitle(messageReq.getKakaoMessageDto().getButtonTitle())
                .buttonUrl(messageReq.getKakaoMessageDto().getButtonUrl())
                .buttonType(messageReq.getKakaoMessageDto().getButtonType())
                .build();
        kakaoMessageRepository.save(kakaoMessage);
        log.info("kakaoMessage : " + kakaoMessage);

        BrokerKakaoMessageDto brokerMessageDto = BrokerKakaoMessageDto.builder()
                .kakaoMessageDto(messageReq.getKakaoMessageDto())
                .kakaoMessage(kakaoMessage)
                .contacts(contacts)
                .member(member)
                .build();
        log.info("brokerMessageDto : " + brokerMessageDto);
        return kakaoBrokerService.sendKakaoMessage(brokerMessageDto);
    }

}
