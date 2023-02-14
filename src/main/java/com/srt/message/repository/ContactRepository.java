package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Entity;
import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByIdAndStatus(long contactId, BaseStatus status);

    Optional<Contact> findByPhoneNumberAndStatus(String phoneNumber, BaseStatus status);

    // 전화번호로 검색
    @Query(value = "select c from Contact c where c.member.id = :memberId and c.status = :status and c.phoneNumber like %:phoneNumber%",
    countQuery = "select count(c) from Contact c where c.member.id = :memberId and c.status = :status and c.phoneNumber like %:phoneNumber%")
    Page<Contact> findbyPhoneNumber(String phoneNumber, Pageable pageable, long memberId, BaseStatus status);

    // 그룹으로 연락처 조회 (페이지네이션 없음)
    List<Contact> findByContactGroupIdAndStatus(Long groupId, BaseStatus status);

    List<Contact> findByContactGroupIdAndMemberIdAndStatus(Long groupId,Long memberId, BaseStatus status);

    List<Contact> findByPhoneNumberIn(List<String> phoneNumber);

    // 메시지 전송용도
    @EntityGraph(value = "Contact.with.Member.ContactGroup")
    List<Contact> findAllByPhoneNumberIn(List<String> phoneNumber);


    // 사용자 아이디로 연락처 조회 (페이지네이션 있음)
    @Query(value = "select c from Contact c where c.member.id = :memberId and c.status = :status",
    countQuery = "select count(c) from Contact c where c.member.id = :memberId and c.status = :status")
    Page<Contact> findAllContact(Long memberId, BaseStatus status, Pageable pageable);

    List<Contact> findByMemberIdAndStatus(long memberId, BaseStatus status);

    // 캐시용
    @EntityGraph(value = "Contact.with.Member.ContactGroup")
    Optional<Contact> findContactById(long contactId);

    @Query(value = "select c from Contact c inner join fetch c.contactGroup where c.id in :contactIdList")
    List<Contact> findAllInContactIdList(List<Long> contactIdList);
}
