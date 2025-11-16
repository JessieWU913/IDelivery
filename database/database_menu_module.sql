-- ====================================
-- 外卖点餐系统 - 菜单模块数据库脚本
-- 创建日期: 2025-11-10
-- ====================================

USE `test`;

-- ====================================
-- 1. 创建菜品分类表 (t_category)
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

-- ====================================
-- 2. 创建菜品表 (t_product)
-- ====================================
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product` (
  `product_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
  `category_id` BIGINT(20) NOT NULL COMMENT '分类ID',
  `product_name` VARCHAR(100) NOT NULL COMMENT '菜品名称',
  `product_desc` VARCHAR(500) DEFAULT NULL COMMENT '菜品描述',
  `product_img` VARCHAR(255) DEFAULT NULL COMMENT '菜品图片URL',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `stock` INT(11) DEFAULT 999 COMMENT '库存数量',
  `sales` INT(11) DEFAULT 0 COMMENT '销量',
  `status` CHAR(1) DEFAULT '1' COMMENT '状态(1上架 0下架)',
  `is_recommend` CHAR(1) DEFAULT '0' COMMENT '是否推荐(1是 0否)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`product_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- ====================================
-- 3. 插入测试数据 - 菜品分类
-- ====================================
INSERT INTO `t_category` (`category_id`, `category_name`, `category_desc`, `category_icon`, `sort_order`, `status`) VALUES
(1, '热销推荐', '最受欢迎的美食', '/img/category/hot.png', 1, '1'),
(2, '主食类', '米饭、面条等主食', '/img/category/staple.png', 2, '1'),
(3, '荤菜类', '各类肉食菜品', '/img/category/meat.png', 3, '1'),
(4, '素菜类', '新鲜蔬菜菜品', '/img/category/vegetable.png', 4, '1'),
(5, '汤品类', '营养汤品', '/img/category/soup.png', 5, '1'),
(6, '饮料类', '各类饮品', '/img/category/drink.png', 6, '1'),
(7, '小吃类', '美味小吃', '/img/category/snack.png', 7, '1');

-- ====================================
-- 4. 插入测试数据 - 菜品
-- ====================================
-- 热销推荐
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `status`, `is_recommend`) VALUES
(1, '宫保鸡丁', '经典川菜，鸡肉鲜嫩，花生酥脆，口感丰富', '/img/product/gongbao.jpg', 28.00, 35.00, 100, 256, '1', '1'),
(1, '鱼香肉丝', '酸甜可口，下饭神器', '/img/product/yuxiang.jpg', 26.00, 32.00, 100, 189, '1', '1'),
(1, '麻婆豆腐', '麻辣鲜香，豆腐嫩滑', '/img/product/mapo.jpg', 18.00, 22.00, 100, 167, '1', '1');

-- 主食类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `status`, `is_recommend`) VALUES
(2, '米饭', '东北优质大米，粒粒饱满', '/img/product/rice.jpg', 2.00, NULL, 999, 523, '1', '0'),
(2, '蛋炒饭', '金黄色的蛋炒饭，粒粒分明', '/img/product/fried_rice.jpg', 12.00, 15.00, 100, 198, '1', '0'),
(2, '担担面', '四川特色面食，麻辣鲜香', '/img/product/dandan.jpg', 15.00, 18.00, 80, 145, '1', '0'),
(2, '阳春面', '清淡爽口，汤鲜面滑', '/img/product/yangchun.jpg', 10.00, NULL, 100, 89, '1', '0');

-- 荤菜类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `status`, `is_recommend`) VALUES
(3, '红烧肉', '肥而不腻，入口即化', '/img/product/hongshao.jpg', 32.00, 38.00, 50, 234, '1', '0'),
(3, '糖醋排骨', '酸甜可口，外酥里嫩', '/img/product/tangcu.jpg', 35.00, 42.00, 50, 178, '1', '0'),
(3, '水煮鱼', '麻辣鲜香，鱼肉鲜嫩', '/img/product/shuizhu.jpg', 48.00, 55.00, 30, 156, '1', '0'),
(3, '辣子鸡', '香辣干脆，回味无穷', '/img/product/lazi.jpg', 30.00, 36.00, 60, 134, '1', '0'),
(3, '小炒肉', '家常美味，香辣下饭', '/img/product/xiaochao.jpg', 25.00, 30.00, 80, 201, '1', '0');

