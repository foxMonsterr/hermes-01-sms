-- ======================================================
-- 零食管理系统 — 测试数据 (匹配 v5.0 schema.sql)
-- 密码: admin / 123456
-- 前置条件: 数据库需含 price/image_url/supplier 等列
--         若缺列 → 先跑 migration-v4.sql + migration-v5.sql
-- ======================================================

-- 0. 清空所有数据
DELETE FROM operation_log;
DELETE FROM notification;
DELETE FROM system_config;
DELETE FROM shopping_list;
DELETE FROM stock_record;
DELETE FROM disposal_record;
DELETE FROM inventory_check_record;
DELETE FROM snack;
DELETE FROM supplier;
DELETE FROM snack_category;
DELETE FROM sys_user;

-- 1. 管理员用户
INSERT INTO sys_user (username, password, nickname, avatar_url, create_time, update_time, is_deleted)
VALUES ('admin', '$2b$12$kLHlX.MLsT27NNFKHUYhveLz/YiuesiuP8uBYB/qIpAToIrjvyZI6', '管理员', '', datetime('now','localtime'), datetime('now','localtime'), 0);

-- 2. 8 个默认分类
INSERT INTO snack_category (user_id, name, icon, sort_order, create_time, update_time, is_deleted) VALUES
(1,'膨化食品', '🍿',1, datetime('now','localtime'),datetime('now','localtime'),0),
(1,'糖果巧克力', '🍬',2, datetime('now','localtime'),datetime('now','localtime'),0),
(1,'坚果炒货', '🥜',3, datetime('now','localtime'),datetime('now','localtime'),0),
(1,'饼干糕点', '🍪',4, datetime('now','localtime'),datetime('now','localtime'),0),
(1,'饮料',     '🥤',5, datetime('now','localtime'),datetime('now','localtime'),0),
(1,'肉干肉脯', '🍖',6, datetime('now','localtime'),datetime('now','localtime'),0),
(1,'果脯蜜饯', '🍑',7, datetime('now','localtime'),datetime('now','localtime'),0),
(1,'其他',     '📦',8, datetime('now','localtime'),datetime('now','localtime'),0);

-- 3. 21 种零食 (cat_id 1-8, 每类 2-3 种)
-- 膨化食品 (1)
INSERT INTO snack (user_id,category_id,name,quantity,unit,price,purchase_date,expiry_date,notes,create_time,update_time,is_deleted) VALUES
(1,1,'乐事原味薯片',  50,'包',8.50, '2026-03-01','2026-08-15','经典原味畅销款', datetime('now','localtime'),datetime('now','localtime'),0),
(1,1,'奇多玉米棒',    30,'包',4.50, '2026-03-10','2026-07-20','起司风味', datetime('now','localtime'),datetime('now','localtime'),0),
(1,1,'上好佳虾条',    25,'包',3.50, '2026-04-01','2026-09-01','', datetime('now','localtime'),datetime('now','localtime'),0);

-- 糖果巧克力 (2)
INSERT INTO snack (user_id,category_id,name,quantity,unit,price,purchase_date,expiry_date,notes,create_time,update_time,is_deleted) VALUES
(1,2,'德芙丝滑巧克力',   25,'盒',15.00,'2026-04-05','2026-12-01','243g分享装', datetime('now','localtime'),datetime('now','localtime'),0),
(1,2,'大白兔奶糖',       60,'颗',0.50, '2026-02-15','2027-03-01','经典奶味', datetime('now','localtime'),datetime('now','localtime'),0),
(1,2,'费列罗榛果巧克力', 10,'盒',45.00,'2026-04-10','2026-10-15','16粒礼盒', datetime('now','localtime'),datetime('now','localtime'),0);

-- 坚果炒货 (3)
INSERT INTO snack (user_id,category_id,name,quantity,unit,price,purchase_date,expiry_date,notes,create_time,update_time,is_deleted) VALUES
(1,3,'恰恰原味瓜子',   40,'包',6.00, '2026-03-20','2026-09-01','308g', datetime('now','localtime'),datetime('now','localtime'),0),
(1,3,'三只松鼠开心果', 15,'罐',25.00,'2026-04-01','2026-10-15','225g', datetime('now','localtime'),datetime('now','localtime'),0),
(1,3,'良品铺子腰果',   20,'罐',19.90,'2026-04-08','2026-11-01','250g', datetime('now','localtime'),datetime('now','localtime'),0);

