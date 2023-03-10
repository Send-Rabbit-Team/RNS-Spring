package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.status.AuthPhoneNumberStatus;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Member;
import com.srt.message.domain.SenderNumber;
import com.srt.message.domain.redis.AuthPhoneNumber;
import com.srt.message.dto.sender_number.get.GetSenderNumberRes;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberReq;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberRes;
import com.srt.message.repository.ContactRepository;
import com.srt.message.repository.MemberRepository;
import com.srt.message.repository.SenderNumberRepository;
import com.srt.message.repository.redis.AuthPhoneNumberRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SenderNumberService {
    private final SenderNumberRepository senderNumberRepository;

    private final AuthPhoneNumberRedisRepository authPhoneNumberRedisRepository;
    private final MemberRepository memberRepository;
    private final ContactRepository contactRepository;

    // 발신자 등록
    @Transactional(readOnly = false)
    public RegisterSenderNumberRes registerSenderNumber(Long memberId, RegisterSenderNumberReq registerSenderNumberReq){
        String phoneNumber = registerSenderNumberReq.getPhoneNumber();

        if(senderNumberRepository.findByMemberIdAndPhoneNumberAndStatus(memberId, phoneNumber, BaseStatus.ACTIVE).isPresent())
            throw new BaseException(ALREADY_EXIST_PHONE_NUMBER);

        // 휴대폰 인증 확인
        AuthPhoneNumber authPhoneNumber = authPhoneNumberRedisRepository.findById(phoneNumber)
                .orElseThrow(() -> new BaseException(NOT_AUTH_PHONE_NUMBER));

        if(authPhoneNumber.getAuthPhoneNumberStatus() != AuthPhoneNumberStatus.CONFIRM)
            throw new BaseException(NOT_AUTH_PHONE_NUMBER);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));

        SenderNumber senderNumber = RegisterSenderNumberReq.toEntity(registerSenderNumberReq, member);

        // 발신자 차단 번호 생성
        senderNumber.createBlockNumber();

        senderNumberRepository.save(senderNumber);

        return RegisterSenderNumberRes.toDto(senderNumber);
    }

    // 발신자 조회(페이징 O)
    public PageResult<GetSenderNumberRes> getPageSenderNumber(long memberId, int page) {
        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("updatedAt").descending());

        Page<SenderNumber> senderNumberPage = senderNumberRepository.findAllSenderNumber(memberId, BaseStatus.ACTIVE, pageRequest);
        if (senderNumberPage.isEmpty())
            throw new BaseException(NOT_EXIST_SENDER_NUMBER);

        Page<GetSenderNumberRes> senderNumberRes = senderNumberPage.map(s -> GetSenderNumberRes.toDto(s));

        return new PageResult<>(senderNumberRes);
    }

    // 발신자 조회(페이징 X)
    public List<GetSenderNumberRes> getAllSenderNumber(long memberId) {
        List<SenderNumber> senderNumberList = senderNumberRepository.findByMemberIdAndStatusOrderByUpdatedAtDesc(memberId, BaseStatus.ACTIVE);
        if (senderNumberList.isEmpty())
            throw new BaseException(NOT_EXIST_SENDER_NUMBER);
        return senderNumberList.stream().map(senderNumber -> GetSenderNumberRes.toDto(senderNumber)).collect(Collectors.toList());
    }


    // 발신자 삭제
    @Transactional(readOnly = false)
    public void deleteSenderNumber(long senderNumberId, long memberId) {
        SenderNumber senderNumber = senderNumberRepository.findByIdAndStatus(senderNumberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_SENDER_NUMBER));

        Member member = memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));

        if (senderNumber.getMember().getId() != memberId)
            throw new BaseException(NOT_AUTH_MEMBER);

        if (member.getPhoneNumber().equals(senderNumber.getPhoneNumber()))
            throw new BaseException(UNDELETABLE_SENDER_NUMBER);

        senderNumber.changeStatusInActive();
        senderNumberRepository.save(senderNumber);
    }


    public String getBlockNumber(long senderNumberId, long memberId){
        SenderNumber senderNumber =  senderNumberRepository.findByIdAndStatus(senderNumberId, BaseStatus.ACTIVE)
                .orElseThrow(()-> new BaseException(NOT_EXIST_SENDER_NUMBER));

        return senderNumber.getBlockNumber();
    }
}
