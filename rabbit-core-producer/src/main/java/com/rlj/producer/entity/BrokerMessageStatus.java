package com.rlj.producer.entity;

/**
 * @author Renlingjie
 * @name 消息的发送状态
 * @date 2021-09-20
 */
public enum BrokerMessageStatus {
    SENDING("0","发送中"),
    SEND_OK("1","已发送"),
    SEND_FAIL("2","发送失败");
    public final String code;
    public final String value;
    BrokerMessageStatus(String code,String value){
        this.code = code;
        this.value = value;
    }
}
