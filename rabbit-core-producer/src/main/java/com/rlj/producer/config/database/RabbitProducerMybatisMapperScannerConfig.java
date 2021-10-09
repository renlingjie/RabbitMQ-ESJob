package com.rlj.producer.config.database;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-18
 */
@Configuration
//等RabbitProducerMybatisConfiguration配置，并生成SqlSessionFactory后再配置这个
@AutoConfigureAfter(RabbitProducerDataSourceConfiguration.class)
public class RabbitProducerMybatisMapperScannerConfig {
    @Bean(name = "rabbitProducerMapperScannerConfigurer")
    public MapperScannerConfigurer rabbitProducerMapperScannerConfigurer(){
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("rabbitProducerSqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.rlj.producer.mapper");
        return mapperScannerConfigurer;
    }
}
