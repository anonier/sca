package com.boredou.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "NewDeptVo", description = "添加部门入参对象")
public class NewDeptVo {

    @NotBlank(message = "部门名称不能为空")
    @ApiModelProperty(value = "部门名称", name = "name", required = true)
    private String name;

    @ApiModelProperty(value = "上级部门id", name = "pId")
    private Integer pId;

    @NotNull(message = "公司id不能为空")
    @ApiModelProperty(value = "公司id", name = "companyId", required = true)
    private Integer companyId;

}
