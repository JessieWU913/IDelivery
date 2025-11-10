package cs.qqq.Service;



import cs.qqq.Entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {


    List<SysUser> getAllUsers();


    SysUser findUserByUsernameAndPassword(String userName,String password);

    void updateUser(SysUser sysUser);
       SysUser findUserById(long userId);

    List<SysUser> findAllUsers(String username, String phone, Long roleid, String beginTime, String endTime);

    void updateUser1(SysUser sysUser);

    String savefile(MultipartFile[] fileUpload);

    void saveUser(SysUser sysUser);


}
