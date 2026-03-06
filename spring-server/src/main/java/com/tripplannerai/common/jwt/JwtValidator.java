package com.tripplannerai.common.jwt;

import com.tripplannerai.common.exception.member.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtValidator {

    @Value("${jwt.secretKey}")
    private String secretKey;

    public Claims validateToken(String jwtToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
        }catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new InvalidJwtTokenException("invalid Token!!");
        }
    }
}
