package com.boredou.user.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private String name;

    private String company;

    private String employeeId;

    private String position;

    private String department;

    private String rank;

    private String roleId;

    private String phone;

    private String email;

    private Integer qq;

    private Date entryTime;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
