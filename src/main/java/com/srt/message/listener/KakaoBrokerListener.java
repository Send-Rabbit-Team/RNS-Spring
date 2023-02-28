package com.srt.message.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.dlx.DlxProcessingErrorHandler;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RKakaoMessageResult;
import com.srt.message.dto.message_result.KakaoMessageResultDto;
import com.srt.message.repository.KakaoMessageResultRepository;
import com.srt.message.repository.cache.BrokerCacheRepository;
import com.srt.message.repository.cache.ContactCacheRepository;
import com.srt.message.repository.cache.MessageCacheRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import com.srt.message.service.kakao.KakaoBrokerCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoBrokerListener {
    private final KakaoBrokerCacheService kakaoBrokerCacheService;

    private final DlxProcessingErrorHandler dlxProcessingErrorHandler;

    private final String CNS_BROKER_NAME = "cns";
    private final String KE_BROKER_NAME = "ke";



    // CNS RESPONSE
    @RabbitListener(queues = "q.kakao.cns.receive", concurrency = "3", containerFactory = "prefetchContainerFactory")
    public void receiveCNSMessage(final KakaoMessageResultDto kakaoMessageResultDto) {
        kakaoBrokerCacheService.updateRMessageResult(kakaoMessageResultDto, CNS_BROKER_NAME);
        kakaoBrokerCacheService.saveMessageResult(kakaoMessageResultDto, CNS_BROKER_NAME);
    }

    // KE RESPONSE
    @RabbitListener(queues = "q.kakao.ke.receive", concurrency = "3", containerFactory = "prefetchContainerFactory")
    public void receiveKEMessage(final KakaoMessageResultDto kakaoMessageResultDto) {
        kakaoBrokerCacheService.updateRMessageResult(kakaoMessageResultDto, KE_BROKER_NAME);
        kakaoBrokerCacheService.saveMessageResult(kakaoMessageResultDto, KE_BROKER_NAME);
    }

    // CNS WAIT
    @RabbitListener(queues = "q.kakao.cns.wait", concurrency = "3", ackMode = "MANUAL")
    public void receiveSenderKTMessage(org.springframework.amqp.core.Message message, Channel channel){
        dlxProcessingErrorHandler.handleErrorProcessingKakaoMessage(message, channel, "cns");
    }

    // KE WAIT
    @RabbitListener(queues = "q.kakao.ke.wait", concurrency = "3", ackMode = "MANUAL")
    public void receiveSenderSKTMessage(org.springframework.amqp.core.Message message, Channel channel){
        dlxProcessingErrorHandler.handleErrorProcessingKakaoMessage(message, channel, "ke");
    }
}
