package com.rlj.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.rlj.api.exception.MessageRunTimeException;
import com.rlj.api.message.Message;
import com.rlj.api.message.MessageType;
import com.rlj.common.convert.GenericMessageConverter;
import com.rlj.common.convert.RabbitMessageConverter;
import com.rlj.producer.service.MessageStoreService;
import com.rlj.common.serializer.JacksonSerializerFactory;
import com.rlj.common.serializer.Serializer;
import com.rlj.common.serializer.SerializerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Renlingjie
 * @name 池化RabbitTemplate封装
 * @date 2021-09-09
 */
@Component
@Slf4j
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback{
    @Autowired
    private ConnectionFactory connectionFactory;//amqp的;创建RabbitTemplate对象的参数
    @Autowired
    private MessageStoreService messageStoreService;
    /**
     * hashMap:读取快，插入慢,线程不安全
     * LinkedHashMap:读取快，插入慢
     * treeMap:排序
     * concurrentHashMap:线程安全，支持高并发的操作
     * 使用了线程池并发发送消息，这里要求rabbitTemplate的容器是线程安全的，hashMap并发的时候线程不安全，
     * hashTable线程安全但是效率不高，因为会锁住整个哈希表，所以这里用currentMap，只会锁住一部分代码，线程安全的同时效率也很高
     */
    private Map<String, RabbitTemplate> rabbitTemplateMap = Maps.newConcurrentMap();
    private Splitter splitter = Splitter.on("#");
    private SerializerFactory serializerFactory = new JacksonSerializerFactory();
    public RabbitTemplate getTemplate(Message message) throws MessageRunTimeException {
        Preconditions.checkNotNull(message);//message为空在这里就抛出空指针异常
        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = rabbitTemplateMap.get(topic);
        if (rabbitTemplate != null){
            return rabbitTemplate;
        }
        //存在则返回，不存在，我们来创建这个RabbitTemplate
        log.info("RabbitTemplateContainer.getTemplate ---> 主题为{}的RabbitTemplate不存在，创建一个",topic);
        RabbitTemplate newRabbitTemplate = new RabbitTemplate(connectionFactory);
        newRabbitTemplate.setExchange(topic);
        newRabbitTemplate.setRoutingKey(message.getRoutingKey());
        newRabbitTemplate.setRetryTemplate(new RetryTemplate());

        //对于message的序列化/反序列化
        //1、SerializerFactory：Serializer实现类，调用Jackson的ObjectMapper实现了序列化/反序列化的四个核心方法
        Serializer serializer = serializerFactory.create();
        //2、GenericMessageConverter：实现MessageConverter，在使用上面的四个核心方法的基础上，重写MessageConverter的toMessage/fromMessage方法
        GenericMessageConverter gmc = new GenericMessageConverter(serializer);
        //3、RabbitMessageConverter：实现MessageConverter，修饰GenericMessageConverter重写的toMessage/fromMessage方法
        RabbitMessageConverter rmc = new RabbitMessageConverter(gmc);
        //4、将RabbitTemplate原来默认的MessageConverter的实现类SimpleMessageConverter替换为我们的RabbitMessageConverter实现类
        newRabbitTemplate.setMessageConverter(rmc);

        String messageType = message.getMessageType();
        if (!MessageType.RAPID.equals(messageType)){//只要不是迅速消息，都是需要进行消息应答
            newRabbitTemplate.setConfirmCallback(this::confirm);
        }
        rabbitTemplateMap.putIfAbsent(topic,newRabbitTemplate);
        return newRabbitTemplate;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //消息--->交换机的确认(只要不是迅速消息，都是需要进行消息应答)。其中，发送消息convertAndSend中的唯一
        //标识CorrelationData是这里的参数，其ID属性由"messageId+#+时间戳+#+messageType"拼接的，可由#分割得到messageId
        List<String> stringList = splitter.splitToList(correlationData.getId());
        String messageId = stringList.get(0);
        Long sendTime = Long.parseLong(stringList.get(1));
        log.info("本次消息的唯一标识是:{}" , messageId);
        log.info("是否存在消息拒绝接收？{}" , ack);
        if(ack){
            //如果收到消息确认(消息成功接收)，且是可靠性消息类型，就需要更新我们的落库的消息状态
            if (MessageType.RELIANT.equals(stringList.get(2))){
                messageStoreService.success(messageId);
            }
            log.info("消息发送成功，messageId:{},sendTime:{}",messageId,sendTime);

        }else{
            log.info("消息发送失败，messageId:{},sendTime:{},拒绝接收的原因是{}" , messageId,sendTime,cause);
        }
    }
}
