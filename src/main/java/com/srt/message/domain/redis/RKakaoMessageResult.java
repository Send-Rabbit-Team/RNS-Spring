package com.srt.message.domain.redis;

import com.srt.message.config.status.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RKakaoMessageResult {
    private String id;

    private Long kakaoMessageId;

    private Long contactId;

    private Long kakaoBrokerId;

    private MessageStatus messageStatus = MessageStatus.PENDING;

    private String description;

    // 편의 메서드
    public void changeMessageStatus(MessageStatus messageStatus){
        this.messageStatus = messageStatus;
    }

    public void addDescription(String description){
        this.description += " " + description;
    }

    public void requeueDescription(String brokerName) {
        this.description = brokerName;
    }

    public void resendOneDescription(String brokerName) {
        switch (brokerName) {
            case "cns":
                this.description = "cns -> ke";
                break;

            case "ke":
                this.description = "ke -> cns";
                break;
        }
    }
}
