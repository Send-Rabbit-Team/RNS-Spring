package com.srt.message.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.*;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // BROKER MSG SEND SETTING
    // SMS QUEUE
    @Bean
    public Queue smsKTQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", KT_WAIT_ROUTING_KEY);
        return new Queue(KT_WORK_QUEUE_NAME, true, false, false, args);
    }

    @Bean
    public Queue smsSKTQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", SKT_WAIT_ROUTING_KEY);
        return new Queue(SKT_WORK_QUEUE_NAME, true, false, false, args);
    }

    @Bean
    public Queue smsLGQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", LG_WAIT_ROUTING_KEY);
        return new Queue(LG_WORK_QUEUE_NAME, true, false, false, args);
    }

    // SMS Exchange
    @Bean
    public DirectExchange smsExchange(){
        return new DirectExchange("dx.sms.work");
    }

    // SMS Binding
    @Bean
    public Binding bindingSmsKT(DirectExchange smsExchange, Queue smsKTQueue){
        return BindingBuilder.bind(smsKTQueue)
                .to(smsExchange)
                .with(KT_WORK_ROUTING_KEY);
    }

    @Bean
    public Binding bindingSmsSKT(DirectExchange smsExchange, Queue smsSKTQueue){
        return BindingBuilder.bind(smsSKTQueue)
                .to(smsExchange)
                .with(SKT_WORK_ROUTING_KEY);
    }

    @Bean
    public Binding bindingSmsLG(DirectExchange smsExchange, Queue smsLGQueue){
        return BindingBuilder.bind(smsLGQueue)
                .to(smsExchange)
                .with(LG_WORK_ROUTING_KEY);
    }

    // BROKER MSG RECEIVE SETTING
    @Bean
    public DirectExchange smsReceiveExchange(){
        return new DirectExchange(RECEIVE_EXCHANGE_NAME);
    }

    // KT
    @Bean
    Queue smsReceiveKTQueue(){
        Map<String, Object> args = new HashMap<>();
        return new Queue(KT_RECEIVE_QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingSmsReceiveKT(DirectExchange smsReceiveExchange, Queue smsReceiveKTQueue){
        return BindingBuilder.bind(smsReceiveKTQueue)
                .to(smsReceiveExchange)
                .with(KT_RECEIVE_ROUTING_KEY);
    }

    // SKT
    @Bean
    Queue smsReceiveSKTQueue(){
        Map<String, Object> args = new HashMap<>();
        return new Queue(SKT_RECEIVE_QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingSmsReceiveSKT(DirectExchange smsReceiveExchange, Queue smsReceiveSKTQueue){
        return BindingBuilder.bind(smsReceiveSKTQueue)
                .to(smsReceiveExchange)
                .with(SKT_RECEIVE_ROUTING_KEY);
    }

    // LG
    @Bean
    Queue smsReceiveLGQueue(){
        Map<String, Object> args = new HashMap<>();
        return new Queue(LG_RECEIVE_QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingSmsReceiveLG(DirectExchange smsReceiveExchange, Queue smsReceiveLGQueue){
        return BindingBuilder.bind(smsReceiveLGQueue)
                .to(smsReceiveExchange)
                .with(LG_RECEIVE_ROUTING_KEY);
    }

    /**
     * DLX 설정
     */
    // DLX Queue
    @Bean
    public Queue smsWaitKTQueue(){
        return new Queue(KT_WAIT_QUEUE_NAME, true);
    }

    @Bean
    public Queue smsWaitSKTQueue(){
        return new Queue(SKT_WAIT_QUEUE_NAME, true);
    }

    @Bean
    public Queue smsWaitLGQueue(){
        return new Queue(LG_WAIT_QUEUE_NAME, true);
    }

    // DLX Exchange
    @Bean
    public DirectExchange dlxSMSExchange(){
        return new DirectExchange(DLX_EXCHANGE_NAME);
    }

    // DLX Binding
    @Bean
    public Binding bindingDLXSmsKT(DirectExchange dlxSMSExchange, Queue smsWaitKTQueue){
        return BindingBuilder.bind(smsWaitKTQueue)
                .to(dlxSMSExchange)
                .with(KT_WAIT_ROUTING_KEY);
    }

    @Bean
    public Binding bindingDLXSmsSKT(DirectExchange dlxSMSExchange, Queue smsWaitSKTQueue){
        return BindingBuilder.bind(smsWaitSKTQueue)
                .to(dlxSMSExchange)
                .with(SKT_WAIT_ROUTING_KEY);
    }

    @Bean
    public Binding bindingDLXSmsLG(DirectExchange dlxSMSExchange, Queue smsWaitLGQueue){
        return BindingBuilder.bind(smsWaitLGQueue)
                .to(dlxSMSExchange)
                .with(LG_WAIT_ROUTING_KEY);
    }

    // KAKAO Exchange
    @Bean
    public DirectExchange kakaoWorkExchange() {
        return new DirectExchange(KAKAO_WORK_EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange kakaoReceiveExchange(){ return new DirectExchange(KAKAO_RECEIVE_EXCHANGE_NAME); }

    @Bean
    public DirectExchange KakaoDlxExchange(){ return new DirectExchange(KAKAO_DLX_EXCHANGE_NAME); }

    // KE Queue(work, wait, receive)
    @Bean
    public Queue kakaoWorkKEQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", KAKAO_DLX_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", KE_WAIT_ROUTING_KEY);
        return new Queue(KE_WORK_QUEUE_NAME, true, false, false, args);
    }

    @Bean
    public Queue kakaoReceiveKEQueue() {
        return new Queue(KE_RECEIVE_QUEUE_NAME, true);
    }

    @Bean
    public Queue kakaoWaitKEQueue() {
        Map<String, Object> args = new HashMap<>();
//        args.put("x-message-ttl", WAIT_TTL);
        args.put("x-dead-letter-exchange", KAKAO_WORK_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", KE_WORK_ROUTING_KEY);

        return new Queue(KE_WAIT_QUEUE_NAME, true, false,false, args);
    }

    // KE Binding
    @Bean
    public Binding bindingKakaoWorkKE(DirectExchange kakaoWorkExchange, Queue kakaoWorkKEQueue){
        return BindingBuilder.bind(kakaoWorkKEQueue)
                .to(kakaoWorkExchange)
                .with(KE_WORK_ROUTING_KEY);
    }


    @Bean
    public Binding bindingKakaoReceiveKE(DirectExchange kakaoReceiveExchange, Queue kakaoReceiveKEQueue){
        return BindingBuilder.bind(kakaoReceiveKEQueue)
                .to(kakaoReceiveExchange)
                .with(KE_RECEIVE_ROUTING_KEY);
    }


    @Bean
    public Binding bindingKakaoDlxKE(DirectExchange KakaoDlxExchange, Queue kakaoWaitKEQueue){
        return BindingBuilder.bind(kakaoWaitKEQueue)
                .to(KakaoDlxExchange)
                .with(KE_WAIT_ROUTING_KEY);
    }
}
