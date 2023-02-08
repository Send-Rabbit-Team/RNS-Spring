package com.srt.message.utils.algorithm;

public class BrokerWeight<T> {
    private T broker;
    private int totalWeight;
    private double currentWeight = 0;

    public BrokerWeight(T broker, int totalWeight) {
        this.broker = broker;
        this.totalWeight = totalWeight;
    }

    public T getBroker() {
        return broker;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public double getCurrentWeight(){return currentWeight; }

    public void addCurrentWeight(double weight){
        this.currentWeight += weight;
    }

    public void resetCurrentWeight(){
        this.currentWeight = 0;
    }
}
