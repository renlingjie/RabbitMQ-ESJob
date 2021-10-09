package com.rlj.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-13
 */
public class JacksonSerializer implements Serializer{
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonSerializer.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    //1、初始化配置
    static {
        //忽略无法转换的对象
        MAPPER.disable(SerializationFeature.INDENT_OUTPUT);

        //忽略输入时在JSON字符串中存在但Java对象实际没有的属性
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        //这个特性，决定了解析器是否将自动关闭那些不属于parser自己的输入源，默认是true。
        //如果禁止，则调用应用不得不分别去关闭那些被用来创建parser的基础输入流InputStream和reader
        MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

        //是否允许解析使用Java/C++ 样式的注释（包括'/'+'*' 和'//' 变量）
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        //设置为true时，属性名称不带双引号
        MAPPER.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);

        //反序列化是是否允许属性名称不带双引号
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        //是否允许单引号来包住属性名称和字符串值
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        //是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        //是否允许JSON整数以多个0开始
        MAPPER.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
    }

    //2、实现类JacksonSerializer的构造方法。需传入反序列化类的class，
    //创建出来的JacksonSerializer对象即专门序列化/反序列化这个类
    private final JavaType type;

    public JacksonSerializer(JavaType type) {
        this.type = type;
    }

    public JacksonSerializer(Type type) {
        this.type = MAPPER.getTypeFactory().constructType(type);
    }

    public static JacksonSerializer createParametericType(Class<?> cls){
        return new JacksonSerializer(MAPPER.getTypeFactory().constructType(cls));
    }

    //3、四个重写的方法内部，直接使用JackSon自带的方法
    @Override
    public byte[] serializerRow(Object data) {
        try{
            return MAPPER.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            LOGGER.error("序列化出错",e);
        }
        return null;
    }

    @Override
    public String serialize(Object data) {
        try{
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            LOGGER.error("序列化出错",e);
        }
        return null;
    }

    @Override
    public <T> T deserialize(String content) {
        try{
            return MAPPER.readValue(content,type);
        } catch (JsonProcessingException e) {
            LOGGER.error("反序列化出错",e);
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] content) {
        try{
            return MAPPER.readValue(content,type);
        } catch (IOException e) {
            LOGGER.error("反序列化出错",e);
        }
        return null;
    }
}
