package cs.qqq.Entity;


import lombok.Data;

import java.time.LocalDateTime;


@Data

public class SysRole {

  private Long roleId;
  private String roleName;
  private Long roleSort;
  private String status;
  private String delFlag;
  private String createBy;
  private LocalDateTime createTime;
  private String updateBy;
  private LocalDateTime updateTime;
  private String remark;




}
