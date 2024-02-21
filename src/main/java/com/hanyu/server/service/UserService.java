package com.hanyu.server.service;

public interface UserService {
    /**
     * 登陆
     * @param username 用户名
     * @param password 密码
     * @return 登陆成功返回结果
     */
    boolean login(String username, String password);
}
