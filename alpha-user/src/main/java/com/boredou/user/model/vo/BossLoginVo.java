package com.boredou.user.model.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BossLoginVo {

    @NotBlank(message = "用户账号不能为空")
    String username;
    @NotBlank(message = "用户密码不能为空")
    String password;
    String verifyCode;
}
