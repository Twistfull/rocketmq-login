package com.rocketmq.demo.controller;

import com.rocketmq.demo.model.User;
import com.rocketmq.demo.service.UserService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class SendMessageController {
    // 用户和Session绑定关系
    public static final Map<String, HttpSession> USR_SESSION = new HashMap<String, HttpSession>();
    // SessionId和用户的绑定关系
    public static final Map<String, String> SESSIONID_USR = new HashMap<String, String>();


    @Autowired
    UserService userService;

    @RequestMapping("/login")
    @ResponseBody
    public Boolean Login(HttpServletRequest request, User user) {
        List<User> users = userService.getUser(user);
        // 当前sessionId
        String sessionId = request.getSession().getId();
        System.out.println(sessionId);
        for (User user1 : users) {
            if (user.getUser().equals(user1.getUser())) {
                if (user.getPassword().equals(user1.getPassword())) {
                    try {
                        saveLoginSession(user.getUser(), "TagA", sessionId);
                    } catch (MQClientException e) {
                        //成功,但session存储失败
                        String msg = "1";
                        System.out.println(msg);
                        System.out.println(e);
                        return false;
                    }
                    //成功
                    return true;
                }
            }
        }
        //失败
        return false;
    }

    /**
     * 判断用户是否同时登陆同一个用户(前端轮询获取)
     */
    @RequestMapping(value = "/checkUserOnline")
    @ResponseBody
    public Boolean checkUserOnline(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Boolean flag = userLoginHandle(request);
        return flag;
    }

    public static Boolean userLoginHandle(HttpServletRequest request) {
        // 当前登录的用户
        String user = request.getParameter("user");
        // 当前sessionId
        String sessionId = request.getSession().getId();
        try {
            String sessionIdHis = checkLoginSession(user, "", "");
            System.out.println("当前Session:" + sessionId);
            System.out.println("历史Session:" + sessionIdHis);
            if (sessionId.equals(sessionIdHis)) {
                //正常状态
                return true;
            } else {
                //被迫下线
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
       /* // 删除当前sessionId绑定的用户，用户--HttpSession
        USR_SESSION.remove(SESSIONID_USR.remove(sessionId));
        // 删除当前登录用户绑定的HttpSession
        HttpSession session = USR_SESSION.remove(user);
        if (session != null) {
            SESSIONID_USR.remove(session.getId());
            session.setAttribute("msg", "您的账号已经在另一处登录了,您被迫下线!");
        }*/
    }

    public void saveLoginSession(String topic, String tag, String value) throws MQClientException {

        DefaultMQProducer producer = new DefaultMQProducer("rmq-group");

        producer.setNamesrvAddr("172.16.140.80:9876");
        //producer.setNamesrvAddr("127.0.0.1:9876");

        producer.setInstanceName("rmq-instance");

        producer.setVipChannelEnabled(false);// // 必须设为false否则连接broker10909端口

        producer.start();

        System.out.println("开始保存登录");

        try {
            final Message msg = new Message(topic,// topic

                    tag,// tag
                    (value).getBytes());// body
            //单向
            //producer.sendOneway(msg);
            //同步
            SendResult sendResult = producer.send(msg);
            System.out.println("msg发送成功:" + sendResult);
            //异步
            /*producer.send(msg, new SendCallback() {

                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%-10d OK %s %n", msg, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    System.out.printf("%-10d Exception %s %n", msg, e);
                    e.printStackTrace();
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }

    public static String checkLoginSession(String topic, String tag, String value) throws InterruptedException, MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("rmq-group");

        consumer.setNamesrvAddr("172.16.140.80:9876");

        System.out.println("开始验证登录");
        final String[] sessionId = new String[1];

        try {

            // 设置topic和标签

            consumer.subscribe(topic, "*");

            consumer.setVipChannelEnabled(false);

            // 程序第一次启动从消息队列头取数据

            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

            consumer.registerMessageListener(new MessageListenerConcurrently() {

                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,

                                                                ConsumeConcurrentlyContext Context) {
                    Message msg = list.get(0);
                    sessionId[0] = new String(msg.getBody());
                    System.out.println("收到数据：" + sessionId[0]);

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionId[0];
    }
}