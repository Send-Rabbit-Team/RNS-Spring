package com.srt.message.dlx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.srt.message.dto.dlx.ReceiveKakaoMessageDto;
import com.srt.message.dto.dlx.ReceiveMessageDto;
import com.srt.message.service.kakao.KakaoBrokerService;
import com.srt.message.service.message.BrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.*;
import static com.srt.message.utils.rabbitmq.RabbitKakaoUtil.*;

@Log4j2
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class DlxProcessingErrorHandler {
    private final BrokerService brokerService;
    private final KakaoBrokerService kakaoBrokerService;

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    public static final int REQUEUE_COUNT = 2;
    public static final int MESSAGE_BROKER_DEAD_COUNT = 4;
    public static final int KAKAO_BROKER_DEAD_COUNT = 3;

    // 일반 메시지 DLX
    public boolean handleErrorProcessingMessage(Message message, Channel channel, String consumeBrokerName) {
        RabbitmqHeader rabbitmqHeader = new RabbitmqHeader(message.getMessageProperties().getHeaders());

        try {
            ReceiveMessageDto receiveMessageDto = objectMapper.readValue(new String(message.getBody()), ReceiveMessageDto.class);
            String brokerName = getMessageBrokerName(receiveMessageDto);

            // retryCount == 4
            // 모든 중계사를 다 돌았을 경우에도 DL이 발생했을 경우, 해당 메시지를 실패로 저장하기
            if (rabbitmqHeader.getFailedRetryCount() >= MESSAGE_BROKER_DEAD_COUNT) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                brokerService.processMessageFailure(brokerName, receiveMessageDto.getMessageResultDto());
                printDeadLog(brokerName, message, rabbitmqHeader.getFailedRetryCount());

                // retryCount == 2 or 3
                // DL이 다른 중계사를 다 돌지 않았을 경우, 다른 중계사의 Work Queue로 보내기
            } else if (rabbitmqHeader.getFailedRetryCount() >= REQUEUE_COUNT) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                rabbitTemplate.convertAndSend(SMS_EXCHANGE_NAME, "sms.work." + getMessageReplaceBrokerName(consumeBrokerName), message);
                printResendLog(brokerName ,consumeBrokerName, message, rabbitmqHeader.getFailedRetryCount());
            }

            // retryCount == 1
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

    // 카카오 알림톡 DLX
    public boolean handleErrorProcessingKakaoMessage(Message message, Channel channel, String consumeBrokerName) {
        RabbitmqHeader rabbitmqHeader = new RabbitmqHeader(message.getMessageProperties().getHeaders());

        try {
            ReceiveKakaoMessageDto receiveMessageDto = objectMapper.readValue(new String(message.getBody()), ReceiveKakaoMessageDto.class);
            String brokerName = getKakaoBrokerName(receiveMessageDto);

            // retryCount == 3
            // 모든 중계사를 다 돌았을 경우에도 DL이 발생했을 경우, 해당 메시지를 실패로 저장하기
            if (rabbitmqHeader.getFailedRetryCount() >= KAKAO_BROKER_DEAD_COUNT) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                kakaoBrokerService.processMessageFailure(brokerName, receiveMessageDto.getKakaoMessageResultDto());
                printDeadLog(brokerName, message, rabbitmqHeader.getFailedRetryCount());

                // retryCount == 2
                // DL이 다른 중계사를 다 돌지 않았을 경우, 다른 중계사의 Work Queue로 보내기
            } else if (rabbitmqHeader.getFailedRetryCount() >= REQUEUE_COUNT) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                rabbitTemplate.convertAndSend(KAKAO_WORK_EXCHANGE_NAME, "kakao.work." + getKakaoReplaceBrokerName(consumeBrokerName), message);
                printResendLog(brokerName ,consumeBrokerName, message, rabbitmqHeader.getFailedRetryCount());
            }

            // retryCount == 1
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

    // 메시지 중계사 이름 받아오기
    public String getMessageBrokerName(ReceiveMessageDto receiveMessageDto) {
        long brokerId = receiveMessageDto.getMessageResultDto().getBrokerId();
        String brokerName = (brokerId == 1) ? "kt" : (brokerId == 2) ? "skt" : "lg";

        return brokerName;
    }

    // 대체하는 메시지 중계사 이름 받아오기
    public String getMessageReplaceBrokerName(String brokerName) {
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

    // 카카오 중계사 이름 받아오기
    public String getKakaoBrokerName(ReceiveKakaoMessageDto receiveMessageDto) {
        long brokerId = receiveMessageDto.getKakaoMessageResultDto().getBrokerId();
        String brokerName = (brokerId == 1) ? "cns" : "ke";

        return brokerName;
    }

    // 대체하는 카카오 중계사 이름 받아오기
    public String getKakaoReplaceBrokerName(String brokerName) {
        switch (brokerName) {
            case "cns":
                return "ke";

            case "ke":
                return "cns";
        }

        return null;
    }

    public void printRequeueLog(String brokerName, Message message, int retryCount) {
        log.info("[RE-QUEUE] " + "[" + brokerName + "]" + " Error at " + new Date() + " on retry " + retryCount
                + " for message " + message);
    }

    public void printResendLog(String brokerName, String consumeBrokerName, Message message, int retryCount) {
        log.warn("[RE-SEND OTHER BROKER] " + "[" + brokerName + " -> " + getMessageReplaceBrokerName(consumeBrokerName) + "]" + " Error at " + new Date() + " on retry " + retryCount
                + " for message " + message);
    }

    public void printDeadLog(String brokerName, Message message, int retryCount) {
        log.warn("[DEAD] " + "[" + brokerName + "]" + " Error at " + new Date() + "on retry " + retryCount
                + " for message " + message);
    }
}
