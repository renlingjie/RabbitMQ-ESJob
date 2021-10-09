package com.rlj.producer.autoconfigure;

import com.rlj.task.annotation.EnableElasticJob;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Renlingjie
 * @name 自动装配
 * @date 2021-09-08
 */
@EnableElasticJob
@Configuration
@ComponentScan({"com.rlj.producer.*"})
public class RabbitProducerAutoConfiguration {
}
