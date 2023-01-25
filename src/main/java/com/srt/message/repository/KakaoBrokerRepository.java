package com.srt.message.repository;

import com.srt.message.domain.KakaoBroker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoBrokerRepository extends JpaRepository<KakaoBroker, Long> {
    KakaoBroker findByName(String name);
}
