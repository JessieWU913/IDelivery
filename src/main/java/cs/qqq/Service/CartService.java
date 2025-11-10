package cs.qqq.Service;

import cs.qqq.Entity.Cart;
import java.util.List;

public interface CartService {
    
    // 添加到购物车
    boolean addToCart(Integer userId, Integer productId, Integer quantity);
    
    // 更新购物车数量
    boolean updateCartQuantity(Integer cartId, Integer quantity);
    
    // 获取用户购物车列表
    List<Cart> getCartList(Integer userId);
    
    // 删除购物车项
    boolean deleteCartItem(Integer cartId);
    
    // 清空购物车
    boolean clearCart(Integer userId);
}
