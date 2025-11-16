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
     * 骑手管理首页
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
        
        // 为每个订单加载订单明细
        for (Order order : availableOrders) {
            Order orderDetail = orderService.getOrderDetail(order.getOrderId());
            if (orderDetail != null) {
                order.setOrderItems(orderDetail.getOrderItems());
            }
        }

        model.addAttribute("availableOrders", availableOrders);
        model.addAttribute("rider", currentUser);

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

            // 接单：订单状态保持为delivering，但可以记录骑手信息
            // 这里可以根据需要添加骑手ID字段到订单表
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

            // 完成送达：将订单状态改为已完成
            boolean success = orderService.confirmOrder(orderId);
            result.put("success", success);
            result.put("message", success ? "送达成功" : "送达失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }
}

