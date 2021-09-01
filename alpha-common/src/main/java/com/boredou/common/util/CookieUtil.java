package com.boredou.common.util;

import com.boredou.common.enums.enums.EnumCookie;
import com.boredou.common.enums.enums.EnumPunctuation;
import org.springframework.util.Base64Utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Cookie操作工具
 *
 * @author yb
 * @since 2021-6-28
 */
public class CookieUtil {

    /**
     * 设置cookie
     *
     * @param response {@link HttpServletResponse}
     * @param name     cookie名字
     * @param value    cookie值
     * @param maxAge   cookie生命周期 以秒为单位
     */
    public static void addCookie(HttpServletResponse response, String domain, String path, String name,
                                 String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        if (domain.contains(",")) {
            String[] domains = domain.split(",");
            for (String d : domains) {
                cookie.setDomain(d);
                response.addCookie(cookie);
            }
        } else {
            cookie.setDomain(domain);
            response.addCookie(cookie);
        }
    }

    /**
     * 根据cookie名称读取cookie
     *
     * @param request     {@link HttpServletRequest}
     * @param cookieNames 多个cookie名称
     * @return map<cookieName, cookieValue>
     */
    public static Map<String, String> readCookie(HttpServletRequest request, String... cookieNames) {
        Map<String, String> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (Optional.ofNullable(cookies).isPresent()) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();
                for (String name : cookieNames) {
                    if (name.equals(cookieName)) {
                        cookieMap.put(cookieName, cookieValue);
                    }
                }
            }
        }
        return cookieMap;
    }

    public static String getHttpBasic(String clientId, String clientSecret) {
        String string = clientId + EnumPunctuation.COLON.getDesc() + clientSecret;
        //将串进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return EnumCookie.BASIC.getDesc() + new String(encode);
    }
}
