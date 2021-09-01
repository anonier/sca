package com.boredou.user.model.dto;

import com.boredou.user.model.entity.DingTalkUserInfo;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class DingTalkBindDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String errcode;
    private String errmsg;
    private DingTalkUserInfo result;
    private String request_id;
}
