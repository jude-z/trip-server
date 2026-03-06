package com.tripplannerai.dto.request.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateRequest {

    @NotBlank(message = "email can't be null")
    @Email
    private String email;
    @NotBlank(message = "password can't be null")
    private String password;
    @NotBlank(message = "nickname can't be null")
    private String nickname;
    @NotBlank(message = "phoneNumber can't be null")
    private String phoneNumber;

}
