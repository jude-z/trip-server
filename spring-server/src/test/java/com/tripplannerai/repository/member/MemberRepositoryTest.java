package com.tripplannerai.repository.member;

import com.tripplannerai.entity.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    void updateTicket() {
//        Member member = Member.dummy();
//        memberRepository.save(member);
//        memberRepository.updateTicket(member.getId(),5000);
    }
}