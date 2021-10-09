package com.rlj.esjob.test;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.rlj.task.annotation.ElasticJobConfig;
import com.rlj.task.annotation.EnableElasticJob;
import org.springframework.stereotype.Component;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-26
 */
@ElasticJobConfig(
        name="mytestjob",
        corn = "0/5 * * * * ?",
        shardingTotalCount = 4,
        eventTraceRdbDataSource = "dataSource"
)
@Component
public class MyTestJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("MyTestJob -- 执行啦");
    }
}
