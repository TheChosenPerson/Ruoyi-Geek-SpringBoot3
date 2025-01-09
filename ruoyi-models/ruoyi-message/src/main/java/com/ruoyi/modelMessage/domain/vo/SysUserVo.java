package com.ruoyi.modelMessage.domain.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.Type;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户对象 sys_user
 * 
 * @author ruoyi
 */
@Schema(title = "用户")
public class SysUserVo
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @Schema(title = "用户编号")
    @Excel(name = "用户编号")
    private Long userId;

    /** 部门ID */
    @Schema(title = "部门编号")
    @Excel(name = "部门编号", type = Type.IMPORT)
    private Long deptId;

    /** 用户账号 */
    @Schema(title = "登录名称")
    @Excel(name = "登录名称")
    private String userName;

    /** 用户邮箱 */
    @Schema(title = "用户邮箱")
    @Excel(name = "用户邮箱")
    private String email;

    /** 手机号码 */
    @Schema(title = "手机号码")
    @Excel(name = "手机号码")
    private String phonenumber;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .append("userName", getUserName())
            .append("email", getEmail())
            .append("phonenumber", getPhonenumber())
            .toString();
    }
}
