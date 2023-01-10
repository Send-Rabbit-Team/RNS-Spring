package com.srt.message.repository;

import com.srt.message.domain.SenderNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderNumberRepository extends JpaRepository<SenderNumber, Long> {
}
