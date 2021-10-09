package com.rlj.esjob.test;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.rlj.task.annotation.ElasticJobConfig;
import org.springframework.stereotype.Component;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-26
 */
@ElasticJobConfig(
        name="mydemojob",
        corn = "0/10 * * * * ?",
        shardingTotalCount = 2
)
@Component
public class MyDemoJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("MyDemoJob -- 执行啦");
    }
}
