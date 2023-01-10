package com.srt.message.service;

import com.srt.message.config.response.BaseResponse;
import com.srt.message.domain.SenderNumber;
import com.srt.message.repository.SenderNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SenderNumberService {
    private final SenderNumberRepository senderNumberRepository;


    // audit 저장 확인 용 테스트 저장
    // 나중에 무조건 지워야 함
    @Transactional(readOnly = false)
    public BaseResponse<String> testSave(){
        SenderNumber senderNumber = SenderNumber.builder()
                .phoneNumber("01012341234")
                .build();

        senderNumberRepository.save(senderNumber);

        return new BaseResponse<>("OK");
    }
}
