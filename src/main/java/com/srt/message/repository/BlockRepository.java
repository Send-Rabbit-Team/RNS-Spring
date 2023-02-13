package com.srt.message.repository;

import com.srt.message.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    Optional<Block> findBySenderNumberAndReceiveNumber(String SenderNumber, String ReceiveNumber);

    @Query(value = "select b.receiveNumber from Block b where b.senderNumber = :senderNumber")
    List<String> findAllBySenderNumber(String senderNumber);
}
