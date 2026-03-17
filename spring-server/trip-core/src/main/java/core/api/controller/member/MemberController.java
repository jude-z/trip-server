package core.api.controller.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import core.api.common.resolver.annotation.Id;
import core.api.request.member.EmailCheckoutRequest;
import core.api.request.member.SignInRequest;
import core.api.request.member.SignUpRequest;
import core.api.request.member.UpdateRequest;
import core.api.response.ApiResponse;
import core.service.member.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse> signIn(@Valid @RequestBody SignInRequest signInRequest, HttpServletResponse response) throws JsonProcessingException {
        return ResponseEntity.ok(memberService.signIn(signInRequest, response));
    }

    @PostMapping(value = "/auth/sign-up")
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(memberService.signUp(signUpRequest));
    }

    @PutMapping(value = "/member/{memberId}")
    public ResponseEntity<ApiResponse> update(@Valid @RequestPart UpdateRequest updateRequest,
                                              @RequestPart(name = "file", required = false) MultipartFile file,
                                              @PathVariable Long memberId) throws IOException {
        return ResponseEntity.ok(memberService.update(updateRequest, file, memberId));
    }

    @GetMapping(value = "/member")
    public ResponseEntity<ApiResponse> fetch(@Id Long memberId) {
        return ResponseEntity.ok(memberService.fetch(memberId));
    }

    @PostMapping(value = "/email-check")
    public ResponseEntity<ApiResponse> emailCheck(@Valid @RequestBody EmailCheckoutRequest emailCheckoutRequest) {
        return ResponseEntity.ok(memberService.emailCheck(emailCheckoutRequest));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) throws JsonProcessingException {
        return ResponseEntity.ok(memberService.refresh(refreshToken, response));
    }
}
