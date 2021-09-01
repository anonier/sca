package com.boredou.user.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chenaiguo
 * 钉钉用户
 * 2019年2月27日下午4:22:34
 */
@Data
@Builder
public class DingTalkInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 钉钉用户表id
     */
    private Integer dUserId;

    /**
     * 钉钉用户id
     */
    private String userId;

    /**
     * 钉钉unionId
     */
    private String unionId;
}
