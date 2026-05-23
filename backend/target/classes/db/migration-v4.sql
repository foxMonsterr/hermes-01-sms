-- ============================================================
-- 零食管理系统 — v3.0 → v4.0 增量迁移脚本
-- 适用场景: 已有 v3.0 snack.db，手动执行一次
-- 执行方式: sqlite3 data/snack.db < db/migration-v4.sql
-- 警告: 执行前请备份 snack.db
-- 注意: 本脚本不可放入 spring.sql.init.schema-locations
-- ============================================================

-- 1. snack 表新增字段
ALTER TABLE snack ADD COLUMN price DECIMAL(10,2) DEFAULT NULL;
ALTER TABLE snack ADD COLUMN image_url VARCHAR(500) DEFAULT '';

-- 2. 库存流水表
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

-- 3. 采购清单表
CREATE TABLE IF NOT EXISTS shopping_list (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id     BIGINT NOT NULL,
    snack_id    BIGINT DEFAULT NULL,
    snack_name  VARCHAR(100) NOT NULL,
    category_id BIGINT,
    planned_qty INT NOT NULL DEFAULT 1,
    price       DECIMAL(10,2) DEFAULT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    source      VARCHAR(20) DEFAULT 'MANUAL',
    remark      VARCHAR(200) DEFAULT '',
    create_time DATETIME DEFAULT (datetime('now','localtime')),
    update_time DATETIME DEFAULT (datetime('now','localtime')),
    is_deleted  TINYINT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_sl_user_status ON shopping_list(user_id, status);
CREATE INDEX IF NOT EXISTS idx_sl_user_snack ON shopping_list(user_id, snack_id);
CREATE INDEX IF NOT EXISTS idx_sl_user_snack_status
    ON shopping_list(user_id, snack_id, status);

-- 4. 消息提醒表
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

-- 5. 历史数据 INIT 流水回填（幂等，可重复执行）
-- 为每个未删除 snack 且无 INIT 流水的记录，插入一条初始化流水
INSERT INTO stock_record (
    user_id, snack_id, snack_name, change_type, change_qty,
    before_qty, after_qty, remark
)
SELECT
    s.user_id,
    s.id,
    s.name,
    'INIT',
    s.quantity,
    0,
    s.quantity,
    'v4.0 初始化库存流水'
FROM snack s
WHERE s.is_deleted = 0
  AND NOT EXISTS (
      SELECT 1 FROM stock_record sr
      WHERE sr.user_id = s.user_id
        AND sr.snack_id = s.id
        AND sr.change_type = 'INIT'
  );
