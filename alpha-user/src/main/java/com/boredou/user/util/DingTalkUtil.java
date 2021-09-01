package com.boredou.user.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.boredou.common.enums.BizException;
import com.boredou.user.model.dto.DingTalkBindDto;
import com.boredou.user.model.result.DingTalkTokenResult;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.request.OapiV2UserGetbymobileRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.api.response.OapiV2UserGetbymobileResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 钉钉工具类
 */
@Slf4j
@RefreshScope
@Component
public class DingTalkUtil {

    @Value("${dingTalk.appKey}")
    private String appKey;
    @Value("${dingTalk.appSecret}")
    private String appSecret;
    @Value("${dingTalk.agentId}")
    private Long agentId;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 钉钉获取token
     */
    public String getToken() {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString() + "Token")))
            return stringRedisTemplate.opsForValue().get(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString() + "Token");
        String token = "";
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(appKey);
        request.setAppsecret(appSecret);
        request.setHttpMethod("GET");
        try {
            OapiGettokenResponse response = client.execute(request);
            if (response.isSuccess()) {
                token = response.getAccessToken();
                stringRedisTemplate.boundValueOps(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString() + "Token").set(token, 7200, TimeUnit.SECONDS);
            } else {
                log.warn("DingtalkUtil getToken failed:" + response.getErrmsg());
            }
        } catch (Exception e) {
            log.error("DingtalkUtil getToken error", e);
        }
        return token;
    }

    /**
     * 根据手机号获取用户id
     *
     * @param phone 手机号
     * @return String 用户id
     */
    public String getUserId(String phone) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/getbymobile");
            OapiV2UserGetbymobileRequest req = new OapiV2UserGetbymobileRequest();
            req.setMobile(phone);
            OapiV2UserGetbymobileResponse rsp = client.execute(req, getToken());
            return JSON.parseObject(JSONObject.parseObject(rsp.getBody(), DingTalkTokenResult.class).getResult(), HashMap.class).get("userid").toString();
        } catch (Exception e) {
            throw new BizException("获取钉钉用户id失败");
        }
    }

    public DingTalkBindDto getUserInfo(String userId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
            OapiV2UserGetRequest req = new OapiV2UserGetRequest();
            req.setUserid(userId);
            OapiV2UserGetResponse rsp = client.execute(req, getToken());
            return JSON.parseObject(rsp.getBody(), new TypeReference<DingTalkBindDto>() {
            });
        } catch (ApiException e) {
            throw new BizException("获取钉钉用户信息失败");
        }
    }

    /**
     * 获取验证码模板消息
     */
    public static OapiMessageCorpconversationAsyncsendV2Request.Msg getVerCodeTemplateMsg(String fristContent, String type, String code) {
        String sb = "# " + fristContent +
                " \n#### 当前操作:" + type +
                " \n#### 验证码:" + code;
        return getTemplateMsg("操作验证码提醒", sb);
    }

    /**
     * 自定义消息
     */
    public static OapiMessageCorpconversationAsyncsendV2Request.Msg getTemplateMsg(String title, String content) {
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("markdown");
        msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
        msg.getMarkdown().setTitle(title);
        msg.getMarkdown().setText(content);
        return msg;
    }

    /**
     * 给指定用户发送消息
     */
    public void sendUserMsg(OapiMessageCorpconversationAsyncsendV2Request.Msg msg, String receiveList) throws Exception {
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(receiveList);
        request.setAgentId(agentId);
        request.setToAllUser(false);
        request.setMsg(msg);
        //发送消息
        sendMsg(request);
    }

    /**
     * 发送消息
     */
    private void sendMsg(OapiMessageCorpconversationAsyncsendV2Request request) throws Exception {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        try {
            OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, getToken());
            if (!response.isSuccess()) {
                log.error("DingTalkUtil sendMsg failed: " + response.getErrmsg());
            }
        } catch (Exception e) {
            log.error("DingTalkUtil sendMsg error", e);
            throw e;
        }
    }
}
