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

    // 편의 메서드
    public void changeMessageStatus(MessageStatus messageStatus){
        this.messageStatus = messageStatus;
    }
}
