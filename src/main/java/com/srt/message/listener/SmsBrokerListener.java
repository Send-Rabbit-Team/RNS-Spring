package com.srt.message.listener;

import com.rabbitmq.client.Channel;
import com.srt.message.dlx.DlxProcessingErrorHandler;
import com.srt.message.dto.message_result.MessageResultDto;
import com.srt.message.service.message.BrokerCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class SmsBrokerListener {
    private final BrokerCacheService brokerCacheService;
    private final DlxProcessingErrorHandler dlxProcessingErrorHandler;

    private final String KT_BROKER_NAME = "kt";
    private final String SKT_BROKER_NAME = "skt";
    private final String LG_BROKER_NAME = "lg";

    /**
     * Receive Consumer (Success)
     */
    // KT
    @RabbitListener(queues = "q.sms.kt.receive", concurrency = "3", containerFactory = "prefetchContainerFactory")
    public void receiveKTMessage(final MessageResultDto messageResultDto) {
        brokerCacheService.updateRMessageResult(messageResultDto, KT_BROKER_NAME);
        brokerCacheService.saveMessageResult(messageResultDto, KT_BROKER_NAME);

    }

    // SKT
    @RabbitListener(queues = "q.sms.skt.receive", concurrency = "3", containerFactory = "prefetchContainerFactory")
    public void receiveSKTMessage(final MessageResultDto messageResultDto) {
        brokerCacheService.updateRMessageResult(messageResultDto, SKT_BROKER_NAME);
        brokerCacheService.saveMessageResult(messageResultDto, SKT_BROKER_NAME);
    }

    // LG
    @RabbitListener(queues = "q.sms.lg.receive", concurrency = "3", containerFactory = "prefetchContainerFactory")
    public void receiveLGMessage(final MessageResultDto messageResultDto) {
        brokerCacheService.updateRMessageResult(messageResultDto, LG_BROKER_NAME);
        brokerCacheService.saveMessageResult(messageResultDto, LG_BROKER_NAME);
    }

    /**
     * Wait Consumer
     */
    // KT
    @RabbitListener(queues = "q.sms.kt.wait", concurrency = "3", ackMode = "MANUAL")
    public void receiveSenderKTMessage(org.springframework.amqp.core.Message message, Channel channel){
        dlxProcessingErrorHandler.handleErrorProcessingMessage(message, channel, "kt");
    }

    // SKT
    @RabbitListener(queues = "q.sms.skt.wait", concurrency = "3", ackMode = "MANUAL")
    public void receiveSenderSKTMessage(org.springframework.amqp.core.Message message, Channel channel){
        dlxProcessingErrorHandler.handleErrorProcessingMessage(message, channel, "skt");
    }

    // LG
    @RabbitListener(queues = "q.sms.lg.wait", concurrency = "3", ackMode = "MANUAL")
    public void receiveSenderLGMessage(org.springframework.amqp.core.Message message, Channel channel){
        dlxProcessingErrorHandler.handleErrorProcessingMessage(message, channel, "lg");
    }
}
