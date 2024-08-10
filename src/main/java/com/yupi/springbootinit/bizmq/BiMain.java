package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * @author dio哒
 * @version 1.0
 * @date 2024/7/14 18:10
 *
 * 用来创建消息队列和交换机
 */
public class BiMain {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // 创建交换机
            channel.exchangeDeclare(BiMqConstant.BI_EXCHANGE_NAME, "direct");
            //创建队列
            channel.queueDeclare(BiMqConstant.BI_QUEUE_NAME,true,false,false,null);
            //绑定
            channel.queueBind(BiMqConstant.BI_QUEUE_NAME,BiMqConstant.BI_EXCHANGE_NAME,BiMqConstant.BI_ROUTING_KEY);
        }
    }
}
