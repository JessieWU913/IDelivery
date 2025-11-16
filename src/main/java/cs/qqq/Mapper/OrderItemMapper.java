package cs.qqq.Mapper;

import cs.qqq.Entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单明细Mapper接口
 */
@Mapper
public interface OrderItemMapper {

    /**
     * 批量插入订单明细
     */
    int batchInsert(@Param("items") List<OrderItem> items);

    /**
     * 根据订单ID查询订单明细
     */
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单号查询订单明细
     */
    List<OrderItem> findByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据商品ID查询订单明细（用于统计）
     */
    List<OrderItem> findByProductId(@Param("productId") Long productId);
}
