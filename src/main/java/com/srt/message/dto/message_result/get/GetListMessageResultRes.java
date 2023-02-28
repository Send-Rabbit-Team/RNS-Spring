package com.srt.message.dto.message_result.get;

import com.srt.message.config.status.MessageStatus;
import com.srt.message.config.type.MessageType;
import lombok.*;

import java.util.HashMap;
import java.util.List;

import static com.srt.message.config.type.MessageType.LMS;
import static com.srt.message.config.type.MessageType.SMS;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class GetListMessageResultRes {
    private long totalCount;

    private long payPoint;
    private long refundPoint;

    private HashMap<String, Integer> broker = new HashMap<>();
    private HashMap<MessageStatus, Integer> messageStatus = new HashMap<>();

    private List<GetMessageResultRes> messageResultRes;

    public GetListMessageResultRes() {
        // init map (broker, status)
        broker.put("kt", 0);
        broker.put("skt", 0);
        broker.put("lg", 0);

        messageStatus.put(MessageStatus.PENDING, 0);
        messageStatus.put(MessageStatus.SUCCESS, 0);
        messageStatus.put(MessageStatus.RESEND, 0);
        messageStatus.put(MessageStatus.FAIL, 0);
    }

    public void addBrokerCount(Long brokerId) {
        if(brokerId == null)
            return;

        String brokerKey = null;
        brokerKey = brokerId == 1 ? "kt" : brokerId == 2 ? "skt" : "lg";

        int cnt = broker.get(brokerKey);
        broker.put(brokerKey, ++cnt);
    }

    public void addStatusCount(MessageStatus status) {
        int cnt = messageStatus.get(status);
        messageStatus.put(status, ++cnt);
    }

    public void addTotalPoint(MessageType type, MessageStatus status){
        int point = type == SMS? 1: type == LMS? 3: 6;

        if(status != MessageStatus.FAIL){
            this.payPoint += point;
        }else{
            this.refundPoint += point;
        }
    }
}
