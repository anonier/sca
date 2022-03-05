package com.boredou.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class SysOrder implements Serializable {
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
     * 订单类型
     */
    private String type;
    /**
     * 类目
     */
    private String category;
    /**
     * 商品
     */
    private String produce;
    /**
     * 订单账号
     */
    private String orderAccount;
    /**
     * 订单面值
     */
    private String orderFaceValue;
    /**
     * 成功面值
     */
    private String successFaceValue;
    /**
     * 成功金额
     */
    private String successAmount;
    /**
     * 订单状态
     */
    private String status;
    /**
     * 操作
     */
    private String operator;
    /**
     * 交易时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
