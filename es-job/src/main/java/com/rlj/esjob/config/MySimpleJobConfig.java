package com.rlj.esjob.config;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.rlj.esjob.listener.SimpleJobListener;
import com.rlj.esjob.task.MySimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-20
 */
//@Configuration
public class MySimpleJobConfig {
    //RegistryCenterConfig配置类中方法名是registryCenter，故这里必须是RegistryCenterConfig
    @Autowired
    private ZookeeperRegistryCenter registryCenter;
    @Autowired
    private JobEventConfiguration jobEventConfiguration;
    //具体真正的定时任务执行逻辑
    @Bean
    public SimpleJob simpleJob(){
        return new MySimpleJob();
    }
    @Bean(initMethod = "init")
    public JobScheduler simpleJobScheduler(final SimpleJob simpleJob,//就是上面注入IOC后在这里直接用了
        @Value("${simpleJob.corn}") final String corn,
        @Value("${simpleJob.shardingTotalCount}") final int shardingTotalCount,
        @Value("${simpleJob.shardingItemParameters}") final String shardingItemParameters,
        @Value("${simpleJob.jobParameter}") final String jobParameter,
        @Value("${simpleJob.failover}") final boolean failover,
        @Value("${simpleJob.monitorExecution}") final boolean monitorExecution,
        @Value("${simpleJob.monitorPort}") final int monitorPort,
        @Value("${simpleJob.maxTimeDiffSeconds}") final int maxTimeDiffSeconds,
        @Value("${simpleJob.jobShardingStrategyClass}") final String jobShardingStrategyClass){
        return new SpringJobScheduler(simpleJob, registryCenter,
                //这是一个配置项，就是配置是否加监控、分片总数、分片策略、最大可接受的误差时间等
                getLiteJobConfiguration(simpleJob.getClass(),corn,shardingTotalCount,
                        shardingItemParameters,jobParameter,
                        failover,monitorExecution,monitorPort,maxTimeDiffSeconds,jobShardingStrategyClass),
                //做一些日志的跟踪就用jobEventConfiguration，将任务执行信息存储到数据库中
                jobEventConfiguration,new SimpleJobListener());
    }

    //最终将配置参数全部set进LiteJobConfiguration对象中返回
    //Class<? extends SimpleJob>表示只有继承SimpleJob的类才可以在此作为参数。
    private LiteJobConfiguration getLiteJobConfiguration(Class<? extends SimpleJob> jobClass,
                String corn, int shardingTotalCount, String shardingItemParameters,
                String jobParameter, boolean failover, boolean monitorExecution, int monitorPort,
                int maxTimeDiffSeconds, String jobShardingStrategyClass) {

        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration
                .newBuilder(jobClass.getName(),corn,shardingTotalCount).misfire(true).failover(failover)
                .jobParameter(jobParameter).shardingItemParameters(shardingItemParameters).build();

        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration,jobClass.getCanonicalName());
        //overwrite表示"本地配置是否可覆盖注册中心配置"，因为写的application.properties中的corn、jobParameter等是自定义的，所以要为true覆盖
        //若为false，那application.properties只用写注册中心的(zookeeper.XXX)，不用写自定义的(simpleJob.XXX)部分
        //之后可以在ESJob前端可视化上改这些配置，其实还是推荐false，因为要在这里改，每次修改后还需要重启服务
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration)
                .jobShardingStrategyClass(jobShardingStrategyClass).monitorExecution(monitorExecution)
                .monitorPort(monitorPort).maxTimeDiffSeconds(maxTimeDiffSeconds).overwrite(true).build();

        return liteJobConfiguration;
    }

}
