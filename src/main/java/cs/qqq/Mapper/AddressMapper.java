package cs.qqq.Mapper;

import cs.qqq.Entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 配送地址Mapper接口
 */
@Mapper
public interface AddressMapper {

    /**
     * 根据用户ID查询地址列表
     */
    List<Address> findByUserId(@Param("userId") Long userId);

    /**
     * 根据地址ID查询地址
     */
    Address findById(@Param("addressId") Long addressId);

    /**
     * 查询用户的默认地址
     */
    Address findDefaultByUserId(@Param("userId") Long userId);

    /**
     * 添加地址
     */
    int insert(Address address);

    /**
     * 更新地址
     */
    int update(Address address);

    /**
     * 删除地址
     */
    int delete(@Param("addressId") Long addressId);

    /**
     * 取消用户的默认地址
     */
    int cancelDefaultByUserId(@Param("userId") Long userId);

    /**
     * 设置默认地址
     */
    int setDefault(@Param("addressId") Long addressId);
}
