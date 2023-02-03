package com.srt.message.utils.algorithm;

public class BrokerWeight<Broker> {
    private Broker broker;
    private int weight;

    public BrokerWeight(Broker broker, int weight) {
        this.broker = broker;
        this.weight = weight;
    }

    public Broker getBroker() {
        return broker;
    }

    public int getWeight() {
        return weight;
    }
}
