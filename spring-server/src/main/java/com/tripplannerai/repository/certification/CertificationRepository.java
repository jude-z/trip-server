package com.tripplannerai.repository.certification;

import com.tripplannerai.entity.certification.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    @Query("select c.certificationNumber from Certification c where c.email =:email order by c.certificationId desc limit 1")
    String findCertificationNumberByEmail(@Param("email") String email);
}
