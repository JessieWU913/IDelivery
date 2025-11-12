package cs.qqq.Controller;

import cs.qqq.Entity.Merchant;
import cs.qqq.Entity.Product;
import cs.qqq.Entity.SysUser;
import cs.qqq.Service.MerchantService;
import cs.qqq.Service.ProductService;
import cs.qqq.Service.CategoryService;
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
        
        System.out.println("=== 添加菜品请求 ===");
        System.out.println("接收到的产品数据: " + product);
        
        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            System.out.println("错误: 用户未登录");
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        
        System.out.println("当前用户: " + currentUser.getUserName());
        
        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        if (merchant == null) {
            System.out.println("错误: 用户没有开通商户");
            result.put("success", false);
            result.put("message", "您还没有开通商户");
            return result;
        }
        
        System.out.println("商户信息: " + merchant.getMerchantName() + " (ID: " + merchant.getMerchantId() + ")");
        
        // 设置商户ID
        product.setMerchantId(merchant.getMerchantId());
        
        // 设置默认值
        if (product.getStatus() == null || product.getStatus().isEmpty()) {
            product.setStatus("1");
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        
        System.out.println("准备插入的产品数据: " + product);
        
        int count = productService.addProduct(product);
        System.out.println("插入结果: " + (count > 0 ? "成功" : "失败") + ", 影响行数: " + count);
        
        if (count > 0) {
            result.put("success", true);
            result.put("message", "添加成功");
        } else {
            result.put("success", false);
            result.put("message", "添加失败");
        }
        
        System.out.println("返回结果: " + result);
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
        
        System.out.println("=== 更新菜品请求 ===");
        System.out.println("接收到的产品数据: " + product);
        
        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            System.out.println("错误: 用户未登录");
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        
        System.out.println("当前用户: " + currentUser.getUserName());
        
        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        Product existProduct = productService.findById(product.getProductId());
        
        // 验证权限
        if (existProduct == null || !existProduct.getMerchantId().equals(merchant.getMerchantId())) {
            System.out.println("错误: 无权修改此菜品");
            result.put("success", false);
            result.put("message", "无权修改此菜品");
            return result;
        }
        
        System.out.println("原产品数据: " + existProduct);
        System.out.println("准备更新的数据: " + product);
        
        int count = productService.updateProduct(product);
        System.out.println("更新结果: " + (count > 0 ? "成功" : "失败") + ", 影响行数: " + count);
        
        if (count > 0) {
            result.put("success", true);
            result.put("message", "更新成功");
        } else {
            result.put("success", false);
            result.put("message", "更新失败");
        }
        
        System.out.println("返回结果: " + result);
        return result;
    }
    
    /**
     * 删除菜品 - API
     */
    @PostMapping("/product/delete/{productId}")
    @ResponseBody
    public Map<String, Object> deleteProduct(@PathVariable Long productId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("=== 删除菜品请求 ===");
        System.out.println("要删除的菜品ID: " + productId);
        
        SysUser currentUser = (SysUser) session.getAttribute("user");
        if (currentUser == null) {
            System.out.println("错误: 用户未登录");
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        
        System.out.println("当前用户: " + currentUser.getUserName());
        
        Merchant merchant = merchantService.findByUserId(currentUser.getUserId());
        Product product = productService.findById(productId);
        
        // 验证权限
        if (product == null || !product.getMerchantId().equals(merchant.getMerchantId())) {
            System.out.println("错误: 无权删除此菜品或菜品不存在");
            result.put("success", false);
            result.put("message", "无权删除此菜品");
            return result;
        }
        
        System.out.println("准备删除菜品: " + product.getProductName());
        
        int count = productService.deleteProduct(productId);
        System.out.println("删除结果: " + (count > 0 ? "成功" : "失败") + ", 影响行数: " + count);
        
        if (count > 0) {
            result.put("success", true);
            result.put("message", "删除成功");
        } else {
            result.put("success", false);
            result.put("message", "删除失败");
        }
        
        System.out.println("返回结果: " + result);
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
}
