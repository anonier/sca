package com.boredou.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class SysBalance implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 公司id
     */
    private String companyId;
    /**
     * 交易类型
     */
    private String transactionType;
    /**
     * 交易金额（元）
     */
    private String transactionAmount;
    /**
     * 申请人
     */
    private String applicant;
    /**
     * 交易状态
     */
    private String status;
    /**
     * 交易时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
