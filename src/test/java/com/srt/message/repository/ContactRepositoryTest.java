//package com.srt.message.repository;
//
//import com.srt.message.domain.Contact;
//import com.srt.message.domain.ContactGroup;
//import com.srt.message.domain.Member;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class ContactRepositoryTest {
//    @Autowired
//    MemberRepository memberRepository;
//    @Autowired
//    ContactRepository contactRepository;
//    @Autowired
//    private ContactGroupRepository contactGroupRepository;
//
//    @Test
//    void registerContact() {
//        Member member = memberRepository.findById(5L).orElseThrow(null);
//
//        ContactGroup contactGroup = ContactGroup.builder()
//                .member(member)
//                .name("Test 집단")
//                .build();
//        contactGroupRepository.save(contactGroup);
//
//        for (int i = 1; i <= 10000; i++) {
//            Contact contact = Contact.builder()
//                    .member(member)
//                    .contactGroup(contactGroup)
//                    .memo("TestUser" + Integer.toString(i))
//                    .phoneNumber("010" + UUID.randomUUID().toString().substring(0,9))
//                    .build();
//            contactRepository.save(contact);
//        }
//
//    }
//
//}
