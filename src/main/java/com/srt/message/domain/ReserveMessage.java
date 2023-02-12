package com.srt.message.domain;

import com.srt.message.config.domain.BaseEntity;
import com.srt.message.config.status.ReserveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NamedEntityGraph(name = "ReserveMessage.with.Message.SenderNumber.Member", attributeNodes = {
        @NamedAttributeNode(value = "message", subgraph = "message")
},
        subgraphs = @NamedSubgraph(name = "message", attributeNodes = {
                @NamedAttributeNode("senderNumber"),
                @NamedAttributeNode("member"),
        })
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class ReserveMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    private String cronExpression;

    private String cronText;

    @Enumerated(EnumType.STRING)
    private ReserveStatus reserveStatus;

    // 편의 메서드
    public void changeReserveStatusStop(){
        this.reserveStatus = ReserveStatus.STOP;
    }
}