-- 饼干糕点 (4)
INSERT INTO snack (user_id,category_id,name,quantity,unit,price,purchase_date,expiry_date,notes,create_time,update_time,is_deleted) VALUES
(1,4,'奥利奥巧克力夹心',35,'盒',9.00, '2026-04-12','2026-11-01','388g', datetime('now','localtime'),datetime('now','localtime'),0),
(1,4,'旺旺仙贝',       45,'包',4.00, '2026-04-05','2026-08-30','52g×10', datetime('now','localtime'),datetime('now','localtime'),0),
(1,4,'达利园蛋黄派',   20,'袋',12.00,'2026-03-25','2026-07-15','23g×20枚', datetime('now','localtime'),datetime('now','localtime'),0);

-- 饮料 (5)
INSERT INTO snack (user_id,category_id,name,quantity,unit,price,purchase_date,expiry_date,notes,create_time,update_time,is_deleted) VALUES
(1,5,'农夫山泉矿泉水',72,'瓶',2.00, '2026-04-15','2027-06-01','550ml', datetime('now','localtime'),datetime('now','localtime'),0),
(1,5,'可口可乐',      48,'罐',3.50, '2026-04-01','2026-10-01','330ml', datetime('now','localtime'),datetime('now','localtime'),0),
(1,5,'旺仔牛奶',      24,'罐',5.00, '2026-03-15','2026-12-31','245ml', datetime('now','localtime'),datetime('now','localtime'),0);

-- 肉干肉脯 (6)
INSERT INTO snack (user_id,category_id,name,quantity,unit,price,purchase_date,expiry_date,notes,create_time,update_time,is_deleted) VALUES
(1,6,'科尔沁牛肉干',  20,'包',18.00,'2026-03-18','2026-07-15','五香味120g', datetime('now','localtime'),datetime('now','localtime'),0),
(1,6,'良品铺子鱿鱼丝',12,'包',15.00,'2026-04-05','2026-09-01','100g', datetime('now','localtime'),datetime('now','localtime'),0);

-- 果脯蜜饯 (7)
INSERT INTO snack (user_id,category_id,name,quantity,unit,price,purchase_date,expiry_date,notes,create_time,update_time,is_deleted) VALUES
(1,7,'华味亨山楂片',   50,'包',3.00,'2026-04-10','2027-01-01','150g', datetime('now','localtime'),datetime('now','localtime'),0),
(1,7,'三只松鼠葡萄干', 30,'包',8.00,'2026-03-28','2026-11-01','200g', datetime('now','localtime'),datetime('now','localtime'),0);

-- 其他 (8)
INSERT INTO snack (user_id,category_id,name,quantity,unit,price,purchase_date,expiry_date,notes,create_time,update_time,is_deleted) VALUES
(1,8,'卫龙大面筋辣条', 80,'包',2.50,'2026-04-08','2026-09-15','106g', datetime('now','localtime'),datetime('now','localtime'),0),
(1,8,'米老头蛋黄煎饼', 18,'袋',10.00,'2026-03-20','2026-08-20','300g', datetime('now','localtime'),datetime('now','localtime'),0);

