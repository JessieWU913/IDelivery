package cs.qqq.Entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ========================================
 * 订单实体类 (Order Entity)
 * ========================================
 * 
 * 【作用】
 * 1. 对应数据库表: t_order
 * 2. 使用 ORM 映射，将数据库记录转换为 Java 对象
 * 3. 作为数据传输对象（DTO），在各层之间传递订单数据
 * 
 * 【设计要点】
 * 1. 使用 @Data 注解（Lombok）自动生成 getter/setter/toString/equals/hashCode
 * 2. 使用 BigDecimal 处理金额，避免浮点数精度问题
 * 3. 订单状态采用字符串枚举设计，便于扩展和理解
 * 4. 冗余 merchantName 字段，减少关联查询，提高性能（空间换时间）
 * 
 * 【订单状态流转】
 * pending(待支付) → paid(已支付) → preparing(制作中) → delivering(配送中) → delivered(已送达) → completed(已完成)

 */
@Data
public class Order {

    // ==================== 基础信息字段 ====================
    
    /** 
     * 订单ID（主键，自增）
     * 数据库字段：order_id
     * 用途：唯一标识一条订单记录
     */
    private Long orderId;

    /** 
     * 订单号（业务主键，唯一索引）
     * 数据库字段：order_no
     * 格式：ORDER + 时间戳 + 4位随机数，例如：ORDER17003456781234
     * 用途：对外展示的订单编号，用户可以通过订单号查询订单
     * 技术要点：使用时间戳+随机数保证唯一性，支持分布式环境
     */
    private String orderNo;

    /** 
     * 用户ID（外键）
     * 数据库字段：user_id
     * 关联：sys_user 表的 user_id
     * 用途：标识下单用户，用于查询"我的订单"
     */
    private Long userId;

    /** 
     * 商户ID（外键）
     * 数据库字段：merchant_id
     * 关联：t_merchant 表的 merchant_id
     * 用途：标识接单商户，用于查询"商户订单"
     */
    private Long merchantId;

    /** 
     * 商户名称（冗余字段）
     * 数据库字段：merchant_name
     * 技术要点：
     * - 冗余设计：将 t_merchant.merchant_name 冗余到订单表
     * - 优点：查询订单列表时无需 JOIN t_merchant 表，提高查询性能
     * - 缺点：商户名称修改时需同步更新历史订单（实际业务中订单不可变，不需要更新）
     * - 适用场景：读多写少的场景，订单列表查询频繁
     */
    private String merchantName;

    // ==================== 金额相关字段（使用 BigDecimal 避免精度问题） ====================
    
    /** 
     * 订单总金额（商品金额合计）
     * 数据库字段：total_amount
     * 计算公式：SUM(商品单价 × 商品数量)
     * 示例：宫保鸡丁(25元×2) + 米饭(3元×1) = 53元
     * 技术要点：使用 BigDecimal 类型，避免 float/double 精度丢失
     */
    private BigDecimal totalAmount;

    /** 
     * 配送费
     * 数据库字段：delivery_fee
     * 当前规则：固定 5.00 元
     * 扩展点：可根据距离、天气、时段动态计算
     */
    private BigDecimal deliveryFee;

    /** 
     * 优惠金额
     * 数据库字段：discount_amount
     * 用途：记录使用优惠券、满减活动等的优惠金额
     * 当前状态：暂未实现优惠功能，默认为 0
     */
    private BigDecimal discountAmount;

    /** 
     * 实付金额（用户实际支付的金额）
     * 数据库字段：actual_amount
     * 计算公式：total_amount + delivery_fee - discount_amount
     * 示例：53 + 5 - 0 = 58 元
     * 技术要点：实付金额是最终结算金额，支付接口以此为准
     */
    private BigDecimal actualAmount;

    // ==================== 支付相关字段 ====================
    
    /** 
     * 支付方式
     * 数据库字段：payment_method
     * 可选值：
     * - online: 在线支付（微信、支付宝等）
     * - cash: 货到付款
     * 默认值：online
     */
    private String paymentMethod;

    /** 
     * 支付状态
     * 数据库字段：payment_status
     * 可选值：
     * - 0: 未支付
     * - 1: 已支付
     * - 2: 已退款
     * 技术要点：支付状态与订单状态是两个维度，支付成功后订单状态才会流转
     */
    private String paymentStatus;

