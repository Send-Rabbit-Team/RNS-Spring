package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.KakaoBroker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KakaoBrokerRepository extends JpaRepository<KakaoBroker, Long> {
    List<KakaoBroker> findAllByStatus(BaseStatus status);
}
