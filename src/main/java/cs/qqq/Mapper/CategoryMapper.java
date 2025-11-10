package cs.qqq.Mapper;

import cs.qqq.Entity.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 菜品分类Mapper接口
 */
@Mapper
public interface CategoryMapper {
    
    /**
     * 查询所有启用的分类
     */
    List<Category> findAllCategories();
    
    /**
     * 根据ID查询分类
     */
    Category findCategoryById(Long categoryId);
    
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
