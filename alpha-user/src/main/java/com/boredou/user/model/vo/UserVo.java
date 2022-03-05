package com.boredou.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "UserVo", description = "添加和编辑用户入参对象")
public class UserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "账号不能为空")
    @ApiModelProperty(value = "账号", name = "username", required = true)
    private String username;

    @ApiModelProperty(value = "真实姓名", name = "name")
    private String name;

    @ApiModelProperty(value = "公司", name = "company")
    private String company;

    @ApiModelProperty(value = "工号", name = "employeeId")
    private String employeeId;

    @ApiModelProperty(value = "职务", name = "position")
    private String position;

    @ApiModelProperty(value = "部门", name = "department")
    private String department;

    @ApiModelProperty(value = "职级", name = "rank")
    private String rank;

    @ApiModelProperty(value = "角色id,以','分割", name = "roleIds")
    private String roleIds;

    @ApiModelProperty(value = "手机号", name = "phone")
    private String phone;

    @Email(message = "邮件格式不正确")
    @ApiModelProperty(value = "邮件", name = "email")
    private String email;

    @ApiModelProperty(value = "qq号码", name = "qq")
    private Integer qq;

    @ApiModelProperty(value = "入职时间", name = "entryTime")
    private Date entryTime;

    @ApiModelProperty(value = "状态", name = "status")
    private Integer status;

    @ApiModelProperty(value = "创建时间", name = "createTime")
    private Date createTime;

    @ApiModelProperty(value = "最近编辑时间", name = "updateTime")
    private Date updateTime;
}
