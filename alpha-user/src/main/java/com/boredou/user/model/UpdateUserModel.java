package com.boredou.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Administrator
 *swagger 注释模板
 */
@Data
//实体路径和描述
@ApiModel(value = "com.boredou.user.entity.UpdateUserModel",description = "接收更新用户数据")
public class UpdateUserModel {
    //字段描述
    @ApiModelProperty(value = "用户id")
    private String id;
    @ApiModelProperty(value = "账号")
    private String username;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "性别")
    private String sex;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "地区")
    private String createWhere;
    @ApiModelProperty(value = "邮件")
    private String email;
    @ApiModelProperty(value = "昵称")
    private String nickName;
}