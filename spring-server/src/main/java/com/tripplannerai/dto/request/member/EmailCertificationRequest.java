package com.tripplannerai.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailCertificationRequest {
    @NotBlank(message = "email can't be null")
    private String email;
    @NotBlank(message = "clientId can't be null")
    private String clientId;
}
