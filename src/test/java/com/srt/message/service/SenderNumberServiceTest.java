package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponseStatus;
import com.srt.message.config.status.AuthPhoneNumberStatus;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.config.type.LoginType;
import com.srt.message.config.type.MemberType;
import com.srt.message.domain.Contact;
import com.srt.message.domain.Member;
import com.srt.message.domain.SenderNumber;
import com.srt.message.domain.redis.AuthPhoneNumber;
import com.srt.message.dto.sender_number.get.GetSenderNumberRes;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberReq;
import com.srt.message.dto.sender_number.post.RegisterSenderNumberRes;
import com.srt.message.repository.MemberRepository;
import com.srt.message.repository.SenderNumberRepository;
import com.srt.message.repository.redis.AuthPhoneNumberRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.srt.message.config.response.BaseResponseStatus.ALREADY_EXIST_PHONE_NUMBER;
import static com.srt.message.config.status.AuthPhoneNumberStatus.CONFIRM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SenderNumberServiceTest {
    @InjectMocks
    private SenderNumberService senderNumberService;

    @Mock
    private SenderNumberRepository senderNumberRepository;

    @Mock
    private AuthPhoneNumberRedisRepository authPhoneNumberRedisRepository;
    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private SenderNumber senderNumber;

    @BeforeEach
    void setUp(){
        member = Member.builder()
                .id(1).name("라이언").email("kakao@gmail.com").password("1q2w3e4r!")
                .memberType(MemberType.PERSON).loginType(LoginType.DEFAULT)
                .build();

        senderNumber = SenderNumber.builder()
                .id(1).phoneNumber("01012341234").blockNumber("07012341234").memo("카카오").member(member)
                .build();
    }

    @Profile("발신자 등록 (성공)")
    @Test
    void registerSenderNumber_Success(){
        // given
        RegisterSenderNumberReq request = RegisterSenderNumberReq.builder()
                .phoneNumber("01012341234").memo("카카오").build();
        AuthPhoneNumber authPhoneNumber = AuthPhoneNumber.builder()
                .authPhoneNumberStatus(CONFIRM).build();

        doReturn(Optional.ofNullable(authPhoneNumber)).when(authPhoneNumberRedisRepository).findById(any());
        doReturn(Optional.ofNullable(member)).when(memberRepository).findById(anyLong());
        doReturn(null).when(senderNumberRepository).save(any());

        // when
        RegisterSenderNumberRes response =
                senderNumberService.registerSenderNumber(member.getId(), request);

        // then
        // 수신자 차단 번호 생성
        assertThat(response.getBlockNumber().startsWith("070")).isTrue();

        // verify
        verify(senderNumberRepository, times(1)).save(any(SenderNumber.class));
    }

    @Profile("발신자 등록 (실패 - 이미 등록된 발신번호)")
    @Test
    void registerSenderNumber_Fail_1(){
        // given
        RegisterSenderNumberReq request = RegisterSenderNumberReq.builder()
                .phoneNumber("01012341234").memo("카카오").build();
        doReturn(Optional.ofNullable(senderNumber)).when(senderNumberRepository).findByPhoneNumberAndStatus(any(), eq(BaseStatus.ACTIVE));

        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                senderNumberService.registerSenderNumber(member.getId(), request));

        // then
        assertEquals(exception.getStatus(), ALREADY_EXIST_PHONE_NUMBER);
    }

    @Profile("발신자 조회 (페이징 O)")
    @Test
    void getPageSenderNumber_Success(){
        // given
        int page = 1;
        PageRequest pageRequest = PageRequest.of(page-1, 5, Sort.by("id").descending());
        Page<SenderNumber> senderNumberPage = new PageImpl<>(getSenderNumberList(), pageRequest, 0); // 페이지 인스턴스 객체 생성

        doReturn(senderNumberPage).when(senderNumberRepository).findAllSenderNumber(anyLong(), eq(BaseStatus.ACTIVE), eq(pageRequest));

        // when
        PageResult<GetSenderNumberRes> response = senderNumberService.getPageSenderNumber(member.getId(), 1);

        // then
        assertThat(response.getDtoList().size()).isEqualTo(3);
    }

    @Profile("발신자 조회 (페이징 X)")
    @Test
    void getAllSenderNumber_Success(){
        // given
        doReturn(getSenderNumberList()).when(senderNumberRepository).findByMemberIdAndStatus(anyLong(), eq(BaseStatus.ACTIVE));

        // when
        List<GetSenderNumberRes> response = senderNumberService.getAllSenderNumber(member.getId());

        // then
        assertThat(response.size()).isEqualTo(3);
    }

    @Profile("발신자 삭제")
    @Test
    void deleteSenderNumber_Success(){
        // given
        doReturn(Optional.ofNullable(senderNumber))
                .when(senderNumberRepository).findByIdAndStatus(anyLong(), eq(BaseStatus.ACTIVE));
        doReturn(null).when(senderNumberRepository).save(any());

        // when
        senderNumberService.deleteSenderNumber(senderNumber.getId(), member.getId());

        // verify
        verify(senderNumberRepository, times(1)).save(any());
    }

    List<SenderNumber> getSenderNumberList(){
        SenderNumber senderNumber1 = SenderNumber.builder()
                .member(member).phoneNumber("01011111111").memo("네오")
                .build();
        SenderNumber senderNumber2 = SenderNumber.builder()
                .member(member).phoneNumber("01022222222").memo("제이지")
                .build();
        SenderNumber senderNumber3 = SenderNumber.builder()
                .member(member).phoneNumber("01033333333").memo("무지")
                .build();

        return Arrays.asList(senderNumber1, senderNumber2, senderNumber3);
    }
}