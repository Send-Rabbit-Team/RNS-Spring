package com.srt.message.dto.kakao_message_result.get;

import com.srt.message.config.status.MessageStatus;
import com.srt.message.config.type.MessageType;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.srt.message.config.type.MessageType.LMS;
import static com.srt.message.config.type.MessageType.SMS;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class GetKakaoMessageResultListRes {
    private long totalCount;

    private long payPoint;
    private long refundPoint;

    private List<GetKakaoMessageResultRes> kakaoMessageResultResList = new ArrayList<>();
    private HashMap<String, Integer> kakaoBrokerMap = new HashMap<>();
    private HashMap<MessageStatus, Integer> messageStatusMap = new HashMap<>();

    public GetKakaoMessageResultListRes() {
        // init map (broker, status)
        kakaoBrokerMap.put("cns", 0);
        kakaoBrokerMap.put("ke", 0);

        messageStatusMap.put(MessageStatus.PENDING, 0);
        messageStatusMap.put(MessageStatus.SUCCESS, 0);
        messageStatusMap.put(MessageStatus.RESEND, 0);
        messageStatusMap.put(MessageStatus.FAIL, 0);
    }

    public void addKakaoMessageResultResList(GetKakaoMessageResultRes getKakaoMessageResultRes) {
        kakaoMessageResultResList.add(getKakaoMessageResultRes);
    }

    public void addBrokerCount(Long brokerId) {
        if(brokerId == null)
            return;

        String brokerKey = null;
        brokerKey = brokerId == 1 ? "cns" : "ke";

        int cnt = kakaoBrokerMap.get(brokerKey);
        kakaoBrokerMap.put(brokerKey, ++cnt);
    }

    public void addStatusCount(MessageStatus status) {
        int cnt = messageStatusMap.get(status);
        messageStatusMap.put(status, ++cnt);
    }

    public void addTotalPoint(MessageStatus status){
        if(status != MessageStatus.FAIL){
            this.payPoint += 1;
        }else{
            this.refundPoint += 1;
        }
    }
}
