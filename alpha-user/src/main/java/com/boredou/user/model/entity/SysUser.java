package com.boredou.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
