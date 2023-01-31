package com.srt.message.listener;

import com.srt.message.config.type.MessageType;
import com.srt.message.service.dto.message.SMSMessageDto;
import com.srt.message.jwt.NoIntercept;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.*;

@RestController
@RequiredArgsConstructor
public class TestBroker {
    private final RabbitTemplate rabbitTemplate;

    private final RabbitAdmin rabbitAdmin;

    private final String EXCHANGE_NAME = "sms";

    private final int KT_RATE = 15;
    private final int SKT_RATE = 35;
    private final int LG_RATE = 50;

    @NoIntercept
    @GetMapping("/producer/test")
    public void sendSMSMessageToKTBroker(){
        // init
        declareBinding(KT_WORK_QUEUE_NAME, KT_WORK_ROUTING_KEY);
        declareBinding(SKT_WORK_QUEUE_NAME, SKT_WORK_ROUTING_KEY);
        declareBinding(LG_WORK_QUEUE_NAME, LG_WORK_ROUTING_KEY);

        sendToAllBroker(getTestMessageDto(), 1000);
    }

    public void sendToAllBroker(SMSMessageDto SMSMessageDto, int count){
        int value = (count / 100);
        int kt_count = value * KT_RATE;
        int skt_count = value * SKT_RATE;
        int lg_count = value * LG_RATE;

        int sum = kt_count + skt_count + lg_count;

        if(sum < count)
            kt_count += count - sum;

        // kt
        for(int i = 0; i < kt_count; i++){
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, KT_WORK_ROUTING_KEY, SMSMessageDto);
            System.out.println((i+1) + " 번째 메시지가 전송되었습니다 - " + KT_WORK_ROUTING_KEY);
        }
        // skt
        for(int i = 0; i < skt_count; i++){
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, SKT_WORK_ROUTING_KEY, SMSMessageDto);
            System.out.println((i+1) + " 번째 메시지가 전송되었습니다 - " + SKT_WORK_ROUTING_KEY);
        }
        // lg
        for(int i = 0; i < lg_count; i++){
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, LG_WORK_ROUTING_KEY, SMSMessageDto);
            System.out.println((i+1) + " 번째 메시지가 전송되었습니다 - " + LG_WORK_ROUTING_KEY);
        }
    }

    public SMSMessageDto getTestMessageDto(){
        return SMSMessageDto.builder()
                .messageType(MessageType.SMS)
                .to("010123441234")
                .from("01025291674")
                .subject("메시지 분배 테스트")
                .content("메시지 분배 발송 테스트 입니다")
                .build();
    }

    public void declareBinding(String queueName, String routingKey){
        Queue queue = new Queue(queueName, true);

        rabbitAdmin.declareQueue(queue);

        TopicExchange topicExchange = new TopicExchange(EXCHANGE_NAME);
        rabbitAdmin.declareExchange(topicExchange);

        Binding binding = BindingBuilder.bind(queue).to(topicExchange).with(routingKey);

        rabbitAdmin.declareBinding(binding);
    }

//    public void declareBinding(){
//        Queue queue1 = new Queue(KT_QUEUE_NAME + ".1", true);
//        Queue queue2 = new Queue(KT_QUEUE_NAME + ".2", true);
//
//        if(rabbitAdmin.getQueueInfo(KT_QUEUE_NAME + ".1") == null)
//            rabbitAdmin.declareQueue(queue1);
//        else if(rabbitAdmin.getQueueInfo(KT_QUEUE_NAME) + ".2" == null)
//            rabbitAdmin.declareQueue(queue2);
//
//        CustomExchange consistentExchange = new CustomExchange(KT_EXCHANGE_NAME, "x-consistent-hash", false, true);
//        rabbitAdmin.declareExchange(consistentExchange);
//
//        Binding binding1 = BindingBuilder.bind(queue1).to(consistentExchange).with("1").noargs();
//        Binding binding2 = BindingBuilder.bind(queue2).to(consistentExchange).with("2").noargs();
//
//        rabbitAdmin.declareBinding(binding1);
//        rabbitAdmin.declareBinding(binding2);
//    }
}
