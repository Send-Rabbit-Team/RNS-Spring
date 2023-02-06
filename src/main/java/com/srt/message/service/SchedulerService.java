package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.ReserveMessage;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.kakao_message.BrokerKakaoMessageDto;
import com.srt.message.repository.ReserveMessageRepository;
import com.srt.message.service.rabbit.BrokerService;
import com.srt.message.service.rabbit.KakaoBrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.srt.message.config.response.BaseResponseStatus.ALREADY_CANCEL_RESERVE;

@Log4j2
@RequiredArgsConstructor
@Service
public class SchedulerService {
    private Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private final BrokerService brokerService;
    private final KakaoBrokerService kakaoBrokerService;
    private final TaskScheduler taskScheduler;

    private final ReserveMessageRepository reserveMessageRepository;

    private final RedisTemplate<String, String> redisTemplate;

    // 스케쥴러 등록
    public void register(BrokerMessageDto brokerMessageDto) {
        long taskId = brokerMessageDto.getMessage().getId();
        String cronExpression = brokerMessageDto.getSmsMessageDto().getCronExpression();
        CronTrigger cronTrigger = new CronTrigger(cronExpression);

        ScheduledFuture<?> task = taskScheduler.schedule(() -> {
            if (checkSchedulerLock(taskId))
                return;

            brokerService.sendSmsMessage(brokerMessageDto);
        }, cronTrigger);

        scheduledTasks.put(taskId, task);
    }

    public void registerKakao(BrokerKakaoMessageDto brokerKakaoMessageDto) {
        long taskId = brokerKakaoMessageDto.getKakaoMessage().getId();
        String cronExpression = brokerKakaoMessageDto.getKakaoMessageDto().getCronExpression();
        CronTrigger cronTrigger = new CronTrigger(cronExpression);

        ScheduledFuture<?> task = taskScheduler.schedule(() -> {
            if (checkSchedulerLock(taskId))
                return;

            kakaoBrokerService.sendKakaoMessage(brokerKakaoMessageDto);
        }, cronTrigger);

        scheduledTasks.put(taskId, task);
    }

    // 스케쥴러 취소
    public String remove(long messageId) {
        Optional<ReserveMessage> reserveMessageOptional = reserveMessageRepository.findByMessageId(messageId);

        if (scheduledTasks.get(messageId) != null && reserveMessageOptional.isPresent()){
            ReserveMessage reserveMessage = reserveMessageOptional.get();
            if(reserveMessage.getStatus() == BaseStatus.INACTIVE)
                throw new BaseException(ALREADY_CANCEL_RESERVE);

            scheduledTasks.get(messageId).cancel(true);
            reserveMessageRepository.delete(reserveMessage);

            log.info(messageId + "번 메시지 예약 발송 스케쥴러를 중지합니다.");

            return messageId + "의 메시지 발송 예약이 취소되었습니다.";
        }else{
            return "해당 메시지는 예약된 메시지가 아닙니다";
        }
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
