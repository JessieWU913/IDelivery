package cs.qqq.Mapper;

import cs.qqq.Entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    
    // 添加到购物车（如果已存在则更新数量）
    int addToCart(Cart cart);
    
    // 查询购物车（根据用户ID和商品ID）
    Cart findCartByUserAndProduct(@Param("userId") Integer userId, @Param("productId") Integer productId);
    
    // 更新购物车商品数量
    int updateQuantity(@Param("cartId") Integer cartId, @Param("quantity") Integer quantity);
    
    // 查询用户的购物车列表（带商品信息）
    List<Cart> findCartListByUserId(@Param("userId") Integer userId);
    
    // 删除购物车项
    int deleteCartItem(@Param("cartId") Integer cartId);
    
    // 清空用户购物车
    int clearCart(@Param("userId") Integer userId);
}
