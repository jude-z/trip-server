CREATE TABLE payment (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_key  VARCHAR(255),
    order_id     VARCHAR(255),
    amount       BIGINT,
    retry        INT,
    create_time  DATETIME(6)
);
