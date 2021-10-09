package com.rlj.esjob.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-20
 */
public class MySimpleJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println(String.format("------开始任务MySimpleJob，Thread ID: %s, 任务总片数: %s, 当前分片项: %s ------",
                Thread.currentThread().getId(), shardingContext.getShardingTotalCount(), shardingContext.getShardingItem()));

    }
}
