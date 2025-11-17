package cs.qqq.Entity;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class SysUser {
  private Long userId;
  private String userName;
  private String password;
  private String realName;
  private String phone;
  private String email;
  private Long roleId;
  private String status;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
  
  // 用于关联查询时显示角色名
  private String roleName;
}
