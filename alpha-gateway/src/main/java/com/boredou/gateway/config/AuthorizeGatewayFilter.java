package com.boredou.gateway.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 拦截器
 */
@Component
@Slf4j
public class AuthorizeGatewayFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZE_UID = "uid";

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("come in AuthorizeGatewayFilter：" + new Date());
        log.info("URI：" + exchange.getRequest().getURI());
        //获取request中的uname参数
        if (!exchange.getRequest().getPath().toString().contains("/signIn")
                && !exchange.getRequest().getPath().toString().contains("/dingTalk)")
                && !exchange.getRequest().getPath().toString().contains("/sendDingTalkCode")) {
            String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
            try {
                String key = "user_token:" + Objects.requireNonNull(exchange.getRequest().getCookies().getFirst("uid")).getValue();
                if (exchange.getRequest().getCookies().containsKey(AUTHORIZE_UID)
                        && StringUtils.isNotBlank(authorization)
                        && Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))
                        && authorization.substring(authorization.indexOf(" ")).trim().equals(JSONObject.parseObject(stringRedisTemplate.opsForValue().get(key)).get("access_token"))) {
                    return chain.filter(exchange);
                } else {
                    log.info("用户认证信息不存在");
                    exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
                    return exchange.getResponse().setComplete();
                }
            } catch (Exception e) {
                log.info("用户认证信息不存在");
                exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
                return exchange.getResponse().setComplete();
            }
        }
        //放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        //返回值是过滤器的优先级，越小优先级越高（最小-2147483648，最大2147483648）
        return 0;
    }
}
