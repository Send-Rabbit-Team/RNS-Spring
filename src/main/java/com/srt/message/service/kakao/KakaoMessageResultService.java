package com.srt.message.service.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.config.type.ButtonType;
import com.srt.message.config.type.KmsgSearchType;
import com.srt.message.domain.Contact;
import com.srt.message.domain.KakaoBroker;
import com.srt.message.domain.KakaoMessage;
import com.srt.message.domain.Member;
import com.srt.message.domain.redis.RKakaoMessageResult;
import com.srt.message.dto.kakao_message.get.GetKakaoMessageRes;
import com.srt.message.dto.kakao_message_result.get.GetKakaoMessageResultListRes;
import com.srt.message.dto.kakao_message_result.get.GetKakaoMessageResultRes;
import com.srt.message.repository.*;
import com.srt.message.repository.redis.RedisHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoMessageResultService {
    private final KakaoBrokerRepository kakaoBrokerRepository;
    private final ContactRepository contactRepository;
    private final ObjectMapper objectMapper;
    private final KakaoMessageResultRepository kakaoMessageResultRepository;
    private final KakaoMessageRepository kakaoMessageRepository;
    private final MemberRepository memberRepository;
    private final RedisHashRepository redisHashRepository;

    public PageResult<GetKakaoMessageRes> getAllKakaoMessage(int page, Long memberId) {
        getExistMember(memberId);
        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("id").descending());
        Page<GetKakaoMessageRes> kakaoMessagePage = kakaoMessageRepository.findAllKakaoMessage(pageRequest, memberId).map(GetKakaoMessageRes::toDto);
        return new PageResult<>(kakaoMessagePage);
    }

    public PageResult<GetKakaoMessageRes> getKakaoMessageByButtonType(int page, Long memberId, ButtonType buttonType) {
        getExistMember(memberId);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        Page<GetKakaoMessageRes> kakaoMessagePage = kakaoMessageRepository.findKakaoMessageByButtonType(pageRequest, memberId, buttonType).map(GetKakaoMessageRes::toDto);
        return new PageResult<>(kakaoMessagePage);
    }

    public PageResult<GetKakaoMessageRes> getSearchKakaoMessage(int page, Long memberId, KmsgSearchType searchType, String keyword) {
        getExistMember(memberId);
        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("id").descending());

        if (searchType == KmsgSearchType.NUMBER) {
            return new PageResult<>(kakaoMessageRepository.findKakaoMessageByContactNumber(pageRequest, memberId, keyword).map(GetKakaoMessageRes::toDto));
        } else if (searchType == KmsgSearchType.MEMO) {
            return new PageResult<>(kakaoMessageRepository.findKakaoMessageByContactMemo(pageRequest, memberId, keyword).map(GetKakaoMessageRes::toDto));
        } else if (searchType == KmsgSearchType.TITLE) {
            return new PageResult<>(kakaoMessageRepository.findKakaoMessageByMessageTitle(pageRequest, memberId, keyword).map(GetKakaoMessageRes::toDto));
        } else if (searchType == KmsgSearchType.CONTENT) {
            return new PageResult<>(kakaoMessageRepository.findKakaoMessageByMessageContent(pageRequest, memberId, keyword).map(GetKakaoMessageRes::toDto));
        }
        return null;
    }

    public GetKakaoMessageResultListRes getKakaoMessageResult(Long memberId, Long messageId) throws JsonProcessingException {
        getExistMember(memberId);
        GetKakaoMessageResultListRes getKakaoMessageResultListRes = new GetKakaoMessageResultListRes();

        String statusKey = "message.status." + messageId;
        Map<String,String> statusMap = redisHashRepository.findAll(statusKey);
        if(!statusMap.isEmpty()){ // 상태 정보가 들어있을 경우 REDIS로 조회
            for(Map.Entry<String, String> entry: statusMap.entrySet()){
                String rMessageResultJson = entry.getValue();
                RKakaoMessageResult rKakaoMessageResult = objectMapper.readValue(rMessageResultJson, RKakaoMessageResult.class);

                getKakaoMessageResultListRes.addKakaoMessageResultResList(toGetKakaoMessageResultRes(rKakaoMessageResult));
                getKakaoMessageResultListRes.addKakaoBrokerCount(getExistKakaoBroker(rKakaoMessageResult.getKakaoBrokerId()).getName());
                getKakaoMessageResultListRes.addMessageStatusCount(rKakaoMessageResult.getMessageStatus());
            }
        }else {
            kakaoMessageResultRepository.findKakaoMessageResultByKakaoMessageId(messageId).forEach(kakaoMessageResult -> {
                getKakaoMessageResultListRes.addKakaoMessageResultResList(GetKakaoMessageResultRes.toDto(kakaoMessageResult));
                getKakaoMessageResultListRes.addKakaoBrokerCount(kakaoMessageResult.getKakaoBroker().getName());
                getKakaoMessageResultListRes.addMessageStatusCount(kakaoMessageResult.getMessageStatus());
            });
        }
        return getKakaoMessageResultListRes;
    }

    private GetKakaoMessageResultRes toGetKakaoMessageResultRes(RKakaoMessageResult rKakaoMessageResult) {
        return GetKakaoMessageResultRes.builder()
                .kakaoMessageId(rKakaoMessageResult.getKakaoMessageId())
                .contactNumber(getExistContact(rKakaoMessageResult.getContactId()).getPhoneNumber())
                .contactMemo(getExistContact(rKakaoMessageResult.getContactId()).getMemo())
                .contactGroup(getExistContact(rKakaoMessageResult.getContactId()).getContactGroup().getName())
                .kakaoBrokerName(getExistKakaoBroker(rKakaoMessageResult.getKakaoBrokerId()).getName())
                .messageStatus(rKakaoMessageResult.getMessageStatus())
                .createdAt(LocalDateTime.now().toString())
                .build();
    }

    private Member getExistMember(long memberId) {
        return memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
    }

    private Contact getExistContact(long contactId) {
        return contactRepository.findByIdAndStatus(contactId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_CONTACT));
    }

    private KakaoBroker getExistKakaoBroker(long brokerId) {
        return kakaoBrokerRepository.findById(brokerId)
                .orElseThrow(() -> new BaseException(NOT_EXIST_BROKER));
    }
}
