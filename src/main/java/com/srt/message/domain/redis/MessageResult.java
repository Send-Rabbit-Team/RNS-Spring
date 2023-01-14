package com.srt.message.domain.redis;

import com.srt.message.config.domain.BaseEntity;
import com.srt.message.config.domain.BaseTimeEntity;
import com.srt.message.config.status.AuthPhoneNumberStatus;
import com.srt.message.config.status.MessageStatus;
import com.srt.message.domain.Broker;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Message;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.*;

public class MessageResult extends BaseEntity {
    @Id
    private String id;

    private Message message;

    private Contact contact;

    private Broker broker;

    @TimeToLive
    private Long expiration;

    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus = MessageStatus.PENDING;
}
