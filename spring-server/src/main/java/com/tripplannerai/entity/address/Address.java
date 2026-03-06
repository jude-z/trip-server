package com.tripplannerai.entity.address;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "address", uniqueConstraints = @UniqueConstraint(columnNames = {"area_code", "sigungu_code"}))
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;
    @Column(name = "area_code")
    private String areaCode;
    @Column(name = "sigungu_code")
    private String sigunguCode;
    private String name;

}
