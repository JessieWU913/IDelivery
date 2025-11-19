# 📚 IDelivery 项目 MVC 三层架构详解

## 🎯 一、什么是三层架构？

**三层架构**是一种经典的软件架构模式,将应用程序分为三个逻辑层:

```
┌─────────────────────────────────────────────┐
│         Controller 层（控制层）               │  ← 接收请求、调用业务、返回响应
├─────────────────────────────────────────────┤
│         Service 层（业务逻辑层）              │  ← 处理业务逻辑、事务管理
├─────────────────────────────────────────────┤
│         Mapper/DAO 层（数据访问层）           │  ← 操作数据库、执行 SQL
└─────────────────────────────────────────────┘
           ↓
    ┌──────────────┐
    │   数据库      │  ← MySQL
    └──────────────┘
```

---

## 🔥 二、以订单模块为例详解三层架构

### 📁 **文件结构**

```
cs.qqq
├── Controller
│   └── OrderController.java        # 控制层
├── Service
│   ├── OrderService.java            # 业务接口
│   └── OrderServiceImpl.java        # 业务实现（标注 @Service）
├── Mapper
│   └── OrderMapper.java             # 数据访问接口（标注 @Mapper）
├── Entity
│   └── Order.java                   # 实体类
└── resources/mapper
    └── OrderMapper.xml              # MyBatis SQL 映射文件
```

---

## 🎯 三、各层职责详解

### **1. Controller 层（控制层）**

#### **职责**
- **接收 HTTP 请求**：处理用户的 HTTP 请求（GET、POST等）
- **参数校验**：验证请求参数的合法性
- **调用 Service**：调用业务逻辑层处理业务
- **返回响应**：将处理结果封装成 JSON 或跳转页面

#### **核心注解**
- `@Controller`：标记为控制器，返回页面（Thymeleaf）
- `@RestController`：标记为 RESTful 控制器，返回 JSON（`@Controller` + `@ResponseBody`）
- `@RequestMapping`：映射请求路径
- `@GetMapping`、`@PostMapping`：指定 HTTP 方法
- `@PathVariable`：获取 URL 路径参数
- `@RequestParam`：获取 URL 查询参数
- `@RequestBody`：获取请求体（JSON）

#### **示例代码：OrderController.java**

```java
@Controller
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;  // 注入 Service 层
    
    /**
     * 创建订单（AJAX请求）
     * URL: POST /order/create
     * 参数: deliveryAddress, contactName, contactPhone, remark
     * 返回: JSON { success: true/false, message: "xxx" }
     */
    @PostMapping("/create")
    @ResponseBody  // 返回 JSON，不是页面
    public Map<String, Object> createOrder(
            @RequestParam String deliveryAddress,  // 从请求参数获取
            @RequestParam String contactName,
            @RequestParam String contactPhone,
            @RequestParam(required = false) String remark,  // 非必填
            HttpSession session) {  // 获取 Session
        
        Map<String, Object> result = new HashMap<>();
        
        // 1. 从 Session 获取当前用户
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        
        try {
            // 2. 调用 Service 层创建订单
            List<Order> orders = orderService.createOrderFromCart(
                user.getUserId(),
                deliveryAddress,
                contactName,
                contactPhone,
                remark
            );
            
            // 3. 返回成功结果
            result.put("success", true);
            result.put("message", "订单创建成功");
            result.put("orders", orders);
        } catch (Exception e) {
            // 4. 捕获异常，返回失败结果
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 订单列表页面
     * URL: GET /order/list
     * 返回: Thymeleaf 页面
     */
    @GetMapping("/list")
    public String orderList(HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login.html";
        }
        
        // 调用 Service 层查询订单
        List<Order> orders = orderService.getUserOrders(user.getUserId());
        
        // 将数据传递给页面
        model.addAttribute("orders", orders);
        
        // 返回页面路径（templates/order/list.html）
        return "order/list";
    }
}
```

