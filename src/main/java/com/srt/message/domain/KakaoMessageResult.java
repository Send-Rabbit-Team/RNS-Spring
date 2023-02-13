package com.srt.message.domain;

import com.srt.message.config.domain.BaseTimeEntity;
import com.srt.message.config.status.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class KakaoMessageResult extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kakao_message_id")
    private KakaoMessage kakaoMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kakao_broker_id")
    private KakaoBroker kakaoBroker;

    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus;

    private String description;

    // 편의 메서드
    public void changeMessageStatus(MessageStatus messageStatus){
        this.messageStatus = messageStatus;
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
