package com.srt.message.dlx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.srt.message.dto.dlx.ReceiveKakaoMessageDto;
import com.srt.message.dto.dlx.ReceiveMessageDto;
import com.srt.message.dto.message_result.KakaoMessageResultDto;
import com.srt.message.dto.message_result.MessageResultDto;
import com.srt.message.listener.SmsBrokerListener;
import com.srt.message.service.rabbit.BrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.*;
import static com.srt.message.utils.rabbitmq.RabbitKakaoUtil.*;

@Log4j2
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class DlxProcessingErrorHandler {
    private final BrokerService brokerService;

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    private int maxBrokerRetryCount = 2;
    private int maxRetryCount = 4;

    public boolean handleErrorProcessingMessage(Message message, Channel channel, String consumeBrokerName) {
        RabbitmqHeader rabbitmqHeader = new RabbitmqHeader(message.getMessageProperties().getHeaders());

        try {
            ReceiveMessageDto receiveMessageDto = objectMapper.readValue(new String(message.getBody()), ReceiveMessageDto.class);
            String brokerName = getBrokerName(message, receiveMessageDto);

            // retryCount > 4
            // 모든 중계사를 다 돌았을 경우에도 DL이 발생했을 경우, 해당 메시지를 실패로 저장하기
            if (rabbitmqHeader.getFailedRetryCount() >= maxRetryCount) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                brokerService.processMessageFailure(brokerName, receiveMessageDto.getMessageResultDto());
                printDeadLog(brokerName, message, rabbitmqHeader.getFailedRetryCount());

                // retryCount <= 4 && retryCount > 2
                // DL이 다른 중계사를 다 돌지 않았을 경우, 다른 중계사의 Work Queue로 보내기
            } else if (rabbitmqHeader.getFailedRetryCount() >= maxBrokerRetryCount) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                rabbitTemplate.convertAndSend(SMS_EXCHANGE_NAME, "sms.work." + getReplaceBrokerName(brokerName), message);
                printResendLog(brokerName ,consumeBrokerName, message, rabbitmqHeader.getFailedRetryCount());
            }

            // retryCount <= 2
            // 자신의 Work Queue로 보내기
            else {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                printRequeueLog(brokerName, message, rabbitmqHeader.getFailedRetryCount());
            }
            return true;

        } catch (IOException e) {
            log.warn("[HANDLER-FAILED] Error at " + new Date() + " on retry " + rabbitmqHeader.getFailedRetryCount()
                    + " for message " + message);
        }
        return false;
    }

    // 중계사 이름 받아오기
    public String getBrokerName(Message message, ReceiveMessageDto receiveMessageDto) {
        long brokerId = receiveMessageDto.getMessageResultDto().getBrokerId();
        String brokerName = (brokerId == 1) ? "kt" : (brokerId == 2) ? "skt" : "lg";

        return brokerName;
    }

    // 대체하는 중계사 이름 받아오기
    public String getReplaceBrokerName(String brokerName) {
        switch (brokerName) {
            case "kt":
                return "skt";

            case "skt":
                return "lg";

            case "lg":
                return "kt";
        }

        return null;
    }

    public void printRequeueLog(String brokerName, Message message, int retryCount) {
        log.info("[RE-QUEUE] " + "[" + brokerName + "]" + " Error at " + new Date() + " on retry " + retryCount
                + " for message " + message);
    }

    public void printResendLog(String brokerName, String consumeBrokerName, Message message, int retryCount) {
        log.warn("[RE-SEND OTHER BROKER] " + "[" + brokerName + " -> " + getReplaceBrokerName(consumeBrokerName) + "]" + " Error at " + new Date() + " on retry " + retryCount
                + " for message " + message);
    }

    public void printDeadLog(String brokerName, Message message, int retryCount) {
        log.warn("[DEAD] " + "[" + brokerName + "]" + " Error at " + new Date() + "on retry " + retryCount
                + " for message " + message);
    }

    public boolean handleErrorProcessingKakaoMessage(Message message, Channel channel) {
        RabbitmqHeader rabbitmqHeader = new RabbitmqHeader(message.getMessageProperties().getHeaders());

        try {
            ReceiveKakaoMessageDto receiveMessageDto = objectMapper.readValue(new String(message.getBody()), ReceiveKakaoMessageDto.class);
            long brokerId = receiveMessageDto.getKakaoMessageResultDto().getBrokerId();

            // 모든 중계사를 다 돌았을 경우, 해당 브로커의 Dead Queue로 보내기
            if (rabbitmqHeader.getFailedRetryCount() >= maxRetryCount) {
                String brokerName = (brokerId == 1) ? "cns" : "ke";
                log.warn("[DEAD] Error at " + new Date() + "on retry " + rabbitmqHeader.getFailedRetryCount()
                        + " for message " + message);

                KakaoMessageResultDto kakaoMessageResultDto = receiveMessageDto.getKakaoMessageResultDto();
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                rabbitTemplate.convertAndSend(KAKAO_DEAD_EXCHANGE_NAME, "kakao.dead." + brokerName, kakaoMessageResultDto);

                // 다른 중계사를 다 돌지 않았을 경우, CNS 중계사 WAIT로 보내기
            } else if (rabbitmqHeader.getFailedRetryCount() >= maxBrokerRetryCount) {
                log.warn("[RE-SEND OTHER BROKER] Error at " + new Date() + "on retry " + rabbitmqHeader.getFailedRetryCount()
                        + " for message " + message);

                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                if (brokerId == 1) {
                    rabbitTemplate.convertAndSend(KAKAO_WAIT_EXCHANGE_NAME, KE_WAIT_ROUTING_KEY, message);
                } else {
                    rabbitTemplate.convertAndSend(KAKAO_WAIT_EXCHANGE_NAME, CNS_WAIT_ROUTING_KEY, message);
                }

            }

            // 자신의 WAIT QUEUE로 넣기
            else {
                log.info("[RE-QUEUE] Error at " + new Date() + " on retry " + rabbitmqHeader.getFailedRetryCount()
                        + " for message " + message);

                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }
            return true;

        } catch (IOException e) {
            log.warn("[HANDLER-FAILED] Error at " + new Date() + " on retry " + rabbitmqHeader.getFailedRetryCount()
                    + " for message " + message);
        }
        return false;
    }
}
