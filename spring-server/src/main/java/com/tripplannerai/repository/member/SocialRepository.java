package com.tripplannerai.repository.member;

import com.tripplannerai.entity.social.Social;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialRepository extends JpaRepository<Social,Long> {
}
