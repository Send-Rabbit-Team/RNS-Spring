package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
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
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int smsPoint;
    private int kakaoPoint;

    public void addSmsPoint(int addPoint) {
        this.smsPoint += addPoint;
    }
    public void subSmsPoint(int subPoint) {
        this.smsPoint -= subPoint;
    }
    public void addKakaoPoint(int addPoint) {
        this.kakaoPoint += addPoint;
    }
    public void subKakaoPoint(int subPoint) {
        this.kakaoPoint -= subPoint;
    }
}
