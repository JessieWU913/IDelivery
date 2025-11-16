package cs.qqq.Entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 对应数据库表: t_order
 */
@Data
public class Order {

    /** 订单ID */
    private Long orderId;

    /** 订单号（唯一） */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 商户ID */
    private Long merchantId;

    /** 商户名称（冗余字段） */
    private String merchantName;

    /** 订单总金额（商品金额） */
    private BigDecimal totalAmount;

    /** 配送费 */
    private BigDecimal deliveryFee;

    /** 优惠金额 */
    private BigDecimal discountAmount;

    /** 实付金额（总金额+配送费-优惠） */
    private BigDecimal actualAmount;

    /** 支付方式：online-在线支付, cash-货到付款 */
    private String paymentMethod;

    /** 支付状态：0-未支付, 1-已支付, 2-已退款 */
    private String paymentStatus;

    /** 订单状态：pending-待支付, paid-已支付, preparing-制作中, delivering-配送中, completed-已完成, cancelled-已取消 */
    private String orderStatus;

    /** 配送地址 */
    private String deliveryAddress;

    /** 联系人姓名 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 订单备注 */
    private String remark;

    /** 预计送达时间 */
    private LocalDateTime deliveryTime;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 完成时间 */
    private LocalDateTime completeTime;

    /** 取消时间 */
    private LocalDateTime cancelTime;

    /** 取消原因 */
    private String cancelReason;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // ========== 关联查询字段（非数据库字段） ==========

    /** 订单明细列表（关联查询时使用） */
    private java.util.List<OrderItem> orderItems;

    /** 订单商品总数（用于统计） */
    private Integer totalQuantity;
}

