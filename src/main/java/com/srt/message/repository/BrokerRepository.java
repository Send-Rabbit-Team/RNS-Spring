package com.srt.message.repository;

import com.srt.message.domain.Broker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrokerRepository extends JpaRepository<Broker, Long> {
}
