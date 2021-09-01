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

    @ApiModelProperty(value = "用户账号", name = "username", example = "huangfeng")
    String username;

    @ApiModelProperty(value = "用户密码", name = "password")
    String password;

    @ApiModelProperty(value = "验证码", name = "code")
    String code;

    @NotBlank(message = "登入类型不能为空")
    @ApiModelProperty(value = "登入类型", name = "type", required = true, example = "password,dingTalk_code,dingTalk_qrcode,sms_code")
    String type;
}
