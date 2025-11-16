-- ====================================
-- 外卖系统数据库初始化脚本
-- 创建日期: 2025-11-10
-- ====================================

-- 创建数据库
DROP DATABASE IF EXISTS `test`;
CREATE DATABASE `test` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `test`;

-- ====================================
-- 1. 创建角色表 (sys_role)
-- ====================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(30) NOT NULL COMMENT '角色名称',
  `role_sort` BIGINT(20) DEFAULT NULL COMMENT '显示顺序',
  `status` CHAR(1) DEFAULT '0' COMMENT '角色状态(0正常 1停用)',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ====================================
-- 2. 创建用户表 (sys_user)
-- ====================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `role_id` BIGINT(20) DEFAULT NULL COMMENT '角色ID',
  `user_name` VARCHAR(30) NOT NULL COMMENT '用户账号',
  `email` VARCHAR(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` VARCHAR(11) DEFAULT '' COMMENT '手机号码',
  `sex` CHAR(1) DEFAULT '0' COMMENT '用户性别(0男 1女 2未知)',
  `password` VARCHAR(100) DEFAULT '' COMMENT '密码',
  `status` CHAR(1) DEFAULT '0' COMMENT '帐号状态(0正常 1停用)',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  `login_date` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `pic` VARCHAR(255) DEFAULT NULL COMMENT '头像路径',
  PRIMARY KEY (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ====================================
-- 3. 插入初始角色数据
-- ====================================
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_sort`, `status`, `del_flag`, `create_by`, `create_time`, `remark`) VALUES
(1, '超级管理员', 1, '0', '0', 'admin', NOW(), '超级管理员角色'),
(2, '普通管理员', 2, '0', '0', 'admin', NOW(), '普通管理员角色'),
(3, '商家', 3, '0', '0', 'admin', NOW(), '商家角色'),
(4, '配送员', 4, '0', '0', 'admin', NOW(), '配送员角色'),
(5, '普通用户', 5, '0', '0', 'admin', NOW(), '普通用户角色');

-- ====================================
-- 4. 插入初始用户数据 (默认密码: 123456)
-- ====================================
INSERT INTO `sys_user` (`user_id`, `role_id`, `user_name`, `email`, `phonenumber`, `sex`, `password`, `status`, `del_flag`, `create_by`, `create_time`, `remark`) VALUES
(1, 1, 'admin', 'admin@waimai.com', '13800138000', '0', '123456', '0', '0', 'system', NOW(), '超级管理员账号'),
(2, 2, 'manager', 'manager@waimai.com', '13800138001', '0', '123456', '0', '0', 'admin', NOW(), '管理员账号'),
(3, 3, 'merchant', 'merchant@waimai.com', '13800138002', '0', '123456', '0', '0', 'admin', NOW(), '商家账号'),
(4, 4, 'delivery', 'delivery@waimai.com', '13800138003', '0', '123456', '0', '0', 'admin', NOW(), '配送员账号'),
(5, 5, 'user001', 'user001@waimai.com', '13800138004', '1', '123456', '0', '0', 'admin', NOW(), '普通用户账号');

-- ====================================
-- 初始化完成
-- ====================================
SELECT '数据库初始化完成！' AS message;
SELECT CONCAT('角色数量: ', COUNT(*)) AS role_count FROM sys_role;
SELECT CONCAT('用户数量: ', COUNT(*)) AS user_count FROM sys_user;
