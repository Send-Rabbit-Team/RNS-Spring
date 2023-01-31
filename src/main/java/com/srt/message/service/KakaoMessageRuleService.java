package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.KakaoMessageRule;
import com.srt.message.domain.Member;
import com.srt.message.repository.KakaoMessageRuleRepository;
import com.srt.message.repository.MemberRepository;
import com.srt.message.dto.kakaoMessageRule.get.GetKakaoMessageRuleRes;
import com.srt.message.dto.kakaoMessageRule.patch.PatchKakaoMessageRuleReq;
import com.srt.message.dto.kakaoMessageRule.post.PostKakaoMessageRuleReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.NOT_EXIST_MEMBER;
import static com.srt.message.config.response.BaseResponseStatus.NOT_EXIST_MESSAGE_RULE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoMessageRuleService {
    private final KakaoMessageRuleRepository kakaoMessageRuleRepository;
    private final MemberRepository memberRepository;

    public List<GetKakaoMessageRuleRes> getKakaoMessageRule(long memberId) {
        List<KakaoMessageRule> kakaoMessageRuleList = kakaoMessageRuleRepository.findByMemberIdAndStatus(memberId, BaseStatus.ACTIVE);

        if (kakaoMessageRuleList.isEmpty())
            throw new BaseException(NOT_EXIST_MESSAGE_RULE);

        List<GetKakaoMessageRuleRes> kakaoMessageRuleDtoList = new ArrayList<>();
        for (KakaoMessageRule kakaoMessageRule : kakaoMessageRuleList) {
            kakaoMessageRuleDtoList.add(GetKakaoMessageRuleRes.toDto(kakaoMessageRule));
        }

        return kakaoMessageRuleDtoList;
    }

    @Transactional(readOnly = false)
    public List<GetKakaoMessageRuleRes> createKakaoMessageRule(List<PostKakaoMessageRuleReq> postKakaoMessageRuleDtoList, long memberId) {
        Member member = getExistMember(memberId);

        List<GetKakaoMessageRuleRes> kakaoMessageRuleDtoList = new ArrayList<>();
        for (PostKakaoMessageRuleReq postKakaoMessageRuleReq : postKakaoMessageRuleDtoList) {
            KakaoMessageRule kakaoMessageRule = kakaoMessageRuleRepository.save(PostKakaoMessageRuleReq.toEntity(postKakaoMessageRuleReq, member));
            kakaoMessageRuleDtoList.add(GetKakaoMessageRuleRes.toDto(kakaoMessageRule));
        }

        return kakaoMessageRuleDtoList;
    }

    @Transactional(readOnly = false)
    public List<GetKakaoMessageRuleRes> editKakaoMessageRule(List<PatchKakaoMessageRuleReq> patchKakaoMessageRuleDtoList, long memberId) {
        Member member = getExistMember(memberId);

        List<KakaoMessageRule> kakaoMessageRuleList = kakaoMessageRuleRepository.findByMemberIdAndStatus(memberId, BaseStatus.ACTIVE);

        if (kakaoMessageRuleList.isEmpty())
            throw new BaseException(NOT_EXIST_MESSAGE_RULE);

        List<GetKakaoMessageRuleRes> kakaoMessageRuleDtoList = new ArrayList<>();

        for (int i=0; i<2; i++) {
            PatchKakaoMessageRuleReq patchKakaoMessageRuleReq = patchKakaoMessageRuleDtoList.get(i);
            KakaoMessageRule kakaoMessageRule = kakaoMessageRuleList.get(i);
            kakaoMessageRule.editMessageRule(PatchKakaoMessageRuleReq.toEntity(patchKakaoMessageRuleReq, member));
            kakaoMessageRuleDtoList.add(GetKakaoMessageRuleRes.toDto(kakaoMessageRuleRepository.save(kakaoMessageRule)));
        }

        return kakaoMessageRuleDtoList;
    }

    private Member getExistMember(long memberId) {
        return memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
    }
}
