package com.boredou.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FlowDetailVo", description = "收支明细入参对象")
public class FlowDetailVo extends PageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    @ApiModelProperty(value = "公司id", name = "id", example = "1")
    String id;

    @ApiModelProperty(value = "开始时间", name = "startDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @ApiModelProperty(value = "结束时间", name = "finishDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishDate;

    @ApiModelProperty(value = "订单号", name = "OrderId", example = "1111111111")
    String OrderId;

    @ApiModelProperty(value = "交易类型", name = "type")
    String transactionType;
}
