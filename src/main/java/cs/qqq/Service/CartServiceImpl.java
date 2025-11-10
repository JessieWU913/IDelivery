package cs.qqq.Service;

import cs.qqq.Entity.Cart;
import cs.qqq.Mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartMapper cartMapper;
    
    @Override
    public boolean addToCart(Integer userId, Integer productId, Integer quantity) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        return cartMapper.addToCart(cart) > 0;
    }
    
    @Override
    public boolean updateCartQuantity(Integer cartId, Integer quantity) {
        return cartMapper.updateQuantity(cartId, quantity) > 0;
    }
    
    @Override
    public List<Cart> getCartList(Integer userId) {
        return cartMapper.findCartListByUserId(userId);
    }
    
    @Override
    public boolean deleteCartItem(Integer cartId) {
        return cartMapper.deleteCartItem(cartId) > 0;
    }
    
    @Override
    public boolean clearCart(Integer userId) {
        return cartMapper.clearCart(userId) > 0;
    }
}
