package com.boredou.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@ApiModel(value = "SignInVo", description = "登入入参对象")
public class SignInVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户账号不能为空")
    @ApiModelProperty(value = "用户账号", name = "username", example = "huangfeng", required = true)
    String username;

    @NotBlank(message = "用户密码不能为空")
    @ApiModelProperty(value = "用户密码", name = "password", example = "123", required = true)
    String password;

    String verifyCode;

}
