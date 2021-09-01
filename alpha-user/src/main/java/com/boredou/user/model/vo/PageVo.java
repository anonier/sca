package com.boredou.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "NewUserVo", description = "添加用户入参对象")
public class PageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "当前页数不能为空")
    @ApiModelProperty(value = "当前页数", name = "current", required = true)
    private Long current;

    @NotNull(message = "单页数据量不能为空")
    @ApiModelProperty(value = "单页数据量", name = "size", required = true)
    private Long size;
}
