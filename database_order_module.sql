-- ====================================
-- 订单结算系统数据库脚本
-- 包含：订单表、订单明细表、配送地址表、支付记录表
-- 创建日期: 2025-11-10
-- ====================================

USE `test`;

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

-- 方式2：如果user_id不是5，可以使用下面的查询（需要根据实际表结构调整字段名）
-- 如果sys_user表使用username字段：
-- INSERT INTO `t_address` (`user_id`, `contact_name`, `contact_phone`, `province`, `city`, `district`, `detail_address`, `full_address`, `is_default`)
-- SELECT user_id, '张三', '13800138004', '湖北省', '武汉市', '洪山区', '珞喻路129号', '湖北省武汉市洪山区珞喻路129号', '1'
-- FROM sys_user WHERE username = 'user001' LIMIT 1;
--
-- 如果sys_user表使用user_name字段：
-- INSERT INTO `t_address` (`user_id`, `contact_name`, `contact_phone`, `province`, `city`, `district`, `detail_address`, `full_address`, `is_default`)
-- SELECT user_id, '张三', '13800138004', '湖北省', '武汉市', '洪山区', '珞喻路129号', '湖北省武汉市洪山区珞喻路129号', '1'
-- FROM sys_user WHERE user_name = 'user001' LIMIT 1;

-- ====================================
-- 初始化完成验证
-- ====================================
SELECT '====================================' AS '';
SELECT '订单结算系统数据库表创建完成！' AS message;
SELECT '====================================' AS '';
SELECT '订单表 (t_order)' AS '表名', '已创建' AS '状态';
SELECT '订单明细表 (t_order_item)' AS '表名', '已创建' AS '状态';
SELECT '配送地址表 (t_address)' AS '表名', '已创建' AS '状态';
SELECT '支付记录表 (t_payment)' AS '表名', '已创建' AS '状态';

