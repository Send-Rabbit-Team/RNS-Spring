package com.srt.message.config.domain;

import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Member;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity extends BaseTimeEntity{
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private long updatedBy;
}
