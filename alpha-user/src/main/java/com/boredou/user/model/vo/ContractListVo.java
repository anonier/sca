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
@ApiModel(value = "ContractListVo", description = "合同列表入参对象")
public class ContractListVo extends PageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    @ApiModelProperty(value = "公司id", name = "id", example = "1")
    private String id;

    @ApiModelProperty(value = "合同号", name = "contractId")
    private String contractId;

    @ApiModelProperty(value = "合同名称", name = "contractName")
    private String contractName;

    @ApiModelProperty(value = "合同类型", name = "type")
    private String type;

    @ApiModelProperty(value = "合同状态", name = "status")
    private String status;

    @ApiModelProperty(value = "证件状态", name = "certificateStatus")
    private String certificateStatus;

    @ApiModelProperty(value = "开始时间", name = "startDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @ApiModelProperty(value = "结束时间", name = "finishDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishDate;
}
