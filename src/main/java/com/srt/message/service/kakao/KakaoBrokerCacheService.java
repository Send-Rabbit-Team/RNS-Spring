package com.srt.message.service.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RKakaoMessageResult;
import com.srt.message.dto.message_result.KakaoMessageResultDto;
import com.srt.message.repository.KakaoMessageResultRepository;
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
public class KakaoBrokerCacheService {
    private final ObjectMapper objectMapper;

    private final BrokerCacheRepository brokerCacheRepository;
    private final MessageCacheRepository messageCacheRepository;
    private final ContactCacheRepository contactCacheRepository;

    private final RedisHashRepository redisHashRepository;

    private final KakaoMessageResultRepository kakaoMessageResultRepository;

    public void updateRMessageResult(final KakaoMessageResultDto kakaoMessageResultDto, String brokerName) {
        String rKakaoMessageResultId = kakaoMessageResultDto.getRMessageResultId();

        // Redis에서 상태 가져오기
        String statusKey = "message.status." + kakaoMessageResultDto.getMessageId();

        // Redis에 해당 데이터가 없을 경우 종료
        if (!redisHashRepository.isExist(statusKey, rKakaoMessageResultId))
            return;

        String jsonRKakaoMessageResult = redisHashRepository.findByRMessageResultId(statusKey, rKakaoMessageResultId);

        // 상태 업데이트 및 저장
        RKakaoMessageResult rKakaoMessageResult = convertToRMessageResult(jsonRKakaoMessageResult);

        // 재전송 여부인지 확인
        // TODO 추후에 알고리즘 작성해서 코드 간략화하기
        long retryCount = kakaoMessageResultDto.getRetryCount();
        if (retryCount >= 1) {
            rKakaoMessageResult.changeMessageStatus(MessageStatus.RESEND);

            if (retryCount == 1) {
                rKakaoMessageResult.requeueDescription(brokerName);
            } else if (retryCount == 2) {
                rKakaoMessageResult.resendOneDescription(brokerName);
            }else{ // 실패 일 경우
                rKakaoMessageResult.changeMessageStatus(MessageStatus.FAIL);
            }
        }else{
            rKakaoMessageResult.changeMessageStatus(MessageStatus.SUCCESS);
        }

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

        // 재전송 여부인지 확인
        // TODO 추후에 알고리즘 작성해서 코드 간략화하기
        long retryCount = kakaoMessageResultDto.getRetryCount();
        if (retryCount >= 1) {
            kakaoMessageResult.changeMessageStatus(MessageStatus.RESEND);

            if (retryCount == 1) {
                kakaoMessageResult.requeueDescription(brokerName);
            } else if (retryCount == 2) {
                kakaoMessageResult.resendOneDescription(brokerName);
            }else{ // 실패일 경우
                kakaoMessageResult.changeMessageStatus(MessageStatus.FAIL);
            }
        }

        kakaoMessageResultRepository.save(kakaoMessageResult);

        log.info("[" + kakaoMessageResult.getMessageStatus() + "] " + "[" + brokerName + "]" +
                "KakaoMessageResult 객체가 저장되었습니다. id : {}", kakaoMessageResult.getId());
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
