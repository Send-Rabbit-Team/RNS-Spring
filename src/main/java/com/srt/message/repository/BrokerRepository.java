package com.srt.message.repository;

import com.srt.message.domain.Broker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrokerRepository extends JpaRepository<Broker, Long> {
    Broker findByName(String name);

    List<Broker> findAll();
}
