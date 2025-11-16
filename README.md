# IDelivery 外卖点餐系统 - 部署说明

## 一、环境要求

### 1. 必需软件
- **JDK 1.8** 或更高版本
- **Maven 3.x**
- **MySQL 5.7** 或更高版本

### 2. 检查环境
```bash
# 检查 Java 版本
java -version

# 检查 Maven 版本
mvn -version

# 检查 MySQL 是否运行
mysql --version
```

---

## 二、部署步骤

### 步骤 1：获取项目文件
将整个项目文件夹复制到你的电脑上
```bash
git clone https://github.com/JessieWU913/IDelivery.git
```

### 步骤 2：创建数据库
1. 启动 MySQL 数据库
2. 使用 MySQL 客户端（Navicat、MySQL Workbench 或命令行）连接数据库
3. 创建test数据库：(注意一定要名为test！！！)
   ```sql
   CREATE DATABASE test CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
   ```

### 步骤 3：导入数据
按顺序执行项目根目录下的 SQL 文件：

1. **打开 `database_init_all.sql` 文件**
2. **复制全部内容到test中**
3. **在 MySQL 客户端中执行**（选择 `test` 数据库）

这会创建以下表：
- `sys_user` - 用户表
- `sys_role` - 角色表
- `t_address` - 地址管理表
- `t_cart` - 购物车表
- `t_category` - 菜品分类表
- `t_merchant` - 商家表
- `t_order` - 订单表
- `t_order_item` - 订单项表
- `t_payment` - 支付项表
- `t_product` - 菜品表

### 步骤 4：配置数据库连接
打开 `src/main/resources/application.properties`，修改以下配置：

```properties
# 数据库连接（根据你的实际情况修改）
spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的MySQL密码

# 端口（如果8080被占用，可以改成8081等）
server.port=8080
```

⚠️ **重要**：将 `spring.datasource.password` 改成你自己的 MySQL 密码！

### 步骤 5：复制图片资源（可选）
如果需要显示菜品图片：
1. 将 `img/product/` 文件夹中的图片
2. 复制到 `src/main/resources/static/img/product/` 目录下

**注意**：目前只有宫保鸡丁有图片（gongbao.jpg），其他菜品会显示占位符。

### 步骤 6：启动应用
在项目根目录下打开命令行，执行：

```bash
# Windows PowerShell 或 CMD
mvn spring-boot:run

# 或者先打包再运行
mvn clean package
java -jar target/qqq_qimo-1.0-SNAPSHOT.jar
```

## 三、访问系统

### 1. 打开浏览器
访问：`http://localhost:8080`

### 2. 默认账号
系统提供以下角色的账号：

#### 普通用户（点餐）
- **账号**：`user001`
- **密码**：`123456`
- **功能**：浏览菜单、添加购物车、下单

#### 超级管理员（后台管理）
- **账号**：`admin`
- **密码**：`123456`
- **功能**：用户管理、角色管理、菜品管理

#### 商家（上架菜品/出餐）
- **账号**：`merchant` / `merchant001` / `merchant002` / `merchant003`
- **密码**：均为`123456`
- **功能**：开店、上架下架菜品、接单出餐

#### 骑手（接单送餐）
- **账号**：`deliver`
- **密码**：`123456`
- **功能**：接单、送餐

---

## 四、功能说明

### 普通用户功能
1. **登录后自动跳转到菜单页面**
2. **菜品分类浏览**：点击分类按钮查看不同类别菜品
3. **搜索菜品**：使用搜索框快速找到想要的菜品
4. **加入购物车**：点击"加入购物车"按钮
5. **查看购物车**：点击"购物车"按钮查看已选菜品
6. **查看订单**：点击"订单"按钮查看已下单订单状态
7. **调整数量**：在购物车中可以增减数量或删除商品
8. **下单**：在购物车界面下单，并支付订单
9. **订单状态追踪**：下单完成后在订单页面可查看当前订单状态。送达后可确认收货
10. **地址管理**：下单时可进行个人地址管理
11. **退出登录**：在主界面可退出登录

