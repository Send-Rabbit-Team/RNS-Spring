package com.srt.message.utils.algorithm;

public class BrokerWeight<T> {
    private T broker;
    private int weight;

    public BrokerWeight(T broker, int weight) {
        this.broker = broker;
        this.weight = weight;
    }

    public T getBroker() {
        return broker;
    }

    public int getWeight() {
        return weight;
    }
}