#### **技术要点**
1. **Controller 不处理业务逻辑**：只负责接收请求、调用 Service、返回响应
2. **依赖 Service 接口，不依赖实现类**：便于替换实现、方便测试
3. **统一异常处理**：使用 try-catch 捕获异常，返回友好的错误信息
4. **Session 管理**：从 Session 获取当前用户信息

---

### **2. Service 层（业务逻辑层）**

#### **职责**
- **处理业务逻辑**：实现具体的业务规则（如订单创建、支付、取消）
- **事务管理**：使用 `@Transactional` 保证数据一致性
- **调用 Mapper 层**：操作数据库
- **组合多个 Mapper**：一个 Service 方法可能调用多个 Mapper

#### **为什么要分接口和实现类？**

**OrderService.java（接口）**
```java
public interface OrderService {
    List<Order> createOrderFromCart(Long userId, String deliveryAddress, ...);
    Order getOrderDetail(Long orderId);
    boolean payOrder(Long orderId, String paymentMethod);
}
```

**OrderServiceImpl.java（实现类）**
```java
@Service  // Spring 会自动创建实例
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Transactional  // 声明式事务
    public List<Order> createOrderFromCart(...) {
        // 具体业务逻辑
    }
}
```

**优势：**
1. **解耦**：Controller 依赖接口，不依赖具体实现
2. **易测试**：可以创建 Mock 实现进行单元测试
3. **易扩展**：可以有多个实现类（如 OrderServiceV2Impl）

#### **示例代码：OrderServiceImpl.java（核心方法）**

```java
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    /**
     * 从购物车创建订单
     * 
     * 【事务管理】
     * @Transactional：保证订单创建、库存扣减、购物车清空要么全部成功，要么全部回滚
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Order> createOrderFromCart(Long userId, ...) {
        // 1. 查询购物车（调用 CartMapper）
        List<Cart> cartList = cartMapper.findCartListByUserId(userId);
        
        // 2. 验证库存（调用 ProductMapper）
        for (Cart cart : cartList) {
            Product product = productMapper.findById(cart.getProductId());
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("库存不足");
            }
        }
        
        // 3. 按商户分组
        Map<Long, List<Cart>> cartByMerchant = cartList.stream()
            .collect(Collectors.groupingBy(Cart::getMerchantId));
        
        // 4. 为每个商户创建订单
        List<Order> orders = new ArrayList<>();
        for (Map.Entry<Long, List<Cart>> entry : cartByMerchant.entrySet()) {
            Order order = new Order();
            // ... 设置订单字段
            orderMapper.insertOrder(order);  // 调用 OrderMapper
            
            // 扣减库存
            for (Cart cart : entry.getValue()) {
                productMapper.updateStock(cart.getProductId(), cart.getQuantity());
            }
            
            orders.add(order);
        }
        
        // 5. 清空购物车（调用 CartMapper）
        cartMapper.clearCart(userId);
        
        return orders;
    }
}
```

#### **技术要点**
1. **事务管理**：`@Transactional` 保证多个数据库操作的原子性
2. **业务逻辑封装**：将复杂的业务规则封装在 Service 中
3. **调用多个 Mapper**：一个 Service 方法可能操作多张表
4. **异常处理**：抛出 RuntimeException，事务会自动回滚

---

### **3. Mapper 层（数据访问层）**

#### **职责**
- **定义数据操作接口**：增删改查方法
- **执行 SQL 语句**：通过 MyBatis 映射到 XML 中的 SQL
- **封装结果集**：将 ResultSet 转换为 Java 对象

#### **核心注解**
- `@Mapper`：标记为 MyBatis Mapper 接口
- `@Param`：给方法参数命名，用于 XML 中引用

#### **示例代码：OrderMapper.java（接口）**

