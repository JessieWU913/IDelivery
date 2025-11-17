package cs.qqq.Mapper;

import cs.qqq.Entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 菜品Mapper接口
 */
@Mapper
public interface ProductMapper {
    
    /**
     * 查询所有上架的菜品
     */
    List<Product> findAllProducts();
    
    /**
     * 根据分类ID查询菜品
     */
    List<Product> findProductsByCategory(Long categoryId);
    
    /**
     * 根据ID查询菜品详情
     */
    Product findProductById(Long productId);
    
    /**
     * 搜索菜品(根据名称模糊查询)
     */
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    /**
     * 查询推荐菜品
     */
    List<Product> findRecommendProducts();
    
    /**
     * 查询热销菜品(按销量排序)
     */
    List<Product> findHotProducts(@Param("limit") Integer limit);
    
    /**
     * 根据商户ID查询菜品
     */
    List<Product> findByMerchantId(@Param("merchantId") Long merchantId);
    
    /**
     * 根据ID查询菜品详情
     */
    Product findById(@Param("productId") Long productId);
    
    /**
     * 添加菜品
     */
    void addProduct(Product product);
    
    /**
     * 更新菜品
     */
    void updateProduct(Product product);
    
    /**
     * 删除菜品
     */
    void deleteProduct(Long productId);
    
    /**
     * 更新菜品库存
     */
    void updateStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 更新菜品销量
     */
    void updateSales(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 统计所有菜品数量
     */
    long countAllProducts();
    
    /**
     * 获取所有菜品及商户信息
     */
    List<Map<String, Object>> getAllProductsWithMerchant();
    
    /**
     * 更新菜品状态
     */
    void updateProductStatus(@Param("productId") Long productId, @Param("status") Integer status);
}
