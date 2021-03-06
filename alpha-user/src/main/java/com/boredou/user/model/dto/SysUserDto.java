package com.boredou.user.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class SysUserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String username;

    private String password;

    private String salt;

    private String name;

    private String userpic;

    private String utype;

    private Date birthday;

    private String sex;

    private String email;

    private String phone;

    private Integer qq;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
