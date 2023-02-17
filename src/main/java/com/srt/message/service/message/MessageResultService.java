package com.srt.message.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponseStatus;
import com.srt.message.config.type.MessageType;
import com.srt.message.config.type.MsgSearchType;
import com.srt.message.domain.Broker;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Message;
import com.srt.message.domain.MessageResult;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.dto.message.get.GetMessageRes;
import com.srt.message.dto.message_result.get.GetListMessageResultRes;
import com.srt.message.dto.message_result.get.GetMessageResultRes;
import com.srt.message.repository.*;
import com.srt.message.repository.cache.BrokerCacheRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Transactional(readOnly = false)
@RequiredArgsConstructor
@Service
public class MessageResultService {
    private final ObjectMapper objectMapper;

    private final MessageRepository messageRepository;
    private final MessageResultRepository messageResultRepository;

    private final ContactRepository contactRepository;

    private final BrokerCacheRepository brokerCacheRepository;

    private final RedisHashRepository redisHashRepository;


    // 발송한 메시지 페이징 조회
    public PageResult<GetMessageRes> getAllMessages(long memberId, int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        Page<GetMessageRes> messagePage = messageRepository.findAllMessage(memberId, pageRequest)
                .map(GetMessageRes::toDto);

        return new PageResult<>(messagePage);
    }

    // 메시지 처리 결과 모두 조회
    public GetListMessageResultRes getMessageResultsById(long messageId) throws JsonProcessingException {
        GetListMessageResultRes response = new GetListMessageResultRes();
        List<GetMessageResultRes> messageResultResList;

        Message message = messageRepository.findById(messageId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_MESSAGE));

        // 레디스에 상태 값 저장되어 있는지 확인
        String statusKey = "message.status." + messageId;
        Map<String, String> statusMap = redisHashRepository.findAll(statusKey);
        if (!statusMap.isEmpty()) { // 상태 정보가 들어있을 경우 REDIS로 조회
            List<RMessageResult> rMessageResultList = new ArrayList<>();

            for (Map.Entry<String, String> entry : statusMap.entrySet()) {
                String rMessageResultJson = entry.getValue();
                RMessageResult rMessageResult = objectMapper.readValue(rMessageResultJson, RMessageResult.class);

                response.addBrokerCount(rMessageResult.getBrokerId());
                response.addStatusCount(rMessageResult.getMessageStatus());
                response.addTotalPoint(message.getMessageType(), rMessageResult.getMessageStatus());

                rMessageResultList.add(rMessageResult);
            }

            List<Long> contactIdList = rMessageResultList.stream().map(RMessageResult::getContactId).collect(Collectors.toList());
            List<Contact> contactList = contactRepository.findAllInContactIdList(contactIdList);
            HashMap<Long, Contact> contactMap = new HashMap<>(contactList.stream().collect(Collectors.toMap(Contact::getId, c -> c)));

            messageResultResList = rMessageResultList.stream().parallel().
                    map(r -> getMessageResultRes(r, contactMap.get(r.getContactId()))).collect(Collectors.toList());

            // 수신 차단 메시지 따로 추가
            List<MessageResult> blockMessages = messageResultRepository.findAllByMessageAndDescriptionLike(message, "%수신 차단%");
            for(MessageResult m: blockMessages)
                messageResultResList.add(getMessageResultRes(m));

        } else { // RDBMS에서 조회
            List<MessageResult> messageResults = messageResultRepository.findAllByMessageIdOrderByIdDesc(messageId);
            messageResultResList = messageResults.stream().parallel()
                    .map(this::getMessageResultRes).collect(Collectors.toList());

            messageResultResList.stream().forEach(r -> {
                response.addBrokerCount(r.getBrokerId());
                response.addStatusCount(r.getMessageStatus());
                response.addTotalPoint(message.getMessageType(), r.getMessageStatus());
            });
        }

        response.setMessageResultRes(messageResultResList);
        response.setTotalCount(messageResultResList.size());

        return response;
    }

    // 메시지 유형별 필터 조회
    public PageResult<GetMessageRes> getMessagesByType(String type, long memberId, int page) {
        MessageType messageType = MessageType.valueOf(type);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("id").descending());

        return new PageResult<>(messageRepository.findMessagesByMessageType(messageType, memberId, pageRequest)
                .map(GetMessageRes::toDto));
    }

    // 검색 조회 (메모, 수신, 발신 번호, 메시지 내용)
    public PageResult<GetMessageRes> getMessageBySearching(String searchType, String keyword, long memberId, int page) {
        MsgSearchType msgSearchType = MsgSearchType.valueOf(searchType);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("id").descending());

        Page<GetMessageRes> messageResPage = null;
        if (msgSearchType == MsgSearchType.RECEIVER) { // 수신자 번호 검색했을 때
            messageResPage = messageRepository.findByReceiveNumber(keyword, memberId, pageRequest)
                    .map(GetMessageRes::toDto);

        } else if (msgSearchType == MsgSearchType.SENDER) { // 발신자 번호 검색했을 때
            messageResPage = messageRepository.findBySenderNumber(keyword, memberId, pageRequest)
                    .map(GetMessageRes::toDto);

        } else if (msgSearchType == MsgSearchType.MEMO) { // 메모 키워드로 검색했을 때
            messageResPage = messageRepository.findByMemo(keyword, memberId, pageRequest)
                    .map(GetMessageRes::toDto);

        } else if (msgSearchType == MsgSearchType.MESSAGE) { // 메시지 내용으로 검색했을 때
            messageResPage = messageRepository.findByMessageContent(keyword, memberId, pageRequest)
                    .map(GetMessageRes::toDto);
        }

        return new PageResult<>(messageResPage);
    }

    /**
     * 편의 메서드
     */
    public GetMessageResultRes getMessageResultRes(MessageResult messageResult) {
        GetMessageResultRes getMessageResultRes = GetMessageResultRes.builder()
                .contactPhoneNumber(messageResult.getContact().getPhoneNumber())
                .memo(messageResult.getContact().getMemo())
                .description(messageResult.getDescription())
                .messageStatus(messageResult.getMessageStatus())
                .createdAt(messageResult.getCreatedAt() == null ? null : messageResult.getCreatedAt().toString())
                .build();

        Broker broker = messageResult.getBroker();

        if(broker != null){
            getMessageResultRes.setBrokerId(broker.getId());
            getMessageResultRes.setBrokerName(broker.getName());
        }

        if (messageResult.getContact().getContactGroup() != null) {
            getMessageResultRes.setContactGroup(messageResult.getContact().getContactGroup().getName());
        }
        return getMessageResultRes;
    }

    public GetMessageResultRes getMessageResultRes(RMessageResult rMessageResult, Contact contact) {
        Broker broker = brokerCacheRepository.findBrokerById(rMessageResult.getBrokerId());

        GetMessageResultRes getMessageResultRes = GetMessageResultRes.builder()
                .contactPhoneNumber(contact.getPhoneNumber())
                .memo(contact.getMemo())
                .brokerId(broker.getId())
                .brokerName(broker.getName())
                .description(rMessageResult.getDescription())
                .messageStatus(rMessageResult.getMessageStatus())
                .createdAt(LocalDateTime.now().toString())
                .build();
        if (contact.getContactGroup() != null) {
            getMessageResultRes.setContactGroup(contact.getContactGroup().getName());
        }
        return getMessageResultRes;
    }
}
