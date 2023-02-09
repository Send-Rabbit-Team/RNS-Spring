package com.srt.message.service;

import com.srt.message.domain.Broker;
import com.srt.message.domain.Member;
import com.srt.message.domain.MessageRule;
import com.srt.message.dto.message_rule.MessageRuleVO;
import com.srt.message.dto.message_rule.get.GetSMSRuleRes;
import com.srt.message.dto.message_rule.patch.PatchSMSRuleReq;
import com.srt.message.dto.message_rule.patch.PatchSMSRuleRes;
import com.srt.message.dto.message_rule.post.PostSMSRuleReq;
import com.srt.message.dto.message_rule.post.PostSMSRuleRes;
import com.srt.message.repository.BrokerRepository;
import com.srt.message.repository.MemberRepository;
import com.srt.message.repository.MessageRuleRepository;
import org.assertj.core.util.diff.Patch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageRuleServiceTest {
    @InjectMocks
    private MessageRuleService messageRuleService;

    @Mock
    private MessageRuleRepository messageRuleRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BrokerRepository brokerRepository;

    private List<MessageRuleVO> messageRuleVOs;
    private List<Broker> brokers;
    private List<MessageRule> messageRules;
    private Member member;

    @BeforeEach
    void setUp(){
        // Member
        member = Member.builder().build();

        // MessageRuleVo list
        MessageRuleVO messageRuleVO1 = MessageRuleVO.builder()
                .brokerId(1).brokerRate(33)
                .build();
        MessageRuleVO messageRuleVO2 = MessageRuleVO.builder()
                .brokerId(2).brokerRate(33)
                .build();
        MessageRuleVO messageRuleVO3 = MessageRuleVO.builder()
                .brokerId(3).brokerRate(34)
                .build();


        messageRuleVOs = Arrays.asList(messageRuleVO1, messageRuleVO2, messageRuleVO3);

        // Broker List
        Broker broker1 = Broker.builder().id(1).name("kt").build();
        Broker broker2 = Broker.builder().id(2).name("skt").build();
        Broker broker3 = Broker.builder().id(3).name("lg").build();

        brokers = Arrays.asList(broker1, broker2, broker3);

        // MessageRule list (brokerRate - 10)
        MessageRule messageRule1 = MessageRule.builder()
                .id(1).broker(broker1).brokerRate(10).build();
        MessageRule messageRule2 = MessageRule.builder()
                .id(1).broker(broker2).brokerRate(10).build();
        MessageRule messageRule3 = MessageRule.builder()
                .id(1).broker(broker3).brokerRate(10).build();

        messageRules = Arrays.asList(messageRule1, messageRule2, messageRule3);
    }

    @DisplayName("메시지 중계사 비율 저장")
    @Test
    void createSMSRule_Success(){
        // given
        PostSMSRuleReq request = PostSMSRuleReq.builder()
                .messageRules(messageRuleVOs)
                .build();

        doReturn(Optional.ofNullable(member)).when(memberRepository).findById(any());

        doReturn(Optional.ofNullable(brokers.get(0))).when(brokerRepository).findById(1L);
        doReturn(Optional.ofNullable(brokers.get(1))).when(brokerRepository).findById(2L);
        doReturn(Optional.ofNullable(brokers.get(2))).when(brokerRepository).findById(3L);

        doReturn(null).when(messageRuleRepository).saveAll(anyCollection());

        // when
        PostSMSRuleRes response = messageRuleService.createSMSRule(request, member.getId());

        // then
        assertThat(response.getMessageRules().size()).isEqualTo(3);

        // verify
        verify(messageRuleRepository, times(1)).saveAll(any());
    }

    @DisplayName("메시지 중계사 비율 조회")
    @Test
    void getAllSmsRules_Success(){
        // given
        doReturn(Optional.ofNullable(member)).when(memberRepository).findById(any());
        doReturn(messageRules).when(messageRuleRepository).findAllByMember(member);

        // when
        GetSMSRuleRes response = messageRuleService.getAllSmsRules(member.getId());

        // then
        assertThat(response.getMessageRules().get(0).getBrokerRate()).isEqualTo(10);
        assertThat(response.getMessageRules().get(1).getBrokerRate()).isEqualTo(10);
        assertThat(response.getMessageRules().get(2).getBrokerRate()).isEqualTo(10);
    }

    @DisplayName("메시지 중계사 비율 수정")
    @Test
    void editSmsRule_Sucess(){
        // when
        PatchSMSRuleReq request = PatchSMSRuleReq.builder()
                .messageRules(messageRuleVOs)
                .build();

        doReturn(Optional.ofNullable(member)).when(memberRepository).findById(any());

        doReturn(Optional.ofNullable(brokers.get(0))).when(brokerRepository).findById(1L);
        doReturn(Optional.ofNullable(brokers.get(1))).when(brokerRepository).findById(2L);
        doReturn(Optional.ofNullable(brokers.get(2))).when(brokerRepository).findById(3L);

        doReturn(Optional.ofNullable(messageRules.get(0))).when(messageRuleRepository).findByBroker(brokers.get(0));
        doReturn(Optional.ofNullable(messageRules.get(1))).when(messageRuleRepository).findByBroker(brokers.get(1));
        doReturn(Optional.ofNullable(messageRules.get(2))).when(messageRuleRepository).findByBroker(brokers.get(2));

        // given
        PatchSMSRuleRes response = messageRuleService.editSmsRule(request, member.getId());

        // then
        assertThat(response.getMessageRules().get(0).getBrokerRate()).isEqualTo(33);
        assertThat(response.getMessageRules().get(1).getBrokerRate()).isEqualTo(33);
        assertThat(response.getMessageRules().get(2).getBrokerRate()).isEqualTo(34);
    }

}