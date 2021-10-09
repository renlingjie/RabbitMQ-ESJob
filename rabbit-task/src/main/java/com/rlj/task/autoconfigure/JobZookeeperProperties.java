package com.rlj.task.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-25
 */
//会被@EnableConfigurationProperties修饰的JobParserAutoConfiguration创建对象后加入到IOC中
//所以最开始一些重要且不怎么变的配置直接配置上，创建对象的时候直接就有了，后续可以set覆盖。
//至于namespace、serverLists经常改变，且为必须的配置，需用户自定义，反而不给默认值，之后从配置文件中读取
@ConfigurationProperties(prefix = "elastic.job.zk")
@Data
public class JobZookeeperProperties {
    private String namespace;
    private String serverLists;
    private int maxRetires = 3;
    private int connectionTimeoutMilliseconds = 15000;//15s
    private int sessionTimeoutMilliseconds = 60000;//60s
    private int baseSleepTimeMilliseconds = 1000;
    private int maxSleepTimeMilliseconds = 3000;
    private String digest = "";//连接ZK的权限令牌，默认为空
}
