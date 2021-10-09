package com.rlj.common.convert;

import com.google.common.base.Preconditions;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author Renlingjie
 * @name 装饰者模式，装饰GenericMessageConverter(完善messageProperties的一些设置)，他们都实现同一个接口
 * @date 2021-09-15
 */
public class RabbitMessageConverter implements MessageConverter {
    private GenericMessageConverter delegate;
    private final String defaultExpire = String.valueOf(24 * 60 * 60 * 1000);
    //被装饰的对象作为参数传入，装饰后返回装饰的对象
    public RabbitMessageConverter(GenericMessageConverter genericMessageConverter){
        Preconditions.checkNotNull(genericMessageConverter);
        this.delegate = genericMessageConverter;//将传过来的对象给这个类的成员变量delegate
    }
    //说白了就是将原有的GenericMessageConverter对象delegate包裹起来，在进行
    //toMessage/fromMessage之前做一些修饰(向messageProperties加配置)
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        //除却设置消息的过期时间，还有比如消息的优先级、消息是否持久化等
        messageProperties.setExpiration(defaultExpire);
        return this.delegate.toMessage(object,messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        com.rlj.api.message.Message msg = (com.rlj.api.message.Message)this.delegate.fromMessage(message);
        return msg;
    }
}
