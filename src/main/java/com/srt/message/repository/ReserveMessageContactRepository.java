package com.srt.message.repository;

import com.srt.message.domain.ReserveMessage;
import com.srt.message.domain.ReserveMessageContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReserveMessageContactRepository extends JpaRepository<ReserveMessageContact, Long> {
    List<ReserveMessageContact> findAllByReserveMessage(ReserveMessage reserveMessage);
}