-- 4. 库存流水 — INIT
INSERT INTO stock_record (user_id,snack_id,snack_name,change_type,change_qty,before_qty,after_qty,remark,create_time) VALUES
(1,1,'乐事原味薯片','INIT',50,0,50,'初始入库','2026-03-01 09:00:00'),
(1,2,'奇多玉米棒','INIT',30,0,30,'初始入库','2026-03-10 10:00:00'),
(1,3,'上好佳虾条','INIT',25,0,25,'初始入库','2026-04-01 09:30:00'),
(1,4,'德芙丝滑巧克力','INIT',25,0,25,'初始入库','2026-04-05 14:00:00'),
(1,5,'大白兔奶糖','INIT',60,0,60,'初始入库','2026-02-15 10:00:00'),
(1,6,'费列罗榛果巧克力','INIT',10,0,10,'初始入库','2026-04-10 11:00:00'),
(1,7,'恰恰原味瓜子','INIT',40,0,40,'初始入库','2026-03-20 10:00:00'),
(1,8,'三只松鼠开心果','INIT',15,0,15,'初始入库','2026-04-01 09:00:00'),
(1,9,'良品铺子腰果','INIT',20,0,20,'初始入库','2026-04-08 15:00:00'),
(1,10,'奥利奥巧克力夹心','INIT',35,0,35,'初始入库','2026-04-12 10:00:00'),
(1,11,'旺旺仙贝','INIT',45,0,45,'初始入库','2026-04-05 15:00:00'),
(1,12,'达利园蛋黄派','INIT',20,0,20,'初始入库','2026-03-25 14:00:00'),
(1,13,'农夫山泉矿泉水','INIT',72,0,72,'初始入库','2026-04-15 10:00:00'),
(1,14,'可口可乐','INIT',48,0,48,'初始入库','2026-04-01 10:30:00'),
(1,15,'旺仔牛奶','INIT',24,0,24,'初始入库','2026-03-15 14:00:00'),
(1,16,'科尔沁牛肉干','INIT',20,0,20,'初始入库','2026-03-18 09:00:00'),
(1,17,'良品铺子鱿鱼丝','INIT',12,0,12,'初始入库','2026-04-05 10:00:00'),
(1,18,'华味亨山楂片','INIT',50,0,50,'初始入库','2026-04-10 16:00:00'),
(1,19,'三只松鼠葡萄干','INIT',30,0,30,'初始入库','2026-03-28 11:00:00'),
(1,20,'卫龙大面筋辣条','INIT',80,0,80,'初始入库','2026-04-08 09:00:00'),
(1,21,'米老头蛋黄煎饼','INIT',18,0,18,'初始入库','2026-03-20 16:00:00');

-- 5. 近期出入库流水
INSERT INTO stock_record (user_id,snack_id,snack_name,change_type,change_qty,before_qty,after_qty,remark,create_time) VALUES
(1,1,'乐事原味薯片','OUT',-3,53,50,'下午茶消耗',datetime('now','-4 days','localtime')),
(1,4,'德芙丝滑巧克力','OUT',-2,27,25,'同事分享',datetime('now','-4 days','localtime')),
(1,14,'可口可乐','OUT',-6,54,48,'午餐消耗',datetime('now','-4 days','localtime')),
(1,20,'卫龙大面筋辣条','OUT',-5,85,80,'休息时间',datetime('now','-4 days','localtime')),
(1,5,'大白兔奶糖','OUT',-8,68,60,'会议小食',datetime('now','-3 days','localtime')),
(1,13,'农夫山泉矿泉水','OUT',-12,84,72,'团队用水',datetime('now','-3 days','localtime')),
(1,10,'奥利奥巧克力夹心','IN',10,25,35,'补货',datetime('now','-3 days','localtime')),
(1,16,'科尔沁牛肉干','OUT',-2,22,20,'个人消耗',datetime('now','-1 days','localtime')),
(1,7,'恰恰原味瓜子','OUT',-4,44,40,'周末观影',datetime('now','-1 days','localtime')),
(1,2,'奇多玉米棒','OUT',-5,35,30,'下午茶',datetime('now','-1 days','localtime')),
(1,14,'可口可乐','OUT',-8,56,48,'聚餐消耗',datetime('now','-1 days','localtime')),
(1,13,'农夫山泉矿泉水','IN',24,48,72,'补货',datetime('now','-1 days','localtime'));

-- 6. 供应商
INSERT INTO supplier (user_id,name,contact,phone,address,notes,create_time,update_time,is_deleted) VALUES
(1,'永辉超市批发','张经理','13800001111','福州市鼓楼区五四路128号','大型连锁，供货稳定可月结',datetime('now','localtime'),datetime('now','localtime'),0),
(1,'良品铺子供应商','李主管','13900002222','武汉市洪山区珞喻路88号','品牌直供，满500免运费',datetime('now','localtime'),datetime('now','localtime'),0),
(1,'本地零食批发市场','王老板','13700003333','南昌市青山湖区北京东路56号','价格灵活，支持小批量',datetime('now','localtime'),datetime('now','localtime'),0);

