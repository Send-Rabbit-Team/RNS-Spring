package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.response.BaseResponse;
import com.srt.message.config.status.AuthPhoneNumberStatus;
import com.srt.message.domain.SenderNumber;
import com.srt.message.domain.redis.AuthPhoneNumber;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberReq;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberRes;
import com.srt.message.repository.SenderNumberRepository;
import com.srt.message.repository.redis.AuthPhoneNumberRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.srt.message.config.response.BaseResponseStatus.ALREADY_EXIST_PHONE_NUMBER;
import static com.srt.message.config.response.BaseResponseStatus.NOT_AUTH_PHONE_NUMBER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SenderNumberService {
    private final SenderNumberRepository senderNumberRepository;

    private final AuthPhoneNumberRedisRepository authPhoneNumberRedisRepository;

    // 발신자 등록
    @Transactional(readOnly = false)
    public RegisterSenderNumberRes registerSenderNumber(RegisterSenderNumberReq registerSenderNumberReq){
        String phoneNumber = registerSenderNumberReq.getPhoneNumber();

        if(senderNumberRepository.findByPhoneNumber(phoneNumber).isPresent())
            throw new BaseException(ALREADY_EXIST_PHONE_NUMBER);

        // 휴대폰 인증 확인
        AuthPhoneNumber authPhoneNumber = authPhoneNumberRedisRepository.findById(phoneNumber)
                .orElseThrow(() -> new BaseException(NOT_AUTH_PHONE_NUMBER));

        if(authPhoneNumber.getAuthPhoneNumberStatus() != AuthPhoneNumberStatus.CONFIRM)
            throw new BaseException(NOT_AUTH_PHONE_NUMBER);

        SenderNumber senderNumber = RegisterSenderNumberReq.toEntity(registerSenderNumberReq);

        senderNumberRepository.save(senderNumber);

        return RegisterSenderNumberRes.toDto(senderNumber);
    }

}
