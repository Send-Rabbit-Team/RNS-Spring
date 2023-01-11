package com.srt.message.dto.company;

import com.srt.message.domain.Company;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CompanyDto {
    @ApiModelProperty(
            example = "카카오 엔터프라이즈"
    )
    private String companyName;

    @ApiModelProperty(
            example = "12345678901"
    )
    private String bsNum;

    public static CompanyDto toDto(Company company){
        return CompanyDto.builder()
                .companyName(company.getCompanyName())
                .bsNum(company.getBsNum())
                .build();
    }
}
