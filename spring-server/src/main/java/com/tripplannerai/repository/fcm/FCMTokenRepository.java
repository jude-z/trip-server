package com.tripplannerai.repository.fcm;

import com.tripplannerai.entity.fcm.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
}
