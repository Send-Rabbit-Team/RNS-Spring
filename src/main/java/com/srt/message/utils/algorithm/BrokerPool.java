package com.srt.message.utils.algorithm;

import com.srt.message.domain.Broker;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

/**
 * 메시지 중계사 분배 발송 알고리즘
 * Weighted round-robin algorithm
 */
public class BrokerPool{
    Random random = new Random();
    TreeMap<Integer, BrokerWeight> pool;
    int totalWeight;

    public BrokerPool(ArrayList<BrokerWeight> brokers){
        this.pool = new TreeMap<>();

        totalWeight = 0;
        for(BrokerWeight bw: brokers){
            totalWeight += bw.getWeight();
            this.pool.put(totalWeight, bw);
        }
    }

    public BrokerWeight getNext(){
        int rnd = random.nextInt(this.totalWeight);
        return pool.ceilingEntry(rnd).getValue();
    }
}

