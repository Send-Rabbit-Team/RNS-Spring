package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Block;
import com.srt.message.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    Optional<Block> findBySenderNumberAndReceiveNumber(String SenderNumber, String ReceiveNumber);

    @Query(value = "select b.receiveNumber from Block b where b.senderNumber = :senderNumber and b.status = :status")
    List<String> findAllBySenderNumberAndStatus(String senderNumber, BaseStatus status);

    @Query(value = "select c from Block b inner join Contact c on b.receiveNumber = c.phoneNumber and c in :contacts " +
            "inner join SenderNumber s on b.senderNumber = s.phoneNumber and b.senderNumber = :senderNumber where b.status = :status")
    List<Contact> findContactList(List<Contact> contacts, String senderNumber, BaseStatus status);
}
