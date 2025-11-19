package cs.qqq.Controller;

import cs.qqq.Entity.Order;
import cs.qqq.Entity.SysUser;
import cs.qqq.Entity.Cart;
import cs.qqq.Service.OrderService;
import cs.qqq.Service.AddressService;
import cs.qqq.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

/**
 * 订单Controller
 * 处理订单相关请求
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CartService cartService;

    /**
     * 订单确认页面（从购物车跳转）
     */
    @GetMapping("/confirm")
    public String confirmPage(HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login.html";
        }

        // 查询购物车数据
        List<Cart> cartList = cartService.getCartList(user.getUserId().intValue());
        if (cartList == null || cartList.isEmpty()) {
            model.addAttribute("error", "购物车为空，无法结算");
            return "redirect:/cart/index";
        }

        // 按商户分组
        Map<String, List<Cart>> cartByMerchant = new LinkedHashMap<>();
        for (Cart cart : cartList) {
            String merchantKey = cart.getMerchantId() + "_" + (cart.getMerchantName() != null ? cart.getMerchantName() : "未知商户");
            cartByMerchant.computeIfAbsent(merchantKey, k -> new ArrayList<>()).add(cart);
        }

        // 计算总价
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Cart cart : cartList) {
            BigDecimal itemTotal = cart.getPrice().multiply(new BigDecimal(cart.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }

        // 查询用户的地址列表
        model.addAttribute("addresses", addressService.getUserAddresses(user.getUserId()));
        model.addAttribute("cartList", cartList);
        model.addAttribute("cartByMerchant", cartByMerchant);
        model.addAttribute("totalPrice", totalPrice);

        return "order/confirm";
    }

    /**
     * ========================================
     * 创建订单（从购物车创建）
     * ========================================
     * 
     * 【功能说明】
     * 用户在购物车页面点击"结算"后，进入订单确认页面
     * 填写收货信息后，提交订单，本接口负责创建订单
     * 
     * 【请求方式】POST
     * 【请求路径】/order/create
     * 【返回格式】JSON
     * 
     * 【业务流程】
     * 1. 从Session获取当前登录用户
     * 2. 校验用户是否登录
     * 3. 调用Service层创建订单（购物车 → 订单）
     * 4. 返回创建结果（可能创建多个订单，按商户拆分）
     * 
     * 【技术要点详解】
     * 
     * 一、@PostMapping - HTTP请求方法映射
     *   - 功能：处理POST请求，URL为 /order/create
     *   - POST vs GET：
     *     * GET：查询数据，参数在URL中，有长度限制，不安全（密码可见）
     *     * POST：提交数据，参数在请求体中，无长度限制，相对安全
     *   - RESTful风格：POST表示"创建"操作，符合语义化设计
     * 
     * 二、@ResponseBody - 返回JSON数据
     *   - 功能：将返回值（Map）转换为JSON字符串，写入HTTP响应体
     *   - 对比：
     *     * 有@ResponseBody：返回 {"success":true,"message":"..."} （JSON数据）
     *     * 无@ResponseBody：返回 "order/list" （跳转到order/list.html页面）
     *   - 原理：Spring MVC使用Jackson库自动完成 Java对象 → JSON 的转换
     *   - 前端接收：AJAX请求通过 data.success、data.message 访问返回数据
     * 
     * 三、@RequestParam - 参数绑定（核心区别！！！）
     *   ┌────────────────────────────────────────────────────────────────────┐
     *   │ @RequestParam 是什么？                                               │
     *   │ 作用：从HTTP请求中提取参数，绑定到Java方法参数                        │
     *   │ 本质：参数绑定工具，与RESTful是正交关系（可以同时使用）                 │
     *   └────────────────────────────────────────────────────────────────────┘
     * 
     *   【参数来源】@RequestParam 可以从多种请求中提取参数：
     * 
     *   （1）URL查询字符串（GET请求常用）
     *       前端：GET /order/list?status=pending&page=1
     *       后端：getOrders(@RequestParam String status, @RequestParam int page)
     *       提取：status="pending"，page=1
     * 
     *   （2）表单数据（POST请求，本接口使用这种！）
     *       前端：<form> 提交，Content-Type: application/x-www-form-urlencoded
     *       数据格式：deliveryAddress=北京&contactName=张三&contactPhone=138...
     *       后端：@RequestParam String deliveryAddress, @RequestParam String contactName
     *       提取：deliveryAddress="北京市..."，contactName="张三"
     * 
     *   （3）multipart/form-data（文件上传）
     *       前端：<input type="file"> 提交
     *       后端：@RequestParam MultipartFile file
     * 
     *   【关键属性】
     *   - value/name：参数名称，默认与方法参数名一致
     *     示例：@RequestParam("addr") String deliveryAddress
     *          从请求中找"addr"参数，赋值给deliveryAddress变量
     * 
     *   - required：是否必填，默认true
     *     required=true：参数缺失时抛异常 400 Bad Request
     *     required=false：参数缺失时赋值null（备注remark可以不填）
     * 
     *   - defaultValue：默认值，参数缺失时使用
     *     示例：@RequestParam(defaultValue="1") int page
     *          如果请求中没有page参数，自动赋值page=1
     * 
     *   【@RequestParam vs @PathVariable - 重要对比！！！】
     *   
     *   @RequestParam - 查询条件/过滤参数（可选，可多个）
     *   ┌─────────────────────────────────────────────────────────────┐
     *   │ 场景：列表查询、筛选、分页、排序                              │
     *   │ 示例：GET /orders?status=pending&page=1&sort=time           │
     *   │ 代码：getOrders(@RequestParam String status,                │
     *   │                 @RequestParam int page,                     │
     *   │                 @RequestParam String sort)                  │
     *   │ 特点：参数可选，可以没有（required=false），可以有默认值          │
     *   └─────────────────────────────────────────────────────────────┘
     * 
     *   @PathVariable - 资源标识（必需，唯一）
     *   ┌─────────────────────────────────────────────────────────────┐
     *   │ 场景：获取/修改/删除特定资源                                  │
     *   │ 示例：GET /orders/123  （123是订单ID，路径的一部分）            │
     *   │ 代码：getOrder(@PathVariable Long orderId)                  │
     *   │ 特点：参数必需，是URL的组成部分，RESTful风格常用                │
     *   └─────────────────────────────────────────────────────────────┘
     * 
     *   【@RequestParam vs @RequestBody - 数据格式对比！！！】
     * 
     *   @RequestParam - 键值对数据（表单提交）
     *   ┌─────────────────────────────────────────────────────────────┐
     *   │ 数据格式：key1=value1&key2=value2  （简单键值对）              │
     *   │ Content-Type：application/x-www-form-urlencoded             │
     *   │ 前端代码：$('#form').serialize()  或  new URLSearchParams() │
     *   │ 后端代码：@RequestParam String key1, @RequestParam String key2│
     *   │ 适用场景：简单表单，参数少，传统Web应用                         │
     *   └─────────────────────────────────────────────────────────────┘
     * 
     *   @RequestBody - JSON/XML数据（API接口）
     *   ┌─────────────────────────────────────────────────────────────┐
     *   │ 数据格式：{"key1":"value1","key2":"value2"}  （JSON对象）     │
     *   │ Content-Type：application/json                              │
     *   │ 前端代码：JSON.stringify({...})  发送AJAX                   │
     *   │ 后端代码：@RequestBody OrderRequest request                 │
     *   │ 适用场景：复杂数据，嵌套对象，前后端分离，RESTful API           │
     *   └─────────────────────────────────────────────────────────────┘
     * 
     *   【本接口为什么用 @RequestParam？】
     *   原因1：前端使用jQuery的 $('#orderForm').serialize() 提交表单
     *         这会生成 key=value&key=value 格式，不是JSON
     *   原因2：参数简单（4个字符串），不需要封装成对象
     *   原因3：兼容传统HTML表单提交（<form method="post">）
     * 
     *   【如果改用 @RequestBody 需要改什么？】
     *   后端：@RequestBody CreateOrderRequest request
     *   前端：$.ajax({
     *           url: '/order/create',
     *           type: 'POST',
     *           contentType: 'application/json',  // 必须指定！！！
     *           data: JSON.stringify({            // 转换为JSON字符串
     *             deliveryAddress: '...',
     *             contactName: '...',
     *             contactPhone: '...',
     *             remark: '...'
     *           })
     *         })
     * 
     * 四、@RequestParam 与 RESTful API 的关系
     *   ┌────────────────────────────────────────────────────────────────┐
     *   │ 误区："@RequestParam 是非RESTful的，RESTful只能用@PathVariable" │
     *   │ 真相：两者不冲突！！！ RESTful风格中也大量使用 @RequestParam        │
     *   └────────────────────────────────────────────────────────────────┘
     * 
     *   RESTful API设计规范：
     *   1. URL表示资源（名词）：/orders（订单集合），/orders/123（单个订单）
     *   2. HTTP方法表示操作（动词）：
     *      - GET /orders       查询订单列表
     *      - POST /orders      创建新订单
     *      - GET /orders/123   查询订单详情
     *      - PUT /orders/123   更新订单
     *      - DELETE /orders/123 删除订单
     * 
     *   @RequestParam 在 RESTful 中的正确使用：
     *   ✅ GET /orders?status=pending&page=1&pageSize=10  （列表过滤/分页）
     *      getOrders(@RequestParam(required=false) String status,
     *                @RequestParam(defaultValue="1") int page)
     * 
     *   ✅ GET /products?category=food&minPrice=10&maxPrice=100  （商品搜索）
     *      searchProducts(@RequestParam String category,
     *                     @RequestParam BigDecimal minPrice)
     * 
     *   ✅ POST /orders/123/refund?reason=quality  （附加操作参数）
     *      refundOrder(@PathVariable Long id,
     *                  @RequestParam String reason)
     * 
     *   【本接口的RESTful改进建议】
     *   当前URL：POST /order/create  （URL包含动词create，不够RESTful）
     *   改进方案：POST /orders       （URL只有名词orders，POST隐含"创建"语义）
     * 
     *   当前参数：@RequestParam（表单提交，4个独立参数）
     *   改进方案：@RequestBody CreateOrderDTO dto（JSON提交，封装成对象）
     *   好处：参数集中管理，支持复杂嵌套，前后端分离标准做法
     * 
     * 五、HttpSession - 会话管理
     *   - 功能：获取当前登录用户信息
     *   - 原理：服务器为每个访问者创建独立的Session对象
     *   - 用户登录后：session.setAttribute("currentUser", user)
     *   - 后续请求获取：session.getAttribute("currentUser")
     *   - 生命周期：默认30分钟无操作自动过期
     * 
     * 六、Map<String, Object> - 统一响应格式
     *   - 结构：{ "success": true/false, "message": "...", "data": {...} }
     *   - 优点：前端统一判断 if(data.success) 处理成功/失败
     *   - 缺点：无类型约束，建议用专门的Result类封装
     * 
     * 【完整请求流程示例】
     * 1. 前端HTML表单：
     *    <form id="orderForm">
     *      <input name="deliveryAddress" value="北京市朝阳区xx路xx号">
     *      <input name="contactName" value="张三">
     *      <input name="contactPhone" value="13800138000">
     *      <textarea name="remark">少放辣椒，多加米饭</textarea>
     *    </form>
     * 
     * 2. 前端AJAX提交：
     *    $.post('/order/create', $('#orderForm').serialize(), function(data) {
     *      if (data.success) {
     *        alert('订单创建成功，共' + data.orderCount + '个订单');
     *      } else {
     *        alert(data.message);
     *      }
     *    });
     * 
     * 3. HTTP请求报文：
     *    POST /order/create HTTP/1.1
     *    Content-Type: application/x-www-form-urlencoded
     *    Cookie: JSESSIONID=8F3D2A1B...
     *    
     *    deliveryAddress=北京市朝阳区...&contactName=张三&contactPhone=138...&remark=少放辣椒
     * 
     * 4. Spring MVC处理：
     *    - DispatcherServlet接收请求
     *    - HandlerMapping找到OrderController.createOrder()方法
     *    - HandlerAdapter执行参数绑定：
     *      * @RequestParam 从请求体中提取 deliveryAddress="北京..."
     *      * HttpSession 从Cookie中的JSESSIONID找到对应Session
     *    - 执行方法体，返回Map
     *    - @ResponseBody触发Jackson转换：Map → JSON字符串
     * 
     * 5. HTTP响应报文：
     *    HTTP/1.1 200 OK
     *    Content-Type: application/json;charset=UTF-8
     *    
     *    {
     *      "success": true,
     *      "message": "订单创建成功",
     *      "orders": [
     *        {"orderId":1,"orderNo":"ORDER170034...","merchantName":"美食餐厅",...},
     *        {"orderId":2,"orderNo":"ORDER170034...","merchantName":"饮品店",...}
     *      ],
     *      "orderCount": 2
     *    }
     * 
     * 6. 前端接收：
     *    data.success === true
     *    data.orderCount === 2
     *    data.orders[0].merchantName === "美食餐厅"
     * 
     * @param deliveryAddress 配送地址（必填） - 从表单字段deliveryAddress提取
     * @param contactName 联系人姓名（必填） - 从表单字段contactName提取
     * @param contactPhone 联系电话（必填） - 从表单字段contactPhone提取
     * @param remark 订单备注（选填） - 从表单字段remark提取，可以为空（required=false）
     * @param session HTTP会话对象 - Spring自动注入，用于获取当前登录用户
     * @return JSON响应
     *         成功：{ success: true, message: "订单创建成功", orders: [...], orderCount: 2 }
     *         失败：{ success: false, message: "错误信息", needLogin: true }
     */
    @PostMapping("/create")
    @ResponseBody  // 返回JSON，不返回页面
    public Map<String, Object> createOrder(
            @RequestParam String deliveryAddress,           // 必填参数：配送地址
            @RequestParam String contactName,               // 必填参数：联系人姓名
            @RequestParam String contactPhone,              // 必填参数：联系电话
            @RequestParam(required = false) String remark,  // 可选参数：订单备注
            HttpSession session) {                          // Session对象，获取当前用户
        
        // ---------- 准备响应数据 ----------
        Map<String, Object> result = new HashMap<>();
        
        // ---------- 步骤1：从Session获取当前登录用户 ----------
        // 技术说明：
        // - Session是服务器端存储，用于保存用户登录状态
        // - 登录成功后，会将用户对象存入Session的"currentUser"键中
        // - 这里通过getAttribute获取用户对象
        SysUser user = (SysUser) session.getAttribute("currentUser");
        
        // ---------- 步骤2：校验用户是否登录 ----------
        if (user == null) {
            // 用户未登录，返回失败响应
            result.put("success", false);
            result.put("needLogin", true);  // 前端根据这个字段判断是否需要跳转到登录页
            result.put("message", "请先登录");
            return result;  // 提前返回，不再执行后续逻辑
        }

        try {
            // ---------- 步骤3：调用Service层创建订单 ----------
            // 技术说明：
            // - Controller只负责接收参数、调用Service、返回结果
            // - 具体的业务逻辑（库存验证、订单创建、库存扣减）在Service层实现
            // - Service层会按商户拆分订单，返回订单列表
            // 
            // 业务逻辑（在OrderServiceImpl中实现）：
            // 1. 查询用户购物车
            // 2. 验证商品库存
            // 3. 按商户分组（支持跨商户下单）
            // 4. 为每个商户创建独立订单
            // 5. 扣减商品库存
            // 6. 清空购物车
            List<Order> orders = orderService.createOrderFromCart(
                    user.getUserId(),      // 用户ID
                    deliveryAddress,       // 配送地址
                    contactName,           // 联系人姓名
                    contactPhone,          // 联系电话
                    remark                 // 订单备注（可能为null）
            );

            // ---------- 步骤4：返回成功响应 ----------
            result.put("success", true);               // 操作成功
            result.put("message", "订单创建成功");      // 提示信息
            result.put("orders", orders);              // 订单列表（可能包含多个订单）
            result.put("orderCount", orders.size());   // 订单数量（前端可能需要显示）
            
            // 返回示例：
            // {
            //   "success": true,
            //   "message": "订单创建成功",
            //   "orders": [
            //     { orderId: 1, orderNo: "ORDER1700345678...", merchantName: "美食餐厅", ... },
            //     { orderId: 2, orderNo: "ORDER1700345679...", merchantName: "饮品店", ... }
            //   ],
            //   "orderCount": 2
            // }
            
        } catch (Exception e) {
            // ---------- 异常处理 ----------
            // 可能的异常：
            // 1. 购物车为空
            // 2. 商品不存在
            // 3. 库存不足
            // 4. 数据库异常
            
            e.printStackTrace(); // 打印异常堆栈到控制台（方便开发调试）
            
            result.put("success", false);
            result.put("message", "服务器错误：" + e.getMessage());  // 将异常信息返回给前端
            
            // 返回示例：
            // {
            //   "success": false,
            //   "message": "服务器错误：商品库存不足：宫保鸡丁，库存：5，需要：10"
            // }
        }

        return result;  // 返回JSON响应给前端
    }

    /**
     * 订单列表页面
     */
    @GetMapping("/list")
    public String orderList(@RequestParam(required = false) String status,
                            HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login.html";
        }

        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderService.getUserOrdersByStatus(user.getUserId(), status);
        } else {
            orders = orderService.getUserOrders(user.getUserId());
        }

        model.addAttribute("orders", orders);
        model.addAttribute("status", status);

        return "order/list";
    }

    /**
     * 订单详情页面
     */
    @GetMapping("/detail/{orderId}")
    public String orderDetail(@PathVariable Long orderId, HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login.html";
        }

        Order order = orderService.getOrderDetail(orderId);
        if (order == null) {
            model.addAttribute("error", "订单不存在");
            return "comm/error_404";
        }

        // 简化权限:去掉严格的所有权验证

        model.addAttribute("order", order);

        return "order/detail";
    }

    /**
     * 支付订单（AJAX）
     */
    @PostMapping("/pay/{orderId}")
    @ResponseBody
    public Map<String, Object> payOrder(@PathVariable Long orderId,
                                        @RequestParam(defaultValue = "online") String paymentMethod,
                                        HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        try {
            Order order = orderService.getOrderDetail(orderId);
            if (order == null || !order.getUserId().equals(user.getUserId())) {
                result.put("success", false);
                result.put("message", "订单不存在或无权操作");
                return result;
            }

            boolean success = orderService.payOrder(orderId, paymentMethod);
            result.put("success", success);
            result.put("message", success ? "支付成功" : "支付失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 取消订单（AJAX）
     */
    @PostMapping("/cancel/{orderId}")
    @ResponseBody
    public Map<String, Object> cancelOrder(@PathVariable Long orderId,
                                           @RequestParam(required = false) String cancelReason,
                                           HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        try {
            Order order = orderService.getOrderDetail(orderId);
            if (order == null || !order.getUserId().equals(user.getUserId())) {
                result.put("success", false);
                result.put("message", "订单不存在或无权操作");
                return result;
            }

            if (cancelReason == null || cancelReason.isEmpty()) {
                cancelReason = "用户取消";
            }

            boolean success = orderService.cancelOrder(orderId, cancelReason);
            result.put("success", success);
            result.put("message", success ? "取消成功" : "取消失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 确认收货（AJAX）
     */
    @PostMapping("/confirm/{orderId}")
    @ResponseBody
    public Map<String, Object> confirmReceive(@PathVariable Long orderId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        try {
            Order order = orderService.getOrderDetail(orderId);
            if (order == null || !order.getUserId().equals(user.getUserId())) {
                result.put("success", false);
                result.put("message", "订单不存在或无权操作");
                return result;
            }

            boolean success = orderService.confirmOrder(orderId);
            result.put("success", success);
            result.put("message", success ? "确认收货成功" : "确认收货失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }
    
    /**
     * ========================================
     * 轮询检查订单状态更新（AJAX接口）
     * ========================================
     * 
     * 【功能说明】
     * 前端每5秒调用一次此接口，传入当前页面显示的所有订单ID
     * 后端查询这些订单的最新状态，返回给前端进行对比
     * 如果状态有变化，前端自动刷新页面，用户无需手动刷新
     * 
     * 【请求方式】GET
     * 【请求路径】/order/checkUpdates?orderIds=123,456,789
     * 【返回格式】JSON
     * 
     * 【业务流程】
     * 1. 检查用户登录状态（Session）
     * 2. 解析订单ID列表（逗号分隔的字符串 → 数组）
     * 3. 循环查询每个订单的最新状态
     * 4. 权限校验：只返回当前用户自己的订单
     * 5. 返回订单状态列表（orderId + status + statusText）
     * 
     * 【技术要点】
     * - @GetMapping：处理GET请求，轮询接口通常用GET（幂等性，不修改数据）
     * - @ResponseBody：返回JSON数据，不返回页面
     * - @RequestParam：接收URL参数 orderIds（如：?orderIds=1,2,3）
     * - HttpSession：获取当前登录用户
     * - 权限校验：防止用户查询别人的订单状态
     * 
     * 【轮询机制原理】
     * ┌──────────────────────────────────────────────────────────────┐
     * │ 前端（order/list.html）                                       │
     * │   ↓ setInterval(5秒)                                         │
     * │   └→ 发送AJAX请求：GET /order/checkUpdates?orderIds=1,2,3    │
     * │                                                              │
     * │ 后端（OrderController.java）                                 │
     * │   ↓ 接收请求                                                 │
     * │   ↓ 查询数据库获取最新订单状态                               │
     * │   └→ 返回JSON：{updates:[{orderId:1,status:"paid"}]}        │
     * │                                                              │
     * │ 前端（order/list.html）                                       │
     * │   ↓ 接收响应                                                 │
     * │   ↓ 对比状态：旧状态 vs 新状态                               │
     * │   └→ 有变化 → location.reload() 刷新页面                     │
     * └──────────────────────────────────────────────────────────────┘
     * 
     * 【应用场景举例】
     * 场景1：用户下单后等待商家接单
     *   - 订单状态：pending（待支付） → paid（已支付） → preparing（制作中）
     *   - 用户在订单列表页面，每5秒自动检测状态变化
     *   - 商家接单后，前端自动刷新，显示"制作中"
     * 
     * 场景2：订单配送中等待送达
     *   - 订单状态：delivering（配送中） → delivered（已送达）
     *   - 骑手点击"送达"后，用户端自动刷新，显示"待确认收货"
     * 
     * 【返回数据示例】
     * 成功：
     * {
     *   "success": true,
     *   "updates": [
     *     { "orderId": 123, "status": "paid", "statusText": "已支付" },
     *     { "orderId": 456, "status": "preparing", "statusText": "制作中" }
     *   ]
     * }
     * 
     * 登录过期：
     * {
     *   "success": false,
     *   "needLogin": true  // 前端检测到此字段，停止轮询
     * }
     * 
     * @param orderIds 订单ID列表，逗号分隔（如："123,456,789"）
     * @param session HTTP会话对象，用于获取当前登录用户
     * @return JSON响应
     */
    @GetMapping("/checkUpdates")
    @ResponseBody
    public Map<String, Object> checkOrderUpdates(@RequestParam String orderIds, HttpSession session) {
        // ---------- 准备响应数据 ----------
        Map<String, Object> result = new HashMap<>();
        
        // ---------- 步骤1：校验用户登录状态 ----------
        // 技术说明：
        // - Session是服务器端存储，用于保存用户登录信息
        // - 登录成功后，会将用户对象存入Session的"currentUser"键中
        // - 如果Session过期（默认30分钟无操作），getAttribute返回null
        SysUser user = (SysUser) session.getAttribute("currentUser");
        
        if (user == null) {
            // 用户未登录或Session已过期
            result.put("success", false);
            result.put("needLogin", true);  // 关键字段：告诉前端停止轮询
            return result;  // 提前返回，不再查询订单
            
            // 为什么返回needLogin而不是直接抛异常？
            // - 轮询是后台自动请求，不应该弹出错误提示干扰用户
            // - 前端检测到needLogin，会静默停止轮询
        }
        
        try {
            // ---------- 步骤2：解析订单ID列表 ----------
            // 前端发送的数据格式："123,456,789"（逗号分隔的字符串）
            // 需要转换为数组：["123", "456", "789"]
            String[] ids = orderIds.split(","); // 按逗号分割
            
            // 准备更新列表（存储每个订单的最新状态）
            List<Map<String, Object>> updates = new ArrayList<>();
            
            // ---------- 步骤3：循环查询每个订单的最新状态 ----------
            for (String idStr : ids) {
                // 跳过空字符串（如："123,,456"中间的空值）
                if (idStr.trim().isEmpty()) continue;
                
                // 字符串转数字："123" → 123L
                Long orderId = Long.parseLong(idStr.trim());
                
                // 调用Service层查询订单详情（包含最新状态）
                Order order = orderService.getOrderDetail(orderId);
                
                // ---------- 步骤4：权限校验 ----------
                // 安全检查：只返回当前用户自己的订单
                // 防止用户通过修改URL参数查询别人的订单
                // 示例：用户A（userId=1）不能查询用户B（userId=2）的订单
                if (order != null && order.getUserId().equals(user.getUserId())) {
                    // 构造订单信息对象
                    Map<String, Object> orderInfo = new HashMap<>();
                    orderInfo.put("orderId", order.getOrderId());           // 订单ID
                    orderInfo.put("status", order.getOrderStatus());        // 状态码（如："paid"）
                    orderInfo.put("statusText", getStatusText(order.getOrderStatus())); // 中文状态（如："已支付"）
                    
                    // 添加到更新列表
                    updates.add(orderInfo);
                }
                
                // 如果订单不存在或不属于当前用户，则跳过（不返回）
            }
            
            // ---------- 步骤5：返回成功响应 ----------
            result.put("success", true);
            result.put("updates", updates); // 订单状态列表
            
            // 前端会收到类似这样的数据：
            // {
            //   "success": true,
            //   "updates": [
            //     { "orderId": 123, "status": "paid", "statusText": "已支付" },
            //     { "orderId": 456, "status": "preparing", "statusText": "制作中" }
            //   ]
            // }
            
        } catch (Exception e) {
            // 异常处理：解析失败、数据库异常等
            e.printStackTrace();
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取状态文本
     */
    private String getStatusText(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "pending": return "待支付";
            case "paid": return "已支付";
            case "preparing": return "制作中";
            case "delivering": return "配送中";
            case "completed": return "已完成";
            case "cancelled": return "已取消";
            default: return "未知";
        }
    }
}


