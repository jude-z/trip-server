package com.tripplannerai.entity.certification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id")
    private Long certificationId;
    private String email;
    @Column(name = "certification_number")
    private String certificationNumber;

    public static Certification of(String email, String certificationNumber) {
        return Certification.builder()
                .email(email)
                .certificationNumber(certificationNumber)
                .build();
    }
}
