-- ============================================================
-- 零食管理系统 — 数据库建表脚本 (v5.0 后期收尾)
-- 仅建表 + 索引，不插入数据
-- 默认分类在用户注册时由 Service 层创建
-- ============================================================

-- 用户表 (v5.0: 新增 avatar_url)
CREATE TABLE IF NOT EXISTS sys_user (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(200) NOT NULL,
    nickname    VARCHAR(50),
    avatar_url  VARCHAR(500) DEFAULT '',
    create_time DATETIME DEFAULT (datetime('now', 'localtime')),
    update_time DATETIME DEFAULT (datetime('now', 'localtime')),
    is_deleted  TINYINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_user_username_active
    ON sys_user(username) WHERE is_deleted = 0;

-- 分类表
CREATE TABLE IF NOT EXISTS snack_category (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id     BIGINT NOT NULL,
    name        VARCHAR(50) NOT NULL,
    icon        VARCHAR(50) DEFAULT '',
    sort_order  INT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT (datetime('now', 'localtime')),
    update_time DATETIME DEFAULT (datetime('now', 'localtime')),
    is_deleted  TINYINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_snack_category_user_name_active
    ON snack_category(user_id, name) WHERE is_deleted = 0;
CREATE INDEX IF NOT EXISTS idx_snack_category_user_id ON snack_category(user_id);

-- 零食表
CREATE TABLE IF NOT EXISTS snack (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
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
    create_time   DATETIME DEFAULT (datetime('now', 'localtime')),
    update_time   DATETIME DEFAULT (datetime('now', 'localtime')),
    is_deleted    TINYINT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_snack_user_id ON snack(user_id);
CREATE INDEX IF NOT EXISTS idx_snack_user_category ON snack(user_id, category_id);
CREATE INDEX IF NOT EXISTS idx_snack_expiry_date ON snack(expiry_date);
CREATE INDEX IF NOT EXISTS idx_snack_name ON snack(name);

-- ============================================================
-- v4.0 表
-- ============================================================

CREATE TABLE IF NOT EXISTS stock_record (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id       BIGINT NOT NULL,
    snack_id      BIGINT NOT NULL,
    snack_name    VARCHAR(100) NOT NULL,
    change_type   VARCHAR(20) NOT NULL,
    change_qty    INT NOT NULL,
    before_qty    INT NOT NULL,
    after_qty     INT NOT NULL,
    remark        VARCHAR(200) DEFAULT '',
    create_time   DATETIME DEFAULT (datetime('now','localtime'))
);
CREATE INDEX IF NOT EXISTS idx_sr_user_time ON stock_record(user_id, create_time);
CREATE INDEX IF NOT EXISTS idx_sr_user_snack ON stock_record(user_id, snack_id);
CREATE INDEX IF NOT EXISTS idx_sr_user_type ON stock_record(user_id, change_type);

-- 采购清单表 (v5.0: 新增 supplier 字段)
CREATE TABLE IF NOT EXISTS shopping_list (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
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
    create_time   DATETIME DEFAULT (datetime('now','localtime')),
    update_time   DATETIME DEFAULT (datetime('now','localtime')),
    is_deleted    TINYINT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_sl_user_status ON shopping_list(user_id, status);
CREATE INDEX IF NOT EXISTS idx_sl_user_snack ON shopping_list(user_id, snack_id);
CREATE INDEX IF NOT EXISTS idx_sl_user_snack_status ON shopping_list(user_id, snack_id, status);

CREATE TABLE IF NOT EXISTS notification (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id     BIGINT NOT NULL,
    type        VARCHAR(30) NOT NULL,
    title       VARCHAR(100) NOT NULL,
    content     VARCHAR(500) NOT NULL,
    related_id  BIGINT NOT NULL,
    notify_date DATE NOT NULL,
    is_read     TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT (datetime('now','localtime'))
);
CREATE INDEX IF NOT EXISTS idx_notif_user_read ON notification(user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_notif_user_type ON notification(user_id, type);
CREATE INDEX IF NOT EXISTS idx_notif_user_time ON notification(user_id, create_time);
CREATE UNIQUE INDEX IF NOT EXISTS uk_notification_daily
    ON notification(user_id, type, related_id, notify_date);

-- ============================================================
-- v5.0 新增表
-- ============================================================

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id      BIGINT NOT NULL,
    config_key   VARCHAR(50) NOT NULL,
    config_value VARCHAR(200) NOT NULL,
    create_time  DATETIME DEFAULT (datetime('now','localtime')),
    update_time  DATETIME DEFAULT (datetime('now','localtime')),
    UNIQUE(user_id, config_key)
);

-- 过期/丢弃处理记录表
CREATE TABLE IF NOT EXISTS disposal_record (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
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
    create_time   DATETIME DEFAULT (datetime('now','localtime'))
);
CREATE INDEX IF NOT EXISTS idx_dr_user_date ON disposal_record(user_id, dispose_date);
CREATE INDEX IF NOT EXISTS idx_dr_user_snack ON disposal_record(user_id, snack_id);

-- 库存盘点记录表
CREATE TABLE IF NOT EXISTS inventory_check_record (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id       BIGINT NOT NULL,
    snack_id      BIGINT NOT NULL,
    snack_name    VARCHAR(100) NOT NULL,
    system_qty    INT NOT NULL,
    actual_qty    INT NOT NULL,
    difference    INT NOT NULL,
    remark        VARCHAR(200) DEFAULT '',
    check_date    DATE NOT NULL,
    create_time   DATETIME DEFAULT (datetime('now','localtime'))
);
CREATE INDEX IF NOT EXISTS idx_icr_user_date ON inventory_check_record(user_id, check_date);
CREATE INDEX IF NOT EXISTS idx_icr_user_snack ON inventory_check_record(user_id, snack_id);

-- 供应商表
CREATE TABLE IF NOT EXISTS supplier (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id     BIGINT NOT NULL,
    name        VARCHAR(100) NOT NULL,
    contact     VARCHAR(50) DEFAULT '',
    phone       VARCHAR(30) DEFAULT '',
    address     VARCHAR(200) DEFAULT '',
    notes       VARCHAR(500) DEFAULT '',
    create_time DATETIME DEFAULT (datetime('now','localtime')),
    update_time DATETIME DEFAULT (datetime('now','localtime')),
    is_deleted  TINYINT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_supplier_user_id ON supplier(user_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_supplier_user_name_active
    ON supplier(user_id, name) WHERE is_deleted = 0;

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id     BIGINT NOT NULL,
    username    VARCHAR(50) NOT NULL,
    action      VARCHAR(50) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id   BIGINT,
    detail      VARCHAR(500) DEFAULT '',
    result      VARCHAR(20) DEFAULT 'SUCCESS',
    ip          VARCHAR(50) DEFAULT '',
    create_time DATETIME DEFAULT (datetime('now','localtime'))
);
CREATE INDEX IF NOT EXISTS idx_log_user_time ON operation_log(user_id, create_time);
