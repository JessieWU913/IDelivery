package cs.qqq.Controller;

import cs.qqq.Entity.Category;
import cs.qqq.Entity.Product;
import cs.qqq.Service.CategoryService;
import cs.qqq.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品控制器
 * 处理菜品相关的页面跳转和数据请求
 */
@Controller
@RequestMapping("/menu")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 跳转到菜单首页
     */
    @GetMapping("/index")
    public String menuIndex(Model model) {
        // 获取所有分类
        List<Category> categories = categoryService.getAllCategories();
        // 获取所有菜品
        List<Product> products = productService.getAllProducts();
        // 获取推荐菜品
        List<Product> recommendProducts = productService.getRecommendProducts();
        
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("recommendProducts", recommendProducts);
        
        return "menu/index";
    }
    
    /**
     * 根据分类ID获取菜品列表(AJAX请求)
     */
    @GetMapping("/category/{categoryId}")
    @ResponseBody
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }
    
    /**
     * 搜索菜品(AJAX请求)
     */
    @GetMapping("/search")
    @ResponseBody
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }
    
    /**
     * 获取菜品详情
     */
    @GetMapping("/detail/{productId}")
    public String productDetail(@PathVariable Long productId, Model model) {
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);
        return "menu/detail";
    }
    
    /**
     * 获取热销菜品(AJAX请求)
     */
    @GetMapping("/hot")
    @ResponseBody
    public List<Product> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        return productService.getHotProducts(limit);
    }
}
