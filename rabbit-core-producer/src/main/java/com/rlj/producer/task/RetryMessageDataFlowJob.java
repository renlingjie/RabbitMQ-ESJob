package com.rlj.producer.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.rlj.producer.broker.RabbitBroker;
import com.rlj.producer.entity.BrokerMessage;
import com.rlj.producer.entity.BrokerMessageStatus;
import com.rlj.producer.service.MessageStoreService;
import com.rlj.task.annotation.ElasticJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-30
 */
@Component //千万别忘啦将任务加入IOC，后续才能通过@ElasticJobConfig找到
@ElasticJobConfig(
        name = "com.rlj.producer.task.RetryMessageDataFlowJob",
        corn = "0/10 * * * * ?",
        description = "可靠性投递消息的补偿任务",
        overwrite = true,
        shardingTotalCount = 1,
        eventTraceRdbDataSource = "dataSource"
)
@Slf4j
//DataflowJob<T> 里面的T指定要抓取的信息，这里肯定是抓取BrokerMessage(调用方法从数据库抓取)
public class RetryMessageDataFlowJob implements DataflowJob<BrokerMessage> {
    @Autowired
    private MessageStoreService messageStoreService;
    @Autowired
    private RabbitBroker rabbitBroker;
    private static final int MAX_RETRY_COUNT = 3;

    @Override
    public List<BrokerMessage> fetchData(ShardingContext shardingContext) {
        //抓取那些需要重发的消息(nextRetry已经超过当前时间，且状态还是Sending的)
        List<BrokerMessage> bmList = messageStoreService.fetchTimeOutMessageForRetry(BrokerMessageStatus.SENDING);
        log.info("---------- 抓取重发的数据集合，数量：{} ----------",bmList.size());
        return bmList;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<BrokerMessage> needRetryList) {
        needRetryList.forEach(brokerMessage -> {
            String messageId = brokerMessage.getMessageId();
            //如果已经超过最大重试次数，直接将该记录置为发送失败
            if (brokerMessage.getTryCount() >= MAX_RETRY_COUNT){
                this.messageStoreService.failure(messageId);
                log.warn("---------- 消息重试次数达到上限，置为失败，消息ID为：{} ----------",messageId);
            }else {
                //RabbitTemplateContainer重写了confirm，一旦接收到消息确认，就将消息
                //状态更新为成功(只要不是迅速消息，都会执行该confirm方法)。这里将重发的消息类型定为可靠性消息，并且做了一些相关的操作
                //比如confirm判断是否是可靠性，如果是才在ack为true的时候更新数据库记录为success
                this.rabbitBroker.reliantSend(brokerMessage.getMessage());
                //消息重发后更新BrokerMessage的重发次数、更新时间
                this.messageStoreService.updateTryCount(messageId);
            }
        });
    }
}
