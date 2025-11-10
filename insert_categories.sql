-- 快速插入分类数据
USE test;

-- 插入分类数据
INSERT INTO `t_category` (`category_id`, `category_name`, `category_desc`, `category_icon`, `sort_order`, `status`) VALUES
(1000, '热销推荐', '最受欢迎的菜品', '/img/category/hot.png', 1, '1'),
(1001, '主食类', '米饭、面条等主食', '/img/category/staple.png', 2, '1'),
(1002, '荤菜类', '肉类菜品', '/img/category/meat.png', 3, '1'),
(1003, '素菜类', '蔬菜类菜品', '/img/category/vegetable.png', 4, '1'),
(1004, '汤品类', '各类汤品', '/img/category/soup.png', 5, '1'),
(1005, '饮料类', '饮品', '/img/category/drink.png', 6, '1'),
(1006, '小吃类', '小吃零食', '/img/category/snack.png', 7, '1');

-- 验证插入结果
SELECT * FROM t_category;
