package com.boredou.user.model.result;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 钉钉获取Token返回对象
 *
 * @author yb
 * @since 2021-6-28
 */
@Data
@NoArgsConstructor
public class DingTalkTokenResult {

    String errcode;

    String result;

    String errmsg;

    String requestId;
}
