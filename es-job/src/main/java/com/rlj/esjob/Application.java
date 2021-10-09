package com.rlj.esjob;

import com.rlj.task.annotation.EnableElasticJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-20
 */
//"com.rlj.esjob.service.*","com.rlj.esjob.annotation.*","com.rlj.esjob.task.*"
@SpringBootApplication
@EnableElasticJob
@ComponentScan(basePackages = {"com.rlj.esjob.*"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
