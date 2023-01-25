package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Member;
import com.srt.message.domain.Template;
import com.srt.message.service.dto.template.get.GetTemplateRes;
import com.srt.message.service.dto.template.patch.PatchTemplateReq;
import com.srt.message.service.dto.template.post.PostTemplateReq;
import com.srt.message.repository.MemberRepository;
import com.srt.message.repository.TemplateRepository;
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
public class TemplateService {
    private final TemplateRepository templateRepository;
    private final MemberRepository memberRepository;

    // 탬플릿 생성
    @Transactional(readOnly = false)
    public GetTemplateRes registerTemplate(Long memberId, PostTemplateReq postTemplateReq) {
        Member member = getExistMember(memberId);

        // template 생성
        Template template = templateRepository.save(PostTemplateReq.toEntity(postTemplateReq, member));

        return GetTemplateRes.toDto(template);
    }

    // 탬플릿 단일 조회
    public GetTemplateRes getOneTemplate(Long memberId, Long templateId) {
        getExistMember(memberId);

        // template 조회
        Template template = templateRepository.findByIdAndStatus(templateId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_TEMPLATE));

        checkMatchMember(template, memberId);

        return GetTemplateRes.toDto(template);
    }

    // 탬플릿 전체 조회(페이징 O)
    public PageResult<GetTemplateRes, Template> getPageTemplate(Long memberId, int page) {
        getExistMember(memberId);

        // pageRequest 생성
        PageRequest pageRequest = PageRequest.of(page-1, 3, Sort.by("id").descending());

        // template 조회
        Page<Template> templatePage = templateRepository.findAllTemplate(memberId, BaseStatus.ACTIVE, pageRequest);
        if (templatePage.isEmpty())
            throw new BaseException(NOT_EXIST_TEMPLATE);

        // template -> template dto 함수 정의
        Function<Template, GetTemplateRes> fn = (template -> GetTemplateRes.toDto(template));

        return new PageResult<>(templatePage, fn);
    }

    // 탬플릿 전체 조회(페이징 X)
    public List<GetTemplateRes> getAllTemplate(Long memberId) {
        getExistMember(memberId);

        // template 조회
        List<Template> templateList = templateRepository.findByMemberIdAndStatus(memberId, BaseStatus.ACTIVE);
        if (templateList.isEmpty())
            throw new BaseException(NOT_EXIST_TEMPLATE);

        return templateList.stream().map(template -> GetTemplateRes.toDto(template)).collect(Collectors.toList());
    }

    // 탬플릿 수정
    @Transactional(readOnly = false)
    public GetTemplateRes editTemplate(Long memberId, PatchTemplateReq patchTemplateReq) {
        getExistMember(memberId);

        // template 조회
        Template template = templateRepository.findByIdAndStatus(patchTemplateReq.getTemplateId(), BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_TEMPLATE));

        checkMatchMember(template, memberId);

        // template 수정
        if (patchTemplateReq.getTitle() != null)
            template.changeTitle(patchTemplateReq.getTitle());
        if (patchTemplateReq.getContent() != null)
            template.changeContent(patchTemplateReq.getContent());
        if (patchTemplateReq.getTemplateType() != null)
            template.changeTemplateType(patchTemplateReq.getTemplateType());

        // template 저장
        Template editedTemplate = templateRepository.save(template);

        return GetTemplateRes.toDto(editedTemplate);
    }

    // 탬플릿 삭제
    @Transactional(readOnly = false)
    public GetTemplateRes deleteTemplate(Long memberId, Long templateId) {
        getExistMember(memberId);

        // template 조회
        Template template = templateRepository.findByIdAndStatus(templateId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_TEMPLATE));

        checkMatchMember(template, memberId);

        // template 삭제
        template.changeStatusInActive();
        Template deletedTemplate = templateRepository.save(template);

        return GetTemplateRes.toDto(deletedTemplate);
    }

    // member 조회
    private Member getExistMember(long memberId) {
        return memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
    }

    // member의 template 인지 조회
    private void checkMatchMember(Template template, long memberId) {
        if (template.getMember().getId() != memberId)
            throw new BaseException(NOT_AUTH_MEMBER);
    }

}
