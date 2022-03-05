package com.boredou.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "RechargeVo", description = "充值入参对象")
public class RechargeVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    @ApiModelProperty(value = "公司id", name = "id", example = "1")
    String id;

    @NotNull
    @ApiModelProperty(value = "金额", name = "amount")
    BigDecimal amount;
}
