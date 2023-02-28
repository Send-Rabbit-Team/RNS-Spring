package com.srt.message.dto.block.get;

import com.srt.message.domain.Block;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetBlockRes {
    private List<String> receiveNumbers;
}
