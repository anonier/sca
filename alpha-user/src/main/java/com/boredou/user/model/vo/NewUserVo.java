package com.boredou.user.model.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
public class NewUserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "账号不能为空")
    private String username;

    private String password;

    private String salt;

    private String name;

    private String userpic;

    private String utype;

    private Date birthday;

    private String sex;

    @Email
    private String email;

    private String phone;

    private Integer qq;

    private Integer status;

    private Date createTime;

    private Date updateTime;

}
