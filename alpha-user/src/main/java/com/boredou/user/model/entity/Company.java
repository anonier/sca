package com.boredou.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class Company implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    private String name;

    private String telephone;

    private String address;

    private String officeWebsite;

    private String defaultPassword;

    private BigDecimal balance;

    private Date authDate;

    private String phoneCodeLogin;

    private String dingTalkCodeLogin;

    private String dingTalkQrcodeLogin;

    private Date gmtCreated;
    private Date gmtModified;
}
