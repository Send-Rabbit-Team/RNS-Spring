package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ContactGroupRepository extends JpaRepository<ContactGroup,Long> {
    Optional<ContactGroup> findByNameAndStatus(String name, BaseStatus status);

    Optional<ContactGroup> findByName(String name);
    Page<ContactGroup> findByMemberIdAndStatus(long memberId, BaseStatus status, Pageable pageable);
    Optional<List<ContactGroup>> findByMemberId(long memberId);

    Optional<ContactGroup> findByIdAndStatus(long id, BaseStatus status);
}
