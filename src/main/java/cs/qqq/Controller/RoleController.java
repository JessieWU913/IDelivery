package cs.qqq.Controller;

import cs.qqq.Entity.SysRole;
import cs.qqq.Entity.SysUser;
import cs.qqq.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class RoleController {
    @Autowired
    RoleService roleService;

    //查看所有的role
    @GetMapping("/roleList")
    public String getAllUsers(Model model) {
        List<SysRole> roleList= roleService.getAllRoles();
        model.addAttribute("roles", roleList);
        return "role/role_list";
    }

    //修改角色状态
    @GetMapping("/role/updateRoleStatus")
    public String del(@RequestParam Long roleId, @RequestParam String status, Model model) {

        SysRole role = roleService.findRoleById(roleId);
        role.setStatus(status);
        roleService.updateRole(role);

        List<SysRole> roleList = roleService.getAllRoles();
        model.addAttribute("roles", roleList);
        return "role/role_list";
    }

    //修改角色信息，回显
    @GetMapping("/role/edit/{id}")
    public String roleedit(@PathVariable("id") Long roleId, Model model){//先查数据 回显
        SysRole sysRole=roleService.findRoleById(roleId);
        model.addAttribute("role",sysRole);
        return "role/role_update";
    }

    @PostMapping("/updaterole")
    public String updateuser(HttpServletRequest request, Long roleId,String roleName,Long roleSort,String remark) {//修改
        SysRole role = roleService.findRoleById(roleId);
        role.setRemark(remark);
        role.setUpdateBy(((SysUser) request.getSession().getAttribute("currentUser")).getUserName());
        role.setUpdateTime(LocalDateTime.now());
        role.setRoleSort(roleSort);
        role.setRoleName(roleName);
        roleService.updateRole1(role);

        return "redirect:/roleList";
    }


    //跳转到保存页面
    @GetMapping("/role/add")
    public String adduser(Model model) {
//        model.addAttribute("roles",roleService.addRole());
        return "role/role_add";
    }

    //新增用户信息
    @PostMapping("/saverole")
    public String saverole(HttpServletRequest request, String roleName, Long roleSort,  String remark) {

        SysRole role = new SysRole();
        role.setRoleName(roleName);
        role.setRemark(remark);
        role.setRoleSort(roleSort);

        role.setCreateBy(((SysUser) request.getSession().getAttribute("currentUser")).getUserName());
        role.setCreateTime(LocalDateTime.now());
        roleService.addRole(role);
        return "redirect:/roleList";

    }

}
