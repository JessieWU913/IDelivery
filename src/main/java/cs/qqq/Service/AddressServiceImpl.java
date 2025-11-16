package cs.qqq.Service;

import cs.qqq.Entity.Address;
import cs.qqq.Mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 配送地址Service实现类
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> getUserAddresses(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    @Override
    public Address getAddressById(Long addressId) {
        return addressMapper.findById(addressId);
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        return addressMapper.findDefaultByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addAddress(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if ("1".equals(address.getIsDefault())) {
            addressMapper.cancelDefaultByUserId(address.getUserId());
        }

        // 构建完整地址
        if (address.getFullAddress() == null || address.getFullAddress().isEmpty()) {
            String fullAddress = buildFullAddress(address);
            address.setFullAddress(fullAddress);
        }

        try {
            return addressMapper.insert(address) > 0;
        } catch (Exception e) {
            e.printStackTrace(); // 添加调试日志
            throw new RuntimeException("添加地址失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAddress(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if ("1".equals(address.getIsDefault())) {
            addressMapper.cancelDefaultByUserId(address.getUserId());
        }

        // 更新完整地址
        if (address.getFullAddress() == null || address.getFullAddress().isEmpty()) {
            String fullAddress = buildFullAddress(address);
            address.setFullAddress(fullAddress);
        }

        return addressMapper.update(address) > 0;
    }

    @Override
    public boolean deleteAddress(Long addressId, Long userId) {
        // 验证地址是否属于该用户
        Address address = addressMapper.findById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此地址");
        }

        return addressMapper.delete(addressId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultAddress(Long addressId, Long userId) {
        // 验证地址是否属于该用户
        Address address = addressMapper.findById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此地址");
        }

        // 取消其他默认地址
        addressMapper.cancelDefaultByUserId(userId);

        // 设置默认地址
        return addressMapper.setDefault(addressId) > 0;
    }

    /**
     * 构建完整地址
     */
    private String buildFullAddress(Address address) {
        StringBuilder sb = new StringBuilder();
        if (address.getProvince() != null && !address.getProvince().isEmpty()) {
            sb.append(address.getProvince());
        }
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            sb.append(address.getCity());
        }
        if (address.getDistrict() != null && !address.getDistrict().isEmpty()) {
            sb.append(address.getDistrict());
        }
        if (address.getDetailAddress() != null && !address.getDetailAddress().isEmpty()) {
            sb.append(address.getDetailAddress());
        }
        return sb.toString();
    }
}

