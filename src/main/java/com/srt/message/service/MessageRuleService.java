package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.Broker;
import com.srt.message.domain.Member;
import com.srt.message.domain.MessageRule;
import com.srt.message.dto.message_rule.get.GetSMSRuleRes;
import com.srt.message.dto.message_rule.MessageRuleVO;
import com.srt.message.dto.message_rule.patch.PatchSMSRuleReq;
import com.srt.message.dto.message_rule.patch.PatchSMSRuleRes;
import com.srt.message.dto.message_rule.post.PostSMSRuleReq;
import com.srt.message.dto.message_rule.post.PostSMSRuleRes;
import com.srt.message.repository.BrokerRepository;
import com.srt.message.repository.MemberRepository;
import com.srt.message.repository.MessageRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageRuleService {
    private final MessageRuleRepository messageRuleRepository;

    private final MemberRepository memberRepository;

    private final BrokerRepository brokerRepository;

    // 메시지 중계사 비율 생성
    public PostSMSRuleRes createSMSRule(PostSMSRuleReq msgRuleReq, long memberId){
        List<MessageRuleVO> messageRuleVOs = msgRuleReq.getMessageRules();

        // 총 합 비율 100인지 검증
        if(messageRuleVOs.stream().mapToInt(m -> m.getBrokerRate()).sum() != 100)
            throw new BaseException(NOT_VALID_BROKER_RATE);


        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));

        // DTO 변환
        List<MessageRule> messageRules = messageRuleVOs.stream().map(m -> {
            Broker broker = brokerRepository.findById(m.getBrokerId())
                    .orElseThrow(() -> new BaseException(NOT_EXIST_BROKER));
            return MessageRuleVO.toEntity(broker, member, m.getBrokerRate());})
                .collect(Collectors.toList());

        messageRuleRepository.saveAll(messageRules);

        return PostSMSRuleRes.toDto(messageRules);
    }

    // 메시지 중계사 규칙 조회
    @Transactional(readOnly = false)
    public GetSMSRuleRes getAllSmsRules(long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new BaseException(NOT_EXIST_MEMBER));

        List<MessageRule> messageRuleList = messageRuleRepository.findAllByMember(member);

        if (messageRuleList.isEmpty()) {
            messageRuleList = setDefaultMessageRule(member);
        }

        return GetSMSRuleRes.toDto(messageRuleList);
    }


    // 메시지 중계사 규칙 수정
    @Transactional(readOnly = false)
    public PatchSMSRuleRes editSmsRule(PatchSMSRuleReq patchSMSRuleReq, long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->new BaseException(NOT_EXIST_MEMBER));

        List<MessageRule> modMessageRuleList = patchSMSRuleReq.getMessageRules().stream().map(
                m -> {
                    Broker broker = brokerRepository.findById(m.getBrokerId())
                            .orElseThrow(()-> new BaseException(NOT_EXIST_BROKER));

                    return PatchSMSRuleReq.toEntity(
                            broker,
                            m.getBrokerRate(), member);
                }
        ).collect(Collectors.toList());

        modMessageRuleList.forEach(
                m -> {
                    MessageRule prevMessageRule = messageRuleRepository.findByMemberIdAndBroker(memberId, m.getBroker())
                            .orElseThrow(()-> new BaseException(NOT_EXIST_MESSAGE_RULE));
                    prevMessageRule.editMessageRule(m);
                }
        );

        return PatchSMSRuleRes.toDto(modMessageRuleList);
    }

    public List<MessageRule> setDefaultMessageRule(Member member) {
        List<MessageRule> newMessageRuleList = new ArrayList<>();
        List<Broker> brokerList = brokerRepository.findAll();
        for (Broker broker : brokerList) {
            MessageRule messageRule = MessageRule.builder()
                    .member(member)
                    .broker(broker)
                    .brokerRate(30)
                    .build();
            newMessageRuleList.add(messageRuleRepository.save(messageRule));
        }
        return newMessageRuleList;
    }
}
