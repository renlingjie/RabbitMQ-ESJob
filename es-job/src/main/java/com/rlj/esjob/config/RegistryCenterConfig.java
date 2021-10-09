package com.rlj.esjob.config;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Renlingjie
 * @name 注册中心的配置
 * @date 2021-09-20
 */
//@Configuration
//只有当我们配置文件application.properties中zookeeper.address的节点数大于0才会执行此配置
//@ConditionalOnExpression("'${zookeeper.address}'.length() > 0")
public class RegistryCenterConfig {
    //将ESJob的注册中心创建出来并进行赋值后，加入到Spring容器中，之后调用
    //该注册中心ZookeeperRegistryCenter的init方法进行一些初始化配置
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter registryCenter(@Value("${zookeeper.address}") final String serverLists,
        @Value("${zookeeper.namespace}") final String namespace,
        @Value("${zookeeper.connectionTimeout}") final int connectionTimeout,
        @Value("${zookeeper.sessionTimeout}") final int sessionTimeout,
        @Value("${zookeeper.maxRetries}") final int maxRetries){
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(serverLists,namespace);
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(connectionTimeout);
        zookeeperConfiguration.setSessionTimeoutMilliseconds(sessionTimeout);
        zookeeperConfiguration.setMaxRetries(maxRetries);
        return new ZookeeperRegistryCenter(zookeeperConfiguration);
    }

}
