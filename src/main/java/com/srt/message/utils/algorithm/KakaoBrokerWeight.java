package com.srt.message.utils.algorithm;

import com.srt.message.domain.KakaoBroker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class KakaoBrokerWeight {
    private KakaoBroker kakaoBroker;
    private int weight;
}
