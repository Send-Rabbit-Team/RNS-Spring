//package com.srt.message.repository;
//
//import com.srt.message.domain.Block;
//import com.srt.message.domain.Contact;
//import com.srt.message.domain.ContactGroup;
//import com.srt.message.domain.Member;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
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
//    @Autowired
//    private BlockRepository blockRepository;
//
//    @Test
//    void registerTestGroup() {
//        Member member = memberRepository.findById(5L).orElseThrow(null);
//
//        ContactGroup contactGroup = ContactGroup.builder()
//                .member(member)
//                .name("Test Group")
//                .build();
//        contactGroupRepository.save(contactGroup);
//
//        for (int i = 1; i <= 9000; i++) {
//            Contact contact = Contact.builder()
//                    .member(member)
//                    .contactGroup(contactGroup)
//                    .memo("Test User " + Integer.toString(i))
//                    .phoneNumber("010" + RandomStringUtils.randomNumeric(8))
//                    .build();
//            contactRepository.save(contact);
//        }
//
//    }
//
//    @Test
//    void registerBlockGroup() {
//        Member member = memberRepository.findById(5L).orElseThrow(null);
//
//        ContactGroup contactGroup = ContactGroup.builder()
//                .member(member)
//                .name("Block Test Group")
//                .build();
//        contactGroupRepository.save(contactGroup);
//
//        for (int i = 1; i <= 1000; i++) {
//            String phoneNumber = "010" + RandomStringUtils.randomNumeric(8);
//            Contact contact = Contact.builder()
//                    .member(member)
//                    .contactGroup(contactGroup)
//                    .memo("Block Test User " + Integer.toString(i))
//                    .phoneNumber(phoneNumber)
//                    .build();
//            contactRepository.save(contact);
//
//            Block block = Block.builder()
//                    .senderNumber("01024145790")
//                    .receiveNumber(phoneNumber)
//                    .build();
//            blockRepository.save(block);
//        }
//
//    }
//
//}
