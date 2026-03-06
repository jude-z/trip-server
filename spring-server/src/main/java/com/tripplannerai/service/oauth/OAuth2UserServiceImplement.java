package com.tripplannerai.service.oauth;

import com.tripplannerai.common.security.CustomOAuth2User;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.entity.social.Social;
import com.tripplannerai.entity.social.SocialType;
import com.tripplannerai.repository.member.MemberRepository;
import com.tripplannerai.repository.member.SocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2UserServiceImplement extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final SocialRepository socialRepository;
    private static final Map<String, SocialType> oauthProviderToSocialTypeMap = Map.of(
            "kakao", SocialType.KAKAO,
            "naver", SocialType.NAVER,
            "google", SocialType.GOOGLE
    );
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(request);
        String oauthClientName = request.getClientRegistration().getClientName();
        String email = extractEmail(oAuth2User,oauthClientName);
        String nickname = extractNickname(oAuth2User,oauthClientName);

        Member member = findOrCreateMember(email,nickname,oauthClientName);

        return new CustomOAuth2User(email);
    }

    private String extractEmail(OAuth2User oAuth2User, String oauthClientName) {
        switch (oauthClientName) {
            case "kakao" :
                return "kakao_"+ oAuth2User.getAttributes().get("id");
            case "naver" :
                return "naver_" + ((Map<String, String>) oAuth2User.getAttributes().get("response")).get("id").substring(0, 14);
            case "google" :
                return "google_" + oAuth2User.getAttributes().get("sub");
            default:
                throw new OAuth2AuthenticationException("Unsupported OAuth provider: " + oauthClientName);
        }
    }

    private String extractNickname(OAuth2User oAuth2User, String oauthClientName) {
        switch (oauthClientName) {
            case "kakao":
                return ((Map<String, String>) oAuth2User.getAttributes().get("properties")).get("nickname");
            case "naver":
                return ((Map<String, String>) oAuth2User.getAttributes().get("response")).get("nickname");
            case "google":
                return (String) oAuth2User.getAttributes().get("name");
            default:
                throw new OAuth2AuthenticationException("Unsupported OAuth provider: " + oauthClientName);
        }
    }

    private Member findOrCreateMember(String email, String nickname, String oauthClientName) {
        // 기존 회원 조회
        Optional<Member> existingMember = memberRepository.findByEmail(email);
        Member member = existingMember.orElseGet(() -> {
            Member newMember = Member.builder()
                    .email(email)
                    .nickname(nickname)
                    .password("") //oauth 로그인 시 비밀번호 필요없음
                    .build();
            memberRepository.save(newMember);

            createSocialAccount(newMember, oauthClientName);
            return newMember;
        });
        return member;
    }

    private void createSocialAccount(Member member, String oauthClientName) {
        SocialType socialType = oauthProviderToSocialTypeMap.get(oauthClientName);
        if (socialType != null) {
            Social social = Social.builder()
                    .socialType(socialType)
                    .member(member)
                    .build();
            socialRepository.save(social);
        } else {
            throw new OAuth2AuthenticationException("Unsupported OAuth provider: " + oauthClientName);
        }
    }

}
