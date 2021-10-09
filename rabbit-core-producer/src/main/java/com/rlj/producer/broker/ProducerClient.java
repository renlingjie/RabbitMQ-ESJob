package com.rlj.producer.broker;

import com.rlj.api.MessageProducer;
import com.rlj.api.message.Message;
import com.rlj.api.message.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Renlingjie
 * @name 发送消息的实际实现类
 * @date 2021-09-08
 */
@Component
public class ProducerClient implements MessageProducer {

    @Autowired
    private RabbitBroker rabbitBroker;

    @Override
    public void send(Message message) {
        //Preconditions.checkNotNull(message.getTopic());
        String messageType = message.getMessageType();
        switch (messageType){
            case MessageType.RAPID:
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.reliantSend(message);
                break;
            default:
                break;
        }
    }

    //批量，指定其type都是迅速消息
    @Override
    public void send(List<Message> messageList) {
        messageList.forEach(message -> {
            message.setMessageType(MessageType.RAPID);
            MessageHolder.add(message);
        });
        rabbitBroker.sendMessages();
    }
}
