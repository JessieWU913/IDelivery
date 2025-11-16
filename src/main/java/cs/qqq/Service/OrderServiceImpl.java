package cs.qqq.Service;

import cs.qqq.Entity.*;
import cs.qqq.Mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单Service实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Order> createOrderFromCart(Long userId, String deliveryAddress,
                                           String contactName, String contactPhone, String remark) {
        // 1. 查询购物车
        List<Cart> cartList = cartMapper.findCartListByUserId(userId.intValue());
        if (cartList == null || cartList.isEmpty()) {
            throw new RuntimeException("购物车为空，无法创建订单");
        }

        // 2. 验证库存
        for (Cart cart : cartList) {
            Product product = productMapper.findById(cart.getProductId().longValue());
            if (product == null) {
                throw new RuntimeException("商品不存在：" + cart.getProductName());
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("商品库存不足：" + cart.getProductName() + "，库存：" + product.getStock() + "，需要：" + cart.getQuantity());
            }
        }

        // 3. 按商户分组
        Map<Long, List<Cart>> cartByMerchant = cartList.stream()
                .collect(Collectors.groupingBy(cart -> cart.getMerchantId()));

        List<Order> orders = new ArrayList<>();

        // 4. 为每个商户创建订单
        for (Map.Entry<Long, List<Cart>> entry : cartByMerchant.entrySet()) {
            Long merchantId = entry.getKey();
            List<Cart> merchantCarts = entry.getValue();

            // 查询商户信息
            Merchant merchant = merchantMapper.findById(merchantId);
            if (merchant == null) {
                throw new RuntimeException("商户不存在：" + merchantId);
            }

            // 计算订单金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Cart cart : merchantCarts) {
                BigDecimal subtotal = cart.getPrice().multiply(new BigDecimal(cart.getQuantity()));
                totalAmount = totalAmount.add(subtotal);
            }

            // 配送费（简单计算，可以根据距离等计算）
            BigDecimal deliveryFee = new BigDecimal("5.00");
            BigDecimal discountAmount = BigDecimal.ZERO; // 暂时没有优惠
            BigDecimal actualAmount = totalAmount.add(deliveryFee).subtract(discountAmount);

            // 创建订单
            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setMerchantId(merchantId);
            order.setMerchantName(merchant.getMerchantName());
            order.setTotalAmount(totalAmount);
            order.setDeliveryFee(deliveryFee);
            order.setDiscountAmount(discountAmount);
            order.setActualAmount(actualAmount);
            order.setPaymentMethod("online");
            order.setPaymentStatus("0");
            order.setOrderStatus("pending");
            order.setDeliveryAddress(deliveryAddress);
            order.setContactName(contactName);
            order.setContactPhone(contactPhone);
            order.setRemark(remark);
            order.setDeliveryTime(LocalDateTime.now().plusHours(1)); // 预计1小时后送达

            try {
                // 插入订单
                orderMapper.insertOrder(order);
            } catch (Exception e) {
                e.printStackTrace(); // 添加调试日志
                throw new RuntimeException("订单创建失败：" + e.getMessage());
            }

            // 创建订单明细
            List<OrderItem> orderItems = new ArrayList<>();
            for (Cart cart : merchantCarts) {
                OrderItem item = new OrderItem();
                item.setOrderId(order.getOrderId());
                item.setOrderNo(order.getOrderNo());
                item.setProductId(cart.getProductId().longValue());
                item.setProductName(cart.getProductName());
                item.setProductImg(cart.getProductImg());
                item.setPrice(cart.getPrice());
                item.setQuantity(cart.getQuantity());
                item.setSubtotal(cart.getPrice().multiply(new BigDecimal(cart.getQuantity())));
                orderItems.add(item);

                // 扣减库存
                productMapper.updateStock(cart.getProductId().longValue(), cart.getQuantity());
            }

            // 批量插入订单明细
            if (!orderItems.isEmpty()) {
                orderItemMapper.batchInsert(orderItems);
            }

            // 查询订单详情（包含明细）
            Order orderDetail = orderMapper.findById(order.getOrderId());
            if (orderDetail != null) {
                List<OrderItem> items = orderItemMapper.findByOrderId(order.getOrderId());
                orderDetail.setOrderItems(items);
                orders.add(orderDetail);
            }
        }

        // 5. 清空购物车
        cartMapper.clearCart(userId.intValue());

        return orders;
    }

    @Override
    public Order getOrderDetail(Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order != null) {
            List<OrderItem> items = orderItemMapper.findByOrderId(orderId);
            order.setOrderItems(items);

            // 计算商品总数
            int totalQuantity = items.stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();
            order.setTotalQuantity(totalQuantity);
        }
        return order;
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> items = orderItemMapper.findByOrderNo(orderNo);
            order.setOrderItems(items);

            int totalQuantity = items.stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();
            order.setTotalQuantity(totalQuantity);
        }
        return order;
    }

    @Override
    public List<Order> getUserOrders(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    @Override
    public List<Order> getUserOrdersByStatus(Long userId, String orderStatus) {
        return orderMapper.findByUserIdAndStatus(userId, orderStatus);
    }

    @Override
    public List<Order> getMerchantOrders(Long merchantId) {
        return orderMapper.findByMerchantId(merchantId);
    }

    @Override
    public List<Order> getMerchantOrdersByStatus(Long merchantId, String orderStatus) {
        return orderMapper.findByMerchantIdAndStatus(merchantId, orderStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId, String paymentMethod) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!"pending".equals(order.getOrderStatus())) {
            throw new RuntimeException("订单状态不正确，无法支付");
        }

        if ("1".equals(order.getPaymentStatus())) {
            throw new RuntimeException("订单已支付");
        }

        // 更新支付状态
        orderMapper.updatePaymentStatus(orderId, "1", LocalDateTime.now());

        // 更新订单状态为已支付
        orderMapper.updateOrderStatus(orderId, "paid");

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, String cancelReason) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 只能取消待支付订单
        if (!"pending".equals(order.getOrderStatus())) {
            throw new RuntimeException("只能取消待支付订单");
        }

        // 恢复库存
        List<OrderItem> items = orderItemMapper.findByOrderId(orderId);
        for (OrderItem item : items) {
            productMapper.updateStock(item.getProductId(), -item.getQuantity());
        }

        // 取消订单
        orderMapper.cancelOrder(orderId, cancelReason, LocalDateTime.now());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmOrder(Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!"delivering".equals(order.getOrderStatus())) {
            throw new RuntimeException("订单状态不正确，无法确认收货");
        }

        // 更新订单状态为已完成
        orderMapper.confirmOrder(orderId, LocalDateTime.now());

        // 更新商品销量
        List<OrderItem> items = orderItemMapper.findByOrderId(orderId);
        for (OrderItem item : items) {
            productMapper.updateSales(item.getProductId(), item.getQuantity());
        }

        // 更新商户销量
        Merchant merchant = merchantMapper.findById(order.getMerchantId());
        if (merchant != null) {
            merchant.setSales(merchant.getSales() + 1);
            merchantMapper.update(merchant);
        }

        return true;
    }

    @Override
    public boolean acceptOrder(Long orderId, Long merchantId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (!"paid".equals(order.getOrderStatus())) {
            throw new RuntimeException("订单状态不正确，无法接单");
        }

        orderMapper.updateOrderStatus(orderId, "preparing");
        return true;
    }

    @Override
    public boolean startDelivery(Long orderId, Long merchantId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (!"preparing".equals(order.getOrderStatus())) {
            throw new RuntimeException("订单状态不正确，无法开始配送");
        }

        orderMapper.updateOrderStatus(orderId, "delivering");
        return true;
    }

    @Override
    public List<Order> getOrdersByStatus(String orderStatus) {
        return orderMapper.findByStatus(orderStatus);
    }

    @Override
    public String generateOrderNo() {
        // 格式：ORDER + 时间戳 + 4位随机数
        long timestamp = System.currentTimeMillis();
        int random = (int)(Math.random() * 9000) + 1000; // 1000-9999
        return "ORDER" + timestamp + random;
    }
}

