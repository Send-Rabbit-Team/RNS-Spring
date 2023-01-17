package com.srt.message.repository;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    Optional<Template> findByIdAndStatus(Long templateId, BaseStatus status);

    @Query(value = "select t from Template t where t.member.id = :memberId and t.status = :status",
    countQuery = "select count(t) from Template t where t.member.id = :memberId and t.status = :status")
    Page<Template> findAll(Long memberId, BaseStatus status, Pageable pageable);


}
