package com.boredou.user.filter.RsaFilter;


import com.alibaba.fastjson.JSONObject;
import com.boredou.common.enums.BizException;
import com.boredou.common.util.AESUtil;
import com.boredou.common.util.RsaUtil;
import com.boredou.user.model.result.RsaResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Rsa+Aes接口加密拦截器
 *
 * @author yb
 * @since 2021-8-31
 */
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "RsaFilter")
public class RsaFilter implements Filter {

    private static String rsaPubKey;
    private static String environment;
    private static String rsaPriKey;

    @Override
    public void init(FilterConfig filterConfig) {
        environment = filterConfig.getInitParameter("environment");
        rsaPubKey = filterConfig.getInitParameter("rsaPubKey");
        rsaPriKey = filterConfig.getInitParameter("rsaPriKey");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (environment.equals("Produce")) {
            RsaHttpServletRequestWrapper wrapRequest;
            RsaHttpServletResponseWrapper wrapResponse = new RsaHttpServletResponseWrapper((HttpServletResponse) response);
            if (StringUtils.isNotBlank(request.getContentType()) && "application/json".equals(request.getContentType())) {
                wrapRequest = new RsaHttpServletRequestWrapper((HttpServletRequest) request, rsaPriKey);
                chain.doFilter(wrapRequest, wrapResponse);
            } else {
                chain.doFilter(request, wrapResponse);
            }
            String content = wrapResponse.getContent();
            Map<String, String> map = AESUtil.encrypt(content);
            try {
                String aesKey = Base64.encodeBase64String(RsaUtil.encryptByPublicKey(map.get("aesKey").getBytes(StandardCharsets.UTF_8), rsaPubKey));
                String responseBody = JSONObject.toJSONString(RsaResult.builder().data(map.get("data")).aesKey(aesKey).build());
                log.info("加密返回数据为: " + responseBody);
                response.setContentLength(-1);
                PrintWriter out = response.getWriter();
                out.write(responseBody);
                out.flush();
                out.close();
            } catch (Exception e) {
                throw new BizException("加密失败");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
