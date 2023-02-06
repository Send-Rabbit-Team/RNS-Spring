package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.config.type.ButtonType;
import com.srt.message.config.type.KmsgSearchType;
import com.srt.message.domain.Member;
import com.srt.message.dto.kakao_message.get.GetKakaoMessageRes;
import com.srt.message.dto.kakao_message_result.get.GetKakaoMessageResultRes;
import com.srt.message.repository.KakaoMessageRepository;
import com.srt.message.repository.KakaoMessageResultRepository;
import com.srt.message.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.NOT_EXIST_MEMBER;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoMessageResultService {
    private final KakaoMessageResultRepository kakaoMessageResultRepository;
    private final KakaoMessageRepository kakaoMessageRepository;
    private final MemberRepository memberRepository;

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

        Page<GetKakaoMessageRes> kakaoMessagePage = null;
        if (searchType == KmsgSearchType.NUMBER) {
            kakaoMessagePage = kakaoMessageRepository.findKakaoMessageByContactNumber(pageRequest, memberId, keyword).map(GetKakaoMessageRes::toDto);
        } else if (searchType == KmsgSearchType.MEMO) {
            kakaoMessagePage = kakaoMessageRepository.findKakaoMessageByContactMemo(pageRequest, memberId, keyword).map(GetKakaoMessageRes::toDto);
        } else if (searchType == KmsgSearchType.CONTENT) {
            kakaoMessagePage = kakaoMessageRepository.findKakaoMessageByMessageContent(pageRequest, memberId, keyword).map(GetKakaoMessageRes::toDto);
        }
        return new PageResult<>(kakaoMessagePage);
    }

    public List<GetKakaoMessageResultRes> getKakaoMessageResult(Long memberId, Long messageId) {
        getExistMember(memberId);
        return kakaoMessageResultRepository.findKakaoMessageResultByKakaoMessageId(messageId)
                .stream().map(kakaoMessageResult -> GetKakaoMessageResultRes.toDto(kakaoMessageResult)).collect(Collectors.toList());
    }

    private Member getExistMember(long memberId) {
        return memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
    }
}
