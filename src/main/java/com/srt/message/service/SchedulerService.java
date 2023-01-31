package com.srt.message.service;

import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.service.rabbit.BrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Log4j2
@RequiredArgsConstructor
@Service
public class SchedulerService {
    private Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private final BrokerService brokerService;
    private final TaskScheduler taskScheduler;

    private final RedisTemplate<String, String> redisTemplate;

    public void register(BrokerMessageDto brokerMessageDto) {
        long taskId = brokerMessageDto.getMessage().getId();
        String cronExpression = brokerMessageDto.getSmsMessageDto().getCronExpression();
        CronTrigger cronTrigger = new CronTrigger(cronExpression);

        ScheduledFuture<?> task = taskScheduler.schedule(() -> {
            if(checkSchedulerLock(taskId))
                return;

            brokerService.sendSmsMessage(brokerMessageDto);
        }, cronTrigger);
        scheduledTasks.put(taskId, task);
    }

    public void remove(long messageId) {
        log.info(messageId + "번 스케쥴러를 중지합니다.");
        scheduledTasks.get(messageId).cancel(true);
    }

    // 스케쥴러 락
    public boolean checkSchedulerLock(long taskId){
        String redisKey = "scheduler.lock.message." + taskId;
        if (redisTemplate.opsForValue().get(redisKey) != null) // 이미 락이 걸려있을 경우
            return true;

        redisTemplate.opsForValue().set(redisKey, "lock");
        redisTemplate.expire(redisKey, 100, TimeUnit.MILLISECONDS);

        return false;
    }
}
