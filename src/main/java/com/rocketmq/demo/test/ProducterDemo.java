
package com.rocketmq.demo.test;


import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.Date;


public class ProducterDemo {


    public static void main(String[] args) throws MQClientException, InterruptedException {

        DefaultMQProducer producer = new DefaultMQProducer("rmq-group");

        producer.setNamesrvAddr("172.16.140.80:9876");
        //producer.setNamesrvAddr("127.0.0.1:9876");

        producer.setInstanceName("rmq-instance");

        producer.setVipChannelEnabled(false);// // 必须设为false否则连接broker10909端口

        producer.start();

        System.out.println("开始发送数据");

        try {

            for (int i = 0; i < 3; i++) {

                final Message msg = new Message("TopicTest",// topic

                        "TagA",// tag

                        (new Date() + "唯我独尊" + i).getBytes()// body

                );
                //单向
                producer.sendOneway(msg);
                //同步
                SendResult sendResult = producer.send(msg);
                //异步
                System.out.println("msg发送成功:" + sendResult);
                producer.send(msg, new SendCallback() {

                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.printf("%-10d OK %s %n", msg, sendResult.getMsgId());
                    }

                    @Override
                    public void onException(Throwable e) {
                        System.out.printf("%-10d Exception %s %n", msg, e);
                        e.printStackTrace();
                    }
                });
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        producer.shutdown();

    }

}
