package cs.qqq.Controller;

import cs.qqq.Entity.SysUser;
import cs.qqq.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping({"/","/login.html"})
    public String root(){
        return "login";
    }

    // 向登录页面跳转，同时封装原始页面地址
    @GetMapping("/tologin")
    public String toLogin(){
        return "login";
    }


    @PostMapping("/login")
    public String checkLogin(String username, String password, HttpServletRequest httpServletRequest){ //前台 提交的数据

        SysUser user=userService.findUserByUsernameAndPassword(username,password);

        System.out.println(user);
        HttpSession httpSession=httpServletRequest.getSession();
        if (user!=null){
            httpSession.setAttribute("currentUser",user);
            httpSession.setAttribute("user",user);  // 兼容性，某些页面使用user
            
            // 根据角色判断跳转页面
            if (user.getRoleId() != null && (user.getRoleId() == 1 || user.getRoleId() == 2)) {
                return "redirect:/userList";  // 管理员进入后台
            } else if (user.getRoleId() != null && user.getRoleId() == 3) {
                return "redirect:/merchant/index";  // 商户进入商户管理页面
            } else if (user.getRoleId() != null && user.getRoleId() == 4) {
                return "redirect:/rider/index";  // 骑手进入骑手管理页面
            } else {
                return "redirect:/menu/index";  // 普通用户进入点餐页面
            }
        }
        else{
            httpSession.invalidate();
            return "login";
        }

    }

    @PostMapping("/logout")
    public String logout(HttpSession httpSession){
        httpSession.invalidate();
        return "redirect:/login.html";
    }
    
    @GetMapping("/logout")
    public String logoutGet(HttpSession httpSession){
        httpSession.invalidate();
        return "redirect:/login.html";
    }




}

