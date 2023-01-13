package com.srt.message.repository;

import com.srt.message.domain.ContactGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ContactGroupRepository extends JpaRepository<ContactGroup,Long> {

    Optional<ContactGroup> findByName(String name);
}
