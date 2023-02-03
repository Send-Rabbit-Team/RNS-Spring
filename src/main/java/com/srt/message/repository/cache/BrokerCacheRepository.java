package com.srt.message.repository.cache;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.Broker;
import com.srt.message.domain.KakaoBroker;
import com.srt.message.repository.BrokerRepository;
import com.srt.message.repository.KakaoBrokerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import static com.srt.message.config.response.BaseResponseStatus.NOT_EXIST_BROKER;

@Slf4j
@Component
@RequiredArgsConstructor
public class BrokerCacheRepository {
    private final KakaoBrokerRepository kakaoBrokerRepository;

    private final BrokerRepository brokerRepository;

    @Cacheable(value = "Broker", key = "#brokerId")
    public Broker findBrokerById(long brokerId){
        return brokerRepository.findById(brokerId).orElseThrow(() -> new BaseException(NOT_EXIST_BROKER));
    }

    @Cacheable(value = "KakaoBroker", key = "#brokerId")
    public KakaoBroker findKakaoBrokerById(long brokerId){
        return kakaoBrokerRepository.findById(brokerId).orElseThrow(() -> new BaseException(NOT_EXIST_BROKER));
    }
}
