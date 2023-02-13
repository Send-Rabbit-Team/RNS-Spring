package com.srt.message.dto.kakao_pay;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentApproveRes {
    private String aid;
    private String tid;
    private String cid;
    private String sid;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;
    private Amount amount;
    private CardInfo card_info;
    private String item_name;
    private String item_code;
    private String quantity;
    private LocalDateTime created_at;
    private LocalDateTime approved_at;
    private String payload;

}
