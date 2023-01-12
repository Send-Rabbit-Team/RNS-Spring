package com.srt.message.repository;

import com.srt.message.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByPhoneNumber(String phoneNumber);

}
