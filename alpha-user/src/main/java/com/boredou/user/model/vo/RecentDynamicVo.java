package com.boredou.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RecentDynamicVo", description = "最近动态入参对象")
public class RecentDynamicVo extends PageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "开始时间", name = "startDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @ApiModelProperty(value = "结束时间", name = "finishDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishDate;

    @ApiModelProperty(value = "模块id", name = "id")
    private String id;
}
