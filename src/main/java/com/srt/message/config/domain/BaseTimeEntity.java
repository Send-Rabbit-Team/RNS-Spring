package com.srt.message.config.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.srt.message.config.status.BaseStatus;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {
    @CreatedDate
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVE', 'INACTIVE', 'EXPIRED')")
    private BaseStatus status = BaseStatus.ACTIVE;

    protected void setStatus(BaseStatus status) {
        this.status = status;
    }

    public void changeStatusActive(){
        setStatus(BaseStatus.ACTIVE);
    }

    public void changeStatusInActive(){
        setStatus(BaseStatus.INACTIVE);
    }
}
