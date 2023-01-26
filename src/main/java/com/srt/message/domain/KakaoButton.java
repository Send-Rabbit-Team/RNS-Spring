package com.srt.message.domain;

import com.srt.message.config.type.ButtonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
public class KakaoButton {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kakao_button_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kakao_message_id")
    private KakaoMessage kakaoMessage;

    private String buttonUrl;

    private String buttonTitle;

    private ButtonType buttonType;

}
