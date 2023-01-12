package com.srt.message.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.srt.message.utils.rabbit.RabbitSMSUtil.*;

@Configuration
public class RabbitConfig {
    @Bean
    public RabbitAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    // SMS QUEUE
    @Bean
    Queue smsKTQueue(){
        return new Queue(KT_QUEUE_NAME, true);
    }

    @Bean
    Queue smsSKTQueue(){
        return new Queue(SKT_QUEUE_NAME, true);
    }

    @Bean
    Queue smsLGQueue(){
        return new Queue(LG_QUEUE_NAME, true);
    }
}
