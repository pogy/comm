package com.vipkid.rest.vo;

/**
 * Created by zfl on 2015/5/26.
 */
public class Response {
    private String msg;
    private int code;
    private int status;

    public Response(int status,String msg) {
        this.status = status;
        this.msg = msg;
    }
    public Response(int status) {
        this.status = status;
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
