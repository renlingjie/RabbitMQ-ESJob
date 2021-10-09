package com.rlj.common.serializer;

/**
 * @author Renlingjie
 * @name 序列化和反序列化的接口
 * @date 2021-09-13
 */
public interface Serializer {
    //序列化：对象进来转换为byte数组或者一个字符串
    byte[] serializerRow(Object data);
    String serialize(Object data);
    //反序列化：byte数组或一个字符串进来转换成你指定泛型的Java对象
    <T> T deserialize(String content);
    <T> T deserialize(byte[] content);
}
