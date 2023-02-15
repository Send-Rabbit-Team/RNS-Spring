package com.srt.message.dto.message_image.get;

import com.srt.message.domain.MessageImage;
import com.srt.message.dto.message.get.GetMessageRes;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetMessageImageRes {
    private List<String> images;

    public static GetMessageImageRes toDto(List<MessageImage> messageImages){
        List<String> images = messageImages.stream().map(m -> m.getData())
                .collect(Collectors.toList());

        return GetMessageImageRes.builder().images(images).build();
    }
}
