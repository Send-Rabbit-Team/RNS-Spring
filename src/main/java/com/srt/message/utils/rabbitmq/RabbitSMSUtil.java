package com.srt.message.utils.rabbitmq;

public class RabbitSMSUtil {
    public static final String EXCHANGE_NAME = "sms";

    // KT
    public static final String KT_QUEUE_NAME = "sms.kt";
    public static final String KT_ROUTING_KEY = "sms.kt";

    // SKT
    public static final String SKT_QUEUE_NAME = "sms.skt";
    public static final String SKT_ROUTING_KEY = "sms.skt";

    // LG
    public static final String LG_QUEUE_NAME = "sms.lg";
    public static final String LG_ROUTING_KEY = "sms.lg";
    public static final long DELAY_TIME = 3000;
}
