package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Member;
import com.srt.message.domain.Message;
import com.srt.message.domain.SenderNumber;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.message.post.PostSendMessageReq;
import com.srt.message.repository.*;
import com.srt.message.service.rabbit.BrokerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.*;
import static com.srt.message.config.status.BaseStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MemberRepository memberRepository;
    private final SenderNumberRepository senderNumberRepository;
    private final ContactRepository contactRepository;

    private final MessageRepository messageRepository;

    private final BrokerService brokerService;


    public void sendMessageToBroker(PostSendMessageReq messageReq, long memberId){
        Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));

        List<Contact> contacts = contactRepository.findByPhoneNumberIn(messageReq.getReceivers());
        // 연락처 예외 처리
        if(contacts.contains(null) || contacts.isEmpty())
            throw new BaseException(NOT_EXIST_CONTACT_NUMBER);

        // 발신자 번호 예외 처리
        SenderNumber senderNumber = senderNumberRepository.findByPhoneNumberAndStatus(messageReq.getSenderNumber(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_SENDER_NUMBER));
        if(!senderNumber.getMember().equals(member))
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

        brokerService.sendSmsMessage(brokerMessageDto);
    }
}
