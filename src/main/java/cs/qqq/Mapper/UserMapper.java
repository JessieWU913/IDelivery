package cs.qqq.Mapper;

import cs.qqq.Entity.SysUser;
import org.apache.ibatis.annotations.Mapper;


import java.time.LocalDateTime;
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
}