package com.srt.message.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.Broker;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Message;
import com.srt.message.domain.MessageResult;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.dto.message_result.MessageResultDto;
import com.srt.message.repository.MessageResultRepository;
import com.srt.message.repository.cache.BrokerCacheRepository;
import com.srt.message.repository.cache.ContactCacheRepository;
import com.srt.message.repository.cache.MessageCacheRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Log4j2
@Service
@RequiredArgsConstructor
public class BrokerCacheService {
    private final ObjectMapper objectMapper;

    private final BrokerCacheRepository brokerCacheRepository;
    private final MessageCacheRepository messageCacheRepository;
    private final ContactCacheRepository contactCacheRepository;

    private final RedisHashRepository redisHashRepository;

    private final MessageResultRepository messageResultRepository;


    public void updateRMessageResult(final MessageResultDto messageResultDto, String brokerName) {
        String rMessageResultId = messageResultDto.getRMessageResultId();

        // Redis에서 상태 가져오기
        String statusKey = "message.status." + messageResultDto.getMessageId();

        // Redis에 해당 데이터가 없을 경우 종료
        if (!redisHashRepository.isExist(statusKey, rMessageResultId))
            return;

        String jsonRMessageResult = redisHashRepository.findById(statusKey, rMessageResultId);

        // 상태 업데이트 및 저장
        RMessageResult rMessageResult = convertToRMessageResult(jsonRMessageResult);

        // 재전송 여부인지 확인
        // TODO 추후에 알고리즘 작성해서 코드 간략화하기
        long retryCount = messageResultDto.getRetryCount();
        if (retryCount >= 1) {
            rMessageResult.changeMessageStatus(MessageStatus.RESEND);

            if (retryCount == 1) {
                rMessageResult.requeueDescription(brokerName);
            } else if (retryCount == 2) {
                rMessageResult.resendOneDescription(brokerName);
            } else if (retryCount == 3) {
                rMessageResult.resendTwoDescription(brokerName);
            }else{ // 실패했을 경우
                rMessageResult.resendTwoDescription(brokerName);
                rMessageResult.addDescription("중계사 오류");
                rMessageResult.changeMessageStatus(MessageStatus.FAIL);
            }

        }else{
            rMessageResult.changeMessageStatus(MessageStatus.SUCCESS);
        }

        redisHashRepository.update(statusKey, rMessageResultId, rMessageResult);
    }

    public void saveMessageResult(final MessageResultDto messageResultDto, String brokerName) {
        Message message = messageCacheRepository.findMessageById(messageResultDto.getMessageId());
        Contact contact = contactCacheRepository.findContactById(messageResultDto.getContactId());
        Broker broker = brokerCacheRepository.findBrokerById(messageResultDto.getBrokerId());

        MessageResult messageResult = MessageResult.builder()
                .message(message)
                .contact(contact)
                .broker(broker)
                .messageStatus(messageResultDto.getMessageStatus())
                .build();

        // 재전송 여부인지 확인
        // TODO 추후에 알고리즘 작성해서 코드 간략화하기
        long retryCount = messageResultDto.getRetryCount();

        if (retryCount >= 1) {
            messageResult.changeMessageStatus(MessageStatus.RESEND);

            if (retryCount == 1) {
                messageResult.requeueDescription(brokerName);
            } else if (retryCount == 2) {
                messageResult.resendOneDescription(brokerName);
            } else if (retryCount == 3){
                messageResult.resendTwoDescription(brokerName);
            }else{ // 실패
                messageResult.resendTwoDescription(brokerName);
                messageResult.addDescription("중계사 오류");
                messageResult.changeMessageStatus(MessageStatus.FAIL);
            }
        }

        messageResultRepository.save(messageResult);
        log.info("[" + messageResult.getMessageStatus() + "] " + "[" + brokerName + "]" + " MessageResult 객체가 저장되었습니다. id : {}", messageResult.getId());
    }

    public RMessageResult convertToRMessageResult(String json) {
        RMessageResult rMessageResult = null;
        try {
            rMessageResult = objectMapper.readValue(json, RMessageResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return rMessageResult;
    }
}