### 管理员功能
1. **登录后跳转到后台管理页面**
2. **用户管理**：查看、添加、编辑、删除用户、商家、骑手和菜品等
3. **角色管理**：管理用户角色权限
4. **菜品管理**：下架商家的菜品

### 商家功能
1. **登录后自动跳转到商店管理页面**
2. **菜品管理**：可以添加或删除菜品，设置菜品状态和数量
3. **店铺信息管理**：可设置店铺营业状态，并查看店铺评分和信息
4. **查看订单**：点击"订单"按钮查看已下单订单
5. **订单状态追踪**：接单后在订单页面可查看当前订单状态。做完后可出餐
6. **退出登录**：在主界面可退出登录

### 骑手功能
1. **登录后自动跳转到骑手接单页面**
2. **查看订单**：查看已出餐订单信息
3. **接单送餐**：可以选择订单接单，送达后点击送达
4. **退出登录**：在主界面可退出登录

---

## 五、常见问题

### ❌ 问题 1：端口 8080 已被占用
**错误信息**：`Address already in use: bind`

**解决方案**：
1. 修改 `application.properties` 中的 `server.port=8081`
2. 或者杀掉占用 8080 的进程：
   ```bash
   # Windows
   netstat -ano | findstr :8080
   taskkill /F /PID <进程ID>
   ```

### ❌ 问题 2：数据库连接失败
**错误信息**：`Access denied for user 'root'@'localhost'`

**解决方案**：
1. 检查 `application.properties` 中的用户名密码是否正确
2. 确保 MySQL 服务已启动
3. 确认数据库 `test` 已创建（名字必须为test）

### ❌ 问题 3：登录后出现 500 错误
**错误信息**：`Unknown column 'user_name' in 'where clause'`

**解决方案**：
1. 确保 `database_init_all.sql` 完整执行（只需要执行这一个）
2. 检查 `sys_user` 表的字段名是 `username`（不是 `user_name`）
3. 重启应用：
   ```bash
   # Ctrl+C 停止应用
   mvn clean spring-boot:run
   ```

### ❌ 问题 4：菜品图片不显示
**解决方案**：
1. 确保已将图片复制到 `src/main/resources/static/img/product/`
2. 图片文件名要与数据库中 `product_img` 字段匹配
3. 当前只有宫保鸡丁有图片，其他显示占位符是正常的

### ❌ 问题 5：添加购物车时返回 403 错误
**错误信息**：`Forbidden`

**解决方案**：
- 这是正常的，系统已配置 CSRF 防护
- 前端页面已自动处理 CSRF token，刷新页面后重试即可

---

## 六、项目结构说明

```
waimai/
├── src/
│   └── main/
│       ├── java/cs/qqq/
│       │   ├── Controller/      # 控制器层（处理请求）
│       │   │   ├── LoginController.java
│       │   │   ├── ProductController.java  # 菜单
│       │   │   ├── CartController.java     # 购物车
│       │   │   └── ...
│       │   ├── Service/         # 业务逻辑层
│       │   ├── Mapper/          # 数据访问层
│       │   ├── Entity/          # 实体类
│       │   └── Config/          # 配置类
│       └── resources/
│           ├── application.properties  # 配置文件 ⚠️ 需要修改
│           ├── mapper/          # MyBatis SQL 映射文件
│           ├── static/          # 静态资源（CSS、JS、图片）
│           └── templates/       # 前端页面
├── database_init_all.sql       # 数据库初始化脚本 ⚠️ 必须执行
└── pom.xml                     # Maven 依赖配置
```

---

## 七、技术栈

- **后端框架**：Spring Boot 2.1.3
- **ORM 框架**：MyBatis 2.0.0
- **模板引擎**：Thymeleaf
- **数据库**：MySQL 5.7+
- **前端框架**：AmazeUI
- **安全框架**：Spring Security

---

## 八、联系与支持

如果部署过程中遇到问题：
1. 检查本文档的"常见问题"部分
2. 确保所有环境配置正确
3. 查看控制台错误信息
4. 联系项目作者获取帮助