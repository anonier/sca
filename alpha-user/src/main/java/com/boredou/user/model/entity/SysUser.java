package com.boredou.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    private String username;

    private String password;

    private String name;

    private String employeeId;

    private String position;

    private String department;

    private String rank;

    private String roleId;

    private String phone;

    private String email;

    private Integer qq;

    private Date entryTime;

    private String company;

    private String status;

    private Date createTime;

    private Date updateTime;
}
