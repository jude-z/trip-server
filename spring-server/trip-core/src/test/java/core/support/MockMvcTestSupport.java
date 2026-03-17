package core.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.api.security.basic.userdetail.CustomUserDetails;
import core.api.security.jwt.JwtSubject;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

public abstract class MockMvcTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected static final Long TEST_MEMBER_ID = 1L;
    protected static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUpSecurityContext() {
        JwtSubject jwtSubject = JwtSubject.builder()
                .id(TEST_MEMBER_ID)
                .email(TEST_EMAIL)
                .nickname("tester")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(
                jwtSubject,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
