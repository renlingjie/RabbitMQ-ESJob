package com.rlj.esjob.config;

import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.rlj.esjob.listener.SimpleJobListener;
import com.rlj.esjob.task.MyDataflowJob;
import com.rlj.esjob.task.MySimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-24
 */
//@Configuration
public class MyDataflowJobConfig {
    //RegistryCenterConfig配置类中方法名是registryCenter，故这里必须是registryCenter
    @Autowired
    private ZookeeperRegistryCenter registryCenter;
    @Autowired
    private JobEventConfiguration jobEventConfiguration;
    //具体真正的定时任务执行逻辑
    @Bean
    public DataflowJob dataflowJob(){
        return new MyDataflowJob();
    }
    @Bean(initMethod = "init")
    public JobScheduler dataflowJobScheduler(final DataflowJob dataflowJob,//就是上面注入IOC后在这里直接用了
                                           @Value("${dataflowJob.corn}") final String corn,
                                           @Value("${dataflowJob.shardingTotalCount}") final int shardingTotalCount,
                                           @Value("${dataflowJob.shardingItemParameters}") final String shardingItemParameters
                                           ){
        return new SpringJobScheduler(dataflowJob, registryCenter,
                //这是一个配置项，就是配置是否加监控、分片总数、分片策略、最大可接受的误差时间等
                getLiteJobConfiguration(dataflowJob.getClass(),corn,shardingTotalCount,
                        shardingItemParameters),jobEventConfiguration);
    }

    //最终将配置参数全部set进LiteJobConfiguration对象中返回
    //Class<? extends SimpleJob>表示只有继承SimpleJob的类才可以在此作为参数。
    private LiteJobConfiguration getLiteJobConfiguration(Class<? extends DataflowJob> jobClass,
                                                         String corn, int shardingTotalCount, String shardingItemParameters) {
        return LiteJobConfiguration.newBuilder(
                new DataflowJobConfiguration(
                        JobCoreConfiguration.newBuilder(jobClass.getName(),corn,shardingTotalCount).shardingItemParameters(shardingItemParameters).build()
                        ,jobClass.getCanonicalName()
                        ,true //streamingProcess,为true会不停的抓取任务处理，为false则按照corn来

                ))
                //overwrite表示"本地配置是否可覆盖注册中心配置"，如果为true，本地为主，如果为false，可视化前端的ZK配置为主，谁为主，另一方修改则不生效
                .overwrite(true).build();
    }
}
