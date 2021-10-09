package com.rlj.api;

import com.rlj.api.message.Message;

import java.util.List;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-08
 */
public interface MessageProducer {
    void send(Message message);
    void send(List<Message> messageList);
}
