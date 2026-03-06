package com.tripplannerai.entity.kindplace;

import com.tripplannerai.dto.response.kindplace.ApiKindPlace;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "kind_place")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KindPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kindPlaceId;
    private String price1;
    private String price2;
    private String price3;
    private String price4;
    private String menu1;
    private String menu2;
    private String menu3;
    private String menu4;
    private String address;
    private String number;
    private String name;
    private String type;

    public static KindPlace of(ApiKindPlace apiKindPlace) {
        return KindPlace.builder()
                .price1(apiKindPlace.get가격1())
                .price2(apiKindPlace.get가격2())
                .price3(apiKindPlace.get가격3())
                .price4(apiKindPlace.get가격4())
                .menu1(apiKindPlace.get메뉴1())
                .menu2(apiKindPlace.get메뉴2())
                .menu3(apiKindPlace.get메뉴3())
                .menu4(apiKindPlace.get메뉴4())
                .address(apiKindPlace.get주소())
                .number(apiKindPlace.get연락처())
                .name(apiKindPlace.get업소명())
                .type(apiKindPlace.get업종())
                .build();
    }
}
