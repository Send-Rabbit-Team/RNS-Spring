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
public class RMessageResult {
    private String id;

    private Long messageId;

    private Long contactId;

    private Long brokerId;

    private String description;

    private MessageStatus messageStatus = MessageStatus.PENDING;

    // 편의 메서드
    public void changeMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public void requeueDescription(String brokerName) {
        this.description = brokerName;
    }

    public void resendOneDescription(String brokerName) {
        switch (brokerName) {
            case "kt":
                this.description = "lg -> kt";
                break;

            case "skt":
                this.description = "kt -> skt";
                break;

            case "lg":
                this.description = "skt -> lg";
                break;
        }
    }

    public void resendTwoDescription(String brokerName) {
        switch (brokerName) {
            case "kt":
                this.description = "skt -> lg -> kt";
                break;

            case "skt":
                this.description = "lg -> kt -> skt";
                break;

            case "lg":
                this.description = "kt -> skt -> lg";
                break;
        }
    }

    public void changeId(String id) {
        this.id = id;
    }
}
