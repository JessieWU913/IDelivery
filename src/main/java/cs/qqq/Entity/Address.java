package cs.qqq.Entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 配送地址实体类
 * 对应数据库表: t_address
 */
@Data
public class Address {

    /** 地址ID */
    private Long addressId;

    /** 用户ID */
    private Long userId;

    /** 联系人姓名 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 区县 */
    private String district;

    /** 详细地址 */
    private String detailAddress;

    /** 完整地址（冗余） */
    private String fullAddress;

    /** 是否默认地址：1-是, 0-否 */
    private String isDefault;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}

