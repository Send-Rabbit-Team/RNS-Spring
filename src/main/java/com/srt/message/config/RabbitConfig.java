package com.srt.message.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.*;

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
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dlx-sms");
        args.put("x-dead-letter-routing-key", SKT_ROUTING_KEY);
        return new Queue(KT_QUEUE_NAME, true);
    }

    @Bean
    Queue smsSKTQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dlx-sms");
        args.put("x-dead-letter-routing-key", LG_ROUTING_KEY);
        return new Queue(SKT_QUEUE_NAME, true);
    }

    @Bean
    Queue smsLGQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dlx-sms");
        args.put("x-dead-letter-routing-key", KT_ROUTING_KEY);
        return new Queue(LG_QUEUE_NAME, true);
    }

    // SMS Exchange
    @Bean
    public DirectExchange smsExchange(){
        return new DirectExchange("direct.sms");
    }

    // DLX Exchange
    @Bean
    public DirectExchange dlxSMSExchange(){
        return new DirectExchange("dlx.sms");
    }


    // SMS Binding
    @Bean
    public Binding bindingSmsKT(DirectExchange smsExchange, Queue smsKTQueue){
        return BindingBuilder.bind(smsKTQueue)
                .to(smsExchange)
                .with(KT_ROUTING_KEY);
    }

    @Bean
    public Binding bindingSmsSKT(DirectExchange smsExchange, Queue smsSKTQueue){
        return BindingBuilder.bind(smsSKTQueue)
                .to(smsExchange)
                .with(SKT_ROUTING_KEY);
    }

    @Bean
    public Binding bindingDLXSmsLG(DirectExchange smsExchange, Queue smsLGQueue){
        return BindingBuilder.bind(smsLGQueue)
                .to(smsExchange)
                .with(LG_ROUTING_KEY);
    }

    // DLX SMS Binding
    @Bean
    public Binding bindingDLXSmsKT(DirectExchange dlxSMSExchange, Queue smsKTQueue){
        return BindingBuilder.bind(smsKTQueue)
                .to(dlxSMSExchange)
                .with(KT_ROUTING_KEY);
    }

    @Bean
    public Binding bindingDLXSmsSKT(DirectExchange dlxSMSExchange, Queue smsSKTQueue){
        return BindingBuilder.bind(smsSKTQueue)
                .to(dlxSMSExchange)
                .with(SKT_ROUTING_KEY);
    }

    @Bean
    public Binding bindingSmsLG(DirectExchange dlxSMSExchange, Queue smsLGQueue){
        return BindingBuilder.bind(smsLGQueue)
                .to(dlxSMSExchange)
                .with(LG_ROUTING_KEY);
    }
}
