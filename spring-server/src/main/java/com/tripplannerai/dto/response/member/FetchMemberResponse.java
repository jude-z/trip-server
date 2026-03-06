package com.tripplannerai.dto.response.member;

import com.tripplannerai.entity.image.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FetchMemberResponse {
    private String code;
    private String message;
    private Long id;
    private String email;
    private String nickname;
    private String phoneNumber;
    private String provider;
    private String providerId;
    private boolean isWithdrawn;
    private String imageUrl;
    private String customerKey;
    private Integer ticket;
}
