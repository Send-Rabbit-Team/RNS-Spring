package com.srt.message.service.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.message.config.exception.BaseException;
import com.srt.message.config.page.PageResult;
import com.srt.message.config.response.BaseResponseStatus;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.config.type.ButtonType;
import com.srt.message.config.type.KmsgSearchType;
import com.srt.message.domain.*;
import com.srt.message.domain.redis.RKakaoMessageResult;
import com.srt.message.domain.redis.RMessageResult;
import com.srt.message.dto.kakao_message.get.GetKakaoMessageRes;
import com.srt.message.dto.kakao_message_result.get.GetKakaoMessageResultListRes;
import com.srt.message.dto.kakao_message_result.get.GetKakaoMessageResultRes;
import com.srt.message.dto.message_result.get.GetMessageResultRes;
import com.srt.message.repository.*;
import com.srt.message.repository.cache.BrokerCacheRepository;
import com.srt.message.repository.redis.RedisHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.srt.message.config.response.BaseResponseStatus.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoMessageResultService {
    private final KakaoBrokerRepository kakaoBrokerRepository;
    private final ContactRepository contactRepository;
    private final ObjectMapper objectMapper;
    private final BrokerCacheRepository brokerCacheRepository;
    private final KakaoMessageResultRepository kakaoMessageResultRepository;
    private final KakaoMessageRepository kakaoMessageRepository;
    private final MemberRepository memberRepository;
    private final RedisHashRepository redisHashRepository;

    // 발송한 알림톡 내역 조회
    public PageResult<GetKakaoMessageRes> getAllKakaoMessage(int page, Long memberId) {
        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("id").descending());
        Page<GetKakaoMessageRes> kakaoMessagePage = kakaoMessageRepository.findAllKakaoMessage(pageRequest, memberId).map(GetKakaoMessageRes::toDto);

        return new PageResult<>(kakaoMessagePage);
    }

    // 알림톡 발송 결과 조회
    public GetKakaoMessageResultListRes getKakaoMessageResult(long messageId) throws JsonProcessingException {
        GetKakaoMessageResultListRes response = new GetKakaoMessageResultListRes();
        List<GetKakaoMessageResultRes> kakaoResultResList;

        // 레디스에 상태 값 저장되어 있는지 확인
        String statusKey = "message.status." + messageId;
        Map<String, String> statusMap = redisHashRepository.findAll(statusKey);
        if (!statusMap.isEmpty()) { // 상태 정보가 들어있을 경우 REDIS로 조회
            List<RKakaoMessageResult> rKakaoMessageResultList = new ArrayList<>();

            for (Map.Entry<String, String> entry : statusMap.entrySet()) {
                String rMessageResultJson = entry.getValue();
                RKakaoMessageResult rKakaoMessageResult = objectMapper.readValue(rMessageResultJson, RKakaoMessageResult.class);

                response.addBrokerCount(rKakaoMessageResult.getKakaoBrokerId());
                response.addStatusCount(rKakaoMessageResult.getMessageStatus());
                response.addTotalPoint(rKakaoMessageResult.getMessageStatus());

                rKakaoMessageResultList.add(rKakaoMessageResult);
            }

            List<Long> contactIdList = rKakaoMessageResultList.stream()
                    .map(RKakaoMessageResult::getContactId).collect(Collectors.toList());
            List<Contact> contactList = contactRepository.findAllInContactIdList(contactIdList);
            HashMap<Long, Contact> contactMap = new HashMap<>(contactList.stream().collect(Collectors.toMap(Contact::getId, c -> c)));

            kakaoResultResList = rKakaoMessageResultList.stream().parallel().
                    map(r -> getKakaoMessageResultRes(r, contactMap.get(r.getContactId()))).collect(Collectors.toList());

        } else { // RDBMS에서 조회
            List<KakaoMessageResult> messageResults = kakaoMessageResultRepository.findAllByKakaoMessageIdOrderByIdDesc(messageId);
            kakaoResultResList = messageResults.stream().parallel()
                    .map(this::getKakaoMessageResultRes).collect(Collectors.toList());

            kakaoResultResList.stream().forEach(r -> {
                response.addBrokerCount(r.getKakaoBrokerId());
                response.addStatusCount(r.getMessageStatus());
                response.addTotalPoint(r.getMessageStatus());
            });
        }

        response.setKakaoMessageResultResList(kakaoResultResList);
        response.setTotalCount(kakaoResultResList.size());

        return response;
    }

    public PageResult<GetKakaoMessageRes> getKakaoMessageByButtonType(int page, Long memberId, ButtonType buttonType) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        Page<GetKakaoMessageRes> kakaoMessagePage = kakaoMessageRepository.findKakaoMessageByButtonType(pageRequest, memberId, buttonType).map(GetKakaoMessageRes::toDto);

        return new PageResult<>(kakaoMessagePage);
    }

    public PageResult<GetKakaoMessageRes> getSearchKakaoMessage(int page, Long memberId, KmsgSearchType searchType, String keyword) {
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

    /*
        편의 메서드
     */
    // REDIS
    public GetKakaoMessageResultRes getKakaoMessageResultRes(RKakaoMessageResult rKakaoMessageResult, Contact contact) {
        KakaoBroker kakaoBroker = brokerCacheRepository.findKakaoBrokerById(rKakaoMessageResult.getKakaoBrokerId());

        GetKakaoMessageResultRes getMessageResultRes = GetKakaoMessageResultRes.builder()
                .contactNumber(contact.getPhoneNumber())
                .contactMemo(contact.getMemo())
                .kakaoBrokerId(kakaoBroker.getId())
                .kakaoBrokerName(kakaoBroker.getName())
                .description(rKakaoMessageResult.getDescription())
                .messageStatus(rKakaoMessageResult.getMessageStatus())
                .createdAt(LocalDateTime.now().toString())
                .build();

        if (contact.getContactGroup() != null) {
            getMessageResultRes.setContactGroup(contact.getContactGroup().getName());
        }
        return getMessageResultRes;
    }

    // RDBMS
    public GetKakaoMessageResultRes getKakaoMessageResultRes(KakaoMessageResult kakaoMessageResult) {
        GetKakaoMessageResultRes getMessageResultRes = GetKakaoMessageResultRes.builder()
                .contactNumber(kakaoMessageResult.getContact().getPhoneNumber())
                .contactMemo(kakaoMessageResult.getContact().getMemo())
                .description(kakaoMessageResult.getDescription())
                .kakaoBrokerId(kakaoMessageResult.getKakaoBroker().getId())
                .kakaoBrokerName(kakaoMessageResult.getKakaoBroker().getName())
                .messageStatus(kakaoMessageResult.getMessageStatus())
                .createdAt(kakaoMessageResult.getCreatedAt() == null ? null : kakaoMessageResult.getCreatedAt().toString())
                .build();

        if (kakaoMessageResult.getContact().getContactGroup() != null) {
            getMessageResultRes.setContactGroup(kakaoMessageResult.getContact().getContactGroup().getName());
        }
        return getMessageResultRes;
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
