-- 购物车模块数据库表
USE test;

-- 创建购物车表
DROP TABLE IF EXISTS t_cart;
CREATE TABLE t_cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID',
    user_id INT NOT NULL COMMENT '用户ID',
    product_id INT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_product (user_id, product_id) COMMENT '同一用户同一商品唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
