-- =============================================
-- Trip Server DDL (MySQL 8.x)
-- 엔티티 기반 재생성
-- =============================================

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 독립 테이블 (FK 없음)

CREATE TABLE `member` (
    `member_id`     BIGINT       NOT NULL AUTO_INCREMENT,
    `email`         VARCHAR(100) NOT NULL,
    `password`      VARCHAR(255) NOT NULL,
    `nickname`      VARCHAR(20),
    `phone_number`  VARCHAR(13),
    `provider`      VARCHAR(10),
    `provider_id`   VARCHAR(50),
    `state`         TINYINT(1)   DEFAULT 0,
    `refresh_token` VARCHAR(2048),
    `customer_key`  VARCHAR(255),
    `ticket`        INT,
    `image_id`      BIGINT,
    `created_at`    DATETIME(6)  NOT NULL,
    `updated_at`    DATETIME(6)  NOT NULL,
    PRIMARY KEY (`member_id`),
    UNIQUE KEY `uk_member_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `address` (
    `address_id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `area_code`    VARCHAR(255),
    `sigungu_code` VARCHAR(255),
    `name`         VARCHAR(255),
    PRIMARY KEY (`address_id`),
    UNIQUE KEY `uk_address_area_sigungu` (`area_code`, `sigungu_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `category` (
    `category_id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `category_code` VARCHAR(255),
    `name`          VARCHAR(255),
    `parent_id`     BIGINT,
    PRIMARY KEY (`category_id`),
    CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `course` (
    `course_id`    BIGINT       NOT NULL AUTO_INCREMENT,
    `content_id`   VARCHAR(255),
    `title`        VARCHAR(255),
    `overview`     TEXT,
    `area_name`    VARCHAR(255),
    `duration`     VARCHAR(255),
    `distance`     VARCHAR(255),
    `rating`       DOUBLE       DEFAULT 0.0,
    `like_count`   INT          DEFAULT 0,
    `review_count` INT          DEFAULT 0,
    PRIMARY KEY (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `kind_place` (
    `kind_place_id` BIGINT       NOT NULL AUTO_INCREMENT,
    `price1`        VARCHAR(255),
    `price2`        VARCHAR(255),
    `price3`        VARCHAR(255),
    `price4`        VARCHAR(255),
    `menu1`         VARCHAR(255),
    `menu2`         VARCHAR(255),
    `menu3`         VARCHAR(255),
    `menu4`         VARCHAR(255),
    `address`       VARCHAR(255),
    `number`        VARCHAR(255),
    `name`          VARCHAR(255),
    `type`          VARCHAR(255),
    PRIMARY KEY (`kind_place_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `fail` (
    `id`        BIGINT       NOT NULL AUTO_INCREMENT,
    `content`   VARCHAR(255),
    `email`     VARCHAR(255),
    `client_id` VARCHAR(255),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `festival` (
    `like_id`    BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT,
    `content_id` VARCHAR(255),
    `create_at`  DATETIME(6),
    PRIMARY KEY (`like_id`),
    UNIQUE KEY `uk_festival_user_content` (`user_id`, `content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `idempotency` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `idempotency_key` VARCHAR(255),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_idempotency_key` (`idempotency_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. member 의존 테이블

CREATE TABLE `image` (
    `image_id`     BIGINT       NOT NULL AUTO_INCREMENT,
    `url`          VARCHAR(255),
    `content_type` VARCHAR(255),
    `review_id`    BIGINT,
    PRIMARY KEY (`image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `member`
    ADD CONSTRAINT `fk_member_image` FOREIGN KEY (`image_id`) REFERENCES `image` (`image_id`);

CREATE TABLE `social` (
    `social_id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `social_type` VARCHAR(255),
    `member_id`   BIGINT,
    PRIMARY KEY (`social_id`),
    CONSTRAINT `fk_social_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `point` (
    `id`        BIGINT NOT NULL AUTO_INCREMENT,
    `member_id` BIGINT,
    `amount`    BIGINT,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_point_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `search_log` (
    `search_log_id` BIGINT       NOT NULL AUTO_INCREMENT,
    `keyword`       VARCHAR(255),
    `search_date`   DATETIME(6),
    `member_id`     BIGINT,
    PRIMARY KEY (`search_log_id`),
    CONSTRAINT `fk_search_log_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. destination 및 관련 테이블

CREATE TABLE `destination` (
    `destination_id`      BIGINT       NOT NULL AUTO_INCREMENT,
    `name`                VARCHAR(255),
    `addr1`               VARCHAR(255),
    `addr2`               VARCHAR(255),
    `tel`                 VARCHAR(255),
    `content_id`          VARCHAR(255),
    `latitude`            VARCHAR(255),
    `longitude`           VARCHAR(255),
    `rating`              VARCHAR(255),
    `origin_image_url`    VARCHAR(1000),
    `thumbnail_image_url` VARCHAR(1000),
    `category_id`         BIGINT,
    `address_id`          BIGINT,
    PRIMARY KEY (`destination_id`),
    CONSTRAINT `fk_destination_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`),
    CONSTRAINT `fk_destination_address`  FOREIGN KEY (`address_id`)  REFERENCES `address` (`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `view_log` (
    `view_log_id`    BIGINT NOT NULL AUTO_INCREMENT,
    `view_log_date`  DATETIME(6),
    `destination_id` BIGINT,
    `member_id`      BIGINT,
    PRIMARY KEY (`view_log_id`),
    CONSTRAINT `fk_view_log_destination` FOREIGN KEY (`destination_id`) REFERENCES `destination` (`destination_id`),
    CONSTRAINT `fk_view_log_member`      FOREIGN KEY (`member_id`)      REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. review 관련 테이블

CREATE TABLE `review` (
    `review_id`      BIGINT       NOT NULL AUTO_INCREMENT,
    `title`          VARCHAR(255),
    `content`        VARCHAR(255),
    `rating`         VARCHAR(255),
    `destination_id` BIGINT,
    `member_id`      BIGINT,
    `created_at`     DATETIME(6)  NOT NULL,
    `updated_at`     DATETIME(6)  NOT NULL,
    PRIMARY KEY (`review_id`),
    CONSTRAINT `fk_review_destination` FOREIGN KEY (`destination_id`) REFERENCES `destination` (`destination_id`),
    CONSTRAINT `fk_review_member`      FOREIGN KEY (`member_id`)      REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `image`
    ADD CONSTRAINT `fk_image_review` FOREIGN KEY (`review_id`) REFERENCES `review` (`review_id`);

-- 5. receipt_review 관련 테이블

CREATE TABLE `receipt_review` (
    `receipt_review_id` BIGINT       NOT NULL AUTO_INCREMENT,
    `address`           VARCHAR(255),
    `title`             VARCHAR(255),
    `content`           VARCHAR(3000),
    `rating`            INT          NOT NULL,
    `member_id`         BIGINT,
    `created_at`        DATETIME(6),
    PRIMARY KEY (`receipt_review_id`),
    CONSTRAINT `fk_receipt_review_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `receipt_review_image` (
    `review_image_id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `image_url`         VARCHAR(255),
    `content_type`      VARCHAR(255),
    `receipt_review_id` BIGINT,
    PRIMARY KEY (`review_image_id`),
    CONSTRAINT `fk_receipt_review_image_review` FOREIGN KEY (`receipt_review_id`) REFERENCES `receipt_review` (`receipt_review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. comment 관련 테이블

CREATE TABLE `comment` (
    `comment_id`        BIGINT       NOT NULL AUTO_INCREMENT,
    `count`             INT          NOT NULL,
    `content`           VARCHAR(255),
    `member_id`         BIGINT,
    `receipt_review_id` BIGINT,
    `is_deleted`        TINYINT(1)   NOT NULL,
    `parent_comment_id` BIGINT,
    `path`              VARCHAR(255),
    `depth`             INT          NOT NULL,
    `created_at`        DATETIME(6)  NOT NULL,
    `updated_at`        DATETIME(6)  NOT NULL,
    PRIMARY KEY (`comment_id`),
    CONSTRAINT `fk_comment_member`         FOREIGN KEY (`member_id`)         REFERENCES `member` (`member_id`),
    CONSTRAINT `fk_comment_receipt_review` FOREIGN KEY (`receipt_review_id`) REFERENCES `receipt_review` (`receipt_review_id`),
    CONSTRAINT `fk_comment_parent`         FOREIGN KEY (`parent_comment_id`) REFERENCES `comment` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `comment_child_comments` (
    `comment_comment_id`        BIGINT NOT NULL,
    `child_comments_comment_id` BIGINT NOT NULL,
    UNIQUE KEY `uk_child_comments` (`child_comments_comment_id`),
    CONSTRAINT `fk_comment_children_parent` FOREIGN KEY (`comment_comment_id`)        REFERENCES `comment` (`comment_id`),
    CONSTRAINT `fk_comment_children_child`  FOREIGN KEY (`child_comments_comment_id`) REFERENCES `comment` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `comment_like` (
    `comment_like_id` BIGINT NOT NULL AUTO_INCREMENT,
    `member_id`       BIGINT,
    `comment_id`      BIGINT,
    PRIMARY KEY (`comment_like_id`),
    CONSTRAINT `fk_comment_like_member`  FOREIGN KEY (`member_id`)  REFERENCES `member` (`member_id`),
    CONSTRAINT `fk_comment_like_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. group 관련 테이블

CREATE TABLE `groups` (
    `group_id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `title`             VARCHAR(255),
    `status`            TINYINT(1)   NOT NULL,
    `description`       LONGTEXT,
    `count`             INT          NOT NULL,
    `participate_count` INT          NOT NULL,
    `group_like_count`  INT          NOT NULL,
    `max_count`         INT          NOT NULL,
    `start_date`        DATETIME(6),
    `end_date`          DATETIME(6),
    `destination_id`    BIGINT,
    `member_id`         BIGINT,
    PRIMARY KEY (`group_id`),
    CONSTRAINT `fk_groups_destination` FOREIGN KEY (`destination_id`) REFERENCES `destination` (`destination_id`),
    CONSTRAINT `fk_groups_member`      FOREIGN KEY (`member_id`)      REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `group_like` (
    `group__like_id` BIGINT NOT NULL AUTO_INCREMENT,
    `member_id`      BIGINT,
    `group_id`       BIGINT,
    PRIMARY KEY (`group__like_id`),
    CONSTRAINT `fk_group_like_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
    CONSTRAINT `fk_group_like_group`  FOREIGN KEY (`group_id`)  REFERENCES `groups` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `group_comment` (
    `group_comment_id` BIGINT       NOT NULL AUTO_INCREMENT,
    `count`            INT          NOT NULL,
    `content`          VARCHAR(255),
    `member_id`        BIGINT,
    `group_id`         BIGINT,
    `is_deleted`       TINYINT(1)   NOT NULL,
    `created_at`       DATETIME(6)  NOT NULL,
    `updated_at`       DATETIME(6)  NOT NULL,
    PRIMARY KEY (`group_comment_id`),
    CONSTRAINT `fk_group_comment_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
    CONSTRAINT `fk_group_comment_group`  FOREIGN KEY (`group_id`)  REFERENCES `groups` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `group_comment_like` (
    `group_comment_like_id` BIGINT NOT NULL AUTO_INCREMENT,
    `member_id`             BIGINT,
    `group_comment_id`      BIGINT,
    PRIMARY KEY (`group_comment_like_id`),
    CONSTRAINT `fk_group_comment_like_member`  FOREIGN KEY (`member_id`)        REFERENCES `member` (`member_id`),
    CONSTRAINT `fk_group_comment_like_comment` FOREIGN KEY (`group_comment_id`) REFERENCES `group_comment` (`group_comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `enroll` (
    `enroll_id` BIGINT     NOT NULL AUTO_INCREMENT,
    `accepted`  TINYINT(1) NOT NULL,
    `member_id` BIGINT,
    `group_id`  BIGINT,
    PRIMARY KEY (`enroll_id`),
    CONSTRAINT `fk_enroll_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
    CONSTRAINT `fk_enroll_group`  FOREIGN KEY (`group_id`)  REFERENCES `groups` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. course 관련 테이블

CREATE TABLE `course_spot` (
    `id`                     BIGINT NOT NULL AUTO_INCREMENT,
    `course_id`              BIGINT,
    `destination_content_id` VARCHAR(255),
    `sub_overview`           TEXT,
    `order_in_course`        INT    NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_course_spot_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `course_review` (
    `course_review_id` BIGINT        NOT NULL AUTO_INCREMENT,
    `course_id`        BIGINT,
    `member_id`        BIGINT,
    `rating`           INT           NOT NULL,
    `review`           VARCHAR(1000),
    PRIMARY KEY (`course_review_id`),
    CONSTRAINT `fk_course_review_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`),
    CONSTRAINT `fk_course_review_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `course_like` (
    `id`        BIGINT NOT NULL AUTO_INCREMENT,
    `course_id` BIGINT,
    `member_id` BIGINT,
    `liked_at`  DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_like` (`course_id`, `member_id`),
    CONSTRAINT `fk_course_like_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`),
    CONSTRAINT `fk_course_like_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tag` (
    `tag_id`    BIGINT       NOT NULL AUTO_INCREMENT,
    `course_id` BIGINT,
    `tag_name`  VARCHAR(255),
    PRIMARY KEY (`tag_id`),
    CONSTRAINT `fk_tag_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tip` (
    `tip_id`        BIGINT       NOT NULL AUTO_INCREMENT,
    `course_id`     BIGINT,
    `content`       VARCHAR(255),
    `display_order` INT,
    PRIMARY KEY (`tip_id`),
    CONSTRAINT `fk_tip_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. plan / itinerary / tour_spot 테이블

CREATE TABLE `plan` (
    `plan_id`    BIGINT NOT NULL AUTO_INCREMENT,
    `start_date` DATETIME(6),
    `end_date`   DATETIME(6),
    `area_code`  VARCHAR(255),
    `member_id`  BIGINT,
    `group_id`   BIGINT NOT NULL,
    PRIMARY KEY (`plan_id`),
    CONSTRAINT `fk_plan_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
    CONSTRAINT `fk_plan_group`  FOREIGN KEY (`group_id`)  REFERENCES `groups` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `itinerary` (
    `itinerary_id` BIGINT NOT NULL AUTO_INCREMENT,
    `day`          INT    NOT NULL,
    `plan_id`      BIGINT NOT NULL,
    PRIMARY KEY (`itinerary_id`),
    CONSTRAINT `fk_itinerary_plan` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tour_spot` (
    `tour_spot_id`   BIGINT NOT NULL AUTO_INCREMENT,
    `orders`         INT,
    `itinerary_id`   BIGINT,
    `destination_id` BIGINT,
    PRIMARY KEY (`tour_spot_id`),
    CONSTRAINT `fk_tour_spot_itinerary`   FOREIGN KEY (`itinerary_id`)   REFERENCES `itinerary` (`itinerary_id`),
    CONSTRAINT `fk_tour_spot_destination` FOREIGN KEY (`destination_id`) REFERENCES `destination` (`destination_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. payment 관련 테이블

CREATE TABLE `payment` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `payment_key`  VARCHAR(255),
    `amount`       BIGINT,
    `order_id`     VARCHAR(255),
    `member_id`    BIGINT,
    `status`       VARCHAR(255),
    `created_time` DATETIME(6),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_key` (`payment_key`),
    UNIQUE KEY `uk_payment_order_id` (`order_id`),
    CONSTRAINT `fk_payment_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `temp_payment` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `payment_key`  VARCHAR(255),
    `order_id`     VARCHAR(255),
    `amount`       BIGINT,
    `member_id`    BIGINT,
    `status`       VARCHAR(255),
    `retry`        INT,
    `created_time` DATETIME(6),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_temp_payment_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `webhook_history` (
                                   `id`           BIGINT       NOT NULL AUTO_INCREMENT,
                                   `payment_key`  VARCHAR(255),
                                   `order_id`     VARCHAR(255),
                                   `amount`       BIGINT,
                                   `status`       VARCHAR(255),
                                   `processed`    TINYINT(1)   NOT NULL,
                                   `created_time` DATETIME(6),
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




SET FOREIGN_KEY_CHECKS = 1;
