package com.srt.message.dto.kakao_pay;

import lombok.Getter;

@Getter
public class Amount {
    private String total;
    private String tax_free;
    private String vat;
    private String point;
    private String discount;
    private String green_deposit;
}
