package com.srt.message.dlx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.srt.message.dto.dlx.ReceiveMessageDto;
import com.srt.message.dto.message_result.MessageResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.*;

@Log4j2
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class DlxProcessingErrorHandler {
    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    private int maxBrokerRetryCount = 2;
    private int maxRetryCount = 4;

    public boolean handleErrorProcessingMessage(Message message, Channel channel) {
        RabbitmqHeader rabbitmqHeader = new RabbitmqHeader(message.getMessageProperties().getHeaders());

        try {
            ReceiveMessageDto receiveMessageDto = objectMapper.readValue(new String(message.getBody()), ReceiveMessageDto.class);
            String brokerName = getBrokerName(message, receiveMessageDto);

            // 모든 중계사를 다 돌았을 경우, 해당 브로커의 Dead Queue로 보내기
            if (rabbitmqHeader.getFailedRetryCount() >= maxRetryCount) {
                log.warn("[DEAD] Error at " + new Date() + "on retry " + rabbitmqHeader.getFailedRetryCount()
                        + " for message " + message);

                MessageResultDto messageResultDto = receiveMessageDto.getMessageResultDto();
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                rabbitTemplate.convertAndSend(DEAD_EXCHANGE_NAME, "sms.dead." + brokerName, messageResultDto);

                // 다른 중계사를 다 돌지 않았을 경우, 자신의 중계사 WAIT로 보내기
            } else if (rabbitmqHeader.getFailedRetryCount() >= maxBrokerRetryCount) {
                log.warn("[RE-SEND OTHER BROKER] Error at " + new Date() + "on retry " + rabbitmqHeader.getFailedRetryCount()
                        + " for message " + message);

                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                rabbitTemplate.convertAndSend(WAIT_EXCHANGE_NAME, "sms.wait." + getReplaceBrokerName(brokerName), message);
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

    // x.death-count + 1 증가
    public Message increaseDeathCount(Message message) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        List<Map<String, Object>> xDeathHeaders = (List<Map<String, Object>>) headers.get("x-death");

        int idx = 0;
        for (Map<String, Object> x : xDeathHeaders) {
            Optional<Object> count = Optional.ofNullable(x.get("count"));
            int finalIdx = idx;
            count.ifPresent(c -> xDeathHeaders.get(finalIdx).put("x-death", (long) c+1));

            idx++;
        }
        message.getMessageProperties().getHeaders().put("x-death", xDeathHeaders);
        return message;
    }

    // 중계사 이름 받아오기
    public String getBrokerName(Message message, ReceiveMessageDto receiveMessageDto){
        long brokerId = receiveMessageDto.getMessageResultDto().getBrokerId();
        String brokerName = (brokerId == 1) ? "kt" : (brokerId == 2) ? "skt" : "lg";

        return brokerName;
    }

    // 대체하는 중계사 이름 받아오기
    public String getReplaceBrokerName(String brokerName){
        switch(brokerName){
            case "kt":
                return "skt";

            case "skt":
                return "lg";

            case "lg":
                return "kt";
        }

        return null;
    }
}
