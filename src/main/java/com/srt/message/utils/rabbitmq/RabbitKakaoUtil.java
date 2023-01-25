package com.srt.message.utils.rabbitmq;

public class RabbitKakaoUtil {

        // KAKAO EXCHANGE
        public static final String KAKAO_WORK_EXCHANGE_NAME = "dx.kakao.work";
        public static final String KAKAO_DLX_EXCHANGE_NAME = "dx.kakao.wait";
        public static final String KAKAO_RECEIVE_EXCHANGE_NAME = "dx.kakao.receive";

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
