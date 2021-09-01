package com.boredou.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.boredou.common.config.SwaggerConfig;
import com.boredou.user.util.DingCallbackCrypto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 扫码回调
 *
 * @author yb
 * @since 2021/7/27
 */
@Slf4j
@RestController
@RequestMapping("dingTalk")
@Api(tags = SwaggerConfig.DingTalk)
public class DingTalkCtrl {

    @Value("${dingTalk.token}")
    private String token;
    @Value("${dingTalk.aesKey}")
    private String aesKey;
    @Value("${dingTalk.appKey}")
    private String appKey;

    @ApiOperation("钉钉回调")
    @PostMapping("callback")
    public Map<String, String> DingTalkCallbackCtrl(
            @RequestParam(value = "msg_signature", required = false) String msg_signature,
            @RequestParam(value = "timestamp", required = false) String timeStamp,
            @RequestParam(value = "nonce", required = false) String nonce,
            @RequestBody(required = false) JSONObject json) {
        try {
            // 1. 从http请求中获取加解密参数
            // 2. 使用加解密类型
            // Constant.OWNER_KEY 说明：
            // 1、开发者后台配置的订阅事件为应用级事件推送，此时OWNER_KEY为应用的APP_KEY。
            // 2、调用订阅事件接口订阅的事件为企业级事件推送，
            // 此时OWNER_KEY为：企业的appKey（企业内部应用）或SUITE_KEY（三方应用）
            DingCallbackCrypto callbackCrypto = new DingCallbackCrypto(token, aesKey, appKey);
            String encryptMsg = json.getString("encrypt");
            String decryptMsg = callbackCrypto.getDecryptMsg(msg_signature, timeStamp, nonce, encryptMsg);

            // 3. 反序列化回调事件json数据
            JSONObject eventJson = JSON.parseObject(decryptMsg);
            String eventType = eventJson.getString("EventType");

            // 4. 根据EventType分类处理
            if ("check_url".equals(eventType)) {
                // 测试回调url的正确性
                log.info("测试回调url的正确性");
            } else if ("user_add_org".equals(eventType)) {
                // 处理通讯录用户增加事件
                log.info("发生了：" + eventType + "事件");
            } else {
                // 添加其他已注册的
                log.info("发生了：" + eventType + "事件");
            }

            return callbackCrypto.getEncryptedMap("success");
        } catch (DingCallbackCrypto.DingTalkEncryptException e) {
            e.printStackTrace();
        }
        return null;
    }
}
