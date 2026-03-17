package core.api.controller.member;

import core.api.common.resolver.IdResolver;
import core.api.common.resolver.UsernameResolver;
import core.api.request.member.EmailCheckoutRequest;
import core.api.request.member.SignInRequest;
import core.api.request.member.SignUpRequest;
import core.api.response.ApiDataResponse;
import core.api.response.ApiStatusResponse;
import core.common.Status;
import core.service.member.MemberService;
import core.support.MockMvcTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({IdResolver.class, UsernameResolver.class})
class MemberControllerTest extends MockMvcTestSupport {

    @MockitoBean
    private MemberService memberService;

    @Test
    @DisplayName("signIn_success")
    void signIn_success() throws Exception {
        SignInRequest request = new SignInRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(memberService.signIn(any(SignInRequest.class), any()))
                .thenReturn(ApiDataResponse.of("token", Status.SUCCESS));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("signUp_success")
    void signUp_success() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setNickname("testuser");
        request.setPhoneNumber("01012345678");

        when(memberService.signUp(any(SignUpRequest.class)))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("fetchMember_success")
    void fetchMember_success() throws Exception {
        when(memberService.fetch(any(Long.class)))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(get("/member"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("emailCheck_success")
    void emailCheck_success() throws Exception {
        EmailCheckoutRequest request = new EmailCheckoutRequest();
        request.setEmail("test@example.com");

        when(memberService.emailCheck(any(EmailCheckoutRequest.class)))
                .thenReturn(ApiStatusResponse.of(Status.SUCCESS));

        mockMvc.perform(post("/email-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
