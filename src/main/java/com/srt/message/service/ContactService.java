package com.srt.message.service;

import com.srt.message.dto.Contact.ContactDTO;
import com.srt.message.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactDTO saveContact(){

    }


}
