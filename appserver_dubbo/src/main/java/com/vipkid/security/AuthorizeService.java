package com.vipkid.security;

import com.vipkid.model.User;

/**
 * Created by zfl on 2015/5/19.
 * 鉴权接口
 */
public interface AuthorizeService {
    /**
     * @param uri 需要鉴权的URI
     * @param user   被鉴权的用户
     * @return true：鉴权通过，false：鉴权未通过
     */
    boolean authorize(String uri, User user);
}
