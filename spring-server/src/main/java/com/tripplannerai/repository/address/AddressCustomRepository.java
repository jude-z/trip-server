package com.tripplannerai.repository.address;

import com.tripplannerai.entity.address.Address;

import java.util.Optional;

public interface AddressCustomRepository {
    Optional<Address> findAddressByCodes(String areaCode, String sigunguCode);
}
