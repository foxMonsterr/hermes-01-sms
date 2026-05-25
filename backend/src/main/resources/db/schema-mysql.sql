-- ============================================================
-- 零食管理系统 — MySQL 完整建表脚本
-- 数据库: snack_manager
-- 表数: 17
-- ============================================================

CREATE DATABASE IF NOT EXISTS snack_manager DEFAULT CHARSET utf8mb4;
USE snack_manager;

-- ============================================================
-- 1. 管理员用户表
-- ============================================================
CREATE TABLE sys_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) NOT NULL,
    password    VARCHAR(200) NOT NULL,
    nickname    VARCHAR(50),
    avatar_url  VARCHAR(500) DEFAULT '',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted  TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE INDEX uk_username (username)
) ENGINE=InnoDB;

-- ============================================================
-- 2. 零食分类表
-- ============================================================
CREATE TABLE snack_category (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    name        VARCHAR(50) NOT NULL,
    icon        VARCHAR(50) DEFAULT '',
    sort_order  INT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted  TINYINT(1) NOT NULL DEFAULT 0,
    INDEX idx_category_user (user_id),
    UNIQUE INDEX uk_category_user_name (user_id, name)
) ENGINE=InnoDB;

-- ============================================================
-- 3. 零食表
-- ============================================================
CREATE TABLE snack (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    name          VARCHAR(100) NOT NULL,
    category_id   BIGINT NOT NULL,
    quantity      INT NOT NULL DEFAULT 1,
    unit          VARCHAR(20) NOT NULL DEFAULT '包',
    price         DECIMAL(10,2) DEFAULT NULL,
    image_url     VARCHAR(500) DEFAULT '',
    purchase_date DATE,
    expiry_date   DATE,
    notes         VARCHAR(500) DEFAULT '',
    is_on_shelf   TINYINT(1) NOT NULL DEFAULT 0,
    description   VARCHAR(1000) DEFAULT '',
    shelf_time    DATETIME DEFAULT NULL,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted    TINYINT(1) NOT NULL DEFAULT 0,
    INDEX idx_snack_user (user_id),
    INDEX idx_snack_category (user_id, category_id),
    INDEX idx_snack_expiry (expiry_date),
    INDEX idx_snack_name (name)
) ENGINE=InnoDB;

-- ============================================================
-- 4. 库存流水表
-- ============================================================
CREATE TABLE stock_record (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    snack_id      BIGINT NOT NULL,
    snack_name    VARCHAR(100) NOT NULL,
    change_type   VARCHAR(20) NOT NULL,
    change_qty    INT NOT NULL,
    before_qty    INT NOT NULL,
    after_qty     INT NOT NULL,
    remark        VARCHAR(200) DEFAULT '',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sr_user_time (user_id, create_time),
    INDEX idx_sr_user_snack (user_id, snack_id),
    INDEX idx_sr_user_type (user_id, change_type)
) ENGINE=InnoDB;

-- ============================================================
-- 5. 采购清单表
-- ============================================================
CREATE TABLE shopping_list (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    snack_id      BIGINT DEFAULT NULL,
    snack_name    VARCHAR(100) NOT NULL,
    category_id   BIGINT,
    planned_qty   INT NOT NULL DEFAULT 1,
    actual_qty    INT DEFAULT NULL,
    price         DECIMAL(10,2) DEFAULT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    source        VARCHAR(20) DEFAULT 'MANUAL',
    supplier_id   BIGINT DEFAULT NULL,
    supplier_name VARCHAR(100) DEFAULT '',
    bought_time   DATETIME DEFAULT NULL,
    remark        VARCHAR(200) DEFAULT '',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted    TINYINT(1) NOT NULL DEFAULT 0,
    INDEX idx_sl_user_status (user_id, status),
    INDEX idx_sl_user_snack (user_id, snack_id)
) ENGINE=InnoDB;

-- ============================================================
-- 6. 消息提醒表
-- ============================================================
CREATE TABLE notification (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    type        VARCHAR(30) NOT NULL,
    title       VARCHAR(100) NOT NULL,
    content     VARCHAR(500) NOT NULL,
    related_id  BIGINT NOT NULL,
    notify_date DATE NOT NULL,
    is_read     TINYINT(1) NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notif_user_read (user_id, is_read),
    INDEX idx_notif_user_type (user_id, type),
    INDEX idx_notif_user_time (user_id, create_time)
) ENGINE=InnoDB;

-- ============================================================
-- 7. 系统配置表
-- ============================================================
CREATE TABLE system_config (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    config_key   VARCHAR(50) NOT NULL,
    config_value VARCHAR(200) NOT NULL,
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_config_user_key (user_id, config_key)
) ENGINE=InnoDB;

-- ============================================================
-- 8. 过期/丢弃处理记录表
-- ============================================================
CREATE TABLE disposal_record (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    snack_id      BIGINT NOT NULL,
    snack_name    VARCHAR(100) NOT NULL,
    quantity      INT NOT NULL,
    unit          VARCHAR(20) DEFAULT '包',
    unit_price    DECIMAL(10,2) DEFAULT NULL,
    total_loss    DECIMAL(10,2) DEFAULT NULL,
    reason        VARCHAR(50) NOT NULL,
    remark        VARCHAR(200) DEFAULT '',
    dispose_date  DATE NOT NULL,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dr_user_date (user_id, dispose_date),
    INDEX idx_dr_user_snack (user_id, snack_id)
) ENGINE=InnoDB;

