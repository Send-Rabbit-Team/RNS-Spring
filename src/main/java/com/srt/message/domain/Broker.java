package com.srt.message.domain;

import com.srt.message.config.domain.BaseTimeEntity;
import com.srt.message.jwt.NoIntercept;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Broker extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "broker_id")
    private long id;

    private String name;
}
