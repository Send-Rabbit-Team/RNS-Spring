package com.srt.message.service.rabbitmq;

import com.srt.message.dto.message.post.PostSendMessageReq;
import com.srt.message.repository.MessageRepository;
import com.srt.message.repository.RepeatRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrokerService {
    private final MessageRepository messageRepository;
    private final RepeatRuleRepository repository;

    public void sendMessageToBroker(PostSendMessageReq messageReq, long memberId){

    }
}
