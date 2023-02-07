package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import com.srt.message.config.status.ReserveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class ReserveKakaoMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kakao_message_id")
    private KakaoMessage kakaoMessage;

    private String cronExpression;

    private String cronText;

    @Enumerated(EnumType.STRING)
    private ReserveStatus reserveStatus;

    public void changeReserveStatusStop(){
        this.reserveStatus = ReserveStatus.STOP;
    }
}
