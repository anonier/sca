package com.boredou.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "UpdateDeptVo", description = "编辑部门入参对象")
public class UpdateDeptVo {

    @NotNull(message = "公司id不能为空")
    @ApiModelProperty(value = "公司id", name = "id", required = true)
    private String id;

    @NotNull(message = "部门负责人不能为空")
    @ApiModelProperty(value = "部门负责人id", name = "sysUserId", required = true)
    private String sysUserId;
}
