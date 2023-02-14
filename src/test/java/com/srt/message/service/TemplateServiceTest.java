package com.srt.message.service;

import com.srt.message.config.page.PageResult;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Member;
import com.srt.message.domain.Template;
import com.srt.message.dto.template.get.GetTemplateRes;
import com.srt.message.dto.template.patch.PatchTemplateReq;
import com.srt.message.dto.template.post.PostTemplateReq;
import com.srt.message.repository.MemberRepository;
import com.srt.message.repository.TemplateRepository;
import com.srt.message.service.message.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {
    @InjectMocks
    private TemplateService templateService;

    @Mock
    private TemplateRepository templateRepository;
    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private Template template;

    @BeforeEach
    void setUp(){
        member = Member.builder()
                .id(1).email("kakao@gmail.com").build();

        template = Template.builder()
                .title("[새해 복 많이 받으세요]").content("올 한해 건강하시고 좋은 일들만 가득하시길 바랍니다.")
                .member(member).build();
    }

    @DisplayName("템플릿 생성")
    @Test
    void registerTemplate_Success(){
        // given
        PostTemplateReq request = PostTemplateReq.builder()
                .title("[새해 복 많이 받으세요]").content("올 한해 건강하시고 좋은 일들만 가득하시길 바랍니다.")
                .build();

        doReturn(Optional.ofNullable(member)).when(memberRepository).findByIdAndStatus(anyLong(), eq(BaseStatus.ACTIVE));
        doReturn(template).when(templateRepository).save(any());

        // when
        GetTemplateRes response = templateService.registerTemplate(member.getId(), request);

        // then
        assertThat(response.getContent()).isEqualTo(request.getContent());

        // verify
        verify(templateRepository, times(1)).save(any());
    }

    @DisplayName("탬플릿 단일 조회")
    @Test
    void getOneTemplate_Success(){
        // given
        doReturn(Optional.ofNullable(member)).when(memberRepository).findByIdAndStatus(anyLong(), eq(BaseStatus.ACTIVE));
        doReturn(Optional.of(template)).when(templateRepository).findByIdAndStatus(any(), eq(BaseStatus.ACTIVE));

        // when
        GetTemplateRes response = templateService.getOneTemplate(member.getId(), template.getId());

        // then
        assertThat(response.getTemplateId()).isEqualTo(template.getId());
    }

    @DisplayName("탬플릿 전체 조회 (페이징 O)")
    @Test
    void getPageTemplate_Success(){
        // given
        int page = 1;
        PageRequest pageRequest = PageRequest.of(page-1, 3, Sort.by("id").descending());
        Page<Template> templatePage = new PageImpl<>(getTemplateList(), pageRequest, 0); // 페이지 인스턴스 객체 생성


        doReturn(Optional.ofNullable(member)).when(memberRepository).findByIdAndStatus(anyLong(), eq(BaseStatus.ACTIVE));
        doReturn(templatePage).when(templateRepository).findAllTemplate(anyLong(), eq(BaseStatus.ACTIVE), eq(pageRequest));

        // when
        PageResult<GetTemplateRes> response = templateService.getPageTemplate(member.getId(), 1);

        // then
        assertThat(response.getDtoList().size()).isEqualTo(3);
    }

    @DisplayName("탬플릿 전체 조회(페이징 X)")
    @Test
    void getAllTemplate_Success(){
        // given
        doReturn(Optional.ofNullable(member)).when(memberRepository).findByIdAndStatus(anyLong(), eq(BaseStatus.ACTIVE));
        doReturn(getTemplateList()).when(templateRepository).findByMemberIdAndStatusOrderByUpdatedAtDesc(anyLong(), eq(BaseStatus.ACTIVE));

        // when
        List<GetTemplateRes> response = templateService.getAllTemplate(member.getId());

        // then
        assertThat(response.size()).isEqualTo(3);
    }

    @DisplayName("탬플릿 수정")
    @Test
    void editTemplate_Success(){
        // given
        PatchTemplateReq request = PatchTemplateReq.builder()
                .templateId(1).content("수정").title("수정").build();

        template.changeTitle(request.getTitle());
        template.changeContent(request.getContent());

        doReturn(Optional.ofNullable(member)).when(memberRepository).findByIdAndStatus(anyLong(), eq(BaseStatus.ACTIVE));
        doReturn(Optional.of(template)).when(templateRepository).findByIdAndStatus(any(), eq(BaseStatus.ACTIVE));
        doReturn(template).when(templateRepository).save(any());

        // when
        GetTemplateRes response = templateService.editTemplate(member.getId(), request);

        // then
        assertThat(response.getTitle()).isEqualTo(template.getTitle());
        assertThat(response.getContent()).isEqualTo(template.getContent());
    }

    @DisplayName("탬플릿 삭제")
    @Test
    void deleteTemplate_Success(){
        // given
        doReturn(Optional.ofNullable(member)).when(memberRepository).findByIdAndStatus(anyLong(), eq(BaseStatus.ACTIVE));
        doReturn(Optional.of(template)).when(templateRepository).findByIdAndStatus(any(), eq(BaseStatus.ACTIVE));

        template.changeStatusInActive();
        doReturn(template).when(templateRepository).save(any());

        // when
        GetTemplateRes response = templateService.deleteTemplate(member.getId(), template.getId());

        // then
        assertThat(response.getTemplateId()).isEqualTo(template.getId());
    }

    List<Template> getTemplateList(){
        Template template1 = Template.builder()
                .title("[새해 복 많이 받으세요]").content("올 한해 건강하시고 좋은 일들만 가득하시길 바랍니다.")
                .member(member).build();
        Template template2 = Template.builder()
                .title("[안전 문자]").content("금일 미세먼지가 매우 나쁨이니 건강에 유의하시길 바랍니다.")
                .member(member).build();
        Template template3 = Template.builder()
                .title("[광고]").content("OOO 청소기 50% 세일! - Kakao 홈쇼핑")
                .member(member).build();

        return Arrays.asList(template1, template2, template3);
    }
}