-- 素菜类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `status`, `is_recommend`) VALUES
(4, '蒜蓉西兰花', '清淡营养，保留蔬菜鲜味', '/img/product/xilan.jpg', 15.00, NULL, 100, 98, '1', '0'),
(4, '清炒油麦菜', '清脆爽口，健康低脂', '/img/product/youmai.jpg', 12.00, NULL, 100, 112, '1', '0'),
(4, '地三鲜', '东北名菜，茄子土豆青椒完美组合', '/img/product/disanxian.jpg', 22.00, 26.00, 80, 156, '1', '0'),
(4, '干煸豆角', '外焦里嫩，香味浓郁', '/img/product/doujiang.jpg', 18.00, 22.00, 70, 89, '1', '0');

-- 汤品类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `status`, `is_recommend`) VALUES
(5, '西红柿蛋汤', '酸甜可口，营养丰富', '/img/product/xihongshi.jpg', 8.00, NULL, 100, 234, '1', '0'),
(5, '紫菜蛋花汤', '清淡鲜美，补碘佳品', '/img/product/zicai.jpg', 6.00, NULL, 100, 198, '1', '0'),
(5, '酸辣汤', '酸辣开胃，驱寒暖胃', '/img/product/suanla.jpg', 10.00, NULL, 80, 167, '1', '0'),
(5, '老鸭汤', '滋补养生，汤鲜肉嫩', '/img/product/laoya.jpg', 28.00, 35.00, 30, 78, '1', '0');

-- 饮料类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `status`, `is_recommend`) VALUES
(6, '可口可乐', '经典可乐，冰爽畅饮', '/img/product/cola.jpg', 3.00, NULL, 200, 456, '1', '0'),
(6, '雪碧', '清爽柠檬味汽水', '/img/product/sprite.jpg', 3.00, NULL, 200, 389, '1', '0'),
(6, '橙汁', '鲜榨橙汁，维C丰富', '/img/product/orange.jpg', 8.00, NULL, 100, 234, '1', '0'),
(6, '酸梅汤', '消暑解渴，酸甜可口', '/img/product/suanmei.jpg', 6.00, NULL, 100, 178, '1', '0'),
(6, '柠檬茶', '清新柠檬，茶香浓郁', '/img/product/lemon.jpg', 10.00, NULL, 80, 145, '1', '0');

-- 小吃类
INSERT INTO `t_product` (`category_id`, `product_name`, `product_desc`, `product_img`, `price`, `original_price`, `stock`, `sales`, `status`, `is_recommend`) VALUES
(7, '鸡米花', '香酥可口，外酥里嫩', '/img/product/popcorn.jpg', 12.00, 15.00, 100, 267, '1', '0'),
(7, '薯条', '金黄酥脆，老少皆宜', '/img/product/fries.jpg', 10.00, NULL, 150, 398, '1', '0'),
(7, '鸡翅', '烤制鸡翅，香嫩多汁', '/img/product/wings.jpg', 15.00, 18.00, 80, 289, '1', '0'),
(7, '春卷', '传统小吃，酥脆美味', '/img/product/spring.jpg', 8.00, NULL, 100, 156, '1', '0');

-- ====================================
-- 验证数据
-- ====================================
SELECT '菜单模块数据初始化完成！' AS message;
SELECT CONCAT('分类数量: ', COUNT(*)) AS category_count FROM t_category;
SELECT CONCAT('菜品数量: ', COUNT(*)) AS product_count FROM t_product;
SELECT category_name, COUNT(*) AS product_count 
FROM t_category c 
LEFT JOIN t_product p ON c.category_id = p.category_id 
GROUP BY c.category_id, c.category_name;
