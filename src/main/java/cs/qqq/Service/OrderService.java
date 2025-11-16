package cs.qqq.Service;

import cs.qqq.Entity.Order;
import java.util.List;

/**
 * 订单Service接口
 */
public interface OrderService {

    /**
     * 创建订单（从购物车创建，按商户拆分）
     * @param userId 用户ID
     * @param deliveryAddress 配送地址
     * @param contactName 联系人姓名
     * @param contactPhone 联系电话
     * @param remark 订单备注
     * @return 订单列表（可能多个订单，按商户拆分）
     */
    List<Order> createOrderFromCart(Long userId, String deliveryAddress,
                                    String contactName, String contactPhone, String remark);

    /**
     * 根据订单ID查询订单详情（包含订单明细）
     */
    Order getOrderDetail(Long orderId);

    /**
     * 根据订单号查询订单详情
     */
    Order getOrderByOrderNo(String orderNo);

    /**
     * 查询用户的所有订单
     */
    List<Order> getUserOrders(Long userId);

    /**
     * 根据订单状态查询用户订单
     */
    List<Order> getUserOrdersByStatus(Long userId, String orderStatus);

    /**
     * 查询商户的所有订单
     */
    List<Order> getMerchantOrders(Long merchantId);

    /**
     * 根据订单状态查询商户订单
     */
    List<Order> getMerchantOrdersByStatus(Long merchantId, String orderStatus);

    /**
     * 支付订单
     */
    boolean payOrder(Long orderId, String paymentMethod);

    /**
     * 取消订单（只能取消未支付订单）
     */
    boolean cancelOrder(Long orderId, String cancelReason);

    /**
     * 骑手送达（更新订单状态为delivered，等待用户确认收货）
     */
    boolean riderDeliverOrder(Long orderId);

    /**
     * 确认收货（用户确认收货，将订单状态改为completed）
     */
    boolean confirmOrder(Long orderId);

    /**
     * 商户接单（更新订单状态为制作中）
     */
    boolean acceptOrder(Long orderId, Long merchantId);

    /**
     * 开始配送（更新订单状态为配送中）
     */
    boolean startDelivery(Long orderId, Long merchantId);

    /**
     * 根据订单状态查询所有订单（用于骑手查看）
     */
    List<Order> getOrdersByStatus(String orderStatus);

    /**
     * 生成订单号
     */
    String generateOrderNo();
}
