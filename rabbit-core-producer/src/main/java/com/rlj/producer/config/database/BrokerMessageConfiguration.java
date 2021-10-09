package com.rlj.producer.config.database;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * @author Renlingjie
 * @name 服务启动时自动执行建表Sql脚本
 * @date 2021-09-18
 */
//主要将Sql脚本找到并执行
@Configuration
public class BrokerMessageConfiguration {
    @Autowired
    private DataSource rabbitProducerDataSource;
    @Value("classpath:rabbit-producer-message-schema.sql")
    private Resource schemaScript;
    @Bean
    public DataSourceInitializer initDataSourceInitializer(){
        //先看一下数据源有没有，有了才说明JDBC连接上了，才能去用上面的脚本操作我们的数据库
        System.err.println("----------rabbitProducerDataSource----------:"+rabbitProducerDataSource);
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(rabbitProducerDataSource);//让我们的数据源去建立连接
        initializer.setDatabasePopulator(databasePopulator());//之后执行Sql脚本
        return initializer;
    }

    private DatabasePopulator databasePopulator(){
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaScript);
        return populator;
    }
}
