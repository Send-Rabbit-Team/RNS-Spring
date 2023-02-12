package com.srt.message.repository.cache;

import com.srt.message.config.exception.BaseException;
import com.srt.message.domain.KakaoMessage;
import com.srt.message.domain.Message;
import com.srt.message.repository.KakaoMessageRepository;
import com.srt.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import static com.srt.message.config.response.BaseResponseStatus.NOT_EXIST_MESSAGE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageCacheRepository {

    private final MessageRepository messageRepository;

    private final KakaoMessageRepository kakaoMessageRepository;

    @Cacheable(value = "Message", key = "#messageId")
    public Message findMessageById(long messageId){
        return messageRepository.findMessageById(messageId).orElseThrow(() -> new BaseException(NOT_EXIST_MESSAGE));
    }

    @Cacheable(value = "KakaoMessage", key = "#messageId")
    public KakaoMessage findKakaoMessageById(long messageId){
        return kakaoMessageRepository.findKakaoMessageById(messageId).orElseThrow(() -> new BaseException(NOT_EXIST_MESSAGE));
    }
}
