package com.rocketmq.demo.service;

import com.rocketmq.demo.model.User;

import java.util.List;

public interface UserService {
    /**
     * 保存用户名加密码
     *
     * @param user
     * @return
     */
    String saveUser(User user);

    List<User> getUser(User user);
}
