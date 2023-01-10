package com.srt.message.controller;

import com.srt.message.repository.SenderNumberRepository;
import com.srt.message.service.SenderNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sender")
public class SenderNumberController {
    private final SenderNumberService senderNumberService;

    @GetMapping("/test")
    public void auditTest(){
        senderNumberService.testSave();
    }
}