-- ============================================================
-- 9. 库存盘点记录表
-- ============================================================
CREATE TABLE inventory_check_record (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    snack_id      BIGINT NOT NULL,
    snack_name    VARCHAR(100) NOT NULL,
    system_qty    INT NOT NULL,
    actual_qty    INT NOT NULL,
    difference    INT NOT NULL,
    remark        VARCHAR(200) DEFAULT '',
    check_date    DATE NOT NULL,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_icr_user_date (user_id, check_date),
    INDEX idx_icr_user_snack (user_id, snack_id)
) ENGINE=InnoDB;

-- ============================================================
-- 10. 供应商表
-- ============================================================
CREATE TABLE supplier (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    name        VARCHAR(100) NOT NULL,
    contact     VARCHAR(50) DEFAULT '',
    phone       VARCHAR(30) DEFAULT '',
    address     VARCHAR(200) DEFAULT '',
    notes       VARCHAR(500) DEFAULT '',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted  TINYINT(1) NOT NULL DEFAULT 0,
    INDEX idx_supplier_user (user_id)
) ENGINE=InnoDB;

-- ============================================================
-- 11. 操作日志表
-- ============================================================
CREATE TABLE operation_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    username    VARCHAR(50) NOT NULL,
    action      VARCHAR(50) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id   BIGINT,
    detail      VARCHAR(500) DEFAULT '',
    result      VARCHAR(20) DEFAULT 'SUCCESS',
    ip          VARCHAR(50) DEFAULT '',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_log_user_time (user_id, create_time)
) ENGINE=InnoDB;

-- ============================================================
-- 12. 客户端用户表 (v7.0)
-- ============================================================
CREATE TABLE shop_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) NOT NULL,
    password    VARCHAR(200) NOT NULL,
    nickname    VARCHAR(50) DEFAULT '',
    phone       VARCHAR(30) DEFAULT '',
    avatar_url  VARCHAR(500) DEFAULT '',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted  TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE INDEX uk_shop_username (username)
) ENGINE=InnoDB;

-- ============================================================
-- 13. 购物车表 (v7.0)
-- ============================================================
CREATE TABLE shop_cart (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    snack_id    BIGINT NOT NULL,
    quantity    INT NOT NULL DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_cart_user_snack (user_id, snack_id),
    INDEX idx_cart_user (user_id)
) ENGINE=InnoDB;

-- ============================================================
-- 14. 订单表 (v7.0)
-- ============================================================
CREATE TABLE shop_order (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no       VARCHAR(50) NOT NULL,
    shop_user_id   BIGINT NOT NULL,
    owner_user_id  BIGINT NOT NULL,
    status         VARCHAR(30) NOT NULL DEFAULT 'PENDING_SHIP',
    total_amount   DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_quantity INT NOT NULL DEFAULT 0,
    receiver       VARCHAR(50) NOT NULL,
    phone          VARCHAR(30) NOT NULL,
    address        VARCHAR(300) NOT NULL,
    remark         VARCHAR(300) DEFAULT '',
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
    ship_time      DATETIME DEFAULT NULL,
    complete_time  DATETIME DEFAULT NULL,
    cancel_time    DATETIME DEFAULT NULL,
    is_deleted     TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE INDEX uk_order_no (order_no),
    INDEX idx_order_user_time (shop_user_id, create_time),
    INDEX idx_order_owner_time (owner_user_id, create_time),
    INDEX idx_order_status (status)
) ENGINE=InnoDB;

-- ============================================================
-- 15. 订单明细表 (v7.0)
-- ============================================================
CREATE TABLE shop_order_item (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT NOT NULL,
    snack_id     BIGINT NOT NULL,
    snack_name   VARCHAR(100) NOT NULL,
    image_url    VARCHAR(500) DEFAULT '',
    price        DECIMAL(10,2) NOT NULL,
    quantity     INT NOT NULL,
    unit         VARCHAR(20) DEFAULT '包',
    subtotal     DECIMAL(10,2) NOT NULL,
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_item_order (order_id),
    INDEX idx_item_snack (snack_id)
) ENGINE=InnoDB;

-- ============================================================
-- 16. 公告表 (v7.0)
-- ============================================================
CREATE TABLE shop_announcement (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    sort_order  INT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted  TINYINT(1) NOT NULL DEFAULT 0,
    INDEX idx_announce_active (is_active, sort_order)
) ENGINE=InnoDB;

-- ============================================================
-- 17. 商品评价表 (v7.1)
-- ============================================================
CREATE TABLE shop_review (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    snack_id    BIGINT NOT NULL,
    order_id    BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    username    VARCHAR(50) NOT NULL,
    content     VARCHAR(500) NOT NULL,
    is_hidden   TINYINT(1) NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_deleted  TINYINT(1) NOT NULL DEFAULT 0,
    INDEX idx_review_snack (snack_id, create_time),
    UNIQUE INDEX uk_review_user_order_snack (user_id, order_id, snack_id)
) ENGINE=InnoDB;
