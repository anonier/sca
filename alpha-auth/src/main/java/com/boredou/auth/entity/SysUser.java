package com.boredou.auth.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class SysUser {

    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String password;
    private String salt;
    private String name;
    private String utype;
    private String birthday;
    private String userpic;
    private String sex;
    private String email;
    private String phone;
    private String company;
    private String status;
    private Date createTime;
    private Date updateTime;

}
