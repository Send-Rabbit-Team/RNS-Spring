package com.srt.message.service;

import com.srt.message.config.exception.BaseException;
import com.srt.message.config.status.BaseStatus;
import com.srt.message.domain.Block;
import com.srt.message.domain.Contact;
import com.srt.message.domain.SenderNumber;
import com.srt.message.dto.block.get.GetBlockRes;
import com.srt.message.dto.block.post.PostBlockReq;
import com.srt.message.dto.block.post.PostBlockRes;
import com.srt.message.repository.BlockRepository;
import com.srt.message.repository.ContactRepository;
import com.srt.message.repository.SenderNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.srt.message.config.response.BaseResponseStatus.*;
import static com.srt.message.config.status.BaseStatus.ACTIVE;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BlockService {
    private final SenderNumberRepository senderNumberRepository;
    private final ContactRepository contactRepository;
    private final BlockRepository blockRepository;

    // 수신 차단
    @Transactional(readOnly = false)
    public PostBlockRes registerBlock(PostBlockReq postBlockReq){
        SenderNumber senderNumber = senderNumberRepository.findByBlockNumberAndStatus(postBlockReq.getBlockNumber(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_BLOCK_NUMBER));

        Contact contact = contactRepository.findByPhoneNumberAndStatus(postBlockReq.getReceiveNumber(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_CONTACT_NUMBER));

        // 이미 존재하는 수신 차단 번호이면
        if(blockRepository.findBySenderNumberAndReceiveNumber(senderNumber.getPhoneNumber(), postBlockReq.getReceiveNumber()).isPresent())
            throw new BaseException(ALREADY_EXIST_BLOCK);

        Block block = Block.builder()
                .senderNumber(senderNumber.getPhoneNumber())
                .receiveNumber(postBlockReq.getReceiveNumber())
                .build();

        blockRepository.save(block);

        return PostBlockRes.toDto(block);
    }

    // 수신 차단한사람 조회
    public GetBlockRes getBlockByNumber(String phoneNumber, long memberId){
        SenderNumber senderNumber = senderNumberRepository.findByPhoneNumberAndStatus(phoneNumber, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_SENDER_NUMBER));

        if(senderNumber.getMember().getId() != memberId)
            throw new BaseException(NOT_MATCH_MEMBER);

        List<String> senderNumberList = blockRepository.findAllBySenderNumber(senderNumber.getPhoneNumber());

        return new GetBlockRes(senderNumberList);
    }
}
