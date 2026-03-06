package com.tripplannerai.repository.fcm;

import com.tripplannerai.entity.fcm.FCMTokenTopic;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FCMTokenTopicRepository extends JpaRepository<FCMTokenTopic, Long> {
}
