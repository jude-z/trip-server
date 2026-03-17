package payment.api.security.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import payment.api.security.jwt.JwtProvider;
import payment.api.security.jwt.JwtSubject;
import payment.api.security.oauth.userdetail.CustomOAuth2User;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.member.Member;
import core.infra.querydsl.member.QueryDslMemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static core.common.util.CookieUtil.addCrossDomainCookie;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final QueryDslMemberRepository queryDslMemberRepository;
    @Value("${jwt.refresh.expiration}")
    private int refreshExpiration;
    @Value("${jwt.access.expiration}")
    private int accessExpiration;
    @Value("${oauth2SuccessRedirectUrl}")
    private String oauth2SuccessRedirectUrl;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getName();


        // DB에 refreshToken 저장
        Member member = queryDslMemberRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        JwtSubject jwtSubject = JwtSubject.of(member);
        String accessToken = jwtProvider.createAccessToken(objectMapper.writeValueAsString(jwtSubject), "user");
        String refreshToken = jwtProvider.createRefreshToken(objectMapper.writeValueAsString(jwtSubject),"user");

        member.setRefreshToken(refreshToken);


        // 크로스 도메인 쿠키 설정
        addCrossDomainCookie(response, "accessToken", accessToken, accessExpiration, false);
        addCrossDomainCookie(response, "refreshToken", refreshToken, refreshExpiration, true);


        //프론트엔드로 리다이렉트
        String redirectUrl = oauth2SuccessRedirectUrl + "?status=success";
        response.sendRedirect(redirectUrl);

    }
}
