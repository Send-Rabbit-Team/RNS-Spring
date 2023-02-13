package com.srt.message.service.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.type.MessageType;
import com.srt.message.domain.*;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.message.post.PostSendMessageReq;
import com.srt.message.repository.*;
import com.srt.message.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.*;
import static com.srt.message.config.status.BaseStatus.ACTIVE;

@Log4j2
@Service
@RequiredArgsConstructor
public class MessageService {
    private final ObjectMapper objectMapper;

    private final MemberRepository memberRepository;
    private final SenderNumberRepository senderNumberRepository;
    private final ContactRepository contactRepository;

    private final MessageRepository messageRepository;
    private final MessageImageRepository messageImageRepository;

    private final BrokerService brokerService;
    private final ReserveMessageService reserveMessageService;

    private final SchedulerService schedulerService;

    // 메시지 중계사에게 전송
    public String sendMessageToBroker(PostSendMessageReq messageReq, long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));

        List<Contact> contacts = contactRepository.findByPhoneNumberIn(messageReq.getReceivers());
        // 연락처 예외 처리
        if (contacts.contains(null) || contacts.isEmpty())
            throw new BaseException(NOT_EXIST_CONTACT_NUMBER);

        // 발신자 번호 예외 처리
        SenderNumber senderNumber = senderNumberRepository.findByPhoneNumberAndStatus(messageReq.getMessage().getFrom(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_SENDER_NUMBER));

        log.info("senderNumber - memberId {}", senderNumber.getMember().getId());
        log.info("member - memberId {}", member.getId());

        if (senderNumber.getMember().getId() != member.getId())
            throw new BaseException(NOT_MATCH_SENDER_NUMBER);

        Message message = Message.builder()
                .member(member)
                .senderNumber(senderNumber)
                .subject(messageReq.getMessage().getSubject())
                .content(messageReq.getMessage().getContent())
                .messageType(messageReq.getMessage().getMessageType())
                .build();

        messageRepository.save(message);

        // MMS 타입인 경우 이미지 저장
        if (message.getMessageType() == MessageType.MMS) {
            String[] images = messageReq.getMessage().getImages();
            List<MessageImage> messageImages = new ArrayList<>();
            for (int i = 0; i < messageImages.size(); i++) {
                MessageImage messageImage = MessageImage.builder()
                        .message(message)
                        .data(images[i])
                        .build();
                messageImages.add(messageImage);
            }
            messageImageRepository.saveAll(messageImages);
        }

        BrokerMessageDto brokerMessageDto = BrokerMessageDto.builder()
                .smsMessageDto(messageReq.getMessage())
                .message(message)
                .contacts(contacts)
                .member(member)
                .build();

        // 크론 표현식 있으면 예약 발송으로 이동
        if (messageReq.getMessage().getCronExpression() != null)
            return reserveMessageService.reserveSmsMessage(brokerMessageDto);

        return brokerService.sendSmsMessage(brokerMessageDto);
    }
}
