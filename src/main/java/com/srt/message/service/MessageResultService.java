package com.srt.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.type.MessageType;
import com.srt.message.config.type.MsgSearchType;
import com.srt.message.domain.Broker;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Message;
import com.srt.message.domain.MessageResult;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.dto.message.get.GetMessageRes;
import com.srt.message.dto.message_result.get.GetMessageResultRes;
import com.srt.message.repository.*;
import com.srt.message.repository.cache.BrokerCacheRepository;
import com.srt.message.repository.cache.ContactCacheRepository;
import com.srt.message.repository.cache.MessageCacheRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = false)
@RequiredArgsConstructor
@Service
public class MessageResultService {
    private final ObjectMapper objectMapper;
    private final MessageRepository messageRepository;
    private final MessageResultRepository messageResultRepository;

    private final MessageCacheRepository messageCacheRepository;
    private final BrokerCacheRepository brokerCacheRepository;
    private final ContactCacheRepository contactCacheRepository;

    private final RedisHashRepository redisHashRepository;


    // 발송한 메시지 페이징 조회
    public PageResult<GetMessageRes> getAllMessages(long memberId, int page){
        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("id").descending());
        Page<GetMessageRes> messagePage = messageRepository.findAllMessage(memberId, pageRequest)
                .map(GetMessageRes::toDto);

        return new PageResult<>(messagePage);
    }

    // 메시지 처리 결과 모두 조회
    public List<GetMessageResultRes> getMessageResultsById(long messageId) throws JsonProcessingException {
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
                    .map(this::getMessageResultRes).forEach(r -> messageResultResList.add(r));
        }

        return messageResultResList;
    }

    // 메시지 유형별 필터 조회
    public List<GetMessageRes> getMessagesByType(String type, long memberId, int page){
        MessageType messageType = MessageType.valueOf(type);
        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("id").descending());

        return messageRepository.findMessagesByMessageType(messageType, memberId, pageRequest)
                .map(GetMessageRes::toDto).toList();
    }

    // 예약된 메시지 필터 조회
    public List<GetMessageRes> getReserveMessages(long memberId, int page){
        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("id").descending());

        return messageRepository.findReserveMessage(memberId, pageRequest)
                .map(GetMessageRes::toDto).toList();
    }

    // 검색 조회 (메모, 수신, 발신 번호)
    public List<GetMessageRes> getMessageBySearching(String searchType, String keyword, long memberId, int page){
        MsgSearchType msgSearchType = MsgSearchType.valueOf(searchType);
        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("id").descending());

        List<GetMessageRes> getMessageResList = null;
        if(msgSearchType == MsgSearchType.RECEIVER){ // 수신자 번호 검색했을 때
            getMessageResList = messageRepository.findByReceiveNumber(keyword, memberId, pageRequest)
                   .map(GetMessageRes::toDto).toList();

        }else if(msgSearchType == MsgSearchType.SENDER){ // 발신자 번호 검색했을 때
            getMessageResList = messageRepository.findBySenderNumber(keyword, memberId, pageRequest)
                    .map(GetMessageRes::toDto).toList();

        }else if(msgSearchType == MsgSearchType.MEMO) { // 메모 키워드로 검색했을 때
            getMessageResList = messageRepository.findByMemo(keyword, memberId, pageRequest)
                    .map(GetMessageRes::toDto).toList();

        }else if(msgSearchType == MsgSearchType.MESSAGE) { // 메시지 내용으로 검색했을 때
            getMessageResList = messageRepository.findByMessageContent(keyword, memberId, pageRequest)
                    .map(GetMessageRes::toDto).toList();
        }

        return getMessageResList;
    }

    /**
     * 편의 메서드
     */
    public GetMessageResultRes getMessageResultRes(MessageResult messageResult){
        return GetMessageResultRes.builder()
                .contactPhoneNumber(messageResult.getContact().getPhoneNumber())
                .brokerName(messageResult.getBroker().getName())
                .messageStatus(messageResult.getMessageStatus())
                .build();
    }
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
}
