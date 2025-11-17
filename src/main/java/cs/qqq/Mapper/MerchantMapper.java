package cs.qqq.Mapper;

import cs.qqq.Entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商户Mapper接口
 */
@Mapper
public interface MerchantMapper {
    
    /**
     * 根据用户ID查询商户信息
     */
    Merchant findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据商户ID查询商户信息
     */
    Merchant findById(@Param("merchantId") Long merchantId);
    
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
    int insert(Merchant merchant);
    
    /**
     * 更新商户信息
     */
    int update(Merchant merchant);
    
    /**
     * 删除商户
     */
    int delete(@Param("merchantId") Long merchantId);
    
    /**
     * 更新商户状态
     */
    int updateStatus(@Param("merchantId") Long merchantId, @Param("status") Integer status);
    
    /**
     * 统计所有商户数量
     */
    long countAllMerchants();
}
