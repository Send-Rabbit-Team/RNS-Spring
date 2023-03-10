package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.config.status.ReserveStatus;
import com.srt.message.domain.Contact;
import com.srt.message.domain.ReserveKakaoMessage;
import com.srt.message.domain.ReserveMessageContact;
import com.srt.message.dto.kakao_message.KakaoMessageDto;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.repository.*;
import com.srt.message.dto.kakao_message.BrokerKakaoMessageDto;
import com.srt.message.service.message.BrokerService;
import com.srt.message.service.kakao.KakaoBrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class SchedulerService {
    private Map<Long, ScheduledFuture<?>> messageScheduledTasks = new ConcurrentHashMap<>();
    private Map<Long, ScheduledFuture<?>> kakaoScheduledTasks = new ConcurrentHashMap<>();

    private final MemberRepository memberRepository;
    private final ReserveKakaoMessageRepository reserveKakaoMessageRepository;

    private final BrokerService brokerService;
    private final KakaoBrokerService kakaoBrokerService;
    private final TaskScheduler taskScheduler;

    private final ReserveMessageRepository reserveMessageRepository;
    private final ReserveMessageContactRepository reserveMessageContactRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    // ?????????????????? ?????? ?????? ???????????? ???????????? ??????
//    @Bean
//    public void registerMessageReserveScheduler(){
//        List<ReserveMessage> reserveMessageList = reserveMessageRepository.findAllByReserveStatus(ReserveStatus.PROCESSING);
//
//        reserveMessageList.stream().forEach(r -> {
//            String cronExpression = r.getCronExpression();
//            long taskId = r.getMessage().getId();
//
//            // ????????? ????????? ????????? ??????
//            List<Contact> contacts =
//                    reserveMessageContactRepository.findAllByReserveMessage(r)
//                            .stream().map(ReserveMessageContact::getContact).collect(Collectors.toList());
//
//            // ???????????? ????????? ?????? BrokerMessageDto ?????? ????????????
//            SMSMessageDto smsMessageDto = SMSMessageDto.toDto(r.getMessage(), r);
//            smsMessageDto.setCronExpression(cronExpression);
//            BrokerMessageDto brokerMessageDto = BrokerMessageDto.builder().smsMessageDto(smsMessageDto)
//                    .message(r.getMessage()).contacts(contacts).member(r.getMessage().getMember()).build();
//
//            register(brokerMessageDto, taskId);
//        });
//    }

    @Bean
    public void registerKakaoMessageReserveScheduler(){
        List<ReserveKakaoMessage> reserveMessageList = reserveKakaoMessageRepository.findAllByReserveStatus(ReserveStatus.PROCESSING);

        reserveMessageList.stream().forEach(reserveKakaoMessage -> {
            String cronExpression = reserveKakaoMessage.getCronExpression();
            long taskId = reserveKakaoMessage.getKakaoMessage().getId();

            // ????????? ????????? ????????? ??????
            List<Contact> contacts =
                    reserveMessageContactRepository.findAllByReserveKakaoMessage(reserveKakaoMessage)
                            .stream().map(ReserveMessageContact::getContact).collect(Collectors.toList());

            // ????????? ?????? ????????? ??????
            String kakaoBizId = memberRepository.findByIdAndStatus(reserveKakaoMessage.getKakaoMessage().getMember().getId(), BaseStatus.ACTIVE)
                    .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER)).getCompany().getKakaoBizId();


            // KakaoMessageDto ?????? ????????????
            KakaoMessageDto kakaoMessageDto = KakaoMessageDto.toDto(reserveKakaoMessage.getKakaoMessage());
            kakaoMessageDto.setCronExpression(cronExpression);
            kakaoMessageDto.setFrom(kakaoBizId);

            // ???????????? ????????? ?????? BrokerMessageDto ?????? ????????????
            BrokerKakaoMessageDto brokerKakaoMessageDto = BrokerKakaoMessageDto.builder()
                    .kakaoMessageDto(kakaoMessageDto)
                    .kakaoMessage(reserveKakaoMessage.getKakaoMessage())
                    .contacts(contacts)
                    .member(reserveKakaoMessage.getKakaoMessage().getMember())
                    .build();

            registerKakao(brokerKakaoMessageDto, taskId);
        });
    }

    // ???????????? ??????
    public void register(BrokerMessageDto brokerMessageDto, long taskId) {
        String cronExpression = brokerMessageDto.getSmsMessageDto().getCronExpression();
        CronTrigger cronTrigger = new CronTrigger(cronExpression);

        ScheduledFuture<?> task = taskScheduler.schedule(() -> {
            if (checkSchedulerLock(taskId))
                return;

            // redis?????? ?????? ?????? ?????? ?????? ???????????? ????????? ??????
            String countKey = "reserve.message." + taskId + ".count";
            ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
            String count = valueOperation.get(countKey) == null? "0" : (String) valueOperation.get(countKey);
            int sendCount = Integer.parseInt(count);
            valueOperation.set(countKey, String.valueOf(sendCount + 1));

            brokerService.sendSmsMessage(brokerMessageDto);
        }, cronTrigger);

        messageScheduledTasks.put(taskId, task);
    }

    public void registerKakao(BrokerKakaoMessageDto brokerKakaoMessageDto, long taskId) {
        String cronExpression = brokerKakaoMessageDto.getKakaoMessageDto().getCronExpression();
        CronTrigger cronTrigger = new CronTrigger(cronExpression);

        ScheduledFuture<?> task = taskScheduler.schedule(() -> {
            if (checkSchedulerLock(taskId))
                return;

            // redis?????? ?????? ?????? ?????? ?????? ???????????? ????????? ??????
            String countKey = "reserve.kakao." + taskId + ".count";
            ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
            String count = valueOperation.get(countKey) == null? "0" : (String) valueOperation.get(countKey);
            int sendCount = Integer.parseInt(count);
            valueOperation.set(countKey, String.valueOf(sendCount + 1));

            kakaoBrokerService.sendKakaoMessage(brokerKakaoMessageDto);
        }, cronTrigger);

        kakaoScheduledTasks.put(taskId, task);
    }

    // ????????? ???????????? ??????
    public void deleteMessageReserve(long reserveMessageId) {
        if (messageScheduledTasks.get(reserveMessageId) != null ) {
            messageScheduledTasks.get(reserveMessageId).cancel(true);
            log.info(reserveMessageId + "??? ????????? ?????? ?????? ??????????????? ???????????????.");

            // ?????? ?????? ????????? ??????
            redisTemplate.delete("reserve." + reserveMessageId + ".count");
        }
        else
            throw new BaseException(NOT_RESERVE_MESSAGE);
    }

    // ????????? ???????????? ??????
    public void deleteKakaoReserve(long reserveMessageId) {
        if (kakaoScheduledTasks.get(reserveMessageId) != null ) {
            kakaoScheduledTasks.get(reserveMessageId).cancel(true);
            log.info(reserveMessageId + "??? ????????? ?????? ?????? ??????????????? ???????????????.");

            // ?????? ?????? ????????? ??????
            redisTemplate.delete("reserve." + reserveMessageId + ".count");
        }
        else
            throw new BaseException(NOT_RESERVE_MESSAGE);
    }

    // ???????????? ???
    public boolean checkSchedulerLock(long taskId) {
        String redisKey = "scheduler.lock.message." + taskId;
        if (redisTemplate.opsForValue().get(redisKey) != null) // ?????? ?????? ???????????? ??????
            return true;

        redisTemplate.opsForValue().set(redisKey, "lock");
        redisTemplate.expire(redisKey, 100, TimeUnit.MILLISECONDS);

        return false;
    }
}
