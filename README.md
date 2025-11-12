# IDelivery 多商户外卖点餐系统

## 项目简介
这是一个基于 Spring Boot + MyBatis + Thymeleaf 的多商户在线外卖点餐系统，支持用户点餐、商户管理和后台管理三大核心功能模块。

## 功能特性

### 用户端
- 用户登录/注册
- 按商户筛选浏览菜品
- 按分类筛选菜品（7大分类）
- 菜品搜索功能
- 商户搜索功能（输入商户名称快速筛选）
- 购物车管理（按商户分组显示）
- 多种排序方式（按商户、销量、价格）

### 商户端
- 商户登录
- 商户信息管理
- 菜品管理（添加、编辑、删除菜品）
- 查看商户统计数据
- 菜品列表查看

### 管理端
- 用户管理
- 角色权限管理
- 商户审核管理
- 系统内容管理

## 技术栈
- Spring Boot 2.1.3
- MyBatis 2.0.0
- MySQL 5.7+
- Thymeleaf 模板引擎
- Spring Security 安全框架
- Druid 数据库连接池
- AmazeUI 前端框架
- Bootstrap 后台管理界面

## 环境要求
- JDK 1.8 或更高版本
- Maven 3.x
- MySQL 5.7 或更高版本

## 快速开始

### 1. 数据库初始化

使用 MySQL 命令行：
```bash
mysql -u root -p
source C:\Users\bless\Desktop\whu\Large_software\waimai\database.sql
```

或使用 MySQL Workbench / Navicat 等工具执行 `database.sql` 脚本。

### 2. 配置数据库连接

编辑 `src/main/resources/application.properties`，修改数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/test?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false
spring.datasource.username=root
spring.datasource.password=你的MySQL密码
```

### 3. 启动项目

方法一：使用 IDEA
1. 打开项目
2. 找到 `cs.qqq.qqqExam` 主类
3. 右键 -> Run 'qqqExam'

方法二：使用 Maven 命令
```bash
cd C:\Users\bless\Desktop\whu\Large_software\waimai
mvn spring-boot:run
```

方法三：打包运行
```bash
mvn clean package
java -jar target/qqq_qimo-1.0-SNAPSHOT.jar
```

### 4. 访问应用

启动成功后，浏览器访问：http://localhost:8080

## 测试账号

系统预置了以下测试账号：

| 用户名 | 密码 | 角色 | 登录后跳转 |
|--------|------|------|------------|
| admin | 123456 | 超级管理员 | 后台管理页面 |
| manager | 123456 | 普通管理员 | 后台管理页面 |
| merchant001 | 123456 | 商户（川香阁） | 商户管理中心 |
| merchant002 | 123456 | 商户（粤味轩） | 商户管理中心 |
| merchant003 | 123456 | 商户（江南小厨） | 商户管理中心 |
| user001 | 123456 | 普通用户 | 菜单浏览页面 |

注意：首次登录后建议修改默认密码。

## 数据库表结构

### 核心表说明

**sys_role** - 角色表
- role_id: 角色ID（主键）
- role_name: 角色名称
- role_sort: 显示顺序
- status: 角色状态
- create_time: 创建时间

**sys_user** - 用户表
- user_id: 用户ID（主键）
- role_id: 角色ID（外键）
- user_name: 用户账号
- password: 密码
- email: 邮箱
- phonenumber: 手机号
- status: 账号状态
- create_time: 创建时间

**t_merchant** - 商户表
- merchant_id: 商户ID（主键）
- user_id: 关联用户ID（外键）
- merchant_name: 商户名称
- description: 商户描述
- logo: 商户Logo
- address: 商户地址
- phone: 联系电话
- business_hours: 营业时间
- status: 营业状态
- rating: 评分
- sales: 销量

**t_product** - 菜品表
- product_id: 菜品ID（主键）
- merchant_id: 商户ID（外键）
- category_id: 分类ID（外键）
- product_name: 菜品名称
- product_desc: 菜品描述
- product_img: 菜品图片
- price: 价格
- original_price: 原价
- stock: 库存
- sales: 销量
- status: 上架状态
- is_recommend: 是否推荐

**t_category** - 分类表
- category_id: 分类ID（主键）
- category_name: 分类名称
- category_sort: 排序

**t_cart** - 购物车表
- cart_id: 购物车ID（主键）
- user_id: 用户ID（外键）
- product_id: 菜品ID（外键）
- quantity: 数量
- create_time: 添加时间

## 常见问题

### 1. 连接数据库失败
- 检查 MySQL 服务是否启动
- 确认用户名和密码是否正确
- 确认数据库 `test` 是否已创建
- 检查端口 3306 是否被占用

### 2. 启动报错端口被占用
- 检查 8080 端口是否被占用
- 可以在 `application.properties` 中修改端口：`server.port=8081`

### 3. 登录失败
- 确认数据库中是否有用户数据
- 检查用户名和密码是否正确（区分大小写）
- 查看控制台日志获取详细错误信息

### 4. 商户菜品操作失败
- 按 F12 打开浏览器开发者工具查看控制台错误
- 检查 VS Code 终端查看后端日志
- 确认已使用商户账号登录

### 5. 页面样式错误
- 清除浏览器缓存（Ctrl + F5 强制刷新）
- 检查静态资源文件是否完整

## 项目结构

```
waimai/
├── src/main/
│   ├── java/cs/qqq/
│   │   ├── Config/          # 配置类（安全、数据源等）
│   │   ├── Controller/      # 控制器
│   │   ├── Entity/          # 实体类
│   │   ├── Mapper/          # MyBatis 映射接口
│   │   └── Service/         # 业务逻辑层
│   └── resources/
│       ├── mapper/          # MyBatis XML 映射文件
│       ├── static/          # 静态资源（CSS、JS、图片）
│       │   ├── assets/      # 用户端资源
│       │   ├── back/        # 后台管理资源
│       │   └── user/        # 用户页面资源
│       ├── templates/       # Thymeleaf 模板
│       │   ├── back/        # 后台管理页面
│       │   ├── merchant/    # 商户管理页面
│       │   ├── menu/        # 用户菜单页面
│       │   ├── cart/        # 购物车页面
│       │   ├── role/        # 角色管理页面
│       │   └── user/        # 用户管理页面
│       └── application.properties  # 应用配置
├── pom.xml                  # Maven 配置
└── database.sql             # 数据库初始化脚本
```

## 更新日志

### 2025-11-12
- 修复商户菜品添加、编辑、删除功能
- 添加商户搜索功能
- 优化购物车按商户分组显示
- 修复 jQuery 路径问题
- 添加详细的调试日志

### 2025-11-10
- 完成多商户系统架构
- 实现商户登录和权限控制
- 添加商户筛选和排序功能
- 优化用户菜单浏览体验

---

WHU 大型应用软件设计课程项目
最后更新：2025-11-12
