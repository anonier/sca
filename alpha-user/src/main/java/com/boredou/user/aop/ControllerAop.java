package com.boredou.user.aop;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.boredou.common.annotation.DetailLog;
import com.boredou.user.model.entity.SysLog;
import com.boredou.user.model.log.BaseDataLog;
import com.boredou.user.model.log.DataChange;
import com.boredou.user.model.vo.SignInVo;
import com.boredou.user.service.SysLogService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yb
 * @since 2021-6-28
 */
@Slf4j
@Aspect
@Component
public class ControllerAop {

    @Resource
    SysLogService sysLogService;
    @Resource
    BaseDataLog baseDataLog;

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(* com.boredou.user.controller.*.*(..)) && !@annotation(com.boredou.common.annotation.DetailLog)")
    public void controllerLog() {
    }

    @PostConstruct
    public void init() {
        baseDataLog.setting();
    }

    @Before("@annotation(dataLog)")
    public void before(JoinPoint joinPoint, DetailLog dataLog) {
        // 使用 ThreadLocal 记录一次操作
        BaseDataLog.DATA_CHANGES.set(new LinkedList<>());
        BaseDataLog.JOIN_POINT.set(joinPoint);
        BaseDataLog.DATA_LOG.set(dataLog);
        if (baseDataLog.isIgnore(dataLog)) {
            BaseDataLog.DATA_CHANGES.set(null);
        }
        startTime.set(System.currentTimeMillis());
    }

    @AfterReturning(value = "@annotation(dataLog)", returning = "rtv")
    public void after(JoinPoint joinPoint, DetailLog dataLog, Object rtv) throws ClassNotFoundException {
        HttpServletRequest request = doLog(rtv, joinPoint, null);
        String apiOperationValue = getApiOperation(joinPoint, null);
        List<DataChange> list = BaseDataLog.DATA_CHANGES.get();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        list.forEach(change -> {
            List<?> oldData = change.getOldData();
            if (CollUtil.isEmpty(oldData)) {
                return;
            }
            List<Long> ids = oldData.stream()
                    .map(o -> ReflectUtil.invoke(o, "getId").toString())
                    .filter(ObjectUtil::isNotNull)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            SqlSession sqlSession = change.getSqlSessionFactory().openSession();
            try {
                Map<String, Object> map = new HashMap<>(1);
                map.put(Constants.WRAPPER, Wrappers.query().in("id", ids));
                List<?> newData = sqlSession.selectList(change.getSqlStatement(), map);
                change.setNewData(Optional.ofNullable(newData).orElse(new ArrayList<>()));
            } finally {
                SqlSessionUtils.closeSqlSession(sqlSession, change.getSqlSessionFactory());
            }
            System.out.println("oldData:" + JSONUtil.toJsonStr(change.getOldData()));
            System.out.println("newData:" + JSONUtil.toJsonStr(change.getNewData()));
        });
        sysLogService.save(SysLog.builder().ip(request.getRemoteAddr())
                .name(joinPoint.getSignature().getName().equals("signIn") ? BeanUtil.copyProperties(Arrays.stream(joinPoint.getArgs()).findFirst().orElse(null), SignInVo.class).getUsername() : request.getRemoteUser())
                .operatorTime(new Date())
                .operatorName(apiOperationValue)
                .operatorStatus("1")
                .operatorModule(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName())
                .detail(this.compareAndTransfer(list)).build());
    }

