package com.boredou.user.model.log;

import lombok.Data;

/**
 * 对比两个对象结果
 *
 * @author yb
 * @since 2021/7/15
 */
@Data
public class CompareResult {
    /**
     * 主键id值
     */
    private Long id;
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 字段注释
     */
    private String fieldComment;
    /**
     * 字段旧值
     */
    private Object oldValue;
    /**
     * 字段新值
     */
    private Object newValue;
}