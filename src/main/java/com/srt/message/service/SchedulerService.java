package com.srt.message.service;

import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.service.rabbit.BrokerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Log4j2
@RequiredArgsConstructor
@Service
public class SchedulerService {
    private Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private final BrokerService brokerService;
    private final TaskScheduler taskScheduler;

    public void register(BrokerMessageDto brokerMessageDto){
        String cronExpression = brokerMessageDto.getSmsMessageDto().getCronExpression();
        CronTrigger cronTrigger = new CronTrigger(cronExpression);

        ScheduledFuture<?> task = taskScheduler.schedule(() ->
                brokerService.sendSmsMessage(brokerMessageDto), cronTrigger);
        scheduledTasks.put(brokerMessageDto.getMessage().getId(), task);
    }

    public void remove(long messageId){
        log.info(messageId + "번 스케쥴러를 중지합니다.");
        scheduledTasks.get(messageId).cancel(true);
    }
}
