package cs.qqq.Entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体类
 * 对应数据库表: t_order_item
 */
@Data
public class OrderItem {

    /** 明细ID */
    private Long itemId;

    /** 订单ID */
    private Long orderId;

    /** 订单号（冗余） */
    private String orderNo;

    /** 商品ID */
    private Long productId;

    /** 商品名称（冗余） */
    private String productName;

    /** 商品图片（冗余） */
    private String productImg;

    /** 商品单价 */
    private BigDecimal price;

    /** 购买数量 */
    private Integer quantity;

    /** 小计金额（单价×数量） */
    private BigDecimal subtotal;

    /** 创建时间 */
    private LocalDateTime createTime;

    // ========== 关联查询字段（非数据库字段） ==========

    /** 商品信息（关联查询时使用） */
    private Product product;
}

