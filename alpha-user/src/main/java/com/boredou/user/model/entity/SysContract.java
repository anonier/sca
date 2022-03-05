package com.boredou.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class SysContract implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;
    /**
     * 合同号
     */
    private String contractId;
    /**
     * 公司id
     */
    private String companyId;
    /**
     * 合同名称
     */
    private String contractName;
    /**
     * 合同类型
     */
    private String type;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 订单状态
     */
    private String status;
    /**
     * 证件状态
     */
    private String certificateStatus;
    /**
     * 交易时间
     */
    private Date createTime;
    /**
     * 剩余时间
     */
    private Date lastTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
