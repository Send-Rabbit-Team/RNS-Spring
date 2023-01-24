package com.srt.message.utils.rabbitmq;

public class RabbitSMSUtil {

        // SMS EXCHANGE
        public static final String SMS_EXCHANGE_NAME = "dx.sms.work";
        public static final String DLX_EXCHANGE_NAME = "dx.sms.wait";
        public static final String RECEIVE_EXCHANGE_NAME = "dx.sms.receive";

        // KAKAO EXCHANGE
        public static final String KAKAO_WORK_EXCHANGE_NAME = "dx.kakao.work";
        public static final String KAKAO_DLX_EXCHANGE_NAME = "dx.kakao.wait";
        public static final String KAKAO_RECEIVE_EXCHANGE_NAME = "dx.kakao.receive";

        // KT
        public static final String KT_WORK_QUEUE_NAME = "q.sms.kt.work";
        public static final String KT_WORK_ROUTING_KEY = "sms.send.kt";

        public static final String KT_RECEIVE_QUEUE_NAME = "q.sms.kt.receive";
        public static final String KT_RECEIVE_ROUTING_KEY = "sms.receive.kt";

        public static final String KT_WAIT_QUEUE_NAME = "q.sms.kt.wait";
        public static final String KT_WAIT_ROUTING_KEY = "sms.wait.kt";

        // SKT
        public static final String SKT_WORK_QUEUE_NAME = "q.sms.skt.work";
        public static final String SKT_WORK_ROUTING_KEY = "sms.send.skt";

        public static final String SKT_RECEIVE_QUEUE_NAME = "q.sms.skt.receive";
        public static final String SKT_RECEIVE_ROUTING_KEY = "sms.receive.skt";

        public static final String SKT_WAIT_QUEUE_NAME = "q.sms.skt.wait";
        public static final String SKT_WAIT_ROUTING_KEY = "sms.wait.skt";

        // LG
        public static final String LG_WORK_QUEUE_NAME = "q.sms.lg.work";
        public static final String LG_WORK_ROUTING_KEY = "sms.send.lg";

        public static final String LG_RECEIVE_QUEUE_NAME = "q.sms.lg.receive";
        public static final String LG_RECEIVE_ROUTING_KEY = "sms.receive.lg";

        public static final String LG_WAIT_QUEUE_NAME = "q.sms.lg.wait";
        public static final String LG_WAIT_ROUTING_KEY = "sms.wait.lg";

        // KE
        public static final String KE_WORK_QUEUE_NAME = "q.kakao.ke.work";
        public static final String KE_WORK_ROUTING_KEY = "kakao.send.ke";

        public static final String KE_RECEIVE_QUEUE_NAME = "q.kakao.ke.receive";
        public static final String KE_RECEIVE_ROUTING_KEY = "kakao.receive.ke";

        public static final String KE_WAIT_QUEUE_NAME = "q.kakao.ke.wait";
        public static final String KE_WAIT_ROUTING_KEY = "kakao.wait.ke";

        // CNS
        public static final String CNS_WORK_QUEUE_NAME = "q.kakao.cns.work";
        public static final String CNS_WORK_ROUTING_KEY = "kakao.send.cns";

        public static final String CNS_RECEIVE_QUEUE_NAME = "q.kakao.cns.receive";
        public static final String CNS_RECEIVE_ROUTING_KEY = "kakao.receive.cns";

        public static final String CNS_WAIT_QUEUE_NAME = "q.kakao.cns.wait";
        public static final String CNS_WAIT_ROUTING_KEY = "kakao.wait.cns";

        public static final long DELAY_TIME = 3000;


}
