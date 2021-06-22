package com.boredou.user.model.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateDeptVo {
    @NotNull(message = "公司id不能为空")
    private Integer id;
    @NotNull(message = "部门负责人不能为空")
    private Integer sysUserId;

}
