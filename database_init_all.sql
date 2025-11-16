-- ====================================
-- 外卖系统完整数据库初始化脚本
-- 包含：用户管理 + 角色管理 + 菜单模块 + 购物车模块 + 商户模块
-- 创建日期: 2025-11-10
-- 更新日期: 2025-11-10
-- ====================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `test` DEFAULT CHARACTER SET utf8mb4;
USE `test`;

-- ====================================
-- 第一部分：用户和角色表
-- ====================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `user_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                            `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                            `password` VARCHAR(100) NOT NULL COMMENT '密码',
                            `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
                            `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
                            `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
                            `role_id` BIGINT(20) DEFAULT NULL COMMENT '角色ID',
                            `status` CHAR(1) DEFAULT '1' COMMENT '状态(1启用 0禁用)',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`user_id`),
                            UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
                            `role_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                            `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
                            `role_desc` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
                            `status` CHAR(1) DEFAULT '1' COMMENT '状态(1启用 0禁用)',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 插入角色数据
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_desc`, `status`) VALUES
                                                                           (1, '超级管理员', '拥有系统所有权限', '1'),
                                                                           (2, '管理员', '拥有系统管理权限', '1'),
                                                                           (3, '商家', '可以管理自己的店铺和菜品', '1'),
                                                                           (4, '配送员', '负责订单配送', '1'),
                                                                           (5, '普通用户', '可以浏览和下单', '1');

-- 插入用户数据（密码都是123456）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `email`, `role_id`, `status`) VALUES
                                                                                                        ('admin', '123456', '超级管理员', '13800138000', 'admin@waimai.com', 1, '1'),
                                                                                                        ('manager', '123456', '系统管理员', '13800138001', 'manager@waimai.com', 2, '1'),
                                                                                                        ('merchant', '123456', '商家001', '13800138002', 'merchant@waimai.com', 3, '1'),
                                                                                                        ('deliver', '123456', '配送员001', '13800138003', 'deliver@waimai.com', 4, '1'),
                                                                                                        ('user001', '123456', '普通用户', '13800138004', 'user001@waimai.com', 5, '1'),
                                                                                                        ('merchant001', '123456', '商户001', '13800000001', 'merchant001@test.com', 3, '1'),
                                                                                                        ('merchant002', '123456', '商户002', '13800000002', 'merchant002@test.com', 3, '1'),
                                                                                                        ('merchant003', '123456', '商户003', '13800000003', 'merchant003@test.com', 3, '1');

