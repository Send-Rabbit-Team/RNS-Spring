package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.ContactGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ContactGroupRepository extends JpaRepository<ContactGroup,Long> {

    Optional<ContactGroup> findByName(String name);
    Page<ContactGroup> findByMemberIdAndStatus(long memberId, BaseStatus status, Pageable pageable);
}
