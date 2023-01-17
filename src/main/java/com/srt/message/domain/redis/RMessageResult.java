package com.srt.message.domain.redis;

import com.srt.message.config.domain.BaseEntity;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.Broker;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@RedisHash("messageResult")
public class RMessageResult implements Serializable {
    @Id
    private String id;

    private Long messageId;

    private Long contactId;

    private Long brokerId;

    @TimeToLive // 유효시간 5분으로 설정
    private Long expiration = 60 * 5L;

    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus = MessageStatus.PENDING;
}