```java
@Mapper  // MyBatis 会自动生成实现类
public interface OrderMapper {
    
    /**
     * 插入订单
     * @param order 订单对象
     * 对应 XML: <insert id="insertOrder">
     */
    void insertOrder(Order order);
    
    /**
     * 根据订单ID查询订单
     * @param orderId 订单ID
     * @return 订单对象
     * 对应 XML: <select id="findById">
     */
    Order findById(@Param("orderId") Long orderId);
    
    /**
     * 查询用户的订单列表
     * @param userId 用户ID
     * @return 订单列表
     * 对应 XML: <select id="findByUserId">
     */
    List<Order> findByUserId(@Param("userId") Long userId);
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param orderStatus 订单状态
     * 对应 XML: <update id="updateOrderStatus">
     */
    void updateOrderStatus(@Param("orderId") Long orderId, 
                          @Param("orderStatus") String orderStatus);
}
```

#### **对应的 XML：OrderMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!-- namespace 必须是 Mapper 接口的全限定名 -->
<mapper namespace="cs.qqq.Mapper.OrderMapper">

    <!-- 插入订单 -->
    <insert id="insertOrder" parameterType="cs.qqq.Entity.Order" 
            useGeneratedKeys="true" keyProperty="orderId">
        INSERT INTO t_order (
            order_no, user_id, merchant_id, total_amount, order_status
        ) VALUES (
            #{orderNo}, #{userId}, #{merchantId}, #{totalAmount}, #{orderStatus}
        )
    </insert>

    <!-- 根据ID查询订单 -->
    <select id="findById" parameterType="Long" resultType="cs.qqq.Entity.Order">
        SELECT * FROM t_order WHERE order_id = #{orderId}
    </select>

    <!-- 查询用户订单列表 -->
    <select id="findByUserId" parameterType="Long" resultType="cs.qqq.Entity.Order">
        SELECT * FROM t_order 
        WHERE user_id = #{userId}
        ORDER BY create_time DESC
    </select>

    <!-- 更新订单状态 -->
    <update id="updateOrderStatus" parameterType="map">
        UPDATE t_order 
        SET order_status = #{orderStatus}
        WHERE order_id = #{orderId}
    </update>

</mapper>
```

#### **技术要点**
1. **Mapper 接口不需要实现类**：MyBatis 通过动态代理自动生成
2. **方法名必须与 XML 中的 id 对应**：`findById()` → `<select id="findById">`
3. **`@Param` 注解的作用**：给参数命名，在 XML 中用 `#{参数名}` 引用
4. **`useGeneratedKeys="true"`**：插入数据后自动回填主键ID

---

## 🔄 四、三层架构调用流程（完整示例）

### **场景：用户创建订单**

```
用户点击"提交订单" 
    ↓
前端发送 AJAX 请求: POST /order/create
    ↓
【Controller 层】OrderController.createOrder()
    ├── 1. 从 Session 获取当前用户
    ├── 2. 调用 Service 层: orderService.createOrderFromCart()
    └── 3. 返回 JSON 结果
         ↓
【Service 层】OrderServiceImpl.createOrderFromCart()
    ├── 1. 调用 CartMapper.findCartListByUserId() 查询购物车
    ├── 2. 调用 ProductMapper.findById() 验证库存
    ├── 3. 调用 OrderMapper.insertOrder() 创建订单
    ├── 4. 调用 ProductMapper.updateStock() 扣减库存
    └── 5. 调用 CartMapper.clearCart() 清空购物车
         ↓
【Mapper 层】OrderMapper、CartMapper、ProductMapper
    ├── OrderMapper.insertOrder() 
    │   → 执行 SQL: INSERT INTO t_order (...)
    ├── ProductMapper.updateStock()
    │   → 执行 SQL: UPDATE t_product SET stock = stock - #{quantity}
    └── CartMapper.clearCart()
        → 执行 SQL: DELETE FROM t_cart WHERE user_id = #{userId}
         ↓
【数据库】MySQL
    ├── t_order 表插入新记录
    ├── t_product 表更新库存
    └── t_cart 表删除购物车数据
```

### **完整代码示例**

#### **1. 前端 AJAX 请求（confirm.html）**

