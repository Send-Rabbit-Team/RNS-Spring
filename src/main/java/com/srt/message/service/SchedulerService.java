package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.config.status.ReserveStatus;
import com.srt.message.domain.Contact;
import com.srt.message.domain.ReserveKakaoMessage;
import com.srt.message.domain.ReserveMessage;
import com.srt.message.domain.ReserveMessageContact;
import com.srt.message.dto.kakao_message.KakaoMessageDto;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.message.SMSMessageDto;
import com.srt.message.repository.*;
import com.srt.message.dto.kakao_message.BrokerKakaoMessageDto;
import com.srt.message.service.rabbit.BrokerService;
import com.srt.message.service.rabbit.KakaoBrokerService;
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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class SchedulerService {
    private final MemberRepository memberRepository;
    private final ReserveKakaoMessageRepository reserveKakaoMessageRepository;
    private Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private final BrokerService brokerService;
    private final KakaoBrokerService kakaoBrokerService;
    private final TaskScheduler taskScheduler;

    private final ReserveMessageRepository reserveMessageRepository;
    private final ReserveMessageContactRepository reserveMessageContactRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    // 어플리케이션 실행 시에 스케쥴러 자동으로 등록
    @Bean
    public void registerMessageReserveScheduler(){
        List<ReserveMessage> reserveMessageList = reserveMessageRepository.findAllByReserveStatus(ReserveStatus.PROCESSING);

        reserveMessageList.stream().forEach(r -> {
            String cronExpression = r.getCronExpression();
            long taskId = r.getMessage().getId();

            // 예약된 메시지 연락처 찾기
            List<Contact> contacts =
                    reserveMessageContactRepository.findAllByReserveMessage(r)
                            .stream().map(ReserveMessageContact::getContact).collect(Collectors.toList());

            // 스케쥴러 등록을 위해 BrokerMessageDto 객체 생성하기
            SMSMessageDto smsMessageDto = SMSMessageDto.toDto(r.getMessage(), r);
            smsMessageDto.setCronExpression(cronExpression);
            BrokerMessageDto brokerMessageDto = BrokerMessageDto.builder().smsMessageDto(smsMessageDto)
                    .message(r.getMessage()).contacts(contacts).member(r.getMessage().getMember()).build();

            register(brokerMessageDto, taskId);
        });
    }

    @Bean
    public void registerKakaoMessageReserveScheduler(){
        List<ReserveKakaoMessage> reserveMessageList = reserveKakaoMessageRepository.findAllByReserveStatus(ReserveStatus.PROCESSING);

        reserveMessageList.stream().forEach(reserveKakaoMessage -> {
            String cronExpression = reserveKakaoMessage.getCronExpression();
            long taskId = reserveKakaoMessage.getKakaoMessage().getId();

            // 예약된 메시지 연락처 찾기
            List<Contact> contacts =
                    reserveMessageContactRepository.findAllByReserveKakaoMessage(reserveKakaoMessage)
                            .stream().map(ReserveMessageContact::getContact).collect(Collectors.toList());

            // 카카오 비즈 아이디 찾기
            String kakaoBizId = memberRepository.findByIdAndStatus(reserveKakaoMessage.getKakaoMessage().getMember().getId(), BaseStatus.ACTIVE)
                    .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER)).getCompany().getKakaoBizId();


            // KakaoMessageDto 객체 생성하기
            KakaoMessageDto kakaoMessageDto = KakaoMessageDto.toDto(reserveKakaoMessage.getKakaoMessage());
            kakaoMessageDto.setCronExpression(cronExpression);
            kakaoMessageDto.setFrom(kakaoBizId);

            // 스케쥴러 등록을 위해 BrokerMessageDto 객체 생성하기
            BrokerKakaoMessageDto brokerKakaoMessageDto = BrokerKakaoMessageDto.builder()
                    .kakaoMessageDto(kakaoMessageDto)
                    .kakaoMessage(reserveKakaoMessage.getKakaoMessage())
                    .contacts(contacts)
                    .member(reserveKakaoMessage.getKakaoMessage().getMember())
                    .build();

            registerKakao(brokerKakaoMessageDto, taskId);
        });
    }

    // 스케쥴러 등록
    public void register(BrokerMessageDto brokerMessageDto, long taskId) {
        String cronExpression = brokerMessageDto.getSmsMessageDto().getCronExpression();
        CronTrigger cronTrigger = new CronTrigger(cronExpression);

        ScheduledFuture<?> task = taskScheduler.schedule(() -> {
            if (checkSchedulerLock(taskId))
                return;

            // redis에서 예약 발송 전송 횟수 가져와서 카운팅 처리
            String countKey = "reserve." + taskId + ".count";
            ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
            String count = valueOperation.get(countKey) == null? "0" : (String) valueOperation.get(countKey);
            int sendCount = Integer.parseInt(count);
            valueOperation.set(countKey, String.valueOf(sendCount + 1));

            brokerService.sendSmsMessage(brokerMessageDto);
        }, cronTrigger);

        scheduledTasks.put(taskId, task);
    }

    public void registerKakao(BrokerKakaoMessageDto brokerKakaoMessageDto, long taskId) {
        String cronExpression = brokerKakaoMessageDto.getKakaoMessageDto().getCronExpression();
        CronTrigger cronTrigger = new CronTrigger(cronExpression);

        ScheduledFuture<?> task = taskScheduler.schedule(() -> {
            if (checkSchedulerLock(taskId))
                return;

            // redis에서 예약 발송 전송 횟수 가져와서 카운팅 처리
            String countKey = "reserve." + taskId + ".count";
            ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
            String count = valueOperation.get(countKey) == null? "0" : (String) valueOperation.get(countKey);
            int sendCount = Integer.parseInt(count);
            valueOperation.set(countKey, String.valueOf(sendCount + 1));

            kakaoBrokerService.sendKakaoMessage(brokerKakaoMessageDto);
        }, cronTrigger);

        scheduledTasks.put(taskId, task);
    }

    // 스케쥴러 취소
    public void remove(long reserveMessageId) {
        if (scheduledTasks.get(reserveMessageId) != null ) {
            scheduledTasks.get(reserveMessageId).cancel(true);
            log.info(reserveMessageId + "번 메시지 예약 발송 스케쥴러를 중지합니다.");

            // 예약 발송 카운트 제거
            redisTemplate.delete("reserve." + reserveMessageId + ".count");
        }
        else
            throw new BaseException(NOT_RESERVE_MESSAGE);
    }

    // 스케쥴러 락
    public boolean checkSchedulerLock(long taskId) {
        String redisKey = "scheduler.lock.message." + taskId;
        if (redisTemplate.opsForValue().get(redisKey) != null) // 이미 락이 걸려있을 경우
            return true;

        redisTemplate.opsForValue().set(redisKey, "lock");
        redisTemplate.expire(redisKey, 100, TimeUnit.MILLISECONDS);

        return false;
    }
}