-- ====================================
-- 第二部分：商户模块
-- ====================================
DROP TABLE IF EXISTS `t_merchant`;
CREATE TABLE `t_merchant` (
                              `merchant_id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商户ID',
                              `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
                              `merchant_name` VARCHAR(100) NOT NULL COMMENT '商户名称',
                              `description` TEXT COMMENT '商户简介',
                              `logo` VARCHAR(255) COMMENT '商户logo',
                              `address` VARCHAR(255) COMMENT '商户地址',
                              `phone` VARCHAR(20) COMMENT '联系电话',
                              `business_hours` VARCHAR(100) DEFAULT '09:00-22:00' COMMENT '营业时间',
                              `status` TINYINT DEFAULT 1 COMMENT '状态 1-营业中 0-休息中',
                              `rating` DECIMAL(3,2) DEFAULT 5.0 COMMENT '评分',
                              `sales` INT DEFAULT 0 COMMENT '总销量',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              UNIQUE KEY `uk_user_id` (`user_id`),
                              KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户表';

-- 插入商户数据
INSERT INTO `t_merchant` (`user_id`, `merchant_name`, `description`, `logo`, `address`, `phone`, `business_hours`, `status`, `rating`, `sales`)
SELECT
    u.user_id,
    CASE
        WHEN u.username = 'merchant001' THEN '川香阁'
        WHEN u.username = 'merchant002' THEN '粤味轩'
        WHEN u.username = 'merchant003' THEN '江南小厨'
        WHEN u.username = 'merchant' THEN '默认商户'
        END,
    CASE
        WHEN u.username = 'merchant001' THEN '正宗川菜，麻辣鲜香'
        WHEN u.username = 'merchant002' THEN '地道粤菜，清淡养生'
        WHEN u.username = 'merchant003' THEN '江南风味，精致小炒'
        WHEN u.username = 'merchant' THEN '综合美食，品种丰富'
        END,
    '/img/merchant/default.jpg',
    CASE
        WHEN u.username = 'merchant001' THEN '成都市锦江区春熙路100号'
        WHEN u.username = 'merchant002' THEN '广州市天河区天河路200号'
        WHEN u.username = 'merchant003' THEN '杭州市西湖区西湖路300号'
        WHEN u.username = 'merchant' THEN '北京市朝阳区商业街500号'
        END,
    CASE
        WHEN u.username = 'merchant001' THEN '028-88888888'
        WHEN u.username = 'merchant002' THEN '020-99999999'
        WHEN u.username = 'merchant003' THEN '0571-77777777'
        WHEN u.username = 'merchant' THEN '010-66666666'
        END,
    '09:00-22:00',
    1,
    CASE
        WHEN u.username = 'merchant001' THEN 4.8
        WHEN u.username = 'merchant002' THEN 4.9
        WHEN u.username = 'merchant003' THEN 4.7
        WHEN u.username = 'merchant' THEN 4.5
        END,
    CASE
        WHEN u.username = 'merchant001' THEN 1280
        WHEN u.username = 'merchant002' THEN 956
        WHEN u.username = 'merchant003' THEN 743
        WHEN u.username = 'merchant' THEN 500
        END
FROM sys_user u
WHERE u.username IN ('merchant001', 'merchant002', 'merchant003', 'merchant')
  AND u.role_id = 3;

-- ====================================
-- 第三部分：菜单模块
-- ====================================
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
                              `category_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                              `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
                              `category_desc` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
                              `category_icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标URL',
                              `sort_order` INT(11) DEFAULT 0 COMMENT '排序顺序',
                              `status` CHAR(1) DEFAULT '1' COMMENT '状态(1启用 0禁用)',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='菜品分类表';

DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product` (
                             `product_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
                             `merchant_id` BIGINT(20) DEFAULT NULL COMMENT '商户ID',
                             `category_id` BIGINT(20) NOT NULL COMMENT '分类ID',
                             `product_name` VARCHAR(100) NOT NULL COMMENT '菜品名称',
                             `product_desc` VARCHAR(500) DEFAULT NULL COMMENT '菜品描述',
                             `product_img` VARCHAR(255) DEFAULT NULL COMMENT '菜品图片URL',
                             `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
                             `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
                             `stock` INT(11) DEFAULT 999 COMMENT '库存',
                             `sales` INT(11) DEFAULT 0 COMMENT '销量',
                             `is_recommend` CHAR(1) DEFAULT '0' COMMENT '是否推荐(1是 0否)',
                             `status` CHAR(1) DEFAULT '1' COMMENT '状态(1上架 0下架)',
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`product_id`),
                             KEY `idx_category` (`category_id`),
                             KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- 插入分类数据（使用category_id从1000开始）
INSERT INTO `t_category` (`category_id`, `category_name`, `category_desc`, `category_icon`, `sort_order`, `status`) VALUES
                                                                                                                        (1000, '热销推荐', '最受欢迎的菜品', '/img/category/hot.png', 1, '1'),
                                                                                                                        (1001, '主食类', '米饭、面条等主食', '/img/category/staple.png', 2, '1'),
                                                                                                                        (1002, '荤菜类', '肉类菜品', '/img/category/meat.png', 3, '1'),
                                                                                                                        (1003, '素菜类', '蔬菜类菜品', '/img/category/vegetable.png', 4, '1'),
                                                                                                                        (1004, '汤品类', '各类汤品', '/img/category/soup.png', 5, '1'),
                                                                                                                        (1005, '饮料类', '饮品', '/img/category/drink.png', 6, '1'),
                                                                                                                        (1006, '小吃类', '小吃零食', '/img/category/snack.png', 7, '1');

-- 插入菜品数据（热销推荐）
INSERT INTO `t_product` (`merchant_id`, `category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1000, '宫保鸡丁', '经典川菜，鸡肉鲜嫩，花生酥脆，口感丰富', '/img/product/gongbao.jpg', 28.00, 35.00, 100, 256, '1', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1000, '鱼香肉丝', '酸甜可口，下饭神器', '/img/product/yuxiang.jpg', 26.00, 32.00, 100, 189, '1', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1000, '麻婆豆腐', '麻辣鲜香，豆腐嫩滑', '/img/product/mapo.jpg', 18.00, 22.00, 100, 167, '1', '1');

-- 主食类
INSERT INTO `t_product` (`merchant_id`, `category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1001, '白米饭', '香软可口的白米饭', '/img/product/rice.jpg', 3.00, NULL, 999, 3200, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1001, '蛋炒饭', '金黄诱人的蛋炒饭', '/img/product/fried_rice.jpg', 15.00, 18.00, 999, 980, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1001, '杨州炒饭', '料足味美的扬州炒饭', '/img/product/yangzhou_rice.jpg', 22.00, 26.00, 999, 720, '1', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1001, '牛肉面', '浓郁牛肉汤底', '/img/product/beef_noodle.jpg', 25.00, 30.00, 999, 650, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1001, '担担面', '四川特色面食，麻辣鲜香', '/img/product/dandan.jpg', 15.00, 18.00, 80, 145, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1001, '阳春面', '清淡爽口，汤鲜面滑', '/img/product/yangchun.jpg', 10.00, NULL, 100, 89, '0', '1');

-- 荤菜类
INSERT INTO `t_product` (`merchant_id`, `category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1002, '红烧肉', '肥而不腻，入口即化', '/img/product/hongshao.jpg', 32.00, 38.00, 50, 234, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1002, '糖醋排骨', '酸甜可口，外酥里嫩', '/img/product/tangcu.jpg', 35.00, 42.00, 50, 178, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1002, '水煮鱼', '麻辣鲜香，鱼肉鲜嫩', '/img/product/shuizhu.jpg', 48.00, 55.00, 30, 156, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1002, '辣子鸡', '香辣干脆，回味无穷', '/img/product/lazi.jpg', 30.00, 36.00, 60, 134, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1002, '小炒肉', '家常美味，香辣下饭', '/img/product/xiaochao.jpg', 25.00, 30.00, 80, 201, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1002, '清蒸鲈鱼', '鱼肉鲜嫩，原汁原味', '/img/product/steamed_fish.jpg', 58.00, 68.00, 999, 520, '1', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1002, '口水鸡', '麻辣鲜香，鸡肉滑嫩', '/img/product/saliva_chicken.jpg', 32.00, 38.00, 999, 680, '0', '1');

-- 素菜类
INSERT INTO `t_product` (`merchant_id`, `category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1003, '清炒时蔬', '新鲜蔬菜，清淡健康', '/img/product/veg_stir.jpg', 18.00, NULL, 999, 1200, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1003, '干煸豆角', '豆角入味，香脆可口', '/img/product/dry_beans.jpg', 20.00, 24.00, 999, 780, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1003, '蒜蓉西兰花', '营养丰富，蒜香浓郁', '/img/product/broccoli.jpg', 22.00, NULL, 999, 650, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1003, '地三鲜', '东北名菜，家常美味', '/img/product/disanxian.jpg', 24.00, 28.00, 999, 820, '1', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '粤味轩' LIMIT 1), 1003, '清炒油麦菜', '清脆爽口，健康低脂', '/img/product/youmai.jpg', 12.00, NULL, 100, 112, '0', '1');

-- 汤品类
INSERT INTO `t_product` (`merchant_id`, `category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1004, '番茄蛋汤', '酸甜开胃，营养丰富', '/img/product/tomato_soup.jpg', 12.00, NULL, 999, 1100, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1004, '紫菜蛋花汤', '清淡鲜美', '/img/product/seaweed_soup.jpg', 10.00, NULL, 999, 980, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1004, '酸辣汤', '酸辣开胃，暖心暖胃', '/img/product/sour_soup.jpg', 15.00, NULL, 999, 850, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1004, '老鸭汤', '滋补养生，汤鲜味美', '/img/product/duck_soup.jpg', 38.00, 45.00, 999, 420, '1', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '江南小厨' LIMIT 1), 1004, '西红柿蛋汤', '酸甜可口，营养丰富', '/img/product/xihongshi.jpg', 8.00, NULL, 100, 234, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '江南小厨' LIMIT 1), 1004, '紫菜蛋花汤', '清淡鲜美，补碘佳品', '/img/product/zicai.jpg', 6.00, NULL, 100, 198, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '江南小厨' LIMIT 1), 1004, '酸辣汤', '酸辣开胃，驱寒暖胃', '/img/product/suanla.jpg', 10.00, NULL, 80, 167, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '江南小厨' LIMIT 1), 1004, '老鸭汤', '滋补养生，汤鲜肉嫩', '/img/product/laoya.jpg', 28.00, 35.00, 30, 78, '0', '1');