    /** 
     * 订单状态（核心字段，状态机设计）
     * 数据库字段：order_status
     * 状态枚举：
     * - pending: 待支付（订单刚创建，等待用户支付）
     * - paid: 已支付（用户支付成功，等待商户接单）
     * - preparing: 制作中（商户已接单，正在制作菜品）
     * - delivering: 配送中（商户已出餐，骑手正在配送）
     * - delivered: 已送达（骑手已送达，等待用户确认收货）
     * - completed: 已完成（用户确认收货，订单完结，更新销量）
     * - cancelled: 已取消（用户取消订单，恢复库存）
     * 
     * 状态流转规则（严格按顺序）：
     * pending → paid → preparing → delivering → delivered → completed
     *    ↓
     * cancelled（只能在 pending 状态取消）
     * 
     * 技术要点：
     * 1. 每次状态变更前校验当前状态，防止非法跳转
     * 2. 使用数据库索引优化按状态查询的性能
     * 3. 可扩展为枚举类，增强类型安全性
     */
    private String orderStatus;

    // ==================== 配送信息字段 ====================
    
    /** 
     * 配送地址
     * 数据库字段：delivery_address
     * 示例：湖北省武汉市洪山区珞喻路129号武汉大学信息学部
     * 用途：骑手配送时的目的地地址
     */
    private String deliveryAddress;

    /** 
     * 联系人姓名
     * 数据库字段：contact_name
     * 用途：配送时联系收货人
     */
    private String contactName;

    /** 
     * 联系电话
     * 数据库字段：contact_phone
     * 用途：骑手配送时联系用户
     * 技术要点：需要做手机号格式校验
     */
    private String contactPhone;

    /** 
     * 订单备注
     * 数据库字段：remark
     * 示例：少放辣椒、多加米饭、不要香菜等
     * 用途：用户的特殊要求，商户制作时参考
     */
    private String remark;

    // ==================== 时间相关字段 ====================
    
    /** 
     * 预计送达时间
     * 数据库字段：delivery_time
     * 计算规则：当前时间 + 1小时（可根据距离、订单量动态调整）
     * 用途：给用户一个预期送达时间
     */
    private LocalDateTime deliveryTime;

    /** 
     * 支付时间
     * 数据库字段：pay_time
     * 记录时机：用户支付成功时
     * 用途：统计支付转化率、分析用户支付行为
     */
    private LocalDateTime payTime;

    /** 
     * 完成时间
     * 数据库字段：complete_time
     * 记录时机：用户确认收货时
     * 用途：计算配送时长、评估骑手绩效
     */
    private LocalDateTime completeTime;

    /** 
     * 取消时间
     * 数据库字段：cancel_time
     * 记录时机：用户取消订单时
     * 用途：分析订单取消原因、统计取消率
     */
    private LocalDateTime cancelTime;

    /** 
     * 取消原因
     * 数据库字段：cancel_reason
     * 示例：不想要了、下错了、商家太慢等
     * 用途：分析订单取消原因，优化服务
     */
    private String cancelReason;

    /** 
     * 创建时间
     * 数据库字段：create_time
     * 默认值：NOW()（数据库自动填充）
     * 用途：记录订单创建时间，用于排序和统计
     */
    private LocalDateTime createTime;

    /** 
     * 更新时间
     * 数据库字段：update_time
     * 默认值：NOW() ON UPDATE NOW()（数据库自动更新）
     * 用途：记录订单最后修改时间，用于审计和追踪
     */
    private LocalDateTime updateTime;

    // ========================================
    // 关联查询字段（非数据库字段）
    // ========================================
    // 技术说明：
    // 这些字段不对应数据库列，是在查询时通过关联表动态填充的
    // 用途：减少前端多次请求，一次查询返回完整的订单信息
    
    /** 
     * 订单明细列表（一对多关联）
     * 对应表：t_order_item
     * 关联字段：order_id
     * 用途：显示订单包含的所有商品详情
     * 技术要点：
     * - 在 OrderMapper.xml 中通过 LEFT JOIN 或分步查询填充
     * - 前端展示订单详情时使用
     */
    private java.util.List<OrderItem> orderItems;

    /** 
     * 订单商品总数（计算字段）
     * 计算规则：SUM(orderItems.quantity)
     * 示例：宫保鸡丁×2 + 米饭×1 = 3
     * 用途：在订单列表快速显示商品总数，无需遍历明细
     */
    private Integer totalQuantity;
}

