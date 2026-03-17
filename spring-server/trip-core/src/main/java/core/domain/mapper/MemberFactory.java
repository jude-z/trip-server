package core.domain.mapper;

import core.api.request.member.SignUpRequest;
import core.infra.projection.member.FetchMemberResponse;
import core.domain.entity.member.Member;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

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
