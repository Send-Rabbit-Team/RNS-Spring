package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Builder
public class ContactGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="group_id")
    private long id;

    private String name;
}
