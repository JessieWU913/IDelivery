package cs.qqq.Mapper;

import cs.qqq.Entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {

    /**
     * 创建订单
     */
    int insertOrder(Order order);

    /**
     * 根据订单ID查询订单
     */
    Order findById(@Param("orderId") Long orderId);

    /**
     * 根据订单号查询订单
     */
    Order findByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID查询订单列表
     */
    List<Order> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和订单状态查询订单列表
     */
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("orderStatus") String orderStatus);

    /**
     * 根据商户ID查询订单列表
     */
    List<Order> findByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 根据商户ID和订单状态查询订单列表
     */
    List<Order> findByMerchantIdAndStatus(@Param("merchantId") Long merchantId, @Param("orderStatus") String orderStatus);

    /**
     * 更新订单状态
     */
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("orderStatus") String orderStatus);

    /**
     * 更新支付状态
     */
    int updatePaymentStatus(@Param("orderId") Long orderId, @Param("paymentStatus") String paymentStatus, @Param("payTime") java.time.LocalDateTime payTime);

    /**
     * 取消订单
     */
    int cancelOrder(@Param("orderId") Long orderId, @Param("cancelReason") String cancelReason, @Param("cancelTime") java.time.LocalDateTime cancelTime);

    /**
     * 确认收货
     */
    int confirmOrder(@Param("orderId") Long orderId, @Param("completeTime") java.time.LocalDateTime completeTime);

    /**
     * 更新订单信息
     */
    int updateOrder(Order order);
}
