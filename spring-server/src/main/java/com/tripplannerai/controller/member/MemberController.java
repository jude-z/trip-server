package com.tripplannerai.controller.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tripplannerai.common.annotation.Id;
import com.tripplannerai.dto.request.member.CertificationRequest;
import com.tripplannerai.dto.request.member.EmailCertificationRequest;
import com.tripplannerai.dto.request.member.EmailCheckoutRequest;
import com.tripplannerai.dto.request.member.SignInRequest;
import com.tripplannerai.dto.request.member.SignUpRequest;
import com.tripplannerai.dto.request.member.UpdateRequest;
import com.tripplannerai.dto.response.member.*;
import com.tripplannerai.service.member.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/auth/login")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest signInRequest, HttpServletResponse response) throws JsonProcessingException {
        SignInResponse signInResponse = memberService.signIn(signInRequest,response);
        return new ResponseEntity<>(signInResponse, HttpStatus.OK);
    }
    @PostMapping(value = "/auth/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse = memberService.signUp(signUpRequest);
        return new ResponseEntity<>(signUpResponse, HttpStatus.OK);
    }
    @PutMapping(value = "/member/{memberId}")
    public ResponseEntity<UpdateResponse> update(@Valid @RequestPart UpdateRequest updateRequest,
            @RequestPart(name = "file",required = false) MultipartFile file,
            @PathVariable Long memberId) throws IOException {
        UpdateResponse updateResponse = memberService.update(updateRequest,file,memberId);
        return new ResponseEntity<>(updateResponse, HttpStatus.OK);
    }
    @GetMapping(value = "/member")
    public ResponseEntity<FetchMemberResponse> fetch(@Id Long memberId){
        FetchMemberResponse fetchMemberResponse = memberService.fetch(memberId);
        return new ResponseEntity<>(fetchMemberResponse, HttpStatus.OK);
    }
    @PostMapping(value = "/email-check")
    public ResponseEntity<EmailCheckoutResponse> emailCheck(@Valid @RequestBody EmailCheckoutRequest emailCheckoutRequest) {
        EmailCheckoutResponse emailCheckoutResponse = memberService.emailCheck(emailCheckoutRequest);
        return new ResponseEntity<>(emailCheckoutResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/certification")
    public ResponseEntity<SendCertificationResponse> sendCertification(@Valid @RequestBody EmailCertificationRequest emailCertificationRequest) {
        SendCertificationResponse sendCertificationResponse = memberService.sendCertification(emailCertificationRequest);
        return new ResponseEntity<>(sendCertificationResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/check-certification")
    public ResponseEntity<CheckCertificationResponse> checkCertification(@Valid @RequestBody CertificationRequest certificationRequest) {
        CheckCertificationResponse checkCertificationResponse = memberService.checkCertification(certificationRequest);
        return new ResponseEntity<>(checkCertificationResponse, HttpStatus.OK);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<RefreshResponse> refresh(@CookieValue(name = "refreshToken",required = false) String refreshToken, HttpServletResponse response) throws JsonProcessingException {
        RefreshResponse refreshResponse = memberService.refresh(refreshToken,response);
        return new ResponseEntity<>(refreshResponse, HttpStatus.OK);
    }

}
