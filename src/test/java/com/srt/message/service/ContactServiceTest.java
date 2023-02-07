package com.srt.message.service;

import com.srt.message.domain.Company;
import com.srt.message.domain.Contact;
import com.srt.message.domain.ContactGroup;
import com.srt.message.domain.Member;
import com.srt.message.dto.contact.post.PostContactReq;
import com.srt.message.repository.CompanyRepository;
import com.srt.message.repository.ContactRepository;
import com.srt.message.repository.ContactGroupRepository;
import com.srt.message.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ContactServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactGroupRepository contactGroupRepository;

    @Autowired
    private CompanyRepository companyRepository;


    public void generateContact(){
        PostContactReq req = PostContactReq.builder()
                .contactGroupId(10L)
                .phoneNumber("01091908201")
                .memo("연락처 메모입니다.")
                .build();

        ContactGroup group = ContactGroup.builder()
                .name("YoungJoo Group")
                .build();

        Company company = Company.builder()
                .companyName("Kakao Enterprise")
                .bsNum("1234123412")
                .build();

        Member member = Member.builder()
                .company(company)
                .email("john@gmail.com")
                .password("1q2w3e4r!")
                .name("오영주")
                .phoneNumber("01091908201")
                .build();

        Contact contact = PostContactReq.toEntity(req,group,member);

        companyRepository.save(company);
        memberRepository.save(member);
        contactGroupRepository.save(group);
        contactRepository.save(contact);
    }

    @Test
    public void saveContact_SaveContact_True(){

    }
}
