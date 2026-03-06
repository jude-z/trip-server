package com.tripplannerai.mapper;

import com.tripplannerai.entity.address.Address;

public class AddressFactory {

    public static Address of(String areaCode,String areaName,String sigunguCode) {
        return Address.builder()
                .areaCode(areaCode)
                .name(areaName)
                .sigunguCode(sigunguCode)
                .build();
    }
}
