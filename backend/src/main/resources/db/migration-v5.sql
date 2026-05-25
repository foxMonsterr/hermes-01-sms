-- ============================================================
-- 零食管理系统 — v4.0 → v5.0 增量迁移脚本
-- 适用场景: 已有 v4.0 snack.db，手动执行一次
-- 执行前必须备份 snack.db
-- 警告: ALTER TABLE 不可重复执行，字段已存在会报错
-- 警告: 本脚本不可放入 spring.sql.init.schema-locations
-- ============================================================

-- 1. sys_user 新增头像字段
ALTER TABLE sys_user ADD COLUMN avatar_url VARCHAR(500) DEFAULT '';

-- 2. shopping_list 新增供应商和实际入库字段
ALTER TABLE shopping_list ADD COLUMN supplier_id BIGINT DEFAULT NULL;
ALTER TABLE shopping_list ADD COLUMN supplier_name VARCHAR(100) DEFAULT '';
ALTER TABLE shopping_list ADD COLUMN actual_qty INT DEFAULT NULL;
ALTER TABLE shopping_list ADD COLUMN bought_time DATETIME DEFAULT NULL;

-- 3. 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id      BIGINT NOT NULL,
    config_key   VARCHAR(50) NOT NULL,
    config_value VARCHAR(200) NOT NULL,
    create_time  DATETIME DEFAULT (datetime('now','localtime')),
    update_time  DATETIME DEFAULT (datetime('now','localtime')),
    UNIQUE(user_id, config_key)
);

-- 4. 过期/丢弃处理记录表
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

-- 5. 库存盘点记录表
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

-- 6. 供应商表
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

-- 7. 操作日志表
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
