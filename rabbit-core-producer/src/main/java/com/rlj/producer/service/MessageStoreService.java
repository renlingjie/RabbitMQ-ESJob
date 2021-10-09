package com.rlj.producer.service;

import com.rlj.producer.entity.BrokerMessage;
import com.rlj.producer.entity.BrokerMessageStatus;
import com.rlj.producer.entity.BrokerMessageValuePool;
import com.rlj.producer.mapper.BrokerMessageMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-20
 */
@Service
public class MessageStoreService {
    @Autowired
    private BrokerMessageMapper brokerMessageMapper;

    //消息发送之前，消息的先落库
    public int insert(BrokerMessage brokerMessage){
        return this.brokerMessageMapper.insert(brokerMessage);
    }

    public BrokerMessage selectByMessageId(String messageId){
        return this.brokerMessageMapper.selectByPrimaryKey(messageId);
    }

    public void success(String messageId) {
        this.brokerMessageMapper.
                changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_OK.code,new Date());
    }

    public void failure(String messageId) {
        this.brokerMessageMapper.
                changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_FAIL.code,new Date());
    }

    //抓取那些需要重发的消息(nextRetry已经超过当前时间，且状态还是Sending的)
    public List<BrokerMessage> fetchTimeOutMessageForRetry(BrokerMessageStatus brokerMessageStatus){
        return this.brokerMessageMapper.queryBrokerMessageStatusForTimeout(brokerMessageStatus.code);
    }

    //重发消息后，更新消息的重发次数、nextRetry、更新时间
    public int updateTryCount(String brokerMessageId){
        return this.brokerMessageMapper.updateForTryCount(brokerMessageId,new Date(), DateUtils.addMinutes(new Date(), BrokerMessageValuePool.TIMEOUT));
    }
}
