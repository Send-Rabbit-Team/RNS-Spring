package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class SenderNumber extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sender_number_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String phoneNumber;

    private String memo;

    private String blockNumber;

    // 편의 메서드
    public void createBlockNumber(){
        String start = "070";
        String middle = RandomStringUtils.randomNumeric(4);
        String end = RandomStringUtils.randomNumeric(4);

        this.blockNumber = start + middle + end;
    }
}
