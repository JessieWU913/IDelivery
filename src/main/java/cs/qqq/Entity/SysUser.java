package cs.qqq.Entity;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class SysUser {
  private Long userId;
  private Long roleId;
  private String roleName;
  private String userName;
  private String email;
  private String phonenumber;
  private String sex;
  private String password;
  private String status;
  private String delFlag;
  private LocalDateTime loginDate;
  private String createBy;
  private LocalDateTime createTime;
  private String updateBy;
  private LocalDateTime updateTime;
  private String remark;
  private String pic;
}