    /**
     * 数据对比
     *
     * @param list list
     * @return void
     */
    public String compareAndTransfer(List<DataChange> list) {
        StringBuilder sb = new StringBuilder();
        StringBuilder rsb = new StringBuilder();
        list.forEach(change -> {
            List<?> oldData = change.getOldData();
            List<?> newData = change.getNewData();
            // 更新前后数据量不对必定是删除（逻辑删除）不做处理
            if (newData == null) {
                return;
            }
            if (oldData == null) {
                return;
            }
            if (oldData.size() != newData.size()) {
                return;
            }
            // 按id排序
            oldData.sort(Comparator.comparingLong(d -> Long.parseLong(ReflectUtil.invoke(d, "getId").toString())));
            newData.sort(Comparator.comparingLong(d -> Long.parseLong(ReflectUtil.invoke(d, "getId").toString())));

            for (int i = 0; i < oldData.size(); i++) {
                final int[] finalI = {0};
                baseDataLog.sameClazzDiff(oldData.get(i), newData.get(i)).forEach(r -> {
                    String oldV = r.getOldValue() == null ? "无" : r.getOldValue().toString();
                    String newV = r.getNewValue() == null ? "无" : r.getNewValue().toString();
                    if (ObjectUtil.equal(oldV.trim(), newV.trim())) {
                        return;
                    }
                    if (finalI[0] == 0) {
                        sb.append(StrUtil.LF);
                        sb.append(StrUtil.format("修改表：【{}】", change.getTableName()));
                        sb.append(StrUtil.format("id：【{}】", r.getId()));
                    }
                    sb.append(StrUtil.LF);
                    rsb.append(StrUtil.LF);
                    sb.append(StrUtil.format("把字段[{}]从[{}]改为[{}]",
                            r.getFieldName(), r.getOldValue(), r.getNewValue()));
                    rsb.append(StrUtil.indexedFormat(baseDataLog.getLogFormat(),
                            r.getId(), r.getFieldName(), r.getFieldComment(),
                            oldV, newV));
                    finalI[0]++;
                });
            }
        });
        if (sb.length() > 0) {
            sb.deleteCharAt(0);
            rsb.deleteCharAt(0);
        }
        BaseDataLog.DATA_CHANGES.set(list);
        BaseDataLog.LOG_STR.set(rsb.toString());
        baseDataLog.transfer();
        return sb.toString();
    }

    @Around("controllerLog()")
    public Object doControllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
        Object obj = joinPoint.proceed();
        HttpServletRequest request = doLog(obj, null, joinPoint);
        String apiOperationValue = getApiOperation(null, joinPoint);

        sysLogService.save(SysLog.builder().ip(request.getRemoteAddr())
                .name(joinPoint.getSignature().getName().equals("signIn") ? BeanUtil.copyProperties(Arrays.stream(joinPoint.getArgs()).findFirst().orElse(null), SignInVo.class).getUsername() : request.getRemoteUser())
                .operatorTime(new Date())
                .operatorName(apiOperationValue)
                .operatorStatus("1")
                .operatorModule(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName())
                .detail("").build());
        return obj;
    }

    public HttpServletRequest doLog(Object obj, JoinPoint jp, ProceedingJoinPoint pjp) {
        Signature signature;
        Object[] arrays;
        if (ObjectUtil.isEmpty(jp)) {
            signature = pjp.getSignature();
            arrays = pjp.getArgs();
        } else {
            signature = jp.getSignature();
            arrays = jp.getArgs();
        }
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        log.info("URL : " + request.getRequestURL().toString());
        log.info("HTTP_METHOD : " + request.getMethod());
        log.info("IP : " + request.getRemoteAddr());
        log.info("CLASS_METHOD : " + signature.getDeclaringTypeName() + "." + signature.getName());
        log.info("ARGS : " + Arrays.toString(arrays));
        log.info("RESPONSE : " + obj);
        log.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
        return request;
    }

    /**
     * 通过反射获取方法上@ApiOperation注解的value值
     *
     * @return String
     */
    public String getApiOperation(JoinPoint jp, ProceedingJoinPoint pjp) throws ClassNotFoundException {
        Signature signature;
        if (ObjectUtil.isEmpty(jp)) {
            signature = pjp.getSignature();
        } else {
            signature = jp.getSignature();
        }
        Class<?> cl = Class.forName(signature.getDeclaringTypeName());
        Method[] methods = cl.getDeclaredMethods();
        String apiOperationValue = null;
        for (Method method : methods) {
            ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
            if (method.getName().equals(signature.getName())) {
                apiOperationValue = apiOperation.value();
            }
        }
        return apiOperationValue;
    }

}
