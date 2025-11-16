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
     * 创建订单（从购物车创建）
     */
    @PostMapping("/create")
    @ResponseBody
    public Map<String, Object> createOrder(@RequestParam String deliveryAddress,
                                           @RequestParam String contactName,
                                           @RequestParam String contactPhone,
                                           @RequestParam(required = false) String remark,
                                           HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("needLogin", true); // 新增
            result.put("message", "请先登录");
            return result;
        }

        try {
            // 调用服务层创建订单
            List<Order> orders = orderService.createOrderFromCart(
                    user.getUserId(),
                    deliveryAddress,
                    contactName,
                    contactPhone,
                    remark
            );

            result.put("success", true);
            result.put("message", "订单创建成功");
            result.put("orders", orders);
            result.put("orderCount", orders.size());
        } catch (Exception e) {
            e.printStackTrace(); // 添加调试日志
            result.put("success", false);
            result.put("message", "服务器错误：" + e.getMessage());
        }

        return result;
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

        // 验证订单是否属于该用户
        if (!order.getUserId().equals(user.getUserId())) {
            model.addAttribute("error", "无权查看此订单");
            return "comm/error_403";
        }

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
}

