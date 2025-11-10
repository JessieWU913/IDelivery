package cs.qqq.Service;

import cs.qqq.Entity.SysUser;
import cs.qqq.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<SysUser> getAllUsers() {
        return userMapper.getAllUsers();
    }

    @Override
    public SysUser findUserByUsernameAndPassword(String userName, String password) {
        return userMapper.findUserByUsernameAndPassword(userName, password);
    }

    @Override
    public void updateUser(SysUser sysUser) {
        userMapper.updateUser(sysUser.getStatus(), sysUser.getUserId());

    }

    @Override
    public SysUser findUserById(long userId) {
        return userMapper.findUserById(userId);
    }

    @Override
    public List<SysUser> findAllUsers(String username, String phone, Long roleid, String beginTime, String endTime) {
        return userMapper.findAllUsers(username, phone, roleid, beginTime,endTime);
    }

    @Override
    public void updateUser1(SysUser sysUser) {
        userMapper.updateUser1(sysUser);
    }

    @Override
    public String savefile(MultipartFile[] fileUpload) {

        String fileName = null;
       // System.out.println("上传成功");
        for (MultipartFile file : fileUpload) {
            //获取文件名以及后缀名
            fileName = file.getOriginalFilename();
            //重新生成文件名（根据具体情况生成对应文件名）
            fileName = UUID.randomUUID() + "_" + fileName;
            //指定上传文件本地目录，不存在则需要提前创建
            String dirPath = "E:/pic/";
            File filepath = new File(dirPath);
            if (!filepath.exists()) {
                filepath.mkdirs();
            }
            try {
                file.transferTo(new File(dirPath + fileName));
            } catch (IOException e) {
                System.out.println("上传失败");
                e.printStackTrace();
            }

        }
        return fileName;
    }

    @Override
    public void saveUser(SysUser sysUser) {
        userMapper.saveUser(sysUser);
    }


}