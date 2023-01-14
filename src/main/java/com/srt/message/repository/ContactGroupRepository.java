package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.ContactGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ContactGroupRepository extends JpaRepository<ContactGroup,Long> {
    Optional<ContactGroup> findByNameAndStatus(String name, BaseStatus status);

    Optional<List<ContactGroup>> findByMemberId(long memberId);
}
