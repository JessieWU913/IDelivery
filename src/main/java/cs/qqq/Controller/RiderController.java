package cs.qqq.Controller;

import cs.qqq.Entity.Order;
import cs.qqq.Entity.SysUser;
import cs.qqq.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 骑手Controller
 * 处理骑手相关请求
 */
@Controller
@RequestMapping("/rider")
public class RiderController {

    @Autowired
    private OrderService orderService;

    /**
     * 骑手管理
     */
    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login.html";
        }

        // 验证是否为骑手角色
        if (currentUser.getRoleId() == null || currentUser.getRoleId() != 4) {
            model.addAttribute("error", "您没有骑手权限");
            return "comm/error_403";
        }

        // 查询所有已出餐的订单（状态为delivering）
        List<Order> availableOrders = orderService.getOrdersByStatus("delivering");
        
        // 获取骑手已接单的订单ID列表
        @SuppressWarnings("unchecked")
        java.util.Set<Long> acceptedOrders = (java.util.Set<Long>) session.getAttribute("riderAcceptedOrders");
        if (acceptedOrders == null) {
            acceptedOrders = new java.util.HashSet<>();
            session.setAttribute("riderAcceptedOrders", acceptedOrders);
        }
        
        // 为每个订单加载订单明细
        for (Order order : availableOrders) {
            Order orderDetail = orderService.getOrderDetail(order.getOrderId());
            if (orderDetail != null) {
                order.setOrderItems(orderDetail.getOrderItems());
            }
        }

        model.addAttribute("availableOrders", availableOrders);
        model.addAttribute("rider", currentUser);
        model.addAttribute("acceptedOrderIds", acceptedOrders);

        return "rider/index";
    }

    /**
     * 骑手接单（AJAX）
     */
    @PostMapping("/order/accept/{orderId}")
    @ResponseBody
    public Map<String, Object> acceptOrder(@PathVariable Long orderId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        // 验证是否为骑手角色
        if (currentUser.getRoleId() == null || currentUser.getRoleId() != 4) {
            result.put("success", false);
            result.put("message", "您没有骑手权限");
            return result;
        }

        try {
            Order order = orderService.getOrderDetail(orderId);
            if (order == null) {
                result.put("success", false);
                result.put("message", "订单不存在");
                return result;
            }

            if (!"delivering".equals(order.getOrderStatus())) {
                result.put("success", false);
                result.put("message", "订单状态不正确，无法接单");
                return result;
            }

            // 在session中记录骑手接单的订单ID列表
            @SuppressWarnings("unchecked")
            java.util.Set<Long> acceptedOrders = (java.util.Set<Long>) session.getAttribute("riderAcceptedOrders");
            if (acceptedOrders == null) {
                acceptedOrders = new java.util.HashSet<>();
                session.setAttribute("riderAcceptedOrders", acceptedOrders);
            }
            
            // 检查是否已经接单
            if (acceptedOrders.contains(orderId)) {
                result.put("success", false);
                result.put("message", "您已经接单了此订单");
                return result;
            }
            
            // 记录接单
            acceptedOrders.add(orderId);
            result.put("success", true);
            result.put("message", "接单成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 骑手送达（AJAX）
     */
    @PostMapping("/order/deliver/{orderId}")
    @ResponseBody
    public Map<String, Object> completeDelivery(@PathVariable Long orderId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        // 验证是否为骑手角色
        if (currentUser.getRoleId() == null || currentUser.getRoleId() != 4) {
            result.put("success", false);
            result.put("message", "您没有骑手权限");
            return result;
        }

        try {
            // 检查是否已接单
            @SuppressWarnings("unchecked")
            java.util.Set<Long> acceptedOrders = (java.util.Set<Long>) session.getAttribute("riderAcceptedOrders");
            if (acceptedOrders == null || !acceptedOrders.contains(orderId)) {
                result.put("success", false);
                result.put("message", "请先接单才能完成送达");
                return result;
            }
            
            Order order = orderService.getOrderDetail(orderId);
            if (order == null) {
                result.put("success", false);
                result.put("message", "订单不存在");
                return result;
            }

            if (!"delivering".equals(order.getOrderStatus())) {
                result.put("success", false);
                result.put("message", "订单状态不正确，无法完成送达");
                return result;
            }

            // 完成送达
            boolean success = orderService.confirmOrder(orderId);
            if (success) {
                // 从接单列表中移除
                acceptedOrders.remove(orderId);
            }
            result.put("success", success);
            result.put("message", success ? "送达成功" : "送达失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }
}

