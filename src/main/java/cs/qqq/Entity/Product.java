package cs.qqq.Entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体类
 * 对应数据库表: t_product
 */
@Data
public class Product {
    
    /** 菜品ID */
    private Long productId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 分类ID */
    private Long categoryId;
    
    /** 菜品名称 */
    private String productName;
    
    /** 菜品描述 */
    private String productDesc;
    
    /** 菜品图片URL */
    private String productImg;
    
    /** 价格 */
    private BigDecimal price;
    
    /** 原价 */
    private BigDecimal originalPrice;
    
    /** 库存数量 */
    private Integer stock;
    
    /** 销量 */
    private Integer sales;
    
    /** 状态(1上架 0下架) */
    private String status;
    
    /** 是否推荐(1是 0否) */
    private String isRecommend;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 分类名称(关联查询时使用) */
    private String categoryName;
    
    /** 商户名称(关联查询时使用) */
    private String merchantName;
}
