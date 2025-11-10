package cs.qqq.Entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户实体类
 * 对应表 t_merchant
 */
@Data
public class Merchant {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 关联用户ID
     */
    private Long userId;
    
    /**
     * 商户名称
     */
    private String merchantName;
    
    /**
     * 商户简介
     */
    private String description;
    
    /**
     * 商户logo
     */
    private String logo;
    
    /**
     * 商户地址
     */
    private String address;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 营业时间
     */
    private String businessHours;
    
    /**
     * 状态 1-营业中 0-休息中
     */
    private Integer status;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 月销量
     */
    private Integer sales;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
