package core.service.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import core.api.request.member.EmailCheckoutRequest;
import core.api.request.member.SignInRequest;
import core.api.request.member.SignUpRequest;
import core.api.response.ApiResponse;
import core.api.security.jwt.JwtProvider;
import core.api.security.jwt.JwtValidator;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.member.Member;
import core.fixture.MemberFixture;
import core.infra.jpa.image.ImageRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.querydsl.member.QueryDslMemberRepository;
import core.service.s3.S3UploadService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private QueryDslMemberRepository queryDslMemberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private JwtValidator jwtValidator;

    @Mock
    private S3UploadService s3UploadService;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(memberService, "refreshExpiration", 10080);
        ReflectionTestUtils.setField(memberService, "serverUrl", "http://localhost:8080");
    }

    @Test
    @DisplayName("signIn - success")
    void signIn_success() throws JsonProcessingException {
        // given
        Member member = MemberFixture.create();
        SignInRequest request = new SignInRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        HttpServletResponse response = mock(HttpServletResponse.class);

        given(queryDslMemberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtProvider.createAccessToken(anyString(), anyString())).willReturn("access-token");
        given(jwtProvider.createRefreshToken(anyString(), anyString())).willReturn("refresh-token");

        // when
        ApiResponse result = memberService.signIn(request, response);

        // then
        assertThat(result).isNotNull();
        verify(jwtProvider).createAccessToken(anyString(), anyString());
        verify(jwtProvider).createRefreshToken(anyString(), anyString());
    }

    @Test
    @DisplayName("signIn - member not found throws CommonException")
    void signIn_notFoundMember() {
        // given
        SignInRequest request = new SignInRequest();
        request.setEmail("notfound@test.com");
        request.setPassword("password123");

        HttpServletResponse response = mock(HttpServletResponse.class);

        given(queryDslMemberRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.signIn(request, response))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_MEMBER));
    }

    @Test
    @DisplayName("signIn - wrong password throws CommonException")
    void signIn_wrongPassword() {
        // given
        Member member = MemberFixture.create();
        SignInRequest request = new SignInRequest();
        request.setEmail("test@test.com");
        request.setPassword("wrongPassword");

        HttpServletResponse response = mock(HttpServletResponse.class);

        given(queryDslMemberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.signIn(request, response))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.UN_CORRECT_PASSWORD));
    }

    @Test
    @DisplayName("signUp - success")
    void signUp_success() {
        // given
        SignUpRequest request = new SignUpRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");
        request.setNickname("tester");
        request.setPhoneNumber("010-1234-5678");

        given(memberRepository.save(any(Member.class))).willReturn(MemberFixture.create());

        // when
        memberService.signUp(request);

        // then
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("emailCheck - success when email not taken")
    void emailCheck_success() {
        // given
        EmailCheckoutRequest request = new EmailCheckoutRequest();
        request.setEmail("available@test.com");

        given(queryDslMemberRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then (no exception thrown)
        memberService.emailCheck(request);
    }

    @Test
    @DisplayName("emailCheck - duplicate email throws CommonException")
    void emailCheck_duplicate() {
        // given
        Member member = MemberFixture.create();
        EmailCheckoutRequest request = new EmailCheckoutRequest();
        request.setEmail("test@test.com");

        given(queryDslMemberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> memberService.emailCheck(request))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.MEMBER_EXISTS));
    }

    @Test
    @DisplayName("fetch - success")
    void fetch_success() {
        // given
        Long memberId = 1L;
        Member member = MemberFixture.create();

        given(queryDslMemberRepository.fetchById(memberId)).willReturn(Optional.of(member));

        // when
        ApiResponse result = memberService.fetch(memberId);

        // then
        assertThat(result).isNotNull();
        verify(queryDslMemberRepository).fetchById(memberId);
    }

    @Test
    @DisplayName("fetch - member not found throws CommonException")
    void fetch_notFound() {
        // given
        Long memberId = 999L;

        given(queryDslMemberRepository.fetchById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.fetch(memberId))
                .isInstanceOf(CommonException.class)
                .satisfies(ex -> assertThat(((CommonException) ex).getStatus()).isEqualTo(Status.NOT_FOUND_MEMBER));
    }
}
