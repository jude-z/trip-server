package com.tripplannerai.common.security;

import com.tripplannerai.common.jwt.JwtSubject;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
@Getter
public class CustomUserDetails implements UserDetails {

    private JwtSubject jwtSubject;

    public CustomUserDetails(JwtSubject jwtSubject,List<GrantedAuthority> authorities) {
        this.jwtSubject = jwtSubject;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return jwtSubject.getEmail();
    }
}
