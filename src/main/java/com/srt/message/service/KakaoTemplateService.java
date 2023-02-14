package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.KakaoTemplate;
import com.srt.message.domain.Member;
import com.srt.message.repository.KakaoTemplateRepository;
import com.srt.message.repository.MemberRepository;
import com.srt.message.dto.kakaoTemplate.get.GetKakaoTemplateRes;
import com.srt.message.dto.kakaoTemplate.patch.PatchKakaoTemplateReq;
import com.srt.message.dto.kakaoTemplate.post.PostKakaoTemplateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoTemplateService {
    private final KakaoTemplateRepository kakaoTemplateRepository;
    private final MemberRepository memberRepository;

    // 탬플릿 생성
    @Transactional(readOnly = false)
    public GetKakaoTemplateRes registerKakaoTemplate(Long memberId, PostKakaoTemplateReq postKakaoTemplateReq) {
        Member member = getExistMember(memberId);

        // template 생성
        KakaoTemplate kakaoTemplate = kakaoTemplateRepository.save(PostKakaoTemplateReq.toEntity(postKakaoTemplateReq, member));

        return GetKakaoTemplateRes.toDto(kakaoTemplate);
    }

    // 탬플릿 단일 조회
    public GetKakaoTemplateRes getOneKakaoTemplate(Long memberId, Long templateId) {
        getExistMember(memberId);

        // template 조회
        KakaoTemplate kakaoTemplate = kakaoTemplateRepository.findByIdAndStatus(templateId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_TEMPLATE));

        checkMatchMember(kakaoTemplate, memberId);

        return GetKakaoTemplateRes.toDto(kakaoTemplate);
    }

    // 탬플릿 전체 조회(페이징 O)
    public PageResult<GetKakaoTemplateRes> getPageKakaoTemplate(Long memberId, int page) {
        getExistMember(memberId);

        // pageRequest 생성
        PageRequest pageRequest = PageRequest.of(page-1, 2, Sort.by("updatedAt").descending());

        // template 조회
        Page<KakaoTemplate> kakaoTemplatePage = kakaoTemplateRepository.findAllTemplate(memberId, BaseStatus.ACTIVE, pageRequest);
        if (kakaoTemplatePage.isEmpty())
            throw new BaseException(NOT_EXIST_TEMPLATE);

        Page<GetKakaoTemplateRes> kakaoTemplateRes = kakaoTemplatePage.map(k -> GetKakaoTemplateRes.toDto(k));

        return new PageResult<>(kakaoTemplateRes);
    }

    // 탬플릿 전체 조회(페이징 X)
    public List<GetKakaoTemplateRes> getAllKakaoTemplate(Long memberId) {
        getExistMember(memberId);

        // template 조회
        List<KakaoTemplate> kakaoTemplateList = kakaoTemplateRepository.findByMemberIdAndStatusOrderByUpdatedAtDesc(memberId, BaseStatus.ACTIVE);
        if (kakaoTemplateList.isEmpty())
            throw new BaseException(NOT_EXIST_TEMPLATE);

        return kakaoTemplateList.stream().map(kakaoTemplate -> GetKakaoTemplateRes.toDto(kakaoTemplate)).collect(Collectors.toList());
    }

    // 탬플릿 수정
    @Transactional(readOnly = false)
    public GetKakaoTemplateRes editKakaoTemplate(Long memberId, PatchKakaoTemplateReq patchKakaoTemplateReq) {
        getExistMember(memberId);

        // template 조회
        KakaoTemplate kakaoTemplate = kakaoTemplateRepository.findByIdAndStatus(patchKakaoTemplateReq.getTemplateId(), BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_TEMPLATE));

        checkMatchMember(kakaoTemplate, memberId);

        // template 수정
        if (patchKakaoTemplateReq.getTitle() != null)
            kakaoTemplate.changeTitle(patchKakaoTemplateReq.getTitle());
        if (patchKakaoTemplateReq.getSubTitle() != null)
            kakaoTemplate.changeSubTitle(patchKakaoTemplateReq.getSubTitle());
        if (patchKakaoTemplateReq.getContent() != null)
            kakaoTemplate.changeContent(patchKakaoTemplateReq.getContent());
        if (patchKakaoTemplateReq.getDescription() != null)
            kakaoTemplate.changeDescription(patchKakaoTemplateReq.getDescription());
        if (patchKakaoTemplateReq.getButtonUrl() != null)
            kakaoTemplate.changeButtonUrl(patchKakaoTemplateReq.getButtonUrl());
        if (patchKakaoTemplateReq.getButtonTitle() != null)
            kakaoTemplate.changeButtonTitle(patchKakaoTemplateReq.getButtonTitle());
        if (patchKakaoTemplateReq.getButtonType() != null)
            kakaoTemplate.changeButtonType(patchKakaoTemplateReq.getButtonType());

        // template 저장
        KakaoTemplate editedKakaoTemplate = kakaoTemplateRepository.save(kakaoTemplate);

            return GetKakaoTemplateRes.toDto(editedKakaoTemplate);
    }

    // 탬플릿 삭제
    @Transactional(readOnly = false)
    public GetKakaoTemplateRes deleteKakaoTemplate(Long memberId, Long templateId) {
        getExistMember(memberId);

        // template 조회
        KakaoTemplate kakaoTemplate = kakaoTemplateRepository.findByIdAndStatus(templateId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_TEMPLATE));

        checkMatchMember(kakaoTemplate, memberId);

        // template 삭제
        kakaoTemplate.changeStatusInActive();
        KakaoTemplate deletedTemplate = kakaoTemplateRepository.save(kakaoTemplate);

        return GetKakaoTemplateRes.toDto(deletedTemplate);
    }

    // member 조회
    private Member getExistMember(long memberId) {
        return memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
    }

    // member의 template 인지 조회
    private void checkMatchMember(KakaoTemplate kakaoTemplate, long memberId) {
        if (kakaoTemplate.getMember().getId() != memberId)
            throw new BaseException(NOT_AUTH_MEMBER);
    }

}
