package com.srt.message.dto.company;

import com.srt.message.domain.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CompanyDto {
    private String companyName;

    private String bsNum;

    public static CompanyDto toDto(Company company){
        return CompanyDto.builder()
                .companyName(company.getCompanyName())
                .bsNum(company.getBsNum())
                .build();
    }
}
