package com.boredou.user.aop;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.boredou.common.annotation.DetailLog;
import com.boredou.common.enums.BizException;
import com.boredou.common.module.log.BaseDataLog;
import com.boredou.common.module.log.DataChange;
import com.boredou.user.model.entity.SysLog;
import com.boredou.user.model.entity.SysMenu;
import com.boredou.user.model.vo.SignInVo;
import com.boredou.user.service.SysLogService;
import com.boredou.user.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
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
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 接口aop拦截
 *
 * @author yb
 * @since 2021-6-28
 */
@Slf4j
@Aspect
@Component
public class LogAop {

    @Resource
    private SysLogService sysLogService;
    @Resource
    private BaseDataLog baseDataLog;
    @Resource
    private SysMenuService sysMenuService;

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(* com.boredou.user.controller.*.*(..)) " +
            "&& @annotation(com.boredou.common.annotation.SysLog)" +
            "&&!@annotation(com.boredou.common.annotation.DetailLog)")
    public void SysLog() {
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
    public void after(JoinPoint joinPoint, DetailLog dataLog, Object rtv) {
        List<DataChange> list = BaseDataLog.DATA_CHANGES.get();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        list.forEach(change -> {
            List<?> oldData = null;
            if (!change.getSqlStatement().equals(SqlCommandType.INSERT.name()) && !change.getSqlStatement().equals(SqlCommandType.DELETE.name())) {
                oldData = change.getOldData();
            } else {
                change.setOldData(change.getNewData());
                change.setNewData(new ArrayList<String>() {
                    {
                        add(change.getNewData().toString());
                    }
                });
            }
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
        });
        String detail = this.compareAndTransfer(list);
        saveAndLog(rtv, joinPoint, null, StringUtils.isBlank(detail) ? "数据无变动" : detail);
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
            if (CollUtil.isEmpty(newData)) {
                return;
            }
            if (CollUtil.isEmpty(oldData)) {
                return;
            }
            if (oldData.size() != newData.size()) {
                return;
            }
            // 按id排序
            oldData.sort(Comparator.comparingLong(d -> Long.parseLong(ReflectUtil.invoke(d, "getId").toString())));
            newData.sort(Comparator.comparingLong(d -> Long.parseLong(ReflectUtil.invoke(d, "getId").toString())));

            if (change.getSqlStatement().equals(SqlCommandType.DELETE.name()) || change.getSqlStatement().equals(SqlCommandType.INSERT.name())) {
                sb.append(change.getNewData().toString());
                rsb.append(change.getNewData().toString());
                return;
            }

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

    @Around("SysLog()")
    public Object doControllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
        Object obj = joinPoint.proceed();
        saveAndLog(obj, null, joinPoint, null);
        return obj;
    }

    /**
     * 获取操作模块
     *
     * @param request {@link HttpServletRequest}
     * @return String
     */
    private String getOperatorModule(HttpServletRequest request) {
        try {
            StringJoiner operatorModule = new StringJoiner("-");
            if (Optional.ofNullable(request.getHeader("referer")).isPresent() && !(new URL(request.getHeader("referer")).getPath()).startsWith("/login")) {
                String path = new URL(request.getHeader("referer")).getPath().substring(1);
                String[] parts = path.split("/");
                parts[0] = "/" + parts[0];
                for (String part : parts) {
                    operatorModule.add(sysMenuService.getOne(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getUrl, part)).getTitle());
                }
            } else if (Optional.ofNullable(request.getHeader("referer")).isPresent() && (new URL(request.getHeader("referer")).getPath()).startsWith("/login")) {
                operatorModule.add("登入");
            } else {
                operatorModule.add("接口测试");
            }
            return operatorModule.toString();
        } catch (Exception e) {
            throw new BizException("获取URL失败");
        }
    }

    /**
     * @param obj    返回内容
     * @param jp     返回信息
     * @param pjp    返回信息
     * @param detail 用户操作详情
     */
    @SneakyThrows
    private void saveAndLog(Object obj, JoinPoint jp, ProceedingJoinPoint pjp, String detail) {
        Signature signature = ObjectUtil.isEmpty(jp) ? pjp.getSignature() : jp.getSignature();
        Object[] arrays = ObjectUtil.isEmpty(jp) ? pjp.getArgs() : jp.getArgs();
        Object[] args = ObjectUtil.isEmpty(jp) ? pjp.getArgs() : jp.getArgs();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        //通过反射获取方法上@ApiOperation注解的value值
        Class<?> cl = Class.forName(signature.getDeclaringTypeName());
        Method[] methods = cl.getDeclaredMethods();
        String apiOperationValue = "该接口未注明事件类型";
        for (Method method : methods) {
            if (method.getName().equals(signature.getName())) {
                ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
                apiOperationValue = apiOperation.value();
                break;
            }
        }

        String ip = Optional.ofNullable(request.getHeader("x-forwarded-for")).isPresent() ? request.getHeader("x-forwarded-for").contains(",") ? request.getHeader("x-forwarded-for").substring(0, request.getHeader("x-forwarded-for").indexOf(",")) : request.getHeader("x-forwarded-for") : "127.0.0.1";
        sysLogService.saveLog(SysLog.builder().ip(ip)
                .name(signature.getName().equals("signIn") ? BeanUtil.copyProperties(Arrays.stream(args).findFirst().orElse(null), SignInVo.class).getUsername() : request.getRemoteUser())
                .operatorTime(new Date())
                .operatorName(apiOperationValue)
                .operatorStatus("1")
                .module(Class.forName(signature.getDeclaringTypeName()).getAnnotation(Api.class).tags()[0])
                .operatorModule(getOperatorModule(request))
                .detail(detail).build());

        log.info("URL : " + request.getRequestURL().toString());
        log.info("HTTP_METHOD : " + request.getMethod());
        log.info("IP : " + ip);
        log.info("CLASS_METHOD : " + signature.getDeclaringTypeName() + "." + signature.getName());
        log.info("ARGS : " + Arrays.toString(arrays));
        log.info("RESPONSE : " + obj);
        log.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
    }
}
