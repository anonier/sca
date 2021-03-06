package com.boredou.common.module.log;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.boredou.common.annotation.DetailLog;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据日志基类，模块使用时继承此类
 *
 * @author yb
 * @since 2021/7/15
 */
public abstract class BaseDataLog {
    /**
     * 注解
     */
    public static final ThreadLocal<DetailLog> DATA_LOG = new ThreadLocal<>();
    /**
     * 切点
     */
    public static final ThreadLocal<JoinPoint> JOIN_POINT = new ThreadLocal<>();
    /**
     * 数据变化 多张表-多条数据
     */
    public static final ThreadLocal<List<DataChange>> DATA_CHANGES = new ThreadLocal<>();
    /**
     * 全部变化对比结果
     */
    public static final ThreadLocal<List<CompareResult>> COMPARE_RESULTS = ThreadLocal.withInitial(ArrayList::new);
    /**
     * 全部变化记录 默认：将[{}]由{}修改为{}
     */
    public static final ThreadLocal<String> LOG_STR = new ThreadLocal<>();

    /**
     * 日志格式
     * 0：数据id
     * 1：字段名
     * 2：字段注释
     * 3：字段旧值
     * 4：字段新值
     * <p>
     * 如：将[{2}]由{3}修改为{4} ==》将[名字]由张三修改为王五
     */
    @Setter
    @Getter
    private String logFormat = "将[{2}]由{3}修改为{4}";


    /**
     * 排除表名
     * 排除表不进行日志记录
     */
    @Setter
    @Getter
    public static List<String> excludeTableNames = new ArrayList<>();

    /**
     * 排除字段名
     * 排除字段不进行对比
     */
    @Setter
    @Getter
    private List<String> excludeFieldNames = new ArrayList<>();

    /**
     * 用于SpEL表达式解析.
     */
    private final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 调用
     *
     * @author yb
     * @since 2021/7/15
     */
    public void transfer() {
        LogData data = new LogData();
        data.setDataChanges(DATA_CHANGES.get());
        data.setCompareResults(COMPARE_RESULTS.get());
        data.setLogStr(LOG_STR.get());
        this.change(DATA_LOG.get(), data);

        DATA_CHANGES.remove();
        COMPARE_RESULTS.remove();
        LOG_STR.remove();
        DATA_LOG.remove();
        JOIN_POINT.remove();
    }

    /**
     * <p>
     * 是否忽略
     * </p>
     *
     * @return {@link DetailLog}
     * @return boolean
     * @author yb
     * @since 2021/7/15
     */
    public boolean isIgnore(DetailLog dataLog) {
        return false;
    }


    /**
     * 增加排除表名
     *
     * @param tableName tableName
     * @return {@link BaseDataLog}
     * @author yb
     * @since 2021/7/15
     */
    public BaseDataLog addExcludeTableName(String tableName) {
        excludeTableNames.add(tableName);
        return this;
    }

    /**
     * 增加排除字段名
     *
     * @param fieldName fieldName
     * @return {@link BaseDataLog}
     * @author yb
     * @since 2021/7/15
     */
    public BaseDataLog addExcludeFieldName(String fieldName) {
        this.excludeFieldNames.add(fieldName);
        return this;
    }


    /**
     * 设置
     * 可在方法内设置 数据字典是否翻译 返回记录文字描述格式
     *
     * @author yb
     * @since 2021/7/15
     */
    public abstract void setting();

    /**
     * <p>
     * 变化
     * </p>
     *
     * @param dataLog 注解
     * @param data    日志数据
     * @author Tophua
     * @since 2020/8/11
     */
    public abstract void change(DetailLog dataLog, LogData data);

    /**
     * 相同类对比
     *
     * @param obj1 obj1
     * @param obj2 obj2
     * @return {@link List<CompareResult>}
     * @author yb
     * @since 2021/7/15
     */
    public List<CompareResult> sameClazzDiff(Object obj1, Object obj2) {
        List<CompareResult> results = new ArrayList<>();
        Field[] obj1Fields = obj1.getClass().getDeclaredFields();
        Field[] obj2Fields = obj2.getClass().getDeclaredFields();
        Long id = null;
        for (int i = 0; i < obj1Fields.length; i++) {
            obj1Fields[i].setAccessible(true);
            obj2Fields[i].setAccessible(true);
            Field field = obj1Fields[i];
            if (this.excludeFieldNames.contains(field.getName())) {
                continue;
            }
            try {
                Object value1 = obj1Fields[i].get(obj1);
                Object value2 = obj2Fields[i].get(obj2);
                if ("id".equals(field.getName())) {
                    id = Long.parseLong(value1.toString());
                }
                if (ObjectUtil.isEmpty(value1) || StrUtil.isBlank(value1.toString())) {
                    value1 = null;
                }
                if (ObjectUtil.isEmpty(value2) || StrUtil.isBlank(value2.toString())) {
                    value2 = null;
                }
                if (!ObjectUtil.equal(value1, value2)) {
                    if (ObjectUtil.isNotEmpty(value1) && ObjectUtil.isNotEmpty(value2) && ObjectUtil.equal(value1.toString().trim(), value2.toString().trim())) {
                        continue;
                    }
                    CompareResult r = new CompareResult();
                    r.setId(id);
                    r.setFieldName(field.getName());
                    // 获取注释
                    r.setFieldComment(field.getName());
//                    ApiModelProperty property = field.getAnnotation(ApiModelProperty.class);
//                    if (property != null && StrUtil.isNotBlank(property.value())) {
//                        r.setFieldComment(property.value());
//                    }
                    r.setOldValue(value1);
                    r.setNewValue(value2);
                    results.add(r);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        COMPARE_RESULTS.get().addAll(results);
        return results;
    }

    /**
     * 根据SpEL表达式和切面返回方法参数值
     *
     * @param spEl  SpEL表达式
     * @param clazz 返回数据类型
     * @return T
     * @author yb
     * @date:2021-7-2
     */
    protected <T> T getValueBySpEl(String spEl, Class<T> clazz) {
        if (!spEl.contains("#")) {
            //注解的值非SPEL表达式，直接解析就好
            return JSONUtil.toBean(spEl, clazz);
        }
        JoinPoint joinPoint = JOIN_POINT.get();
        // 通过joinPoint获取被注解方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // 使用spring的DefaultParameterNameDiscoverer获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        if (ObjectUtil.isEmpty(paramNames) || paramNames.length == 0) {
            return null;
        }
        // 解析过后的Spring表达式对象
        Expression expression = parser.parseExpression(spEl);
        // spring的表达式上下文对象
        EvaluationContext context = new StandardEvaluationContext(joinPoint);
        // 通过joinPoint获取被注解方法的形参
        Object[] args = joinPoint.getArgs();
        // 给上下文赋值
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        // 表达式从上下文中计算出实际参数值
        /*如:
            @annotation(id="#student.name")
             method(Student student)
             那么就可以解析出方法形参的某属性值，return “xiaoming”;
          */
        return expression.getValue(context, clazz);
    }
}
