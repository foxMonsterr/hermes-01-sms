-- ============================================================
-- 零食管理系统 — v6.0 → v7.0 小卖铺扩展迁移脚本
-- 执行前备份 snack.db
-- ALTER TABLE 不可重复执行（字段已存在会报错）
-- ============================================================

-- 1. snack 表新增店铺字段
ALTER TABLE snack ADD COLUMN is_on_shelf TINYINT NOT NULL DEFAULT 0;
ALTER TABLE snack ADD COLUMN description VARCHAR(1000) DEFAULT '';
ALTER TABLE snack ADD COLUMN shelf_time DATETIME DEFAULT NULL;

-- 2. 客户端用户表
CREATE TABLE IF NOT EXISTS shop_user (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    username    VARCHAR(50) NOT NULL,
    password    VARCHAR(200) NOT NULL,
    nickname    VARCHAR(50) DEFAULT '',
    phone       VARCHAR(30) DEFAULT '',
    avatar_url  VARCHAR(500) DEFAULT '',
    create_time DATETIME DEFAULT (datetime('now','localtime')),
    update_time DATETIME DEFAULT (datetime('now','localtime')),
    is_deleted  TINYINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_shop_user_username_active ON shop_user(username) WHERE is_deleted=0;

-- 3. 购物车表
CREATE TABLE IF NOT EXISTS shop_cart (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id     BIGINT NOT NULL,
    snack_id    BIGINT NOT NULL,
    quantity    INT NOT NULL DEFAULT 1,
    create_time DATETIME DEFAULT (datetime('now','localtime')),
    update_time DATETIME DEFAULT (datetime('now','localtime'))
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_cart_user_snack ON shop_cart(user_id, snack_id);
CREATE INDEX IF NOT EXISTS idx_cart_user ON shop_cart(user_id);

-- 4. 订单表
CREATE TABLE IF NOT EXISTS shop_order (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
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
    create_time    DATETIME DEFAULT (datetime('now','localtime')),
    ship_time      DATETIME DEFAULT NULL,
    complete_time  DATETIME DEFAULT NULL,
    cancel_time    DATETIME DEFAULT NULL,
    is_deleted     TINYINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_shop_order_no ON shop_order(order_no);
CREATE INDEX IF NOT EXISTS idx_order_user_time ON shop_order(shop_user_id, create_time);
CREATE INDEX IF NOT EXISTS idx_order_owner_time ON shop_order(owner_user_id, create_time);
CREATE INDEX IF NOT EXISTS idx_order_status ON shop_order(status);

-- 5. 订单明细表（商品快照）
CREATE TABLE IF NOT EXISTS shop_order_item (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id     BIGINT NOT NULL,
    snack_id     BIGINT NOT NULL,
    snack_name   VARCHAR(100) NOT NULL,
    image_url    VARCHAR(500) DEFAULT '',
    price        DECIMAL(10,2) NOT NULL,
    quantity     INT NOT NULL,
    unit         VARCHAR(20) DEFAULT '包',
    subtotal     DECIMAL(10,2) NOT NULL,
    create_time  DATETIME DEFAULT (datetime('now','localtime'))
);
CREATE INDEX IF NOT EXISTS idx_order_item_order ON shop_order_item(order_id);
CREATE INDEX IF NOT EXISTS idx_order_item_snack ON shop_order_item(snack_id);

-- 6. 公告表
CREATE TABLE IF NOT EXISTS shop_announcement (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    title       VARCHAR(100) NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    is_active   TINYINT NOT NULL DEFAULT 1,
    sort_order  INT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT (datetime('now','localtime')),
    update_time DATETIME DEFAULT (datetime('now','localtime')),
    is_deleted  TINYINT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_announcement_active ON shop_announcement(is_active, sort_order);

-- 7. 收货地址表 (P1)
CREATE TABLE IF NOT EXISTS shop_address (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id     BIGINT NOT NULL,
    receiver    VARCHAR(50) NOT NULL,
    phone       VARCHAR(30) NOT NULL,
    address     VARCHAR(300) NOT NULL,
    is_default  TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT (datetime('now','localtime')),
    update_time DATETIME DEFAULT (datetime('now','localtime')),
    is_deleted  TINYINT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_shop_address_user ON shop_address(user_id);

-- 8. 商品评价表 (v7.1)
CREATE TABLE IF NOT EXISTS shop_review (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    snack_id    BIGINT NOT NULL,
    order_id    BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    username    VARCHAR(50) NOT NULL,
    content     VARCHAR(500) NOT NULL,
    is_hidden   TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT (datetime('now','localtime')),
    is_deleted  TINYINT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_review_snack ON shop_review(snack_id, create_time);
CREATE UNIQUE INDEX IF NOT EXISTS uk_review_user_order_snack ON shop_review(user_id, order_id, snack_id);
