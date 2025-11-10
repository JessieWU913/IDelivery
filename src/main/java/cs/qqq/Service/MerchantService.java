package cs.qqq.Service;

import cs.qqq.Entity.Merchant;
import java.util.List;

/**
 * 商户Service接口
 */
public interface MerchantService {
    
    /**
     * 根据用户ID查询商户信息
     */
    Merchant findByUserId(Long userId);
    
    /**
     * 根据商户ID查询商户信息
     */
    Merchant findById(Long merchantId);
    
    /**
     * 查询所有营业中的商户
     */
    List<Merchant> findAllActive();
    
    /**
     * 查询所有商户
     */
    List<Merchant> findAll();
    
    /**
     * 添加商户
     */
    int addMerchant(Merchant merchant);
    
    /**
     * 更新商户信息
     */
    int updateMerchant(Merchant merchant);
    
    /**
     * 删除商户
     */
    int deleteMerchant(Long merchantId);
    
    /**
     * 更新商户状态（营业中/休息中）
     */
    int updateStatus(Long merchantId, Integer status);
}
