package com.tripplannerai.dto.request.recommend;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RecommendRequestDto {
    @NotNull
    String areaCode;

    String sigunguCode;

    @NotNull
    List<String> categoryCodes;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date startDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date endDate;
}
