-- ====================================
-- 外卖系统完整数据库初始化脚本
-- 包含：用户管理 + 菜单模块 + 购物车模块
-- 创建日期: 2025-11-10
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
(3, '商家', '可以管理商品和订单', '1'),
(4, '配送员', '负责订单配送', '1'),
(5, '普通用户', '可以浏览和下单', '1');

-- 插入用户数据（密码都是123456）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `role_id`, `status`) VALUES
('admin', '123456', '超级管理员', '13800138000', 1, '1'),
('manager', '123456', '系统管理员', '13800138001', 2, '1'),
('merchant', '123456', '商家001', '13800138002', 3, '1'),
('delivery', '123456', '配送员001', '13800138003', 4, '1'),
('user001', '123456', '普通用户', '13800138004', 5, '1');

-- ====================================
-- 第二部分：菜单模块
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
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- 插入分类数据
INSERT INTO `t_category` (`category_name`, `category_desc`, `category_icon`, `sort_order`, `status`) VALUES
('热销推荐', '最受欢迎的菜品', '/img/category/hot.png', 1, '1'),
('主食类', '米饭、面条等主食', '/img/category/staple.png', 2, '1'),
('荤菜类', '肉类菜品', '/img/category/meat.png', 3, '1'),
('素菜类', '蔬菜类菜品', '/img/category/vegetable.png', 4, '1'),
('汤品类', '各类汤品', '/img/category/soup.png', 5, '1'),
('饮料类', '饮品', '/img/category/drink.png', 6, '1'),
('小吃类', '小吃零食', '/img/category/snack.png', 7, '1');

-- 插入菜品数据（热销推荐）
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
(1000, '宫保鸡丁', '经典川菜，鸡肉鲜嫩，花生酥脆', '/img/product/gongbao.jpg', 28.00, 35.00, 999, 1580, '1', '1'),
(1000, '鱼香肉丝', '酸甜可口，下饭神器', '/img/product/yuxiang.jpg', 26.00, 32.00, 999, 1420, '1', '1'),
(1000, '麻婆豆腐', '麻辣鲜香，嫩滑入味', '/img/product/mapo.jpg', 22.00, 28.00, 999, 1350, '1', '1');

-- 主食类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
(1001, '白米饭', '香软可口的白米饭', '/img/product/rice.jpg', 3.00, NULL, 999, 3200, '0', '1'),
(1001, '蛋炒饭', '金黄诱人的蛋炒饭', '/img/product/fried_rice.jpg', 15.00, 18.00, 999, 980, '0', '1'),
(1001, '杨州炒饭', '料足味美的扬州炒饭', '/img/product/yangzhou_rice.jpg', 22.00, 26.00, 999, 720, '1', '1'),
(1001, '牛肉面', '浓郁牛肉汤底', '/img/product/beef_noodle.jpg', 25.00, 30.00, 999, 650, '0', '1');

-- 荤菜类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
(1002, '红烧肉', '肥而不腻，入口即化', '/img/product/hongshao.jpg', 35.00, 42.00, 999, 890, '1', '1'),
(1002, '糖醋排骨', '酸甜适中，色泽诱人', '/img/product/tangcu.jpg', 38.00, 45.00, 999, 760, '1', '1'),
(1002, '清蒸鲈鱼', '鱼肉鲜嫩，原汁原味', '/img/product/steamed_fish.jpg', 58.00, 68.00, 999, 520, '1', '1'),
(1002, '口水鸡', '麻辣鲜香，鸡肉滑嫩', '/img/product/saliva_chicken.jpg', 32.00, 38.00, 999, 680, '0', '1'),
(1002, '辣子鸡', '香辣干脆，越吃越香', '/img/product/spicy_chicken.jpg', 36.00, 42.00, 999, 590, '0', '1');

-- 素菜类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
(1003, '清炒时蔬', '新鲜蔬菜，清淡健康', '/img/product/veg_stir.jpg', 18.00, NULL, 999, 1200, '0', '1'),
(1003, '干煸豆角', '豆角入味，香脆可口', '/img/product/dry_beans.jpg', 20.00, 24.00, 999, 780, '0', '1'),
(1003, '蒜蓉西兰花', '营养丰富，蒜香浓郁', '/img/product/broccoli.jpg', 22.00, NULL, 999, 650, '0', '1'),
(1003, '地三鲜', '东北名菜，家常美味', '/img/product/disanxian.jpg', 24.00, 28.00, 999, 820, '1', '1');

-- 汤品类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
(1004, '番茄蛋汤', '酸甜开胃，营养丰富', '/img/product/tomato_soup.jpg', 12.00, NULL, 999, 1100, '0', '1'),
(1004, '紫菜蛋花汤', '清淡鲜美', '/img/product/seaweed_soup.jpg', 10.00, NULL, 999, 980, '0', '1'),
(1004, '酸辣汤', '酸辣开胃，暖心暖胃', '/img/product/sour_soup.jpg', 15.00, NULL, 999, 850, '0', '1'),
(1004, '老鸭汤', '滋补养生，汤鲜味美', '/img/product/duck_soup.jpg', 38.00, 45.00, 999, 420, '1', '1');

-- 饮料类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
(1005, '可乐', '冰爽解渴', '/img/product/cola.jpg', 5.00, NULL, 999, 2500, '0', '1'),
(1005, '雪碧', '清凉透心', '/img/product/sprite.jpg', 5.00, NULL, 999, 2200, '0', '1'),
(1005, '鲜榨橙汁', '新鲜果汁，维C满满', '/img/product/orange_juice.jpg', 15.00, NULL, 999, 680, '1', '1'),
(1005, '柠檬茶', '清新爽口，解腻消暑', '/img/product/lemon_tea.jpg', 12.00, NULL, 999, 920, '0', '1'),
(1005, '酸梅汤', '传统饮品，生津止渴', '/img/product/plum_juice.jpg', 10.00, NULL, 999, 750, '0', '1');

-- 小吃类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `is_recommend`, `status`) VALUES
(1006, '炸鸡翅', '外酥里嫩，香气扑鼻', '/img/product/fried_wings.jpg', 18.00, 22.00, 999, 1350, '1', '1'),
(1006, '薯条', '金黄酥脆', '/img/product/fries.jpg', 12.00, NULL, 999, 1800, '0', '1'),
(1006, '春卷', '酥脆可口，馅料丰富', '/img/product/spring_roll.jpg', 15.00, NULL, 999, 680, '0', '1'),
(1006, '锅贴', '底部酥脆，馅料鲜美', '/img/product/guotie.jpg', 16.00, NULL, 999, 590, '0', '1');

-- ====================================
-- 第三部分：购物车模块
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
-- 初始化完成
-- ====================================
SELECT '数据库初始化完成！' AS message;
SELECT COUNT(*) AS '用户数' FROM sys_user;
SELECT COUNT(*) AS '角色数' FROM sys_role;
SELECT COUNT(*) AS '分类数' FROM t_category;
SELECT COUNT(*) AS '菜品数' FROM t_product;
SELECT '购物车表' AS '表名', '已创建' AS '状态';
