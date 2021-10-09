package com.rlj.esjob.config;

import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-20
 */
//将任务执行的一些信息放到数据库中(ESJob会自动为我们建两张表)
//@Configuration
public class JobEventConfig {
    @Autowired
    private DataSource dataSource;
    @Bean
    public JobEventConfiguration jobEventConfiguration(){
        return new JobEventRdbConfiguration(dataSource);
    }
}
