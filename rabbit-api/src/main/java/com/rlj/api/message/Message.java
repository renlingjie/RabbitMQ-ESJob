package com.rlj.api.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Renlingjie
 * @name 建造者模式
 * @date 2021-09-04
 */
@Data //lombok
public class Message implements Serializable {

    private static final long serialVersionUID = -6192106411266193587L;
    private String messageId;//消息的唯一ID
    private String topic;//交换机类型
    private String routingKey = "";//路由规则
    private Map<String,Object> attributes = new HashMap<>();//传递一些属性所需的容器
    private String messageType = MessageType.CONFIRM;//迅速消息、确认消息、可靠性消息(默认是确认消息)
    private int delayMills;//这个是需要延迟发送的消息的延迟时间

    public static class MessageBuilder{
        private String messageId;//必填
        private String topic;//必填
        private String routingKey = "";
        private Map<String,Object> attributes = new HashMap<>();
        private String messageType = MessageType.CONFIRM;
        private int delayMills;
        //必填项的唯一构造函数
        public MessageBuilder(String messageId,String topic){
            this.messageId = messageId;
            this.topic = topic;
        }
        //下面折叠的withXXX和这个格式一致
        public MessageBuilder withRoutingKey(String routingKey){
            this.routingKey = routingKey;
            return this;
        }
        public MessageBuilder withAttributes(Map<String,Object> attributes){
            this.attributes = attributes;
            return this;
        }
        public MessageBuilder withAttribute(String key,Object value){
            this.attributes.put(key,value);
            return this;
        }
        public MessageBuilder withMessageType(String messageType){
            this.messageType = messageType;
            return this;
        }
        public MessageBuilder withDelayMills(int delayMills){
            this.delayMills = delayMills;
            return this;
        }

        public Message finalBuild(){
            return new Message(this);
        }
    }

    //被序列化对象应提供一个无参的构造函数，否则会抛出异常
    private Message() {}

    private Message(MessageBuilder msgbd){
        this.messageId = msgbd.messageId;
        this.topic = msgbd.topic;
        this.routingKey = msgbd.routingKey;
        this.attributes = msgbd.attributes;
        this.messageType = msgbd.messageType;
        this.delayMills = msgbd.delayMills;
    }
}
