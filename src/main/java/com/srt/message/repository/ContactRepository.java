package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByIdAndStatus(long contactId, BaseStatus status);

    Optional<Contact> findByPhoneNumberAndStatus(String phoneNumber, BaseStatus status);

    // 전화번호로 검색
    Page<Contact> findByPhoneNumberContainingAndMemberIdAndStatus(String phoneNumber, Pageable pageable,long memberId,BaseStatus status);

    // 그룹으로 힐터링
    Page<Contact> findByContactGroupIdAndMemberIdAndStatus(Long groupId,Long memberId, Pageable pageable, BaseStatus status);
}
