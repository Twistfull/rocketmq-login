package com.rocketmq.demo.dao;

import com.rocketmq.demo.model.User;

import java.util.List;

public interface UserMapper {
    int insert(User user);

    List<User> selectByPrimaryKey(User user);
}
