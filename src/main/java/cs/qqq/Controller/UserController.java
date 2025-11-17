package cs.qqq.Controller;



import cs.qqq.Entity.SysRole;
import cs.qqq.Entity.SysUser;
import cs.qqq.Mapper.UserMapper;
import cs.qqq.Service.RoleService;
import cs.qqq.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    UserMapper userMapper;

    //查看所有的user
    @GetMapping("/userList")
    public String getAllUsers(Model model) {
        List<SysUser> userList = userService.getAllUsers();
        model.addAttribute("users", userList);
        List<SysRole> roleList= roleService.getAllRoles();
        model.addAttribute("roles", roleList);
        return "user/user_list";
    }

//根据某一具体字段查看用户
    @PostMapping("/searchUser")
    public String search(Model model, String userName, String phonenumber, Long roleId, String beginTime,String endTime){
        List<SysUser> userList = userService.findAllUsers(userName,phonenumber,roleId,beginTime,endTime);
        List<SysRole> roleList= roleService.getAllRoles();
        model.addAttribute("roles", roleList);
        model.addAttribute("users", userList);
        return "user/user_list";
    }

    // 修改用户信息，回显
    @GetMapping("/user/edit/{id}")
    public String useredit(@PathVariable("id") Long userId,Model model){//先查数据 回显
        SysUser sysUser=userService.findUserById(userId);
        List<SysRole> roleList=roleService.getAllRoles();
        model.addAttribute("roles",roleList);
        model.addAttribute("user",sysUser);

        return "user/user_update";
    }
    @GetMapping("/user/delete/{id}")
    public String userdelete(@PathVariable("id") Long userId) {//删除
        userMapper.deleteUserByID(userId);
        return "redirect:/user/list";
    }
    //用户
    @GetMapping("/user/updateStatus")
    public String del(@RequestParam String userId,@RequestParam String status,Model model) {
        Long userid = Long.parseLong(userId);
        SysUser user = userService.findUserById(userid);
        System.out.println(status);

        user.setStatus(status);
        userService.updateUser(user);

        List<SysUser> userList = userService.getAllUsers();
        model.addAttribute("users", userList);
        return "user/user_list";
    }

    @PostMapping("/updateuser")
    public String updateuser(HttpServletRequest request,MultipartFile[] fileUpload, String username, String password, String roleName, String phone, String email, Long userId) {//修改
        SysUser sysUser=userService.findUserById(userId);
        System.out.println("修改"+sysUser);
        sysUser.setRoleId(Long.parseLong(roleName));
        sysUser.setUserName(username);
        sysUser.setEmail(email);
        sysUser.setPhone(phone);
        if(password != null && !password.isEmpty()) {
            sysUser.setPassword(password);
        }

        userService.updateUser1(sysUser);
        return "redirect:/userList";
    }

    //跳转到保存页面
    @GetMapping("/user/add")
    public String adduser(Model model) {
        List<SysRole> roleList= roleService.getAllRoles();
        model.addAttribute("roles", roleList);
        return "user/user_add";
    }

    // 新增用户信息
    @PostMapping("/saveuser")
    public String saveuser(HttpServletRequest request, MultipartFile[] fileUpload, String username, String password, String roleName, String phone, String email) {
        // 初始化用户对象
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setPassword(password);
        sysUser.setRoleId(Long.parseLong(roleName));
        sysUser.setPhone(phone);
        sysUser.setEmail(email);
        sysUser.setStatus("1"); // 1表示启用

        userService.saveUser(sysUser);
        return "redirect:/userList";
    }





}