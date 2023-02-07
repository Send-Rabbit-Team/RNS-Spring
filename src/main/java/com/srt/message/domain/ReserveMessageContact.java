package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

// 예약 발송 대상자들 정보 저장 테이블
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class ReserveMessageContact extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_message_id")
    private ReserveMessage reserveMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_kakao_message_id")
    private ReserveKakaoMessage reserveKakaoMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;
}
