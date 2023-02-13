package com.srt.message.service.kakao;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.config.status.ReserveStatus;
import com.srt.message.domain.*;
import com.srt.message.dto.kakao_message.BrokerKakaoMessageDto;
import com.srt.message.dto.kakao_message.KakaoMessageDto;
import com.srt.message.dto.kakao_message_reserve.get.GetKakaoMessageReserveContactRes;
import com.srt.message.dto.kakao_message_reserve.get.GetKakaoMessageReserveRes;
import com.srt.message.dto.message.BrokerMessageDto;
import com.srt.message.dto.message.SMSMessageDto;
import com.srt.message.repository.*;
import com.srt.message.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoMessageReserveService {
    private final ReserveMessageRepository reserveMessageRepository;
    private final KakaoMessageRepository kakaoMessageRepository;
    private final MemberRepository memberRepository;
    private final ReserveKakaoMessageRepository reserveKakaoMessageRepository;
    private final ReserveMessageContactRepository reserveMessageContactRepository;

    private final SchedulerService schedulerService;

    // 알림톡 예약발송
    public String reserveKakaoMessage(BrokerKakaoMessageDto brokerKakaoMessageDto){
        KakaoMessageDto messageDto = brokerKakaoMessageDto.getKakaoMessageDto();

        ReserveKakaoMessage reserveMessage = ReserveKakaoMessage.builder()
                .kakaoMessage(brokerKakaoMessageDto.getKakaoMessage())
                .cronExpression(messageDto.getCronExpression())
                .cronText(messageDto.getCronText())
                .reserveStatus(ReserveStatus.PROCESSING)
                .build();

        reserveMessage = reserveKakaoMessageRepository.save(reserveMessage);

        // 예약 대상자 정보 추가
        for(Contact contact: brokerKakaoMessageDto.getContacts()){
            ReserveMessageContact reserveMessageContact = ReserveMessageContact.builder()
                    .reserveKakaoMessage(reserveMessage)
                    .contact(contact)
                    .build();

            reserveMessageContactRepository.save(reserveMessageContact);

            brokerKakaoMessageDto.getKakaoMessageDto().setTo(contact.getPhoneNumber());
        }

        schedulerService.registerKakao(brokerKakaoMessageDto, reserveMessage.getId());

        return "예약성공";
    }

    // 알림톡 예약 취소
    public GetKakaoMessageReserveRes cancelKakaoMessageReserve(long memberId, long messageId) {
        // NOT_EXIST_MEMBER
        getExistMember(memberId);

        // NOT_EXIST_MESSAGE
        KakaoMessage kakaoMessage = kakaoMessageRepository.findById(messageId)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MESSAGE));

        // NOT_MATCH_MEMBER
        if (kakaoMessage.getMember().getId() != memberId)
            throw new BaseException(NOT_MATCH_MEMBER);

        // NOT_RESERVE_MESSAGE
        ReserveKakaoMessage reserveKakaoMessage = reserveKakaoMessageRepository.findByKakaoMessageId(messageId)
                .orElseThrow(() -> new BaseException(NOT_RESERVE_MESSAGE));

        // ALREADY_CANCEL_RESERVE
        if(reserveKakaoMessage.getStatus() == BaseStatus.INACTIVE)
            throw new BaseException(ALREADY_CANCEL_RESERVE);

        schedulerService.remove(messageId);

        reserveKakaoMessage.changeReserveStatusStop();
        return GetKakaoMessageReserveRes.toDto(reserveKakaoMessageRepository.save(reserveKakaoMessage));
    }

    public PageResult<GetKakaoMessageReserveRes> getKakaoMessageReserveList(int page, long memberId) {
        getExistMember(memberId);
        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("id").descending());
        Page<GetKakaoMessageReserveRes> getKakaoMessageReserveResPage = reserveKakaoMessageRepository.findByMemberId(pageRequest, memberId).map(GetKakaoMessageReserveRes::toDto);;
        return new PageResult<>(getKakaoMessageReserveResPage);
    }

    public List<GetKakaoMessageReserveContactRes> getKakaoMessageReserveContactList(long memberId, long messageId) {
        getExistMember(memberId);
        ReserveKakaoMessage reserveKakaoMessage = reserveKakaoMessageRepository.findByKakaoMessageId(messageId).orElseThrow(() -> new BaseException(NOT_RESERVE_MESSAGE));
        List<ReserveMessageContact> reserveMessageContactList = reserveMessageContactRepository.findAllByReserveKakaoMessage(reserveKakaoMessage);
        return reserveMessageContactList.stream().map(reserveMessageContact -> GetKakaoMessageReserveContactRes.toDto(reserveMessageContact)).collect(Collectors.toList());
    }


    private Member getExistMember(long memberId) {
        return memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
    }
}
