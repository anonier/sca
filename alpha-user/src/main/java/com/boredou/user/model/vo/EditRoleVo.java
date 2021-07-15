package com.boredou.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@ApiModel(value = "EditRoleVo", description = "编辑角色入参对象")
public class EditRoleVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", name = "id", required = true)
    private String id;

    @NotBlank(message = "权限id不能为空")
    @ApiModelProperty(value = "多个权限id,以,分割", name = "ids", required = true)
    private String ids;

}
