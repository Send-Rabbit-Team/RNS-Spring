package com.srt.message.utils.rabbitmq;

public class RabbitSMSUtil {
        public static final int WORK_TTL = 5 * 1000;

        public static final String SMS_EXCHANGE_NAME = "dx.sms.work";
        public static final String SENDER_EXCHANGE_NAME = "dx.sms.sender";
        public static final String WAIT_EXCHANGE_NAME = "dx.sms.wait";
        public static final String RECEIVE_EXCHANGE_NAME = "dx.sms.receive";
        public static final String DEAD_EXCHANGE_NAME = "dx.sms.dead";

        // KT
        public static final String KT_WORK_QUEUE_NAME = "q.sms.kt.work";
        public static final String KT_WORK_ROUTING_KEY = "sms.work.kt";

        public static final String KT_RECEIVE_QUEUE_NAME = "q.sms.kt.receive";
        public static final String KT_RECEIVE_ROUTING_KEY = "sms.receive.kt";

        public static final String KT_SENDER_QUEUE_NAME = "q.sms.kt.sender";
        public static final String KT_SENDER_ROUTING_KEY = "sms.sender.kt";

        public static final String KT_WAIT_QUEUE_NAME = "q.sms.kt.wait";
        public static final String KT_WAIT_ROUTING_KEY = "sms.wait.kt";

        public static final String KT_DAED_QUEUE_NAME = "q.sms.kt.dead";
        public static final String KT_DEAD_ROUTING_KEY = "sms.dead.kt";

        // SKT
        public static final String SKT_WORK_QUEUE_NAME = "q.sms.skt.work";
        public static final String SKT_WORK_ROUTING_KEY = "sms.work.skt";

        public static final String SKT_RECEIVE_QUEUE_NAME = "q.sms.skt.receive";
        public static final String SKT_RECEIVE_ROUTING_KEY = "sms.receive.skt";

        public static final String SKT_SENDER_QUEUE_NAME = "q.sms.skt.sender";
        public static final String SKT_SENDER_ROUTING_KEY = "sms.sender.skt";

        public static final String SKT_WAIT_QUEUE_NAME = "q.sms.skt.wait";
        public static final String SKT_WAIT_ROUTING_KEY = "sms.wait.skt";

        public static final String SKT_DAED_QUEUE_NAME = "q.sms.skt.dead";
        public static final String SKT_DEAD_ROUTING_KEY = "sms.dead.skt";

        // LG
        public static final String LG_WORK_QUEUE_NAME = "q.sms.lg.work";
        public static final String LG_WORK_ROUTING_KEY = "sms.work.lg";

        public static final String LG_RECEIVE_QUEUE_NAME = "q.sms.lg.receive";
        public static final String LG_RECEIVE_ROUTING_KEY = "sms.receive.lg";

        public static final String LG_SENDER_QUEUE_NAME = "q.sms.lg.sender";
        public static final String LG_SENDER_ROUTING_KEY = "sms.sender.lg";

        public static final String LG_WAIT_QUEUE_NAME = "q.sms.lg.wait";
        public static final String LG_WAIT_ROUTING_KEY = "sms.wait.lg";

        public static final String LG_DAED_QUEUE_NAME = "q.sms.lg.dead";
        public static final String LG_DEAD_ROUTING_KEY = "sms.dead.lg";

        public static final long DELAY_TIME = 3000;
}
