package com.tripplannerai.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeletePlanResponse {
    private String code;
    private String message;

    public static DeletePlanResponse of(String code, String message) {
        return DeletePlanResponse.builder().code(code).message(message).build();
    }
}
