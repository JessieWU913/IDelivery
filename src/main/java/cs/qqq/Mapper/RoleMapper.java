package cs.qqq.Mapper;


import cs.qqq.Entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {


    List<SysRole> getAllRoles();
    SysRole  findRoleById(Long roleId);
    void updateRole( String status,Long roleId);
    void updateRole1(SysRole sysRole);

    void addRole(SysRole sysRole);
    
    void deleteRole(Long roleId);
}