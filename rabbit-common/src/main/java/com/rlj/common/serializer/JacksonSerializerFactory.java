package com.rlj.common.serializer;

import com.rlj.api.message.Message;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-13
 */
public class JacksonSerializerFactory implements SerializerFactory{
    @Override
    public Serializer create() {
        return JacksonSerializer.createParametericType(Message.class);
    }
}
