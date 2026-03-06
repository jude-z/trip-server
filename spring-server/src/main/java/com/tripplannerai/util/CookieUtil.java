package com.tripplannerai.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    public static Cookie getCookie(String name, String value, int expiration){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiration);
        return cookie;
    }

    public static void addCrossDomainCookie(HttpServletResponse response, String name, String value, int expiration, boolean httpOnly) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .maxAge(expiration)
                .httpOnly(httpOnly)
                .sameSite("None")
                .secure(true)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
