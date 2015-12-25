package com.vipkid.service.pojo;

public enum FollowUpStatus {
    NOT_CONTACT (0,""),
    CONTACTED(1,""),
    NEED_CONTACTED_AGAIN(2,"");
    
    private int code;
    private String desc;

    FollowUpStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}