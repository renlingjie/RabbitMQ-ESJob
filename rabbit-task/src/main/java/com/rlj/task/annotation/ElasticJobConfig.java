package com.rlj.task.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-25
 */
@Target(ElementType.TYPE)//在哪都能用
@Retention(RetentionPolicy.RUNTIME)//注解在运行时生效
public @interface ElasticJobConfig {
    String name(); //ESJob名称
    String corn() default "";
    String shardingItemParameters() default "";
    String jobParameter() default "";
    String eventTraceRdbDataSource() default "";
    String scriptCommandLine() default "";//脚本作业执行命令行
    String description() default "";
    String listener() default "";
    String distributeListener() default "";
    String jobShardingStrategyClass() default "";//作业分片策略实现类全路径,默认使用平均分配策略
    String jobExceptionHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultJobExceptionHandler";
    String executorServiceHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultExecutorServiceHandler";

    boolean failover() default false;
    boolean misfire() default true;
    boolean overwrite() default false;
    //是否流式处理数据，如果是, 则fetchData不返回空结果将持续执行作业，如果不是则处理数据完成后作业结束
    boolean streamingProcess() default false;
    boolean monitorExecution() default false;
    boolean disabled() default false;

    int shardingTotalCount() default 1;
    //作业监控端口,建议配置作业监控端口, 方便开发者dump作业信息。
    int monitorPort() default -1;
    //最大允许的本机与注册中心的时间误差秒数,如果时间误差超过配置秒数则作业启动时将抛异常,配置为-1表示不校验时间误差
    int maxTimeDiffSeconds() default -1;
    //修复作业服务器不一致状态服务调度间隔时间，配置为小于1的任意值表示不执行修复。单位：分钟
    int reconcileIntervalMinutes() default 10;

    long startedTimeoutMilliseconds() default Long.MAX_VALUE;
    long completedTimeoutMilliseconds() default Long.MAX_VALUE;

}
