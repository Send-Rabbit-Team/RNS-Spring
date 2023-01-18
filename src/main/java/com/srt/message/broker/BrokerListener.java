package com.srt.message.broker;

import com.srt.message.domain.Broker;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Message;
import com.srt.message.domain.MessageResult;
import com.srt.message.dto.message_result.MessageResultDto;
import com.srt.message.repository.MessageResultRepository;
import com.srt.message.repository.cache.BrokerCacheRepository;
import com.srt.message.repository.cache.ContactCacheRepository;
import com.srt.message.repository.cache.MessageCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class BrokerListener {
    private final MessageResultRepository messageResultRepository;

    private final MessageCacheRepository messageCacheRepository;
    private final ContactCacheRepository contactCacheRepository;
    private final BrokerCacheRepository brokerCacheRepository;

    // KT RESPONSE
    @RabbitListener(queues = "q.sms.kt.receive", concurrency = "3")
    public void receiveSmsKTMessage(final MessageResultDto messageResultDto){
        saveMessageResult(messageResultDto);
    }

    // SKT RESPONSE
    @RabbitListener(queues = "q.sms.skt.receive", concurrency = "3")
    public void receiveSmsSKTMessage(final MessageResultDto messageResultDto){
        saveMessageResult(messageResultDto);
    }

    // LG RESPONSE
    @RabbitListener(queues = "q.sms.lg.receive", concurrency = "3")
    public void receiveSmsLGMessage(final MessageResultDto messageResultDto){
        saveMessageResult(messageResultDto);
    }

    @Transactional
    public void saveMessageResult(final MessageResultDto messageResultDto){
        Message message = messageCacheRepository.findMessageById(messageResultDto.getMessageId());
        Contact contact = contactCacheRepository.findContactById(messageResultDto.getContactId());
        Broker broker = brokerCacheRepository.findBrokerById(messageResultDto.getBrokerId());

        MessageResult messageResult = MessageResult.builder()
                .message(message)
                .contact(contact)
                .broker(broker)
                .build();

        messageResultRepository.save(messageResult);

        log.info("MessageResult 객체가 저장되었습니다. id : {}", messageResult.getId());
    }
}
