package com.boredou.user.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class CompanyEditDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String telephone;

    private String address;

    private String officeWebsite;

    private String defaultPassword;

    private Date authDate;

    private String phoneCodeLogin;

    private String dingTalkCodeLogin;

    private String dingTalkQrcodeLogin;
}
