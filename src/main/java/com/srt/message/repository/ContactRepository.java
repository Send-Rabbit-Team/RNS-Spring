package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByIdAndStatus(long contactId, BaseStatus status);

    Optional<Contact> findByPhoneNumberAndStatus(String phoneNumber, BaseStatus status);

    // 전화번호로 검색
    Page<Contact> findByPhoneNumberContainingAndMemberIdAndStatus(String phoneNumber, Pageable pageable,long memberId,BaseStatus status);

    // 그룹으로 힐터링
    Page<Contact> findByContactGroupId(Long groupId, Pageable pageable);

    // 그룹으로 연락처 조회 (페이지네이션 없음)
    Optional<List<Contact>> findByContactGroupIdAndStatus(Long groupId, BaseStatus status);
    Page<Contact> findByContactGroupIdAndMemberIdAndStatus(Long groupId,Long memberId, Pageable pageable, BaseStatus status);

    // 사용자 아이디로 연락처 조회 (페이지네이션 있음)
    Page<Contact> findByMemberIdAndStatus(Long memberId, BaseStatus baseStatus, Pageable pageable);
}
