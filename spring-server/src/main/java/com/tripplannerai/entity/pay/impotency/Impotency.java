package com.tripplannerai.entity.pay.impotency;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Impotency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "imptency_key")
    private String impotencyKey;

    public static Impotency of(String impotencyKey) {
        return Impotency.builder()
                .impotencyKey(impotencyKey).build();
    }
}
