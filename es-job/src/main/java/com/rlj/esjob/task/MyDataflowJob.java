package com.rlj.esjob.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-20
 */
public class MyDataflowJob implements DataflowJob {
    //第一个方法去抓取任务，然后作为参数给第二个方法去执行，如果抓取的list为null，那么第二个方法不会执行
    @Override
    public List fetchData(ShardingContext shardingContext) {
        System.out.println("---------- 抓取数据集合 ----------");
        List list = new ArrayList();
        list.add("这是被抓取的任务");
        return list;
    }

    @Override
    public void processData(ShardingContext shardingContext, List list) {
        System.out.println("---------- 处理数据集合 ----------");
    }
}
