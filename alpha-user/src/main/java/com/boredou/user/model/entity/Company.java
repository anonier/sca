package com.boredou.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Company implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    private String name;

    private String telephone;

    private String address;

    private String officeWebsite;

    private String defaultPassword;

    private Date authDate;

    private String phoneLogin;

    private String dingtalkLogin;

    private Date gmtCreated;

    private Date gmtModified;

}
