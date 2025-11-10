package cs.qqq.Service;


import cs.qqq.Entity.SysRole;
import cs.qqq.Mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;



@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<SysRole> getAllRoles() {
        return roleMapper.getAllRoles();
    }

    @Override
    public SysRole findRoleById(Long id) {
        return roleMapper.findRoleById(id);
    }

    @Override
    public void updateRole(SysRole role) {
        roleMapper.updateRole(role.getStatus(),role.getRoleId());
    }

    @Override
    public void updateRole1(SysRole sysRole) {
        roleMapper.updateRole1(sysRole);
    }

    @Override
    public void addRole(SysRole sysRole) {
        roleMapper.addRole(sysRole);
    }


}