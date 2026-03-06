package com.tripplannerai.service.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripplannerai.common.exception.member.*;
import com.tripplannerai.common.jwt.JwtSubject;
import com.tripplannerai.dto.request.member.CertificationRequest;
import com.tripplannerai.dto.request.member.EmailCertificationRequest;
import com.tripplannerai.dto.request.member.EmailCheckoutRequest;
import com.tripplannerai.dto.request.member.SignInRequest;
import com.tripplannerai.dto.request.member.SignUpRequest;
import com.tripplannerai.dto.request.member.UpdateRequest;
import com.tripplannerai.dto.response.member.*;
import com.tripplannerai.entity.image.Image;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.mapper.MemberFactory;
import com.tripplannerai.repository.certification.CertificationRepository;
import com.tripplannerai.repository.image.ImageRepository;
import com.tripplannerai.repository.member.MemberRepository;
import com.tripplannerai.common.jwt.JwtProvider;
import com.tripplannerai.common.s3.S3UploadService;
import com.tripplannerai.service.AsyncService;
import com.tripplannerai.common.jwt.JwtValidator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.tripplannerai.util.ConstClass.SUCCESS_CODE;
import static com.tripplannerai.util.ConstClass.SUCCESS_MESSAGE;
import static com.tripplannerai.util.CookieUtil.getCookie;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CertificationRepository certificationRepository;
    private final ImageRepository imageRepository;
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final AsyncService asyncService;
    private final S3UploadService s3UploadService;
    private ObjectMapper mapper = new ObjectMapper();
    @Value("${server.url}")
    private String serverUrl;
    @Value("${jwt.refresh.expiration}")
    private int refreshExpiration;

    public SignInResponse signIn(SignInRequest signInRequest, HttpServletResponse response) throws JsonProcessingException {

        Member member = memberRepository.findByEmail(signInRequest.getEmail()).orElseThrow(()-> new NotFoundMemberException("not Found Member"));
        boolean matches = passwordEncoder.matches(signInRequest.getPassword(), member.getPassword());
        if(!matches){
            throw new UnCorrectPasswordException("doesn't match Password!");
        }
        JwtSubject jwtSubject = JwtSubject.of(member);
        String accessToken = jwtProvider.createAccessToken(mapper.writeValueAsString(jwtSubject),"user");
        String refreshToken = jwtProvider.createRefreshToken(mapper.writeValueAsString(jwtSubject),"user");
        Cookie cookie = getCookie("refreshToken",refreshToken,refreshExpiration);
        response.addCookie(cookie);
        member.setRefreshToken(refreshToken);
        return SignInResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,accessToken);
    }

    public SignUpResponse signUp(SignUpRequest signUpRequest){

            Member member = MemberFactory.of(signUpRequest);
            memberRepository.save(member);
            return SignUpResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public EmailCheckoutResponse emailCheck( EmailCheckoutRequest emailCheckoutRequest) {
        Optional<Member> optionalMember = memberRepository.findByEmail(emailCheckoutRequest.getEmail());
        if(optionalMember.isPresent()){
            throw new MemberExistException("member exist");
        }
        return EmailCheckoutResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public SendCertificationResponse sendCertification(EmailCertificationRequest emailCertificationRequest) {
        asyncService.asyncTask(emailCertificationRequest);
        return SendCertificationResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public CheckCertificationResponse checkCertification(CertificationRequest certificationRequest) {
        String certificationNumberCheck = certificationRequest.getCertification();
        String certificationNumber = certificationRepository.findCertificationNumberByEmail(certificationRequest.getEmail());
        if(!StringUtils.hasText(certificationNumber)) throw new NotFoundCertificationException("not found certification");
        if(!certificationNumber.equals(certificationNumberCheck)){
            throw new NotCorrectCertificationException("not correct certification");
        }
        return CheckCertificationResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    public UpdateResponse update(UpdateRequest updateRequest, MultipartFile file,Long memberId) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("not Found Member"));
        Image image = null;
        if(!file.isEmpty()){
            String contentType = file.getContentType();
            String url = s3UploadService.upload(file);
            image = new Image();
            image.setUrl(url);
            image.setContentType(contentType);
            imageRepository.save(image);
            member.setImage(image);
        }
        member.setEmail(updateRequest.getEmail());
        member.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        member.setNickname(updateRequest.getNickname());
        member.setPhoneNumber(updateRequest.getPhoneNumber());

        return UpdateResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,member);
    }
    public FetchMemberResponse fetch(Long memberId) {
        Member member = memberRepository.fetchById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("not Found Member"));
        String imageUrl = member.getImage() == null ? null : getUrl(member.getImage());
        return MemberFactory.of(member, imageUrl);
    }

    private String getUrl(Image image){
        return serverUrl + image.getImageId();
    }

    public RefreshResponse refresh(String refreshToken, HttpServletResponse response) throws JsonProcessingException {

        Claims claims = jwtValidator.validateToken(refreshToken);
        String subject = claims.getSubject();
        JwtSubject jwtSubject = mapper.readValue(subject, JwtSubject.class);
        Member member = memberRepository.findById(jwtSubject.getId())
                .orElseThrow(() -> new NotFoundMemberException("not Found Member"));
        String accessToken = jwtProvider.createAccessToken(mapper.writeValueAsString(jwtSubject),"user");
        String issuedRefreshToken = jwtProvider.createRefreshToken(mapper.writeValueAsString(jwtSubject),"user");
        Cookie cookie = getCookie("refreshToken",refreshToken,refreshExpiration);
        response.addCookie(cookie);
        member.setRefreshToken(issuedRefreshToken);
        return RefreshResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,accessToken);
    }
}
