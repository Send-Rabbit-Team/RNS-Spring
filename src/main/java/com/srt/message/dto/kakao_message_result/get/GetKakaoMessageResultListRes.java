package com.srt.message.dto.kakao_message_result.get;

import com.srt.message.config.status.MessageStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetKakaoMessageResultListRes {
    private List<GetKakaoMessageResultRes> kakaoMessageResultResList = new ArrayList<>();
    private HashMap<String, Integer> kakaoBrokerMap = new HashMap<>();
    private HashMap<MessageStatus, Integer> messageStatusMap = new HashMap<>();

    public void addKakaoMessageResultResList(GetKakaoMessageResultRes getKakaoMessageResultRes) {
        kakaoMessageResultResList.add(getKakaoMessageResultRes);
    }

    public void addKakaoBrokerCount(String kakaoBrokerName) {
        int count = 0;
        if (kakaoBrokerMap.containsKey(kakaoBrokerName))
            count = kakaoBrokerMap.get(kakaoBrokerName);

        kakaoBrokerMap.put(kakaoBrokerName, count+1);
    }

    public void addMessageStatusCount(MessageStatus messageStatus) {
        int count = 0;
        if (messageStatusMap.containsKey(messageStatus))
            count = messageStatusMap.get(messageStatus);

        messageStatusMap.put(messageStatus, count+1);
    }
}
