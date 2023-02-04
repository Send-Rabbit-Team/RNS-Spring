package com.srt.message.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RKakaoMessageResult;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.dto.message_result.KakaoMessageResultDto;
import com.srt.message.dto.message_result.MessageResultDto;
import com.srt.message.repository.KakaoMessageResultRepository;
import com.srt.message.repository.MessageResultRepository;
import com.srt.message.repository.cache.BrokerCacheRepository;
import com.srt.message.repository.cache.ContactCacheRepository;
import com.srt.message.repository.cache.MessageCacheRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoBrokerListener {
    private final KakaoMessageResultRepository kakaoMessageResultRepository;
    private final RedisHashRepository redisHashRepository;
    private final ObjectMapper objectMapper;

    private final MessageCacheRepository messageCacheRepository;
    private final ContactCacheRepository contactCacheRepository;
    private final BrokerCacheRepository brokerCacheRepository;

    private final String CNS_BROKER_NAME = "cns";
    private final String KE_BROKER_NAME = "ke";



    // CNS RESPONSE
    @RabbitListener(queues = "q.kakao.cns.receive", concurrency = "3", containerFactory = "prefetchContainerFactory")
    public void receiveKTMessage(final KakaoMessageResultDto kakaoMessageResultDto) {
        updateRMessageResult(kakaoMessageResultDto, CNS_BROKER_NAME);
        saveMessageResult(kakaoMessageResultDto, CNS_BROKER_NAME);
    }

    // KE RESPONSE
    @RabbitListener(queues = "q.kakao.ke.receive", concurrency = "3", containerFactory = "prefetchContainerFactory")
    public void receiveSKTMessage(final KakaoMessageResultDto kakaoMessageResultDto) {
        updateRMessageResult(kakaoMessageResultDto, KE_BROKER_NAME);
        saveMessageResult(kakaoMessageResultDto, KE_BROKER_NAME);
    }

    /**
     * Dead Consumer
     */
    // CNS
    @RabbitListener(queues = "q.kakao.cns.dead")
    public void receiveDeadKTMessage(final KakaoMessageResultDto kakaoMessageResultDto) {
        kakaoMessageResultDto.setMessageStatus(MessageStatus.FAIL);
        updateRMessageResult(kakaoMessageResultDto, CNS_BROKER_NAME);
        saveMessageResult(kakaoMessageResultDto, CNS_BROKER_NAME);

        log.warn(CNS_BROKER_NAME + " broker got dead letter - {}", kakaoMessageResultDto);
    }

    // KE
    @RabbitListener(queues = "q.kakao.ke.dead")
    public void receiveDeadSKTMessage(final KakaoMessageResultDto kakaoMessageResultDto) {
        kakaoMessageResultDto.setMessageStatus(MessageStatus.FAIL);
        updateRMessageResult(kakaoMessageResultDto, KE_BROKER_NAME);
        saveMessageResult(kakaoMessageResultDto, KE_BROKER_NAME);

        log.warn(KE_BROKER_NAME + " broker got dead letter - {}", kakaoMessageResultDto);
    }


    public void updateRMessageResult(final KakaoMessageResultDto kakaoMessageResultDto, String brokerName) {
        Broker broker = brokerCacheRepository.findBrokerById(kakaoMessageResultDto.getBrokerId());
        String rKakaoMessageResultId = kakaoMessageResultDto.getRMessageResultId();

        // Redis에서 상태 가져오기
        String statusKey = "message.status." + kakaoMessageResultDto.getMessageId();

        // Redis에 해당 데이터가 없을 경우 종료
        if (!redisHashRepository.isExist(statusKey, rKakaoMessageResultId))
            return;

        String jsonRKakaoMessageResult = redisHashRepository.findById(statusKey, rKakaoMessageResultId);

        // 상태 업데이트 및 저장
        RKakaoMessageResult rKakaoMessageResult = convertToRMessageResult(jsonRKakaoMessageResult);
        rKakaoMessageResult.changeMessageStatus(MessageStatus.SUCCESS);

        redisHashRepository.update(statusKey, rKakaoMessageResultId, rKakaoMessageResult);
    }

    public void saveMessageResult(final KakaoMessageResultDto kakaoMessageResultDto, String brokerName) {
        KakaoMessage kakaoMessage = messageCacheRepository.findKakaoMessageById(kakaoMessageResultDto.getMessageId());
        Contact contact = contactCacheRepository.findContactById(kakaoMessageResultDto.getContactId());
        KakaoBroker kakaoBroker = brokerCacheRepository.findKakaoBrokerById(kakaoMessageResultDto.getBrokerId());

        KakaoMessageResult kakaoMessageResult = KakaoMessageResult.builder()
                .kakaoMessage(kakaoMessage)
                .contact(contact)
                .kakaoBroker(kakaoBroker)
                .messageStatus(kakaoMessageResultDto.getMessageStatus())
                .build();

        kakaoMessageResultRepository.save(kakaoMessageResult);

        log.info("KakaoMessageResult 객체가 저장되었습니다. id : {}", kakaoMessageResult.getId());
    }

    public RKakaoMessageResult convertToRMessageResult(String json) {
        RKakaoMessageResult rKakaoMessageResult = null;
        try {
            rKakaoMessageResult = objectMapper.readValue(json, RKakaoMessageResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return rKakaoMessageResult;
    }
}
