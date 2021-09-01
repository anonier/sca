package com.boredou.common.module.log;

import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

@Data
public class DataChange {
    /**
     * sqlSessionFactory
     */
    private SqlSessionFactory sqlSessionFactory;
    /**
     * sqlStatement
     */
    private String sqlStatement;
    /**
     * 表名
     */
    private String tableName;
    /**
     * where 条件
     */
    private String whereSql;
    /**
     * 对应实体类
     */
    private Class<?> entityType;
    /**
     * 更新前数据
     */
    private List<?> oldData;
    /**
     * 更新后数据
     */
    private List<?> newData;

}
