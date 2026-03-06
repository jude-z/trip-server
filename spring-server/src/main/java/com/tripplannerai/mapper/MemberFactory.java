package com.tripplannerai.mapper;

import com.tripplannerai.dto.request.member.SignUpRequest;
import com.tripplannerai.dto.response.member.FetchMemberResponse;
import com.tripplannerai.entity.member.Member;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static com.tripplannerai.util.ConstClass.SUCCESS_CODE;
import static com.tripplannerai.util.ConstClass.SUCCESS_MESSAGE;

public class MemberFactory {

    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public static Member of(SignUpRequest signUpRequest){
        return Member.builder()
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .nickname(signUpRequest.getNickname())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .image(null)
                .customerKey(UUID.randomUUID().toString())
                .ticket(10)
                .build();
    }

    public static FetchMemberResponse of(Member member,String imageUrl){
        return FetchMemberResponse.builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .provider(member.getProvider())
                .providerId(member.getProviderId())
                .isWithdrawn(member.isWithdrawn())
                .imageUrl(imageUrl)
                .customerKey(member.getCustomerKey())
                .ticket(member.getTicket())
                .build();
    }
}
