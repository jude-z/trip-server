package com.tripplannerai.common.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

import static com.tripplannerai.util.ConstClass.*;

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2FailureHandler.class);
    @Value("${oauth2SuccessRedirectUrl}")
    private String oauth2SuccessRedirectUrl;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String error = request.getParameter("error");

        record ErrorPair(String errorCode, String errorMessage) {}
        ErrorPair errorPair = switch (error) {
            case "access_denied" -> new ErrorPair(
                    OAUTH_DENIED_CODE,
                    OAUTH_DENIED_MESSAGE
            );
            case "invalid_client" -> new ErrorPair(
                    OAUTH_INVALID_CLIENT_CODE,
                    OAUTH_INVALID_CLIENT_MESSAGE
            );
            case "server_error" -> new ErrorPair(
                    OAUTH_SERVER_ERROR_CODE,
                    OAUTH_SERVER_ERROR_MESSAGE
            );
            case null -> new ErrorPair(
                    AUTHENTICATION_FAILED_CODE,
                    AUTHENTICATION_FAILED_MESSAGE + ": " + exception.getMessage()
            );
            default -> new ErrorPair(
                    AUTHENTICATION_FAILED_CODE,
                    AUTHENTICATION_FAILED_MESSAGE + ": " + error
            );
        };
        logger.error("소셜 로그인 실패 - error: {}, message: {}",error, exception.getMessage());

        String redirectUrl = oauth2SuccessRedirectUrl + "?status=error&errorCode=" + errorPair.errorCode()
                + "&errorMessage=" + URLEncoder.encode(errorPair.errorMessage(),"UTF-8");
        response.sendRedirect(redirectUrl);
    }
}
