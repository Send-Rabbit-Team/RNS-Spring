package com.srt.message.utils.algorithm;

import com.srt.message.domain.KakaoBroker;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

/**
 * 메시지 중계사 분배 발송 알고리즘
 * Weighted round-robin algorithm
 */
public class KakaoBrokerPool {
    Random random = new Random();
    TreeMap<Integer, KakaoBrokerWeight> pool;
    int totalWeight;

    public KakaoBrokerPool(ArrayList<KakaoBrokerWeight> brokers){
        this.pool = new TreeMap<>();

        totalWeight = 0;
        for(KakaoBrokerWeight bw: brokers){
            totalWeight += bw.getWeight();
            this.pool.put(totalWeight, bw);
        }
    }

    public KakaoBrokerWeight getNext(){
        int rnd = random.nextInt(this.totalWeight);
        return pool.ceilingEntry(rnd).getValue();
    }
}

