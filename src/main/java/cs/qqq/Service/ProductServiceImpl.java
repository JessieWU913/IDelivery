package cs.qqq.Service;

import cs.qqq.Entity.Product;
import cs.qqq.Mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜品Service实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public List<Product> getAllProducts() {
        return productMapper.findAllProducts();
    }
    
    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productMapper.findProductsByCategory(categoryId);
    }
    
    @Override
    public Product getProductById(Long productId) {
        return productMapper.findProductById(productId);
    }
    
    @Override
    public List<Product> searchProducts(String keyword) {
        return productMapper.searchProducts(keyword);
    }
    
    @Override
    public List<Product> getRecommendProducts() {
        return productMapper.findRecommendProducts();
    }
    
    @Override
    public List<Product> getHotProducts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return productMapper.findHotProducts(limit);
    }
    
    @Override
    public void addProduct(Product product) {
        productMapper.addProduct(product);
    }
    
    @Override
    public void updateProduct(Product product) {
        productMapper.updateProduct(product);
    }
    
    @Override
    public void deleteProduct(Long productId) {
        productMapper.deleteProduct(productId);
    }
}
