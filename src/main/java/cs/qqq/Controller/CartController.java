package cs.qqq.Controller;

import cs.qqq.Entity.Cart;
import cs.qqq.Entity.SysUser;
import cs.qqq.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 购物车页面
     */
    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login.html";
        }
        
        List<Cart> cartList = cartService.getCartList(user.getUserId().intValue());
        
        // 计算总价
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Cart cart : cartList) {
            BigDecimal itemTotal = cart.getPrice().multiply(new BigDecimal(cart.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }
        
        model.addAttribute("cartList", cartList);
        model.addAttribute("totalPrice", totalPrice);
        
        return "cart/index";
    }
    
    /**
     * 添加到购物车（AJAX）
     */
    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam Integer productId,
                                          @RequestParam(defaultValue = "1") Integer quantity,
                                          HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        
        boolean success = cartService.addToCart(user.getUserId().intValue(), productId, quantity);
        result.put("success", success);
        result.put("message", success ? "添加成功" : "添加失败");
        
        return result;
    }
    
    /**
     * 更新购物车数量（AJAX）
     */
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateQuantity(@RequestParam Integer cartId,
                                               @RequestParam Integer quantity) {
        Map<String, Object> result = new HashMap<>();
        
        if (quantity <= 0) {
            result.put("success", false);
            result.put("message", "数量必须大于0");
            return result;
        }
        
        boolean success = cartService.updateCartQuantity(cartId, quantity);
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        
        return result;
    }
    
    /**
     * 删除购物车项（AJAX）
     */
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> deleteItem(@RequestParam Integer cartId) {
        Map<String, Object> result = new HashMap<>();
        
        boolean success = cartService.deleteCartItem(cartId);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        
        return result;
    }
    
    /**
     * 清空购物车（AJAX）
     */
    @PostMapping("/clear")
    @ResponseBody
    public Map<String, Object> clearCart(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        
        boolean success = cartService.clearCart(user.getUserId().intValue());
        result.put("success", success);
        result.put("message", success ? "清空成功" : "清空失败");
        
        return result;
    }
}
