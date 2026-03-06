package com.tripplannerai.repository.address;

import com.tripplannerai.entity.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AddressRepository extends JpaRepository<Address, Long>,AddressCustomRepository {
}
