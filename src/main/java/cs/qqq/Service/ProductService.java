package cs.qqq.Service;

import cs.qqq.Entity.Product;
import java.util.List;

/**
 * 菜品Service接口
 */
public interface ProductService {
    
    /**
     * 查询所有上架的菜品
     */
    List<Product> getAllProducts();
    
    /**
     * 根据分类ID查询菜品
     */
    List<Product> getProductsByCategory(Long categoryId);
    
    /**
     * 根据ID查询菜品详情
     */
    Product getProductById(Long productId);
    
    /**
     * 搜索菜品
     */
    List<Product> searchProducts(String keyword);
    
    /**
     * 查询推荐菜品
     */
    List<Product> getRecommendProducts();
    
    /**
     * 查询热销菜品
     */
    List<Product> getHotProducts(Integer limit);
    
    /**
     * 根据商户ID查询菜品
     */
    List<Product> findByMerchantId(Long merchantId);
    
    /**
     * 根据ID查询菜品详情
     */
    Product findById(Long productId);
    
    /**
     * 添加菜品
     */
    int addProduct(Product product);
    
    /**
     * 更新菜品
     */
    int updateProduct(Product product);
    
    /**
     * 删除菜品
     */
    int deleteProduct(Long productId);
}
