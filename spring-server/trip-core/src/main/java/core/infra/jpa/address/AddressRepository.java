package core.infra.jpa.address;

import core.domain.entity.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AddressRepository extends JpaRepository<Address, Long>{
}
