package com.boredou.common.enums.enums;

public enum EnumCategoryType {

    recharge("号通道"),

    cardpay("卡通道"),

    bankpay("网银通道");

    private String value;

    private EnumCategoryType(String value) {
        this.value = value;
    }

    /**
     * getValue
     *
     * @return
     */
    public String getValue() {
        return value;
    }
}
