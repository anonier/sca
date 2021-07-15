package com.boredou.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@ApiModel(value = "NewRoleVo", description = "添加角色入参对象")
public class NewRoleVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "角色名称不能为空")
    @ApiModelProperty(value = "角色名称", name = "roleName", required = true)
    private String roleName;

    @NotBlank(message = "角色权限id不能为空")
    @ApiModelProperty(value = "多个角色权限id,以,分割", name = "ids", required = true)
    private String ids;

}
