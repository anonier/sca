package com.boredou.user.model.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NewDeptVo {
    @NotBlank(message = "部门名称不能为空")
    private String name;
    private Integer pId;
    @NotNull(message = "公司id不能为空")
    private Integer companyId;

}
