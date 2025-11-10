package cs.qqq.Entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 菜品分类实体类
 * 对应数据库表: t_category
 */
@Data
public class Category {
    
    /** 分类ID */
    private Long categoryId;
    
    /** 分类名称 */
    private String categoryName;
    
    /** 分类描述 */
    private String categoryDesc;
    
    /** 分类图标URL */
    private String categoryIcon;
    
    /** 排序顺序 */
    private Integer sortOrder;
    
    /** 状态(1启用 0禁用) */
    private String status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
