package com.boredou.auth.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Date;

/**
 * 自定义封装jwt信息
 *
 * @author yb
 * @since 2021/5/27
 */
public class UserJwt extends User {
    @Getter
    private String id;
    @Getter
    private String name;
    @Getter
    private String employeeId;
    @Getter
    private String position;
    @Getter
    private String department;
    @Getter
    private String rank;
    @Getter
    private String phone;
    @Getter
    private String email;
    @Getter
    private Integer qq;
    @Getter
    private String dingTalkBindStatus;
    @Getter
    private Date entryTime;
    @Getter
    private String company;

    public UserJwt(String id, String name, String employeeId, String position
            , String department, String rank, String phone
            , String email, Integer qq, Date entryTime, String company, String dingTalkBindStatus
            , String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.name = name;
        this.employeeId = employeeId;
        this.position = position;
        this.department = department;
        this.rank = rank;
        this.phone = phone;
        this.email = email;
        this.qq = qq;
        this.dingTalkBindStatus = dingTalkBindStatus;
        this.entryTime = entryTime;
        this.company = company;
    }
}
