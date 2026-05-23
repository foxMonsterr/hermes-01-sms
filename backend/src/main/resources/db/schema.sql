-- ============================================================
-- 零食管理系统 — v3 → v5 一键迁移脚本
-- 适用: v3.0 snack.db（无 price/image_url/avatar_url 等新列）
-- 幂等: ALTER TABLE 失败可忽略，CREATE TABLE IF NOT EXISTS 安全
-- 用法: DataGrip 连接 snack.db 后全选执行
-- ============================================================

-- ── 1. snack 表新增字段 (v4.0) ──
ALTER TABLE snack ADD COLUMN price DECIMAL(10,2) DEFAULT NULL;
ALTER TABLE snack ADD COLUMN image_url VARCHAR(500) DEFAULT '';

-- ── 2. sys_user 新增头像 (v5.0) ──
ALTER TABLE sys_user ADD COLUMN avatar_url VARCHAR(500) DEFAULT '';

-- ── 3. shopping_list 新增供应商字段 (v5.0) ──
ALTER TABLE shopping_list ADD COLUMN supplier_id BIGINT DEFAULT NULL;
ALTER TABLE shopping_list ADD COLUMN supplier_name VARCHAR(100) DEFAULT '';
ALTER TABLE shopping_list ADD COLUMN actual_qty INT DEFAULT NULL;
ALTER TABLE shopping_list ADD COLUMN bought_time DATETIME DEFAULT NULL;

-- ═══════════════════════════════════════════════════
-- 以下为 v4.0 + v5.0 新增表（表不存在则创建）
-- ═══════════════════════════════════════════════════

-- ── 4. 库存流水表 (v4.0) ──
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
CREATE INDEX IF NOT EXISTS idx_sr_user_time  ON stock_record(user_id, create_time);
CREATE INDEX IF NOT EXISTS idx_sr_user_snack ON stock_record(user_id, snack_id);
CREATE INDEX IF NOT EXISTS idx_sr_user_type  ON stock_record(user_id, change_type);

-- ── 5. 采购清单表 (v4.0 + v5.0 supplier 字段) ──
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
CREATE INDEX IF NOT EXISTS idx_sl_user_status      ON shopping_list(user_id, status);
CREATE INDEX IF NOT EXISTS idx_sl_user_snack       ON shopping_list(user_id, snack_id);
CREATE INDEX IF NOT EXISTS idx_sl_user_snack_status ON shopping_list(user_id, snack_id, status);

-- ── 6. 消息提醒表 (v4.0) ──
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

-- ── 7. 系统配置表 (v5.0) ──
CREATE TABLE IF NOT EXISTS system_config (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id      BIGINT NOT NULL,
    config_key   VARCHAR(50) NOT NULL,
    config_value VARCHAR(200) NOT NULL,
    create_time  DATETIME DEFAULT (datetime('now','localtime')),
    update_time  DATETIME DEFAULT (datetime('now','localtime')),
    UNIQUE(user_id, config_key)
);

-- ── 8. 过期/丢弃处理记录表 (v5.0) ──
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

-- ── 9. 库存盘点记录表 (v5.0) ──
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

-- ── 10. 供应商表 (v5.0) ──
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

-- ── 11. 操作日志表 (v5.0) ──
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

-- ── 12. 历史库存 INIT 流水回填 (v4.0, 幂等) ──
INSERT INTO stock_record (user_id, snack_id, snack_name, change_type, change_qty, before_qty, after_qty, remark)
SELECT s.user_id, s.id, s.name, 'INIT', s.quantity, 0, s.quantity, '初始化库存流水'
FROM snack s
WHERE s.is_deleted = 0
  AND NOT EXISTS (
      SELECT 1 FROM stock_record sr
      WHERE sr.user_id = s.user_id AND sr.snack_id = s.id AND sr.change_type = 'INIT'
  );
