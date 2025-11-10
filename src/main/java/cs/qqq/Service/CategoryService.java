package cs.qqq.Service;

import cs.qqq.Entity.Category;
import java.util.List;

/**
 * 菜品分类Service接口
 */
public interface CategoryService {
    
    /**
     * 查询所有启用的分类
     */
    List<Category> getAllCategories();
    
    /**
     * 根据ID查询分类
     */
    Category getCategoryById(Long categoryId);
    
    /**
     * 添加分类
     */
    void addCategory(Category category);
    
    /**
     * 更新分类
     */
    void updateCategory(Category category);
    
    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);
}