```javascript
function submitOrder() {
    $.ajax({
        url: '/order/create',
        type: 'POST',
        data: {
            deliveryAddress: $('#address').val(),
            contactName: $('#name').val(),
            contactPhone: $('#phone').val(),
            remark: $('#remark').val()
        },
        success: function(result) {
            if (result.success) {
                alert('订单创建成功！');
                window.location.href = '/order/list';
            } else {
                alert('订单创建失败：' + result.message);
            }
        }
    });
}
```

#### **2. Controller 层接收请求**

```java
@PostMapping("/create")
@ResponseBody
public Map<String, Object> createOrder(...) {
    // 调用 Service 层
    List<Order> orders = orderService.createOrderFromCart(...);
    return result;
}
```

#### **3. Service 层处理业务**

```java
@Transactional
public List<Order> createOrderFromCart(...) {
    // 调用多个 Mapper 操作数据库
    cartMapper.findCartListByUserId(userId);
    orderMapper.insertOrder(order);
    productMapper.updateStock(productId, quantity);
    cartMapper.clearCart(userId);
    return orders;
}
```

#### **4. Mapper 层执行 SQL**

```java
@Mapper
public interface OrderMapper {
    void insertOrder(Order order);
}
```

```xml
<insert id="insertOrder">
    INSERT INTO t_order (...) VALUES (...)
</insert>
```

---

## 🎨 五、三层架构的优势

| 优势 | 说明 |
|------|------|
| **分层解耦** | 每层只依赖下一层，降低耦合度 |
| **职责清晰** | Controller处理请求、Service处理业务、Mapper操作数据库 |
| **易于测试** | 可以单独测试每一层（单元测试） |
| **易于维护** | 修改业务逻辑只需修改 Service，不影响 Controller |
| **可复用性** | Service 方法可以被多个 Controller 调用 |
| **易于扩展** | 可以轻松添加新功能或替换实现 |

---

## 🔥 六、常见面试问题

### **1. 为什么要分 Service 接口和实现类？**

**答：**
- **解耦**：Controller 依赖接口，不依赖具体实现，便于替换实现类
- **易测试**：可以创建 Mock 实现类进行单元测试
- **多实现**：可以有多个实现类（如 OrderServiceV1、OrderServiceV2）

### **2. @Autowired 的作用是什么？**

**答：**
- Spring 的依赖注入注解
- 自动从 Spring 容器中查找匹配的 Bean 并注入
- 无需手动 `new` 对象，Spring 自动管理对象的生命周期

### **3. MyBatis 的 Mapper 接口没有实现类，为什么可以使用？**

**答：**
- MyBatis 使用 **JDK 动态代理** 在运行时生成实现类
- 代理对象拦截方法调用，读取 XML 中的 SQL 并执行
- 封装结果集返回 Java 对象

### **4. @Transactional 的作用是什么？**

**答：**
- 声明式事务管理
- 保证方法中的多个数据库操作要么全部成功，要么全部回滚
- `rollbackFor = Exception.class` 表示遇到任何异常都回滚

### **5. Controller、Service、Mapper 三层的职责分别是什么？**

**答：**
- **Controller 层**：接收请求、参数校验、调用 Service、返回响应
- **Service 层**：处理业务逻辑、事务管理、调用 Mapper
- **Mapper 层**：操作数据库、执行 SQL、封装结果集

---

## 📝 七、总结

### **记忆口诀**

```
Controller 接请求，调 Service 做业务
Service 管事务，调 Mapper 访数据
Mapper 执行 SQL，结果返回给上层
```

### **核心原则**

1. **单一职责原则**：每一层只做自己该做的事
2. **依赖倒置原则**：依赖接口，不依赖实现
3. **面向接口编程**：Service 定义接口，Impl 实现业务
4. **事务管理**：Service 层使用 `@Transactional` 保证数据一致性
5. **分层调用**：Controller → Service → Mapper → Database

---

**恭喜！你已经掌握了 MVC 三层架构的核心知识！** 🎉
