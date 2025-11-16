package cs.qqq.Service;

import cs.qqq.Entity.Address;
import java.util.List;

/**
 * 配送地址Service接口
 */
public interface AddressService {

    /**
     * 查询用户的所有地址
     */
    List<Address> getUserAddresses(Long userId);

    /**
     * 根据地址ID查询地址
     */
    Address getAddressById(Long addressId);

    /**
     * 查询用户的默认地址
     */
    Address getDefaultAddress(Long userId);

    /**
     * 添加地址
     */
    boolean addAddress(Address address);

    /**
     * 更新地址
     */
    boolean updateAddress(Address address);

    /**
     * 删除地址
     */
    boolean deleteAddress(Long addressId, Long userId);

    /**
     * 设置默认地址
     */
    boolean setDefaultAddress(Long addressId, Long userId);
}
