package cs.qqq.Service;



import cs.qqq.Entity.SysRole;

import java.util.List;

public interface RoleService {

    List<SysRole> getAllRoles();
    SysRole findRoleById(Long id);
    void updateRole(SysRole sysRole);
    void updateRole1(SysRole sysRole);

    void addRole(SysRole sysRole);
    
    void deleteRole(Long roleId);

}
