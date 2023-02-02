package com.srt.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.message.get.GetMessageRes;
import com.srt.message.dto.message.post.PostSendMessageReq;
import com.srt.message.dto.message_result.get.GetMessageResultRes;
import com.srt.message.repository.*;
import com.srt.message.repository.cache.BrokerCacheRepository;
import com.srt.message.repository.cache.ContactCacheRepository;
import com.srt.message.repository.cache.MessageCacheRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import com.srt.message.service.rabbit.BrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private final MessageResultRepository messageResultRepository;

    private final RedisHashRepository redisHashRepository;

    private final MessageCacheRepository messageCacheRepository;
    private final BrokerCacheRepository brokerCacheRepository;
    private final ContactCacheRepository contactCacheRepository;

    private final BrokerService brokerService;

    // 메시지 중계사에게 전송
    public String sendMessageToBroker(PostSendMessageReq messageReq, long memberId){
        Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));

        List<Contact> contacts = contactRepository.findByPhoneNumberIn(messageReq.getReceivers());
        // 연락처 예외 처리
        if(contacts.contains(null) || contacts.isEmpty())
            throw new BaseException(NOT_EXIST_CONTACT_NUMBER);

        // 발신자 번호 예외 처리
        SenderNumber senderNumber = senderNumberRepository.findByPhoneNumberAndStatus(messageReq.getMessage().getFrom(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_SENDER_NUMBER));

        log.info("senderNumber - memberId {}", senderNumber.getMember().getId());
        log.info("member - memberId {}", member.getId());

        if(senderNumber.getMember().getId() != member.getId())
            throw new BaseException(NOT_MATCH_SENDER_NUMBER);

        Message message = Message.builder()
                .member(member)
                .senderNumber(senderNumber)
                .content(messageReq.getMessage().getContent())
                .messageType(messageReq.getMessage().getMessageType())
                .build();

        messageRepository.save(message);

        BrokerMessageDto brokerMessageDto = BrokerMessageDto.builder()
                .smsMessageDto(messageReq.getMessage())
                .message(message)
                .contacts(contacts)
                .member(member)
                .build();

        // 크론 표현식 있으면 예약 발송으로 이동
        if(messageReq.getMessage().getCronExpression() != null)
            return brokerService.reserveSmsMessage(brokerMessageDto);

        return brokerService.sendSmsMessage(brokerMessageDto);
    }

    // 발송한 메시지 페이징 조회
    public PageResult<GetMessageRes> getMessagesByPaging(long memberId, int page){
        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("id").descending());
        Page<GetMessageRes> messagePage = messageRepository.findAllMessage(memberId, pageRequest)
                .map(m -> GetMessageRes.toDto(m));

        return new PageResult<>(messagePage);
    }

    // 메시지 처리 결과 모두 조회
    public List<GetMessageResultRes> getAllMessageResult(long messageId) throws JsonProcessingException {
        List<GetMessageResultRes> messageResultResList = new ArrayList<>();

        // 레디스에 상태 값 저장되어 있는지 확인
        String statusKey = "message.status." + messageId;
        Map<String,String> statusMap = redisHashRepository.findAll(statusKey);
        if(!statusMap.isEmpty()){ // 상태 정보가 들어있을 경우 REDIS로 조회

            for(Map.Entry<String, String> entry: statusMap.entrySet()){
                String rMessageResultJson = entry.getValue();
                RMessageResult rMessageResult = objectMapper.readValue(rMessageResultJson, RMessageResult.class);

                messageResultResList.add(getMessageResultRes(rMessageResult));
            }
        }else{ // RDBMS에서 조회
            List<MessageResult> messageResults = messageResultRepository.findAllByMessageId(messageId);
            messageResults.stream()
                    .map(m -> getMessageResultRes(m)).forEach(r -> messageResultResList.add(r));
        }

        return messageResultResList;
    }

    // 편의 메서드
    public GetMessageResultRes getMessageResultRes(RMessageResult rMessageResult){
        Message message = messageCacheRepository.findMessageById(rMessageResult.getMessageId());
        Contact contact = contactCacheRepository.findContactById(rMessageResult.getContactId());
        Broker broker = brokerCacheRepository.findBrokerById(rMessageResult.getBrokerId());

        return GetMessageResultRes.builder()
                .contactPhoneNumber(contact.getPhoneNumber())
                .brokerName(broker.getName())
                .messageStatus(rMessageResult.getMessageStatus())
                .build();
    }

    public GetMessageResultRes getMessageResultRes(MessageResult messageResult){
        return GetMessageResultRes.builder()
                .contactPhoneNumber(messageResult.getContact().getPhoneNumber())
                .brokerName(messageResult.getBroker().getName())
                .messageStatus(messageResult.getMessageStatus())
                .build();
    }
}
