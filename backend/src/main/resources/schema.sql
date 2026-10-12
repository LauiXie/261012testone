-- 身份证信息表（可重复执行）
CREATE TABLE IF NOT EXISTS id_card
(
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name       VARCHAR(64)  NOT NULL COMMENT '姓名',
    id_number  VARCHAR(32)  NOT NULL COMMENT '身份证号',
    gender     VARCHAR(8)            DEFAULT NULL COMMENT '性别：男/女',
    birth_date DATE                  DEFAULT NULL COMMENT '出生日期',
    address    VARCHAR(255)          DEFAULT NULL COMMENT '地址',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_id_card_number (id_number)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='身份证信息表';