-- 饮料类
INSERT INTO `t_product` (`merchant_id`, `category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1005, '可乐', '冰爽解渴', '/img/product/cola.jpg', 5.00, NULL, 999, 2500, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1005, '雪碧', '清凉透心', '/img/product/sprite.jpg', 5.00, NULL, 999, 2200, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1005, '鲜榨橙汁', '新鲜果汁，维C满满', '/img/product/orange_juice.jpg', 15.00, NULL, 999, 680, '1', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1005, '柠檬茶', '清新爽口，解腻消暑', '/img/product/lemon_tea.jpg', 12.00, NULL, 999, 920, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1005, '酸梅汤', '传统饮品，生津止渴', '/img/product/plum_juice.jpg', 10.00, NULL, 999, 750, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '粤味轩' LIMIT 1), 1005, '可口可乐', '经典可乐，冰爽畅饮', '/img/product/cola.jpg', 3.00, NULL, 200, 456, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '粤味轩' LIMIT 1), 1005, '雪碧', '清爽柠檬味汽水', '/img/product/sprite.jpg', 3.00, NULL, 200, 389, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '粤味轩' LIMIT 1), 1005, '橙汁', '鲜榨橙汁，维C丰富', '/img/product/orange.jpg', 8.00, NULL, 100, 234, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '粤味轩' LIMIT 1), 1005, '酸梅汤', '消暑解渴，酸甜可口', '/img/product/suanmei.jpg', 6.00, NULL, 100, 178, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '粤味轩' LIMIT 1), 1005, '柠檬茶', '清新柠檬，茶香浓郁', '/img/product/lemon.jpg', 10.00, NULL, 80, 145, '0', '1');

-- 小吃类
INSERT INTO `t_product` (`merchant_id`, `category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1006, '炸鸡翅', '外酥里嫩，香气扑鼻', '/img/product/fried_wings.jpg', 18.00, 22.00, 999, 1350, '1', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1006, '薯条', '金黄酥脆', '/img/product/fries.jpg', 12.00, NULL, 999, 1800, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1006, '春卷', '酥脆可口，馅料丰富', '/img/product/spring_roll.jpg', 15.00, NULL, 999, 680, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '默认商户' LIMIT 1), 1006, '锅贴', '底部酥脆，馅料鲜美', '/img/product/guotie.jpg', 16.00, NULL, 999, 590, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1006, '鸡米花', '香酥可口，外酥里嫩', '/img/product/popcorn.jpg', 12.00, 15.00, 100, 267, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1006, '薯条', '金黄酥脆，老少皆宜', '/img/product/fries.jpg', 10.00, NULL, 150, 398, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1006, '鸡翅', '烤制鸡翅，香嫩多汁', '/img/product/wings.jpg', 15.00, 18.00, 80, 289, '0', '1'),
                                                                                                                                                                                 ((SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1), 1006, '春卷', '传统小吃，酥脆美味', '/img/product/spring.jpg', 8.00, NULL, 100, 156, '0', '1');

