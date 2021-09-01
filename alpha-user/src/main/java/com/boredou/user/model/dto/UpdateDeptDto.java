package com.boredou.user.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateDeptDto {

    private Integer id;
    private Integer sysUserId;
}
