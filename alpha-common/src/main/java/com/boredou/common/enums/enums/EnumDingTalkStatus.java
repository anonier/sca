package com.boredou.common.enums.enums;

public enum EnumDingTalkStatus {

    display("已启用"),

    undisplay("已禁用");

    private String value;

    /**
     * Status
     *
     * @param value
     */
    private EnumDingTalkStatus(String value) {
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
