package com.srt.message.dlx;

import java.util.*;

public class RabbitmqHeader {
    private static final String KEY_WORD_QUEUE_WAIT = "wait";
    private List<RabbitmqHeaderXDeath> xDeaths = new ArrayList<>();
    private String xFirstDeathExchange;
    private String xFirstDeathQueue;
    private String xFirstDeathReason;

    public RabbitmqHeader(Map<String, Object> headers){
        if(headers != null){
            Optional<Object> xFirstDeathExchange = Optional.ofNullable(headers.get("x-first-death-exchange"));
            Optional<Object> xFirstDeathQueue = Optional.ofNullable(headers.get("x-first-death-queue"));
            Optional<Object> xFirstDeathReason  = Optional.ofNullable(headers.get("x-first-death-reason"));

            xFirstDeathExchange.ifPresent(s -> this.setXFirstDeathExchange(s.toString()));
            xFirstDeathQueue.ifPresent(s -> this.setXFirstDeathQueue(s.toString()));
            xFirstDeathReason.ifPresent(s -> this.setXFirstDeathReason(s.toString()));

            List<Map<String,Object>> xDeathHeaders = (List<Map<String, Object>>) headers.get("x-death");

            if(xDeathHeaders != null){
                for(Map<String, Object> x : xDeathHeaders){
                    RabbitmqHeaderXDeath headerXDeath = new RabbitmqHeaderXDeath();
                    Optional<Object> reason = Optional.ofNullable(x.get("reason"));
                    Optional<Object> count = Optional.ofNullable(x.get("count"));
                    Optional<Object> exchange = Optional.ofNullable(x.get("exchange"));
                    Optional<Object> queue = Optional.ofNullable(x.get("queue"));
                    Optional<Object> routingKeys = Optional.ofNullable(x.get("routing-keys"));
                    Optional<Object> time = Optional.ofNullable(x.get("time"));

                    reason.ifPresent(s -> headerXDeath.setReason(s.toString()));
                    count.ifPresent(s -> headerXDeath.setCount(Integer.parseInt(s.toString())));
                    exchange.ifPresent(s -> headerXDeath.setExchange(s.toString()));
                    queue.ifPresent(s -> headerXDeath.setQueue(s.toString()));
                    routingKeys.ifPresent(r -> {
                        List<String> listR = (List<String>) r;
                        headerXDeath.setRoutingKeys(listR);
                    });
                    time.ifPresent(d -> headerXDeath.setTime((Date) d));

                    xDeaths.add(headerXDeath);
                }
            }
        }
    }

    // wait queue에서 가져옴
    public int getFailedRetryCount(){
        int deadCount = 0;
        for(RabbitmqHeaderXDeath xDeath: xDeaths){
            if(xDeath.getExchange().toLowerCase().endsWith(KEY_WORD_QUEUE_WAIT)
            && xDeath.getQueue().toLowerCase().endsWith(KEY_WORD_QUEUE_WAIT))
               deadCount += xDeath.getCount();
        }
        return deadCount;
    }

    public void setXFirstDeathExchange(String xFirstDeathExchange) {
        this.xFirstDeathExchange = xFirstDeathExchange;
    }

    public void setXFirstDeathQueue(String xFirstDeathQueue) {
        this.xFirstDeathQueue = xFirstDeathQueue;
    }

    public void setXFirstDeathReason(String xFirstDeathReason) {
        this.xFirstDeathReason = xFirstDeathReason;
    }
}
