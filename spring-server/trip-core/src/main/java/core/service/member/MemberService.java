package core.service.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.api.response.ApiDataResponse;
import core.api.response.ApiResponse;
import core.api.response.ApiStatusResponse;
import core.api.security.jwt.JwtSubject;
import core.api.request.member.EmailCheckoutRequest;
import core.api.request.member.SignInRequest;
import core.api.request.member.SignUpRequest;
import core.api.request.member.UpdateRequest;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.image.Image;
import core.domain.entity.member.Member;
import core.domain.mapper.MemberFactory;
import core.infra.jpa.image.ImageRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.querydsl.member.QueryDslMemberRepository;
import core.api.security.jwt.JwtProvider;
import core.service.s3.S3UploadService;
import core.api.security.jwt.JwtValidator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static core.common.util.CookieUtil.getCookie;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final QueryDslMemberRepository queryDslMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageRepository imageRepository;
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final S3UploadService s3UploadService;
    private ObjectMapper mapper = new ObjectMapper();
    @Value("${server.url}")
    private String serverUrl;
    @Value("${jwt.refresh.expiration}")
    private int refreshExpiration;

    public ApiResponse signIn(SignInRequest signInRequest, HttpServletResponse response) throws JsonProcessingException {

        Member member = queryDslMemberRepository.findByEmail(signInRequest.getEmail()).orElseThrow(()-> new CommonException(Status.NOT_FOUND_MEMBER));
        boolean matches = passwordEncoder.matches(signInRequest.getPassword(), member.getPassword());
        if(!matches){
            throw new CommonException(Status.UN_CORRECT_PASSWORD);
        }
        JwtSubject jwtSubject = JwtSubject.of(member);
        String accessToken = jwtProvider.createAccessToken(mapper.writeValueAsString(jwtSubject),"user");
        String refreshToken = jwtProvider.createRefreshToken(mapper.writeValueAsString(jwtSubject),"user");
        Cookie cookie = getCookie("refreshToken",refreshToken,refreshExpiration);
        response.addCookie(cookie);
        member.setRefreshToken(refreshToken);
        return ApiDataResponse.of(accessToken, Status.SUCCESS);
    }

    public ApiResponse signUp(SignUpRequest signUpRequest){

            Member member = MemberFactory.of(signUpRequest);
            memberRepository.save(member);
            return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse emailCheck( EmailCheckoutRequest emailCheckoutRequest) {
        Optional<Member> optionalMember = queryDslMemberRepository.findByEmail(emailCheckoutRequest.getEmail());
        if(optionalMember.isPresent()){
            throw new CommonException(Status.MEMBER_EXISTS);
        }
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse update(UpdateRequest updateRequest, MultipartFile file, Long memberId) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
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

        return ApiDataResponse.of(member, Status.SUCCESS);
    }
    public ApiResponse fetch(Long memberId) {
        Member member = queryDslMemberRepository.fetchById(memberId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        String imageUrl = member.getImage() == null ? null : getUrl(member.getImage());
        return ApiDataResponse.of(MemberFactory.of(member, imageUrl), Status.SUCCESS);
    }

    private String getUrl(Image image){
        return serverUrl + image.getImageId();
    }

    public ApiResponse refresh(String refreshToken, HttpServletResponse response) throws JsonProcessingException {

        Claims claims = jwtValidator.validateToken(refreshToken);
        String subject = claims.getSubject();
        JwtSubject jwtSubject = mapper.readValue(subject, JwtSubject.class);
        Member member = memberRepository.findById(jwtSubject.getId())
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        String accessToken = jwtProvider.createAccessToken(mapper.writeValueAsString(jwtSubject),"user");
        String issuedRefreshToken = jwtProvider.createRefreshToken(mapper.writeValueAsString(jwtSubject),"user");
        Cookie cookie = getCookie("refreshToken",refreshToken,refreshExpiration);
        response.addCookie(cookie);
        member.setRefreshToken(issuedRefreshToken);
        return ApiDataResponse.of(accessToken, Status.SUCCESS);
    }
}
