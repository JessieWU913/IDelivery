-- ========================================
-- 商户模块数据库脚本
-- ========================================

USE test;

-- 1. 创建商户表
DROP TABLE IF EXISTS t_merchant;
CREATE TABLE t_merchant (
    merchant_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商户ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    merchant_name VARCHAR(100) NOT NULL COMMENT '商户名称',
    description TEXT COMMENT '商户简介',
    logo VARCHAR(255) COMMENT '商户logo',
    address VARCHAR(255) COMMENT '商户地址',
    phone VARCHAR(20) COMMENT '联系电话',
    business_hours VARCHAR(100) DEFAULT '09:00-22:00' COMMENT '营业时间',
    status TINYINT DEFAULT 1 COMMENT '状态 1-营业中 0-休息中',
    rating DECIMAL(2,1) DEFAULT 5.0 COMMENT '评分',
    sales INT DEFAULT 0 COMMENT '月销量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户表';

-- 2. 更新商户角色描述（roleId=3已存在）
UPDATE sys_role SET role_name='商户', role_desc='可以管理自己的店铺和菜品' WHERE role_id=3;

-- 3. 创建测试商户账号
INSERT INTO sys_user (role_id, username, email, phone, password, status, create_time)
VALUES 
    (3, 'merchant001', 'merchant001@test.com', '13800000001', '123456', '1', NOW()),
    (3, 'merchant002', 'merchant002@test.com', '13800000002', '123456', '1', NOW()),
    (3, 'merchant003', 'merchant003@test.com', '13800000003', '123456', '1', NOW())
ON DUPLICATE KEY UPDATE username=username;

-- 4. 创建商户信息
INSERT INTO t_merchant (user_id, merchant_name, description, logo, address, phone, business_hours, status, rating, sales)
SELECT 
    u.user_id,
    CASE 
        WHEN u.username = 'merchant001' THEN '川香阁'
        WHEN u.username = 'merchant002' THEN '粤味轩'
        WHEN u.username = 'merchant003' THEN '江南小厨'
    END,
    CASE 
        WHEN u.username = 'merchant001' THEN '正宗川菜，麻辣鲜香'
        WHEN u.username = 'merchant002' THEN '地道粤菜，清淡养生'
        WHEN u.username = 'merchant003' THEN '江南风味，精致小炒'
    END,
    '/img/merchant/default.jpg',
    CASE 
        WHEN u.username = 'merchant001' THEN '成都市锦江区春熙路100号'
        WHEN u.username = 'merchant002' THEN '广州市天河区天河路200号'
        WHEN u.username = 'merchant003' THEN '杭州市西湖区西湖路300号'
    END,
    CASE 
        WHEN u.username = 'merchant001' THEN '028-88888888'
        WHEN u.username = 'merchant002' THEN '020-99999999'
        WHEN u.username = 'merchant003' THEN '0571-77777777'
    END,
    '09:00-22:00',
    1,
    CASE 
        WHEN u.username = 'merchant001' THEN 4.8
        WHEN u.username = 'merchant002' THEN 4.9
        WHEN u.username = 'merchant003' THEN 4.7
    END,
    CASE 
        WHEN u.username = 'merchant001' THEN 1280
        WHEN u.username = 'merchant002' THEN 956
        WHEN u.username = 'merchant003' THEN 743
    END
FROM sys_user u
WHERE u.username IN ('merchant001', 'merchant002', 'merchant003')
ON DUPLICATE KEY UPDATE merchant_name=VALUES(merchant_name);

-- 5. 修改 t_product 表，添加 merchant_id 字段
ALTER TABLE t_product ADD COLUMN merchant_id BIGINT DEFAULT NULL COMMENT '商户ID' AFTER product_id;
ALTER TABLE t_product ADD KEY idx_merchant_id (merchant_id);

-- 6. 将现有菜品分配给不同商户
UPDATE t_product SET merchant_id = (
    SELECT merchant_id FROM t_merchant WHERE merchant_name = '川香阁' LIMIT 1
)
WHERE product_name IN ('宫保鸡丁', '麻婆豆腐', '鱼香肉丝', '回锅肉', '水煮鱼');

UPDATE t_product SET merchant_id = (
    SELECT merchant_id FROM t_merchant WHERE merchant_name = '粤味轩' LIMIT 1
)
WHERE product_name IN ('白切鸡', '蒜蓉粉丝蒸扇贝', '菠萝咕咾肉', '广式烧鹅', '清蒸石斑鱼');

UPDATE t_product SET merchant_id = (
    SELECT merchant_id FROM t_merchant WHERE merchant_name = '江南小厨' LIMIT 1
)
WHERE product_name IN ('东坡肉', '西湖醋鱼', '龙井虾仁', '叫花鸡', '松鼠鳜鱼');

-- 剩余菜品随机分配
UPDATE t_product SET merchant_id = (
    SELECT merchant_id FROM t_merchant ORDER BY RAND() LIMIT 1
)
WHERE merchant_id IS NULL;

-- 7. 查看结果
SELECT '=== 商户列表 ===' AS info;
SELECT merchant_id, merchant_name, description, phone, rating, sales, status 
FROM t_merchant;

SELECT '=== 商户账号 ===' AS info;
SELECT u.user_id, u.username, u.role_id, m.merchant_name 
FROM sys_user u 
LEFT JOIN t_merchant m ON u.user_id = m.user_id 
WHERE u.role_id = 3;

SELECT '=== 菜品分布 ===' AS info;
SELECT 
    m.merchant_name AS '商户名称',
    COUNT(p.product_id) AS '菜品数量',
    GROUP_CONCAT(p.product_name SEPARATOR ', ') AS '菜品列表'
FROM t_merchant m
LEFT JOIN t_product p ON m.merchant_id = p.merchant_id
GROUP BY m.merchant_id, m.merchant_name;

SELECT '=== 创建完成 ===' AS info;
SELECT '商户账号：merchant001/merchant002/merchant003，密码：123456' AS message;
