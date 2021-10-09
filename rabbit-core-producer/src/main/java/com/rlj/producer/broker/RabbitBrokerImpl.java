package com.rlj.producer.broker;

import com.rlj.api.message.Message;
import com.rlj.api.message.MessageType;
import com.rlj.producer.entity.BrokerMessage;
import com.rlj.producer.entity.BrokerMessageStatus;
import com.rlj.producer.entity.BrokerMessageValuePool;
import com.rlj.producer.service.MessageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author Renlingjie
 * @name 真正的发送不同类型消息的实现类
 * @date 2021-09-09
 */
@Slf4j
@Component
public class RabbitBrokerImpl implements RabbitBroker{

    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;

    @Autowired
    private MessageStoreService messageStoreService;

    //发送消息的核心方法 ---> 使用异步线程池进行发消息
    private void sendKernel(Message message){
        AsyncBaseQueue.submit((Runnable) () -> {//调用线程池进行处理的代码都放在这里
            //将消息的唯一messageId与当前时间戳、messageType(后面无论是确认还是可靠性消息，都会执行重写的confirm方法，重发消
            //息的时候，如果ack成功，那是需要更新状态的，所以在confirm中判断是否是可靠性)用"#"拼接作为消息的唯一标识(%s标识占位符)。
            //不直接用messageId作为唯一标识，是因为万一消息要重发，消息的唯一标识若是messageId，那么这个消息两次的唯一标识就一样了
            CorrelationData correlationData = new CorrelationData(String.format("%s#%s#%s",
                    message.getMessageId(),System.currentTimeMillis(),message.getMessageType()));
            RabbitTemplate template = rabbitTemplateContainer.getTemplate(message);
            //四个参数分别是交换机名称、路由、消息、唯一标识
            template.convertAndSend(template.getExchange(),template.getRoutingKey(),message,correlationData);
            log.info("RabbitBrokerImpl.sendKernel ---> 消息已发出,messageId:{}",message.getMessageId());
        });

    }

    @Override
    public void rapidSend(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }

    @Override
    public void confirmSend(Message message) {
        message.setMessageType(MessageType.CONFIRM);
        sendKernel(message);
    }

    @Override
    public void reliantSend(Message message) {
        message.setMessageType(MessageType.RELIANT);
        BrokerMessage bm = messageStoreService.selectByMessageId(message.getMessageId());
        if (bm == null){
            //1、发送消息之前，消息的落库
            Date now = new Date();
            BrokerMessage brokerMessage = new BrokerMessage();
            brokerMessage.setMessageId(message.getMessageId());
            brokerMessage.setStatus(BrokerMessageStatus.SENDING.code);
            //首次发送，tryCount默认就是0，无需设置
            brokerMessage.setNextRetry(DateUtils.addMinutes(now, BrokerMessageValuePool.TIMEOUT));
            brokerMessage.setCreateTime(now);
            brokerMessage.setUpdateTime(now);
            brokerMessage.setMessage(message);
            messageStoreService.insert(brokerMessage);
        }


        //2、发送消息
        sendKernel(message);
    }

    @Override
    public void sendMessages() {
        List<Message> messages = MessageHolder.fetchAndClear();
        messages.forEach(message -> {
            AsyncBaseQueue.submit((Runnable) () -> {
                CorrelationData correlationData = new CorrelationData(String.format("%s#%s#%s",
                        message.getMessageId(),System.currentTimeMillis(),message.getMessageType()));
                RabbitTemplate template = rabbitTemplateContainer.getTemplate(message);
                //四个参数分别是交换机名称、路由、消息、唯一标识
                template.convertAndSend(template.getExchange(),template.getRoutingKey(),message,correlationData);
                log.info("RabbitBrokerImpl.sendMessages ---> 消息已发出,messageId:{}",message.getMessageId());
            });
        });
    }
}