-- ====================================
-- 第四部分：购物车模块
-- ====================================
DROP TABLE IF EXISTS `t_cart`;
CREATE TABLE `t_cart` (
                          `cart_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID',
                          `user_id` INT NOT NULL COMMENT '用户ID',
                          `product_id` INT NOT NULL COMMENT '商品ID',
                          `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          UNIQUE KEY `uk_user_product` (`user_id`, `product_id`) COMMENT '同一用户同一商品唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ====================================
-- 1. 订单表 (t_order)
-- ====================================
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
                           `order_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
                           `order_no` VARCHAR(32) NOT NULL COMMENT '订单号（唯一）',
                           `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
                           `merchant_id` BIGINT(20) NOT NULL COMMENT '商户ID',
                           `merchant_name` VARCHAR(100) DEFAULT NULL COMMENT '商户名称（冗余字段）',
                           `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额（商品金额）',
                           `delivery_fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '配送费',
                           `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
                           `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额（总金额+配送费-优惠）',
                           `payment_method` VARCHAR(20) DEFAULT 'online' COMMENT '支付方式：online-在线支付, cash-货到付款',
                           `payment_status` CHAR(1) DEFAULT '0' COMMENT '支付状态：0-未支付, 1-已支付, 2-已退款',
                           `order_status` VARCHAR(20) DEFAULT 'pending' COMMENT '订单状态：pending-待支付, paid-已支付, preparing-制作中, delivering-配送中, completed-已完成, cancelled-已取消',
                           `delivery_address` VARCHAR(255) NOT NULL COMMENT '配送地址',
                           `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
                           `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
                           `remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
                           `delivery_time` DATETIME DEFAULT NULL COMMENT '预计送达时间',
                           `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
                           `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
                           `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
                           `cancel_reason` VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`order_id`),
                           UNIQUE KEY `uk_order_no` (`order_no`),
                           KEY `idx_user_id` (`user_id`),
                           KEY `idx_merchant_id` (`merchant_id`),
                           KEY `idx_order_status` (`order_status`),
                           KEY `idx_payment_status` (`payment_status`),
                           KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ====================================
