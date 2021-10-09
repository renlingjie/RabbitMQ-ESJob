package com.rlj.producer.entity;

import com.rlj.api.message.Message;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-17
 */
@Data
public class BrokerMessage implements Serializable {
    private static final long serialVersionUID = -4971844585806821514L;
    private String messageId;
    private Message message;//后续序列化后存储
    private Integer tryCount = 0;
    private String status;
    private Date nextRetry;
    private Date createTime;
    private Date updateTime;
}
