package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Member;
import com.srt.message.domain.Template;
import com.srt.message.dto.template.get.GetTemplateRes;
import com.srt.message.dto.template.patch.PatchTemplateReq;
import com.srt.message.dto.template.post.PostTemplateReq;
import com.srt.message.repository.MemberRepository;
import com.srt.message.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

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
        Member member = memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
        Template template = templateRepository.save(PostTemplateReq.toEntity(postTemplateReq, member));
        return GetTemplateRes.toDto(template);
    }

    // 탬플릿 단일 조회
    public GetTemplateRes getOneTemplate(Long memberId, Long templateId) {
        Template template = templateRepository.findByIdAndMemberIdAndStatus(templateId, memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_TEMPLATE));
        return GetTemplateRes.toDto(template);
    }

    // 탬플릿 전체 조회
    public PageResult<GetTemplateRes, Template> getAllTemplate(Long memberId, int page) {
        PageRequest pageRequest = PageRequest.of(page-1, 3, Sort.by("id").descending());
        Page<Template> templatePage = templateRepository.findByMemberIdAndStatus(memberId, BaseStatus.ACTIVE, pageRequest);
        if (templatePage.isEmpty())
            throw new BaseException(NOT_EXIST_TEMPLATE);
        Function<Template, GetTemplateRes> fn = (template -> GetTemplateRes.toDto(template));
        return new PageResult<>(templatePage, fn);
    }

    // 탬플릿 수정
    @Transactional(readOnly = false)
    public GetTemplateRes editTemplate(Long memberId, PatchTemplateReq patchTemplateReq) {
        // member 조회
        if (memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE).isEmpty())
            throw new BaseException(NOT_EXIST_MEMBER);

        // template 조회
        Template template = templateRepository.findByIdAndStatus(patchTemplateReq.getTemplateId(), BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_TEMPLATE));

        // member의 template 인지 조회
        if (template.getMember().getId() != memberId)
            throw new BaseException(NOT_ACCESS_MEMBER);

        // template 수정
        if (patchTemplateReq.getTitle() != null)
            template.changeTitle(patchTemplateReq.getTitle());
        if (patchTemplateReq.getContent() != null)
            template.changeContent(patchTemplateReq.getContent());

        // template 저장
        Template editedTemplate = templateRepository.save(template);

        return GetTemplateRes.toDto(editedTemplate);
    }

    // 탬플릿 삭제
    @Transactional(readOnly = false)
    public GetTemplateRes deleteTemplate(Long memberId, Long templateId) {
        // member 조회
        memberRepository.findByIdAndStatus(memberId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));

        // template 조회
        Template template = templateRepository.findByIdAndStatus(templateId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_TEMPLATE));

        // member의 template 인지 조회
        if (template.getMember().getId() != memberId)
            throw new BaseException(NOT_ACCESS_MEMBER);

        // template 삭제
        template.changeStatusInActive();
        Template deletedTemplate = templateRepository.save(template);

        return GetTemplateRes.toDto(deletedTemplate);
    }

}
