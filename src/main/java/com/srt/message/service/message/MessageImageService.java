package com.srt.message.service.message;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.response.BaseResponseStatus;
import com.srt.message.config.type.MessageType;
import com.srt.message.domain.Message;
import com.srt.message.domain.MessageImage;
import com.srt.message.dto.message_image.get.GetMessageImageRes;
import com.srt.message.repository.MessageImageRepository;
import com.srt.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.NOT_EXIST_MESSAGE;
import static com.srt.message.config.response.BaseResponseStatus.NOT_MMS_TYPE;

@RequiredArgsConstructor
@Service
public class MessageImageService {
    private final MessageImageRepository messageImageRepository;
    private final MessageRepository messageRepository;

    // MMS 메시지 불러오기
    public GetMessageImageRes getMMSImages(long messageId){
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BaseException(NOT_EXIST_MESSAGE));

        if(message.getMessageType() != MessageType.MMS)
            throw new BaseException(NOT_MMS_TYPE);

        List<MessageImage> messageImages = messageImageRepository.findAllByMessageId(messageId);

        return GetMessageImageRes.toDto(messageImages);
    }
}
