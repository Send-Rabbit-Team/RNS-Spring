package com.srt.message.repository;

import com.srt.message.domain.SenderNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SenderNumberRepository extends JpaRepository<SenderNumber, Long> {
    Optional<SenderNumber> findByPhoneNumber(String phoneNumber);
}
