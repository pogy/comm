package com.vipkid.rest.vo.query;

/**
 * Created by zfl on 2015/6/12.
 */
public class UserVO {
    private Long id;
    private String name;
    private String safeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSafeName() {
        return safeName;
    }

    public void setSafeName(String safeName) {
        this.safeName = safeName;
    }
}
