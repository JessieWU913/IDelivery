package cs.qqq.Controller;

import cs.qqq.Entity.Merchant;
import cs.qqq.Entity.Product;
import cs.qqq.Entity.SysUser;
import cs.qqq.Entity.Order;
import cs.qqq.Service.MerchantService;
import cs.qqq.Service.ProductService;
import cs.qqq.Service.CategoryService;
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
 * 商户Controller
 * 处理商户管理相关请求
 */
@Controller
@RequestMapping("/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    /**
     * 商户管理首页
     */
    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        // 获取当前登录用户
        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // 查询商户信息
        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        if (merchant == null) {
            model.addAttribute("error", "您还没有开通商户");
            return "comm/error_403";
        }

        // 查询该商户的菜品列表
        List<Product> products = productService.findByMerchantId(merchant.getMerchantId());

        model.addAttribute("merchant", merchant);
        model.addAttribute("products", products);
        model.addAttribute("productCount", products.size());

        return "merchant/index";
    }

    /**
     * 商户菜品管理页面
     */
    @GetMapping("/products")
    public String products(HttpSession session, Model model) {
        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        if (merchant == null) {
            return "comm/error_403";
        }

        List<Product> products = productService.findByMerchantId(merchant.getMerchantId());
        model.addAttribute("merchant", merchant);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAll());

        return "merchant/product_list";
    }

    /**
     * 添加菜品页面
     */
    @GetMapping("/product/add")
    public String addProductPage(HttpSession session, Model model) {
        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        model.addAttribute("merchant", merchant);
        model.addAttribute("categories", categoryService.findAll());

        return "merchant/product_add";
    }

    /**
     * 添加菜品 - API
     */
    @PostMapping("/product/add")
    @ResponseBody
    public Map<String, Object> addProduct(@RequestBody Product product, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        if (merchant == null) {
            result.put("success", false);
            result.put("message", "您还没有开通商户");
            return result;
        }

        // 设置商户ID
        product.setMerchantId(merchant.getMerchantId());

        // 设置默认值
        if (product.getStatus() == null || product.getStatus().isEmpty()) {
            product.setStatus("1");
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }

        int count = productService.addProduct(product);
        if (count > 0) {
            result.put("success", true);
            result.put("message", "添加成功");
        } else {
            result.put("success", false);
            result.put("message", "添加失败");
        }

        return result;
    }

    /**
     * 编辑菜品页面
     */
    @GetMapping("/product/edit/{productId}")
    public String editProductPage(@PathVariable Long productId, HttpSession session, Model model) {
        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Product product = productService.findById(productId);
        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());

        // 验证是否是该商户的菜品
        if (product == null || !product.getMerchantId().equals(merchant.getMerchantId())) {
            return "comm/error_403";
        }

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());

        return "merchant/product_edit";
    }

    /**
     * 更新菜品 - API
     */
    @PostMapping("/product/update")
    @ResponseBody
    public Map<String, Object> updateProduct(@RequestBody Product product, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        Product existProduct = productService.findById(product.getProductId());

        // 验证权限
        if (existProduct == null || !existProduct.getMerchantId().equals(merchant.getMerchantId())) {
            result.put("success", false);
            result.put("message", "无权修改此菜品");
            return result;
        }

        int count = productService.updateProduct(product);
        if (count > 0) {
            result.put("success", true);
            result.put("message", "更新成功");
        } else {
            result.put("success", false);
            result.put("message", "更新失败");
        }

        return result;
    }

    /**
     * 删除菜品 - API
     */
    @PostMapping("/product/delete/{productId}")
    @ResponseBody
    public Map<String, Object> deleteProduct(@PathVariable Long productId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        Product product = productService.findById(productId);

        // 验证权限
        if (product == null || !product.getMerchantId().equals(merchant.getMerchantId())) {
            result.put("success", false);
            result.put("message", "无权删除此菜品");
            return result;
        }

        int count = productService.deleteProduct(productId);
        if (count > 0) {
            result.put("success", true);
            result.put("message", "删除成功");
        } else {
            result.put("success", false);
            result.put("message", "删除失败");
        }

        return result;
    }

    /**
     * 更新商户信息 - API
     */
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateMerchant(@RequestBody Merchant merchant, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        Merchant existMerchant = merchantService.findByUserId(currentUser.getUserId());
        if (existMerchant == null || !existMerchant.getMerchantId().equals(merchant.getMerchantId())) {
            result.put("success", false);
            result.put("message", "无权修改");
            return result;
        }

        int count = merchantService.updateMerchant(merchant);
        if (count > 0) {
            result.put("success", true);
            result.put("message", "更新成功");
        } else {
            result.put("success", false);
            result.put("message", "更新失败");
        }

        return result;
    }

    /**
     * 切换营业状态 - API
     */
    @PostMapping("/toggle-status")
    @ResponseBody
    public Map<String, Object> toggleStatus(HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        if (merchant == null) {
            result.put("success", false);
            result.put("message", "商户不存在");
            return result;
        }

        // 切换状态：1变0，0变1
        Integer newStatus = merchant.getStatus() == 1 ? 0 : 1;
        int count = merchantService.updateStatus(merchant.getMerchantId(), newStatus);

        if (count > 0) {
            result.put("success", true);
            result.put("status", newStatus);
            result.put("message", newStatus == 1 ? "已开始营业" : "已暂停营业");
        } else {
            result.put("success", false);
            result.put("message", "操作失败");
        }

        return result;
    }

    /**
     * 商户订单列表页面
     */
    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) String status,
                         HttpSession session, Model model) {
        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        if (merchant == null) {
            return "comm/error_403";
        }

        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderService.getMerchantOrdersByStatus(merchant.getMerchantId(), status);
        } else {
            orders = orderService.getMerchantOrders(merchant.getMerchantId());
        }

        model.addAttribute("merchant", merchant);
        model.addAttribute("orders", orders);
        model.addAttribute("status", status);

        return "merchant/order_list";
    }

    /**
     * 商户订单详情页面
     */
    @GetMapping("/order/detail/{orderId}")
    public String orderDetail(@PathVariable Long orderId, HttpSession session, Model model) {
        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        if (merchant == null) {
            return "comm/error_403";
        }

        Order order = orderService.getOrderDetail(orderId);
        if (order == null) {
            model.addAttribute("error", "订单不存在");
            return "comm/error_404";
        }

        // 验证订单是否属于该商户
        if (!order.getMerchantId().equals(merchant.getMerchantId())) {
            model.addAttribute("error", "无权查看此订单");
            return "comm/error_403";
        }

        model.addAttribute("merchant", merchant);
        model.addAttribute("order", order);

        return "merchant/order_detail";
    }

    /**
     * 商户接单（AJAX）
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

        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        if (merchant == null) {
            result.put("success", false);
            result.put("message", "商户不存在");
            return result;
        }

        try {
            boolean success = orderService.acceptOrder(orderId, merchant.getMerchantId());
            result.put("success", success);
            result.put("message", success ? "接单成功" : "接单失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 开始配送（AJAX）
     */
    @PostMapping("/order/deliver/{orderId}")
    @ResponseBody
    public Map<String, Object> startDelivery(@PathVariable Long orderId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        if (merchant == null) {
            result.put("success", false);
            result.put("message", "商户不存在");
            return result;
        }

        try {
            boolean success = orderService.startDelivery(orderId, merchant.getMerchantId());
            result.put("success", success);
            result.put("message", success ? "开始配送成功" : "开始配送失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }
}
