package com.rlj.producer.config.database;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-18
 */
@Configuration
//必须等RabbitProducerDataSourceConfiguration配置完后再进行此配置--得先有DataSource注入进来，否则空指针
@AutoConfigureAfter(value = {RabbitProducerDataSourceConfiguration.class})
public class RabbitProducerMybatisConfiguration {
    @Resource(name = "rabbitProducerDataSource")
    private DataSource rabbitProducerDataSource;

    @Bean(name = "rabbitProducerSqlSessionFactory")
    public SqlSessionFactory rabbitProducerSqlSessionFactory(DataSource rabbitProducerDataSource){
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(rabbitProducerDataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            //mapper中的方法要和xml对应上，从而让Mybatis去解析，所以指定xml位置
            bean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
            SqlSessionFactory sqlSessionFactory = bean.getObject();
            sqlSessionFactory.getConfiguration().setCacheEnabled(Boolean.TRUE);
            return sqlSessionFactory;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
