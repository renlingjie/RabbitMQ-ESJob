package com.rlj.producer.mapper;

import com.rlj.producer.entity.BrokerMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-17
 */
@Mapper
@Repository
public interface BrokerMessageMapper {
    int deleteByPrimaryKry(String messageId);
    int insert(BrokerMessage record);
    int insertSelective(BrokerMessage record);
    BrokerMessage selectByPrimaryKey(String messageId);
    int updateByPrimaryKeySelective(BrokerMessage record);
    int updateByPrimaryKeyWithBLOBs(BrokerMessage record);
    int updateByPrimaryKey(BrokerMessage record);
    void changeBrokerMessageStatus(@Param("brokerMessageId")String brokerMessageId,@Param("brokerMessageStatus")String brokerMessageStatus,@Param("updateTime")Date updateTime);
    List<BrokerMessage> queryBrokerMessageStatusForTimeout(@Param("brokerMessageStatus")String brokerMessageStatus);
    List<BrokerMessage> queryBrokerMessageStatus(@Param("brokerMessageStatus")String brokerMessageStatus);
    int updateForTryCount(@Param("brokerMessageId")String brokerMessageId,@Param("updateTime") Date updateTime,@Param("nextRetry") Date nextRetry);
}
