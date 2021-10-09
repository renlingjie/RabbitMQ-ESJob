package com.rlj.task.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.rlj.task.parser.ElasticJobConfigParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Driver;

/**
 * @author Renlingjie
 * @name 解析ESJob的配置类
 * @date 2021-09-25
 */
@Slf4j
@Configuration
//只有配置文件中有elastic.job.zk.serverLists/elastic.job.zk.namespace，Spring才会加载这个配置类。否则ESJob强依赖
//的ZK的两个关键参数都没有，没必要启动。若满足，则把配置信息读到前缀是"elastic.job.zk"的JobZookeeperProperties对象中
@ConditionalOnProperty(prefix = "elastic.job.zk",name = {"serverLists","namespace"},matchIfMissing = false)
@EnableConfigurationProperties(JobZookeeperProperties.class)
public class JobParserAutoConfiguration {
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(JobZookeeperProperties jobZookeeperProperties){
        ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(
                jobZookeeperProperties.getServerLists(),
                jobZookeeperProperties.getNamespace());
        zkConfig.setMaxRetries(jobZookeeperProperties.getMaxRetires());
        zkConfig.setBaseSleepTimeMilliseconds(jobZookeeperProperties.getBaseSleepTimeMilliseconds());
        zkConfig.setMaxSleepTimeMilliseconds(jobZookeeperProperties.getMaxSleepTimeMilliseconds());
        zkConfig.setSessionTimeoutMilliseconds(jobZookeeperProperties.getSessionTimeoutMilliseconds());
        zkConfig.setConnectionTimeoutMilliseconds(jobZookeeperProperties.getConnectionTimeoutMilliseconds());
        zkConfig.setDigest(jobZookeeperProperties.getDigest());
        log.info("初始化ESJob注册中心配置成功，zk的serverLists:{},namespace:{}",jobZookeeperProperties.getServerLists(),
                jobZookeeperProperties.getNamespace());
        return new ZookeeperRegistryCenter(zkConfig);
    }

    //上面初始化完ZK后，再去初始化ElasticJobConfigParser，因为它要在这里作为参数
    @Bean
    public ElasticJobConfigParser elasticJobConfigParser(JobZookeeperProperties jobZookeeperProperties,
                                                         ZookeeperRegistryCenter zookeeperRegistryCenter){
        return new ElasticJobConfigParser(jobZookeeperProperties,zookeeperRegistryCenter);
    }

    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource dataSourceTwo() {
        DruidDataSource build = DruidDataSourceBuilder.create().build();
        return build;
    }

}
