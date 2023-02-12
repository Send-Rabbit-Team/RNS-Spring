package com.srt.message.dlx;

import java.util.Date;
import java.util.List;

public class RabbitmqHeaderXDeath {
    private int count;
    private String exchange;
    private String queue;
    private String reason;
    private List<String> routingKeys;
    private Date time;

    public int getCount() {
        return count;
    }

    public String getExchange() {
        return exchange;
    }

    public String getQueue() {
        return queue;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getRoutingKeys() {
        return routingKeys;
    }

    public Date getTime() {
        return time;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setRoutingKeys(List<String> routingKeys) {
        this.routingKeys = routingKeys;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
