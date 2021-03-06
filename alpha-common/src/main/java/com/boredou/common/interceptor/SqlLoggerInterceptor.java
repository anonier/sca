package com.boredou.common.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.boredou.common.module.log.BaseDataLog;
import com.boredou.common.module.log.DataChange;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.*;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.*;

/**
 * sql拦截器
 *
 * @author yb
 * @since 2021/7/2
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),//需要代理的对象和方法
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})//需要代理的对象和方法
})
public class SqlLoggerInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            //获得查询方法的参数，比如selectById(Integer id,String name)，那么就可以获取到四个参数分别是：
            //{id:1,name:"user1",param1:1,param2:"user1"}
            parameter = invocation.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();//获得mybatis的*mapper.xml文件中映射的方法，如：com.best.dao.UserMapper.selectById

        //将参数和映射文件组合在一起得到BoundSql对象
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);

        //获取配置信息
        Configuration configuration = mappedStatement.getConfiguration();
        log.info("/*---------------java:" + sqlId + "[begin]---------------*/");

        //通过配置信息和BoundSql对象来生成带值得sql语句
        String sql = geneSql(configuration, boundSql);
        log.info("==> sql:" + sql);

        //SELECT和其他分别处理
        if (SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
            return doProceed(invocation, sqlId);
        } else {
            // 使用mybatis-plus 工具解析sql获取表名
            Collection<String> tables = new TableNameParser(sql).tables();
            if (CollectionUtils.isEmpty(tables)) {
                return doProceed(invocation, sqlId);
            }
            String tableName = tables.iterator().next();
            // 排除表名判断
            if (BaseDataLog.excludeTableNames.contains(tableName)) {
                return doProceed(invocation, sqlId);
            }
            // 使用mybatis-plus 工具根据表名找出对应的实体类
            TableInfo tableInfo = Optional.ofNullable(TableInfoHelper.getTableInfo(tableName))
                    .orElse(new TableInfo(null));
            Class<?> entityType = tableInfo.getEntityType();
            DataChange change = new DataChange();
            change.setTableName(tableName);
            change.setEntityType(entityType);
            // 设置sql用于执行完后查询新数据
            String selectSql = "AND " + sql.substring(sql.lastIndexOf("WHERE") + 5);
            // 同表对同条数据操作多次只进行一次对比
            if (BaseDataLog.DATA_CHANGES.get().stream().anyMatch(c -> tableName.equals(c.getTableName())
                    && selectSql.equals(c.getWhereSql()))) {
                return doProceed(invocation, sqlId);
            }
            change.setWhereSql(selectSql);
            Map<String, Object> map = new HashMap<>(1);
            map.put(Constants.WRAPPER, Wrappers.query().eq("1", 1).last(selectSql));
            // 查询更新前数据
            SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(entityType);
            change.setSqlSessionFactory(sqlSessionFactory);
            change.setSqlStatement(tableInfo.getSqlStatement(SqlMethod.SELECT_LIST.getMethod()));
            SqlSession sqlSession = sqlSessionFactory.openSession();
            if (SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType())) {
                change.setSqlStatement(mappedStatement.getSqlCommandType().name());
                change.setNewData(new ArrayList<String>() {
                    {
                        add("插入表: " + change.getTableName() + " 信息: " + sql.substring(sql.indexOf("(") + 1, sql.indexOf(")")) + ":" + sql.substring(sql.indexOf("VALUES") + 6).trim());
                    }
                });
                BaseDataLog.DATA_CHANGES.get().add(change);
                return doProceed(invocation, sqlId);
            }
            if (SqlCommandType.DELETE.equals(mappedStatement.getSqlCommandType())) {
                change.setSqlStatement(mappedStatement.getSqlCommandType().name());
                change.setNewData(new ArrayList<String>() {
                    {
                        add("删除表: " + change.getTableName() + " WHERE: " + sql.substring(sql.indexOf("WHERE") + 5).trim());
                    }
                });
                BaseDataLog.DATA_CHANGES.get().add(change);
                return doProceed(invocation, sqlId);
            }
            try {
                List<?> oldData = sqlSession.selectList(change.getSqlStatement(), map);
                change.setOldData(Optional.ofNullable(oldData).orElse(new ArrayList<>()));
            } finally {
                SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory);
            }
            BaseDataLog.DATA_CHANGES.get().add(change);
        }
        return doProceed(invocation, sqlId);
    }

    private Object doProceed(Invocation invocation, String sqlId) throws InvocationTargetException, IllegalAccessException {
        long start = System.currentTimeMillis();
        Object o = invocation.proceed();
        log.info("<== sql执行历时：" + (System.currentTimeMillis() - start) + "毫秒");
        log.info("/*---------------java:" + sqlId + "[finish]---------------*/");
        return o;
    }

    /**
     * 如果是字符串对象则加上单引号返回，如果是日期则也需要转换成字符串形式，如果是其他则直接转换成字符串返回。
     *
     * @param obj Object
     * @return String
     */
    private static String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        } else {
            if (ObjectUtil.isNotEmpty(obj)) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    /**
     * 生成对应的带有值得sql语句
     *
     * @param configuration configuration
     * @param boundSql      sql
     * @return String
     */
    private static String geneSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();//获得参数对象，如{id:1,name:"user1",param1:1,param2:"user1"}
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();//获得映射的对象参数
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");//获得带问号的sql语句
        if (parameterMappings.size() > 0 && ObjectUtil.isNotEmpty(parameterObject)) {//如果参数个数大于0且参数对象不为空，说明该sql语句是带有条件的
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {//检查该参数是否是一个参数
                //getParameterValue用于返回是否带有单引号的字符串，如果是字符串则加上单引号
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));//如果是一个参数则只替换一次，将问号直接替换成值

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);//将映射文件的参数和对应的值返回，比如：id，name以及对应的值。
                for (ParameterMapping parameterMapping : parameterMappings) {//遍历参数，如:id,name等
                    String propertyName = parameterMapping.getProperty();//获得属性名，如id,name等字符串
                    if (metaObject.hasGetter(propertyName)) {//检查该属性是否在metaObject中
                        Object obj = metaObject.getValue(propertyName);//如果在metaObject中，那么直接获取对应的值
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));//然后将问号?替换成对应的值。
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println(properties);
    }
}