-- 7. 采购清单
INSERT INTO shopping_list (user_id,snack_name,planned_qty,price,status,source,supplier_id,supplier_name,remark,create_time) VALUES
(1,'乐事原味薯片',20,8.50,'PENDING','LOW_STOCK',1,'永辉超市批发','库存偏低自动生成',datetime('now','-2 days','localtime')),
(1,'可口可乐',24,3.50,'BOUGHT','MANUAL',2,'良品铺子供应商','已采购入库',datetime('now','-5 days','localtime')),
(1,'德芙丝滑巧克力',10,15.00,'PENDING','MANUAL',1,'永辉超市批发','',datetime('now','-1 days','localtime')),
(1,'良品铺子鱿鱼丝',15,15.00,'PENDING','LOW_STOCK',3,'本地零食批发市场','',datetime('now','localtime')),
(1,'旺旺仙贝',30,3.80,'BOUGHT','MANUAL',2,'良品铺子供应商','特价采购',datetime('now','-7 days','localtime')),
(1,'三只松鼠开心果',8,25.00,'PENDING','MANUAL',2,'良品铺子供应商','下周促销活动备货',datetime('now','localtime'));

-- 8. 系统配置
INSERT INTO system_config (user_id,config_key,config_value) VALUES
(1,'lowStockThreshold','2'),
(1,'expiryAlertDays','30'),
(1,'defaultPageSize','10'),
(1,'autoGenerateNotification','true');

-- 9. 消息提醒 (schema列: title/content/related_id/notify_date)
INSERT INTO notification (user_id,type,title,content,related_id,notify_date,is_read,create_time) VALUES
(1,'EXPIRY_SOON','科尔沁牛肉干即将过期','科尔沁牛肉干 将于 2026-07-15 过期，剩余库存 20',16,'2026-07-15',0,datetime('now','-1 days','localtime')),
(1,'EXPIRY_SOON','达利园蛋黄派即将过期','达利园蛋黄派 将于 2026-07-15 过期，剩余库存 20',12,'2026-07-15',0,datetime('now','localtime')),
(1,'LOW_STOCK','费列罗巧克力库存不足','费列罗榛果巧克力 库存仅剩 10，低于阈值',6,date('now'),1,datetime('now','-3 days','localtime')),
(1,'LOW_STOCK','鱿鱼丝库存不足','良品铺子鱿鱼丝 库存仅剩 12，低于阈值',17,date('now'),1,datetime('now','-3 days','localtime')),
(1,'EXPIRY_SOON','奇多玉米棒即将过期','奇多玉米棒 将于 2026-07-20 过期，剩余库存 30',2,'2026-07-20',0,datetime('now','localtime'));

-- 10. 操作日志
INSERT INTO operation_log (user_id,username,action,target_type,target_id,detail,result,ip,create_time) VALUES
(1,'admin','LOGIN_SUCCESS','AUTH',NULL,'用户登录','SUCCESS','127.0.0.1',datetime('now','-5 days','localtime')),
(1,'admin','SNACK_CREATE','SNACK',1,'新增零食: 乐事原味薯片','SUCCESS','127.0.0.1',datetime('now','-5 days','localtime')),
(1,'admin','STOCK_IN','STOCK',1,'入库 乐事原味薯片 +50','SUCCESS','127.0.0.1',datetime('now','-5 days','localtime')),
(1,'admin','SUPPLIER_CREATE','SUPPLIER',1,'新增供应商: 永辉超市批发','SUCCESS','127.0.0.1',datetime('now','-3 days','localtime')),
(1,'admin','SUPPLIER_CREATE','SUPPLIER',2,'新增供应商: 良品铺子供应商','SUCCESS','127.0.0.1',datetime('now','-3 days','localtime')),
(1,'admin','SHOPPING_STOCK_IN','SHOPPING_LIST',2,'采购入库: 可口可乐 +24','SUCCESS','127.0.0.1',datetime('now','-3 days','localtime')),
(1,'admin','CONFIG_UPDATE','CONFIG',NULL,'修改系统配置','SUCCESS','127.0.0.1',datetime('now','-2 days','localtime')),
(1,'admin','LOGIN_SUCCESS','AUTH',NULL,'用户登录','SUCCESS','127.0.0.1',datetime('now','-1 days','localtime')),
(1,'admin','STOCK_OUT','STOCK',14,'出库 可口可乐 -8','SUCCESS','127.0.0.1',datetime('now','-1 days','localtime')),
(1,'admin','STOCK_IN','STOCK',13,'入库 农夫山泉矿泉水 +24','SUCCESS','127.0.0.1',datetime('now','-1 days','localtime')),
(1,'admin','LOGIN_SUCCESS','AUTH',NULL,'用户登录','SUCCESS','127.0.0.1',datetime('now','localtime'));
