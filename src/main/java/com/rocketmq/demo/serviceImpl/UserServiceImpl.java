package com.rocketmq.demo.serviceImpl;

import com.rocketmq.demo.dao.UserMapper;
import com.rocketmq.demo.model.User;
import com.rocketmq.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public String saveUser(User user) {
        Integer restult = userMapper.insert(user);
        return restult.toString();
    }

    @Override
    public List<User> getUser(User user) {
        return userMapper.selectByPrimaryKey(user);
    }
}
