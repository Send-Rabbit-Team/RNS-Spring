package com.srt.message.service.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RKakaoMessageResult;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.message.BrokerSendMessageDto;
import com.srt.message.dto.message_result.MessageResultDto;
import com.srt.message.repository.KakaoBrokerRepository;
import com.srt.message.repository.KakaoMessageRuleRepository;
import com.srt.message.dto.kakao_message.BrokerKakaoMessageDto;
import com.srt.message.dto.kakao_message.BrokerSendKakaoMessageDto;
import com.srt.message.dto.kakao_message.KakaoMessageDto;
import com.srt.message.dto.message_result.KakaoMessageResultDto;
import com.srt.message.repository.redis.RedisHashRepository;
import com.srt.message.repository.redis.RedisListRepository;
import com.srt.message.utils.algorithm.BrokerPool;
import com.srt.message.utils.algorithm.BrokerWeight;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.srt.message.config.status.BaseStatus.ACTIVE;
import static com.srt.message.dlx.DlxProcessingErrorHandler.KAKAO_BROKER_DEAD_COUNT;
import static com.srt.message.dlx.DlxProcessingErrorHandler.MESSAGE_BROKER_DEAD_COUNT;
import static com.srt.message.utils.rabbitmq.RabbitKakaoUtil.*;
import static com.srt.message.utils.rabbitmq.RabbitSMSUtil.SMS_EXCHANGE_NAME;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoBrokerService {
    private final int TMP_MESSAGE_DURATION = 5 * 60;
    private final int VALUE_MESSAGE_DURATION = 30 * 60;

    private final KakaoBrokerCacheService kakaoBrokerCacheService;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private final KakaoBrokerRepository kakaoBrokerRepository;
    private final KakaoMessageRuleRepository kakaoMessageRuleRepository;

    private final RedisListRepository redisListRepository;
    private final RedisHashRepository redisHashRepository;

    private KakaoMessageDto kakaoMessageDto;
    private KakaoMessage kakaoMessage;
    private List<Contact> contacts;

    // Broker ???????????? ????????? ??????
    public String sendKakaoMessage(BrokerKakaoMessageDto brokerKakaoMessageDto) {
        // ?????? ??????
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // BrokerKakaoMessageDto?????? ?????? ?????????
        this.kakaoMessageDto = brokerKakaoMessageDto.getKakaoMessageDto();
        this.kakaoMessage = brokerKakaoMessageDto.getKakaoMessage();
        this.contacts = brokerKakaoMessageDto.getContacts();
        Member member = brokerKakaoMessageDto.getMember();

        // Redis??? ?????? ?????? (????????? ?????? ??????)
        int idx = 0;
        List<String> rKakaoMessageResultDtoList = new ArrayList<>();
        List<KakaoMessageResultDto> kakaoMessageResultDtoList = new ArrayList<>();
        for (Contact contact : contacts) {
            KakaoMessageResultDto kakaoMessageResultDto = KakaoMessageResultDto.builder()
                    .rMessageResultId(String.valueOf(++idx))
                    .messageId(kakaoMessage.getId())
                    .contactId(contact.getId())
                    .messageStatus(MessageStatus.PENDING)
                    .build();
            rKakaoMessageResultDtoList.add(convertToJson(kakaoMessageResultDto));
            kakaoMessageResultDtoList.add(kakaoMessageResultDto);
        }

        // Redis Repository??? ?????? ??????
        String tmpKey = "message.tmp." + kakaoMessage.getId();
        redisListRepository.rightPushAll(tmpKey, rKakaoMessageResultDtoList, TMP_MESSAGE_DURATION);

        String valueKey = "message.value." + kakaoMessage.getId(); // tmp?????? TTL ???????????? ?????? value??? ???????????? ??????
        redisListRepository.rightPushAll(valueKey, rKakaoMessageResultDtoList, VALUE_MESSAGE_DURATION);

        // ????????? ?????? ??????
        Map<Long, String> brokerMap = new HashMap<>();
        List<KakaoMessageRule> messageRules = kakaoMessageRuleRepository.findByMemberIdAndStatus(member.getId(), BaseStatus.ACTIVE);

        if (messageRules.isEmpty()) { // ?????? ????????? ?????? ????????? ??????
            List<KakaoBroker> brokers = kakaoBrokerRepository.findAll();
            for (KakaoBroker broker : brokers) {
                messageRules.add(KakaoMessageRule.builder()
                        .kakaoBroker(broker)
                        .brokerRate(30)
                        .build());
            }
        }

        ArrayList<BrokerWeight<KakaoBroker>> kakaoBrokerWeightList = new ArrayList<>();
        for (KakaoMessageRule messageRule : messageRules) {
            kakaoBrokerWeightList.add(new BrokerWeight<>(messageRule.getKakaoBroker(), messageRule.getBrokerRate()));

            KakaoBroker broker = messageRule.getKakaoBroker();
            brokerMap.put(broker.getId(), broker.getName().toLowerCase());
        }

        BrokerPool<KakaoBroker> brokerPool = new BrokerPool<>(kakaoBrokerWeightList);

        HashMap<String, String> rMessageResultMap = new HashMap<>();
        HashMap<String, String> contactMap = new HashMap<>();

        // ?????? DB??? ????????????
        for (int i = 0; i < contacts.size(); i++) {
            KakaoBroker kakaoBroker = (KakaoBroker) brokerPool.getNext().getBroker();
            kakaoMessageDto.setTo(contacts.get(i).getPhoneNumber());

            // Redis?????? MessageResultDTO ????????????
            KakaoMessageResultDto kakaoMessageResultDto = kakaoMessageResultDtoList.get(i);
            kakaoMessageResultDto.setBrokerId(kakaoBroker.getId());

            // ????????? ?????????
            Contact contact = contacts.get(i);
            contactMap.put(String.valueOf(contact.getId()), convertToJson(contact));

            // ?????? ??? ??????
            RKakaoMessageResult rKakaoMessageResult = KakaoMessageResultDto.toRMessageResult(kakaoMessageResultDto);
            rMessageResultMap.put(rKakaoMessageResult.getId(), convertToJson(rKakaoMessageResult));
        }
        String contactKey = "message.contact." + kakaoMessage.getId();
        redisHashRepository.saveContactAll(contactKey, contactMap);

        String statusKey = "message.status." + kakaoMessage.getId();
        redisHashRepository.saveAll(statusKey, rMessageResultMap);

        // ??? ????????? ????????? ?????? ?????????
        for (int i = 0; i < contacts.size(); i++) {
            KakaoMessageResultDto kakaoMessageResultDto = kakaoMessageResultDtoList.get(i);
            BrokerSendKakaoMessageDto brokerSendKakaoMessageDto = new BrokerSendKakaoMessageDto(kakaoMessageDto, kakaoMessageResultDto);

            long brokerId = kakaoMessageResultDto.getBrokerId();
            String routingKey = "kakao.work." + brokerMap.get(brokerId);

            // AMQP Message Builder
            org.springframework.amqp.core.Message amqpMessage = MessageBuilder
                    .withBody(convertToJson(brokerSendKakaoMessageDto).getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            rabbitTemplate.convertAndSend(KAKAO_WORK_EXCHANGE_NAME, routingKey, amqpMessage);

            log.info((i + 1) + " ?????? ???????????? ????????????????????? - " + routingKey);
        }

        // ?????? ????????? ??? ??????
        redisListRepository.remove(tmpKey);
        redisListRepository.remove(valueKey);

        // ?????? ?????? ??????
        stopWatch.stop();
        String processTime = String.valueOf(stopWatch.getTime());
        log.info("Process Time: {} ", processTime);
        return processTime;
    }

    // ????????? ?????? ?????? ??????
    public void processMessageFailure(String brokerName, KakaoMessageResultDto messageResultDto) {
        messageResultDto.setRetryCount(MESSAGE_BROKER_DEAD_COUNT);
        kakaoBrokerCacheService.saveMessageResultFailure(messageResultDto, brokerName);

        log.warn(brokerName + " broker got dead letter - {}", messageResultDto);
    }

    // Json ????????? ??????
    public String convertToJson(Object object){
        String sendMessageJson = null;
        try {
             sendMessageJson = objectMapper.writeValueAsString(object);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return sendMessageJson;
    }
}
