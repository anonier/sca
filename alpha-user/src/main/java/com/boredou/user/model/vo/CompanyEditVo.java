package com.boredou.user.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CompanyEditVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String telephone;

    private String address;

    private String officeWebsite;

    private String defaultPassword;

    private Date authDate;

    private String phoneLogin;

    private String dingTalkLogin;
}
