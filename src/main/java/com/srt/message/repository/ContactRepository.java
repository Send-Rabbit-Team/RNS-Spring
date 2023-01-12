package com.srt.message.repository;

import com.srt.message.domain.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByPhoneNumber(String phoneNumber);

    // 전화번호로 검색
    Page<Contact> findByPhoneNumberContaining(String phoneNumber, Pageable pageable);
}
