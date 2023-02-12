package com.srt.message.utils.rabbitmq;

public class RabbitKakaoUtil {

        // TTL
        public static final int KAKAO_WORK_TTL = 10 * 1000;
        public static final int KAKAO_WAIT_TTL = 5 * 1000;

        // KAKAO EXCHANGE
        public static final String KAKAO_WORK_EXCHANGE_NAME = "dx.kakao.work";
        public static final String KAKAO_SENDER_EXCHANGE_NAME = "dx.kakao.sender";
        public static final String KAKAO_WAIT_EXCHANGE_NAME = "dx.kakao.wait";
        public static final String KAKAO_RECEIVE_EXCHANGE_NAME = "dx.kakao.receive";
        public static final String KAKAO_DEAD_EXCHANGE_NAME = "dx.kakao.dead";

        // KE
        public static final String KE_WORK_QUEUE_NAME = "q.kakao.ke.work";
        public static final String KE_WORK_ROUTING_KEY = "kakao.work.ke";

        public static final String KE_RECEIVE_QUEUE_NAME = "q.kakao.ke.receive";
        public static final String KE_RECEIVE_ROUTING_KEY = "kakao.receive.ke";

        public static final String KE_SENDER_QUEUE_NAME = "q.kakao.ke.sender";
        public static final String KE_SENDER_ROUTING_KEY = "kakao.sender.ke";

        public static final String KE_WAIT_QUEUE_NAME = "q.kakao.ke.wait";
        public static final String KE_WAIT_ROUTING_KEY = "kakao.wait.ke";

        public static final String KE_DEAD_QUEUE_NAME = "q.kakao.ke.dead";
        public static final String KE_DEAD_ROUTING_KEY = "kakao.dead.ke";

        // CNS
        public static final String CNS_WORK_QUEUE_NAME = "q.kakao.cns.work";
        public static final String CNS_WORK_ROUTING_KEY = "kakao.work.cns";

        public static final String CNS_RECEIVE_QUEUE_NAME = "q.kakao.cns.receive";
        public static final String CNS_RECEIVE_ROUTING_KEY = "kakao.receive.cns";

        public static final String CNS_SENDER_QUEUE_NAME = "q.kakao.cns.sender";
        public static final String CNS_SENDER_ROUTING_KEY = "kakao.sender.cns";

        public static final String CNS_WAIT_QUEUE_NAME = "q.kakao.cns.wait";
        public static final String CNS_WAIT_ROUTING_KEY = "kakao.wait.cns";

        public static final String CNS_DEAD_QUEUE_NAME = "q.kakao.cns.dead";
        public static final String CNS_DEAD_ROUTING_KEY = "kakao.dead.cns";


}
