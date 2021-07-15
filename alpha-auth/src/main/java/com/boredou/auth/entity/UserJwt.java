package com.boredou.auth.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Date;

@Data
public class UserJwt extends User {

    private String id;

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

    public UserJwt(String id, String name, String employeeId, String position
            , String department, String rank, String roleId, String phone
            , String email, Integer qq, Date entryTime, String company
            , String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.name = name;
        this.employeeId = employeeId;
        this.position = position;
        this.department = department;
        this.rank = rank;
        this.roleId = roleId;
        this.phone = phone;
        this.email = email;
        this.qq = qq;
        this.entryTime = entryTime;
        this.company = company;
    }

}
