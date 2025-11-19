package cs.qqq.Entity;


import lombok.Data;

import java.time.LocalDateTime;


@Data
public class SysRole {
  private Long roleId;
  private String roleName;
  private String roleDesc;
  private String status;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
