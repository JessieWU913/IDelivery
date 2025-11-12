package cs.qqq.Entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Cart {
    private Integer cartId;        // 购物车ID
    private Integer userId;        // 用户ID
    private Integer productId;     // 商品ID
    private Integer quantity;      // 数量
    private Date createTime;       // 添加时间
    private Date updateTime;       // 更新时间
    
    // 关联商品信息（用于前端展示）
    private String productName;    // 商品名称
    private BigDecimal price;      // 商品单价
    private String productImg;     // 商品图片
    private Integer stock;         // 商品库存
    private Long merchantId;       // 商户ID
    private String merchantName;   // 商户名称
}

