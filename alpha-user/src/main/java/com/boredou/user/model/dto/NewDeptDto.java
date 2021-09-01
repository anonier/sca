package com.boredou.user.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class NewDeptDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Integer pId;
    private Integer companyId;
    private Integer sysUserId;
}
