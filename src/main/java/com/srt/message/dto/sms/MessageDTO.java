package com.srt.message.dto.sms;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MessageDTO {
    String to;
    String content;
}
