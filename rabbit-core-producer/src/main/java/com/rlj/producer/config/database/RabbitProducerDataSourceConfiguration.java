package com.rlj.producer.config.database;

/**
 * @author Renlingjie
 * @name 可靠性消息发送之前要落库，落库的数据源配置
 * @date 2021-09-18
 */
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.sql.SQLException;


@Configuration
//要加载的配置文件路径，因为都在同一个项目下，且在resources中，直接classpath即可
@PropertySource({"classpath:rabbit-producer-message.properties"})
public class RabbitProducerDataSourceConfiguration {
    private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RabbitProducerDataSourceConfiguration.class);

    @Value("${rabbit.producer.druid.type}")//拿到上面读取的配置文件的数据源类型
    private Class<? extends DataSource> dataSourceType;

    //主数据源，并命名为rabbitProducerDataSource
    @Bean(name = "rabbitProducerDataSource")
    @Primary
    //上面配置文件中的以rabbit.producer.druid.jdbc配置信息都会注入到DataSource对象rabbitProducerDataSource中
    @ConfigurationProperties(prefix = "rabbit.producer.druid.jdbc")
    public DataSource rabbitProducerDataSource() throws SQLException {
        DataSource rabbitProducerDataSource = DataSourceBuilder.create().type(dataSourceType).build();
        LOGGER.info("========== rabbitProducerDataSource:{} ==========",rabbitProducerDataSource);
        return rabbitProducerDataSource;
    }

    //定义可以获取当前的DataSourceProperties、DataSource的方法
    public DataSourceProperties primaryDataSourceProperties(){
        return new DataSourceProperties();
    }

    public DataSource primaryDataSource(){
        return primaryDataSourceProperties().initializeDataSourceBuilder().build();
    }
}
