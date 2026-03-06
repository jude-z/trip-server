package com.tripplannerai.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificationRequest {
    @NotBlank(message = "email can't be null")
    private String email;
    @NotBlank(message = "certification can't be null")
    private String certification;
}
