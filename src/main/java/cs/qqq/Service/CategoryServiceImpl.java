package cs.qqq.Service;

import cs.qqq.Entity.Category;
import cs.qqq.Mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜品分类Service实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.findAllCategories();
    }
    
    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryMapper.findCategoryById(categoryId);
    }
    
    @Override
    public void addCategory(Category category) {
        categoryMapper.addCategory(category);
    }
    
    @Override
    public void updateCategory(Category category) {
        categoryMapper.updateCategory(category);
    }
    
    @Override
    public void deleteCategory(Long categoryId) {
        categoryMapper.deleteCategory(categoryId);
    }
}
