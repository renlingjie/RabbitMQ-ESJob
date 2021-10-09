package com.rlj.common.convert;

import com.google.common.base.Preconditions;
import com.rlj.common.serializer.Serializer;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-14
 */
public class GenericMessageConverter implements MessageConverter {
    private Serializer serializer;
    public GenericMessageConverter(Serializer serializer){
        Preconditions.checkNotNull(serializer);
        //最开始的成员变量Serializer就等于这个方法接收的Serializer
        this.serializer = serializer;
    }

    @Override
    //反序列化 ----> org.springframework.amqp.core.Message变为我们定义的Message(这里的Object)
    //接收的是spring.amqp的Message，要将它转换成我们自定义的Message
    public Object fromMessage(org.springframework.amqp.core.Message message) throws MessageConversionException {
        return this.serializer.deserialize(message.getBody());//message.getBody()拿到的是message的字节数组
    }

    @Override
    //序列化 ---> 我们定义的Message(这里的Object)变为org.springframework.amqp.core.Message
    //我们写出去的自定义的Message，spring.amqp肯定不能识别，我们要转换成spring.amqp的Message
    public org.springframework.amqp.core.Message toMessage(Object object, MessageProperties messageProperties)
            throws MessageConversionException {
        return new org.springframework.amqp.core.Message(this.serializer.serializerRow(object),messageProperties);
    }
}
