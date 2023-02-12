package com.srt.message.utils.algorithm;

import java.util.*;

/**
 * 메시지 중계사 분배 발송 알고리즘
 * Weighted round-robin algorithm
 */
public class BrokerPool{
    Queue<BrokerWeight> brokerQueue;
    ArrayList<BrokerWeight> brokers;
    int totalWeight;
    double value;

    public BrokerPool(ArrayList<BrokerWeight> brokers){
        this.brokers = brokers;
        this.brokerQueue = new LinkedList<>();
        totalWeight = 0;

        for(BrokerWeight bw: brokers){
            this.brokerQueue.add(bw);
            totalWeight += bw.getTotalWeight();
        }

        value = totalWeight / 100d;
    }

    public BrokerWeight getNext(){
        BrokerWeight broker = null;
        while(!brokerQueue.isEmpty()){
            BrokerWeight brokerWeight = brokerQueue.poll();
            double currentWeight = brokerWeight.getCurrentWeight();
            if(currentWeight >= brokerWeight.getTotalWeight())
                continue;

            broker = brokerWeight;
            broker.addCurrentWeight(value);
            brokerQueue.add(brokerWeight);
            break;
        }

        if(brokerQueue.isEmpty()){
            broker = resetBrokerQueue();
        }

        return broker;
    }

    public BrokerWeight resetBrokerQueue(){
        brokers.forEach(bw -> {
            bw.resetCurrentWeight();
            brokerQueue.add(bw);
        });

        BrokerWeight broker = brokerQueue.peek();
        broker.addCurrentWeight(value);
        brokerQueue.add(broker);

        return broker;
    }
}

