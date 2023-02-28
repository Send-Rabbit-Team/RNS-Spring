package com.srt.message.service.message;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.config.status.ReserveStatus;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Message;
import com.srt.message.domain.ReserveMessage;
import com.srt.message.domain.ReserveMessageContact;
import com.srt.message.dto.contact.get.GetContactAllRes;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.message.SMSMessageDto;
import com.srt.message.repository.MessageRepository;
import com.srt.message.repository.ReserveMessageContactRepository;
import com.srt.message.repository.ReserveMessageRepository;
import com.srt.message.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Transactional
@RequiredArgsConstructor
@Service
public class ReserveMessageService {
    private final MessageRepository messageRepository;
    private final ReserveMessageRepository reserveMessageRepository;
    private final ReserveMessageContactRepository reserveMessageContactRepository;

    private final SchedulerService schedulerService;


    // 메시지 예약발송
    public String reserveSmsMessage(BrokerMessageDto brokerMessageDto){
        SMSMessageDto messageDto = brokerMessageDto.getSmsMessageDto();

        ReserveMessage reserveMessage = ReserveMessage.builder()
                .message(brokerMessageDto.getMessage())
                .cronExpression(messageDto.getCronExpression())
                .cronText(messageDto.getCronText())
                .reserveStatus(ReserveStatus.PROCESSING)
                .build();

        reserveMessage = reserveMessageRepository.save(reserveMessage);

        // 예약 대상자 정보 추가
        for(Contact contact: brokerMessageDto.getContacts()){
            ReserveMessageContact reserveMessageContact = ReserveMessageContact.builder()
                    .reserveMessage(reserveMessage)
                    .contact(contact)
                    .build();
            reserveMessageContactRepository.save(reserveMessageContact);

            brokerMessageDto.getSmsMessageDto().setTo(contact.getPhoneNumber());
        }

        schedulerService.register(brokerMessageDto, reserveMessage.getId());

        return "예약성공";
    }

    // 예약 수신자 조회
    public GetContactAllRes getReserveMessageContacts(long messageId){
        ReserveMessage reserveMessage = reserveMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new BaseException(NOT_RESERVE_MESSAGE));

        List<Contact> contacts = reserveMessageContactRepository.findAllByReserveMessage(reserveMessage)
                .stream().map(ReserveMessageContact::getContact).collect(Collectors.toList());

        return GetContactAllRes.toDto(contacts);
    }

    // 예약 취소
    public String cancelReserveMessage(long messageId, long memberId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MESSAGE));

        if (message.getMember().getId() != memberId)
            throw new BaseException(NOT_MATCH_MEMBER);

        ReserveMessage reserveMessage = reserveMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new BaseException(NOT_RESERVE_MESSAGE));

        if(reserveMessage.getReserveStatus() == ReserveStatus.STOP)
            throw new BaseException(ALREADY_CANCEL_RESERVE);

        schedulerService.deleteMessageReserve(reserveMessage.getId());

        reserveMessage.changeReserveStatusStop();
        reserveMessageRepository.save(reserveMessage);

        return messageId + "번 메시지의 발송 예약이 취소되었습니다.";
    }
}
