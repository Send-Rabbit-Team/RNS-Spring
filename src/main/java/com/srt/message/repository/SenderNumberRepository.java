package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.SenderNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Status;
import java.util.List;
import java.util.Optional;

public interface SenderNumberRepository extends JpaRepository<SenderNumber, Long> {
    Optional<SenderNumber> findByPhoneNumberAndStatus(String phoneNumber, BaseStatus status);
    Page<SenderNumber> findByMemberIdAndStatus(Long memberId, BaseStatus status, Pageable pageable);
    Optional<SenderNumber> findByIdAndStatus(Long senderNumberId, BaseStatus status);

}