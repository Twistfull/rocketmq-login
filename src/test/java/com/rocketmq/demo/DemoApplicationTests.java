package com.rocketmq.demo;

import com.rocketmq.demo.model.User;
import com.rocketmq.demo.service.UserService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan("com.rocketmq.demo.dao")
public class DemoApplicationTests {
    @Autowired
    UserService userService;

    @Test
    @Ignore
    public void contextLoads() {
    }

    @Test
    public void saveUser() {
        User user = new User();
        user.setUser("pan");
        user.setPassword("123456");
        //System.out.println(userService.saveUser(user));
        System.out.println(userService.getUser(user).get(0).toString());
    }

}

