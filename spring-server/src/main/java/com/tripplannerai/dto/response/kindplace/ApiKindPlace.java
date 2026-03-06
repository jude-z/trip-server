package com.tripplannerai.dto.response.kindplace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiKindPlace {
    private String 가격1;
    private String 가격2;
    private String 가격3;
    private String 가격4;
    private String 메뉴1;
    private String 메뉴2;
    private String 메뉴3;
    private String 메뉴4;
    private String 주소;
    private String 연락처;
    private String 업소명;
    private String 업종;
    private String 시도;
    private String 시군;
}
