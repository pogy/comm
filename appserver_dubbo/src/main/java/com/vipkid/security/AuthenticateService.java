package com.vipkid.security;

import com.vipkid.model.User;

/**
 * Created by zfl on 2015/5/19.
 * 身份验证服务接口
 */
public interface AuthenticateService {

    /**
     *
     * @param authorization token信息
     * @return User 认证用户 null 无效用户
     */
    public User authenticate(String authorization);
}