-- 2. 订单明细表 (t_order_item)
-- ====================================
DROP TABLE IF EXISTS `t_order_item`;
CREATE TABLE `t_order_item` (
                                `item_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
                                `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
                                `order_no` VARCHAR(32) NOT NULL COMMENT '订单号（冗余）',
                                `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
                                `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称（冗余）',
                                `product_img` VARCHAR(255) DEFAULT NULL COMMENT '商品图片（冗余）',
                                `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
                                `quantity` INT(11) NOT NULL COMMENT '购买数量',
                                `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计金额（单价×数量）',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                PRIMARY KEY (`item_id`),
                                KEY `idx_order_id` (`order_id`),
                                KEY `idx_order_no` (`order_no`),
                                KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ====================================
-- 3. 配送地址表 (t_address)
-- ====================================
DROP TABLE IF EXISTS `t_address`;
CREATE TABLE `t_address` (
                             `address_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
                             `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
                             `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
                             `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
                             `province` VARCHAR(50) DEFAULT NULL COMMENT '省份',
                             `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
                             `district` VARCHAR(50) DEFAULT NULL COMMENT '区县',
                             `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
                             `full_address` VARCHAR(500) NOT NULL COMMENT '完整地址（冗余）',
                             `is_default` CHAR(1) DEFAULT '0' COMMENT '是否默认地址：1-是, 0-否',
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`address_id`),
                             KEY `idx_user_id` (`user_id`),
                             KEY `idx_is_default` (`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送地址表';

-- ====================================
-- 4. 支付记录表 (t_payment) - 可选
-- ====================================
DROP TABLE IF EXISTS `t_payment`;
CREATE TABLE `t_payment` (
                             `payment_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
                             `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
                             `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
                             `payment_no` VARCHAR(32) DEFAULT NULL COMMENT '支付流水号',
                             `payment_method` VARCHAR(20) NOT NULL COMMENT '支付方式',
                             `payment_amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
                             `payment_status` VARCHAR(20) DEFAULT 'pending' COMMENT '支付状态：pending-待支付, success-支付成功, failed-支付失败, refund-已退款',
                             `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             PRIMARY KEY (`payment_id`),
                             KEY `idx_order_id` (`order_id`),
                             KEY `idx_order_no` (`order_no`),
                             KEY `idx_payment_status` (`payment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ====================================
-- 5. 插入测试地址数据（可选）
-- ====================================
-- 为user001用户添加一个默认地址
-- 方式1：直接使用user_id（推荐，user001的user_id通常是5）
-- 如果该用户已有地址，此语句会插入失败，可以忽略或手动删除后重新执行
INSERT INTO `t_address` (`user_id`, `contact_name`, `contact_phone`, `province`, `city`, `district`, `detail_address`, `full_address`, `is_default`)
SELECT 5, '张三', '13800138004', '湖北省', '武汉市', '洪山区', '珞喻路129号', '湖北省武汉市洪山区珞喻路129号', '1'
WHERE NOT EXISTS (SELECT 1 FROM t_address WHERE user_id = 5);

-- ====================================
-- 初始化完成验证
-- ====================================
SELECT '====================================' AS '';
SELECT '数据库初始化完成！' AS message;
SELECT '====================================' AS '';
SELECT CONCAT('用户数: ', COUNT(*)) AS '用户数' FROM sys_user;
SELECT CONCAT('角色数: ', COUNT(*)) AS '角色数' FROM sys_role;
SELECT CONCAT('商户数: ', COUNT(*)) AS '商户数' FROM t_merchant;
SELECT CONCAT('分类数: ', COUNT(*)) AS '分类数' FROM t_category;
SELECT CONCAT('菜品数: ', COUNT(*)) AS '菜品数' FROM t_product;
SELECT '购物车表' AS '表名', '已创建' AS '状态';

-- 显示商户信息
SELECT '=== 商户列表 ===' AS info;
SELECT merchant_id, merchant_name, description, phone, rating, sales, status
FROM t_merchant;

-- 显示商户账号
SELECT '=== 商户账号 ===' AS info;
SELECT u.user_id, u.username, u.role_id, m.merchant_name
FROM sys_user u
         LEFT JOIN t_merchant m ON u.user_id = m.user_id
WHERE u.role_id = 3;

-- 显示菜品分布
SELECT '=== 菜品分布 ===' AS info;
SELECT
    m.merchant_name AS '商户名称',
    COUNT(p.product_id) AS '菜品数量',
    GROUP_CONCAT(p.product_name SEPARATOR ', ') AS '菜品列表'
FROM t_merchant m
         LEFT JOIN t_product p ON m.merchant_id = p.merchant_id
GROUP BY m.merchant_id, m.merchant_name;

SELECT '====================================' AS '';
SELECT '订单结算系统数据库表创建完成！' AS message;
SELECT '====================================' AS '';
SELECT '订单表 (t_order)' AS '表名', '已创建' AS '状态';
SELECT '订单明细表 (t_order_item)' AS '表名', '已创建' AS '状态';
SELECT '配送地址表 (t_address)' AS '表名', '已创建' AS '状态';
SELECT '支付记录表 (t_payment)' AS '表名', '已创建' AS '状态';


SELECT '=== 初始化完成 ===' AS info;
SELECT '测试账号：admin/manager/merchant/deliver/user001，密码：123456' AS message;
SELECT '商户账号：merchant001/merchant002/merchant003，密码：123456' AS message;
