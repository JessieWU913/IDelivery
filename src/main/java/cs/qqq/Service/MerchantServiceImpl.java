package cs.qqq.Service;

import cs.qqq.Entity.Merchant;
import cs.qqq.Mapper.MerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商户Service实现类
 */
@Service
public class MerchantServiceImpl implements MerchantService {
    
    @Autowired
    private MerchantMapper merchantMapper;
    
    @Override
    public Merchant findByUserId(Long userId) {
        return merchantMapper.findByUserId(userId);
    }
    
    @Override
    public Merchant findById(Long merchantId) {
        return merchantMapper.findById(merchantId);
    }
    
    @Override
    public List<Merchant> findAllActive() {
        return merchantMapper.findAllActive();
    }
    
    @Override
    public List<Merchant> findAll() {
        return merchantMapper.findAll();
    }
    
    @Override
    public int addMerchant(Merchant merchant) {
        return merchantMapper.insert(merchant);
    }
    
    @Override
    public int updateMerchant(Merchant merchant) {
        return merchantMapper.update(merchant);
    }
    
    @Override
    public int deleteMerchant(Long merchantId) {
        return merchantMapper.delete(merchantId);
    }
    
    @Override
    public int updateStatus(Long merchantId, Integer status) {
        return merchantMapper.updateStatus(merchantId, status);
    }
}
