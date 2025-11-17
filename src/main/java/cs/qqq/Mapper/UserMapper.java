package cs.qqq.Mapper;

import cs.qqq.Entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;


@Mapper
public interface UserMapper {


    List<SysUser> getAllUsers();

    List<SysUser> findAllUsers(String userName, String phonenumber, Long roleId, String beginTime , String endTime);

    SysUser findUserByUsernameAndPassword(String userName,String password);

    void updateUser(String status,Long userId);

    SysUser findUserById(Long userId);

    void updateUser1(SysUser sysUser);

    void saveUser(SysUser sysUser);

    //通过ID删除用户信息
    void deleteUserByID(Long id);
    
    // 统计所有用户数量
    long countAllUsers();
    
    // 根据角色ID查询用户列表
    List<SysUser> getUsersByRoleId(Long roleId);
    
    // 根据用户名查询用户
    SysUser findByUsername(String username);
}