package com.rlj.producer.broker;

import com.rlj.api.message.Message;

/**
 * @author Renlingjie
 * @name 具体发送不同种类型消息的接口
 * @date 2021-09-08
 */
public interface RabbitBroker {
    void rapidSend(Message message);//迅速消息
    void confirmSend(Message message);//确认消息
    void reliantSend(Message message);//可靠性消息
    void sendMessages();//批量消息
}
