package com.srt.message.utils.rabbitmq;

public class RabbitSMSUtil {
        public static final String SMS_EXCHANGE_NAME = "dx.sms.work";
        public static final String RECEIVE_EXCHANGE_NAME = "dx.sms.receive";

        // KT
        public static final String KT_WORK_QUEUE_NAME = "q.sms.kt.work";
        public static final String KT_WORK_ROUTING_KEY = "sms.send.kt";

        public static final String KT_RECEIVE_QUEUE_NAME = "q.sms.kt.receive";
        public static final String KT_RECEIVE_ROUTING_KEY = "sms.receive.kt";

        // SKT
        public static final String SKT_WORK_QUEUE_NAME = "q.sms.skt.work";
        public static final String SKT_WORK_ROUTING_KEY = "sms.send.skt";

        public static final String SKT_RECEIVE_QUEUE_NAME = "q.sms.skt.receive";
        public static final String SKT_RECEIVE_ROUTING_KEY = "sms.receive.skt";

        // LG
        public static final String LG_WORK_QUEUE_NAME = "q.sms.lg.work";
        public static final String LG_WORK_ROUTING_KEY = "sms.send.lg";

        public static final String LG_RECEIVE_QUEUE_NAME = "q.sms.lg.receive";
        public static final String LG_RECEIVE_ROUTING_KEY = "sms.receive.lg";
        public static final long DELAY_TIME = 3000;
}
