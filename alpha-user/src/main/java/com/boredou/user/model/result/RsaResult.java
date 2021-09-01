package com.boredou.user.model.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@ApiModel(value = "RsaResult", description = "Rsa加密返回对象")
public class RsaResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "加密数据", name = "data", required = true)
    String data;

    @ApiModelProperty(value = "aes码", name = "aesKey", required = true)
    String aesKey;
}
