package com.rlj.task.annotation;

import com.rlj.task.autoconfigure.JobParserAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-25
 */
@Target(ElementType.TYPE)//在哪都能用
@Retention(RetentionPolicy.RUNTIME)//注解在运行时生效
@Documented
@Inherited
@Import(JobParserAutoConfiguration.class)
public @interface EnableElasticJob {

}
