package com.rlj.task.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.rlj.task.annotation.ElasticJobConfig;
import com.rlj.task.annotation.EnableElasticJob;
import com.rlj.task.autoconfigure.JobZookeeperProperties;
import com.rlj.task.enums.ElasticJobTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Renlingjie
 * @name 专门解析ElasticJobConfig注解定义的几十个属性
 * @date 2021-09-26
 */

/**
 * 1、最终的配置，要求在其他配置完成之后，所以继承ApplicationListener<ApplicationReadyEvent>，字面意思都能看出来，是在Application初始化完成后触发的事件
 * 2、由getBeansWithAnnotation拿到被@ElasticJobConfig修饰的所有任务对象（已经编译的.class）的Map集合
 * 3、循环，对每次拿到的已经编译任务对象clazz：
 *     I、根据clazz.getAnnotation(ElasticJobConfig.class)拿到修饰在该对象上的配置注解，进而拿到注解中的参数A
 *     II、参数A作参数创建核心配置类JobCoreConfiguration的对象B
 *     III、根据clazz.getInterfaces()[0].getSimpleName()拿到该任务对象clazz实现的接口，知道它是什么任务，上面的A、B作为参数创建出这个任务对象C（XXXJobConfiguration）
 *     IV、参数A和任务对象C作为参数创建ESJob的LiteJobConfiguration的对象D
 *     V、最后就是创建SpringJobScheduler， 并执行其init()。这里的创建不是直接new SpringJobScheduler，因为不确定是否有监听、数据轨迹这两个可选参数，
 *     所以使用BeanDefinitionBuilder建造者E，指定建造的对象SpringJobScheduler.class，之后和new SpringJobScheduler的参数一样，使用E.addConstructorArgValue添加参数，
 *     这样监听、数据跟踪这两个参数根据有无确定是否添加，最终创建SpringJobScheduler并init()
 */
@Slf4j
//强依赖Spring，需要在某个时机为我们做MySimpleJob中的一系列配置，这个时机就是当Spring的所有Bean加载完成后(ApplicationReadyEvent)
//也就是所有的都初始化完成了，为什么，下面很直白的一个例子，要拿到ElasticJobConfig注解中的所有的Bean，什么时候拿？必然是在最后，因此其
//他时间段它可能没初始化好，拿到的可能不全
public class ElasticJobConfigParser implements ApplicationListener<ApplicationReadyEvent> {
    private JobZookeeperProperties jobZookeeperProperties;
    private ZookeeperRegistryCenter zookeeperRegistryCenter;
    public ElasticJobConfigParser(JobZookeeperProperties jobZookeeperProperties, ZookeeperRegistryCenter zookeeperRegistryCenter) {
        this.jobZookeeperProperties = jobZookeeperProperties;
        this.zookeeperRegistryCenter = zookeeperRegistryCenter;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            ApplicationContext applicationContext = event.getApplicationContext();
            //拿到ElasticJobConfig注解中的所有的Bean
            Map<String,Object> beanMap = applicationContext.getBeansWithAnnotation(ElasticJobConfig.class);
            //然后for循环读里面所有Bean的配置
            for (Iterator<Object> iterator = beanMap.values().iterator();iterator.hasNext();){
                Object configBean = iterator.next();
                //拿到当前"被注解ElasticJobConfig修饰"的这个类的"描述类"(类的一些信息如编译后的.class文件的name会放到这个类中)
                Class<?> clazz = configBean.getClass();
                //因为编译后每个类对应的.clsaa的名称都是类似于"类名+$+数字/类名"的格式，$前面的就是真正的类名
                if (clazz.getName().indexOf("$") > 0){
                    String className = clazz.getName();
                    clazz = Class.forName(className.substring(0,className.indexOf("$")));
                }
                //获取接口类型，用于判断是什么类型的任务
                String jobTypeName = clazz.getInterfaces()[0].getSimpleName();
                //获取配置项ElasticJobConfig
                ElasticJobConfig config = clazz.getAnnotation(ElasticJobConfig.class);
                String jobClass = clazz.getName();
                String jobName = this.jobZookeeperProperties.getNamespace() + "." + config.name();
                String corn = config.corn();
                String shardingItemParameters = config.shardingItemParameters();
                String description = config.description();
                String jobParameter = config.jobParameter();
                String jobExceptionHandler = config.jobExceptionHandler();
                String executorServiceHandler = config.executorServiceHandler();
                String jobShardingStrategyClass = config.jobShardingStrategyClass();
                String scriptCommandLine = config.scriptCommandLine();
                String eventTraceRdbDataSource = config.eventTraceRdbDataSource();

                boolean failover = config.failover();
                boolean misfire = config.misfire();
                boolean overwrite = config.overwrite();
                boolean disabled = config.disabled();
                boolean monitorExecution = config.monitorExecution();
                boolean streamingProcess = config.streamingProcess();

                int shardingTotalCount = config.shardingTotalCount();
                int monitorPort = config.monitorPort();
                int maxTimeDiffSeconds = config.maxTimeDiffSeconds();
                int reconcileIntervalMinutes = config.reconcileIntervalMinutes();

                //先创建当当网的核心配置对象JobCoreConfiguration(一些最核心的配置)
                JobCoreConfiguration coreConfig = JobCoreConfiguration
                        .newBuilder(jobName,corn,shardingTotalCount).shardingItemParameters(shardingItemParameters)
                        .description(description).failover(failover).jobParameter(jobParameter).misfire(misfire)
                        .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),jobExceptionHandler)
                        .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(),executorServiceHandler)
                        .build();

                //之后去确定创建什么类型的任务
                JobTypeConfiguration typeConfig = null;
                if (ElasticJobTypeEnum.SIMPLE.getType().equals(jobTypeName)){
                    typeConfig = new SimpleJobConfiguration(coreConfig,jobClass);
                }
                if (ElasticJobTypeEnum.DATAFLOW.getType().equals(jobTypeName)){//第三个参数：是否进行实时的流处理
                    typeConfig = new DataflowJobConfiguration(coreConfig,jobClass,streamingProcess);
                }
                if (ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)){
                    typeConfig = new ScriptJobConfiguration(coreConfig,scriptCommandLine);
                }
                //之后还是当当网的事情：创建LiteJobConfiguration
                LiteJobConfiguration liteConfig = LiteJobConfiguration.newBuilder(typeConfig)
                        .overwrite(overwrite).disabled(disabled).monitorPort(monitorPort)
                        .monitorExecution(monitorExecution).maxTimeDiffSeconds(maxTimeDiffSeconds)
                        .jobShardingStrategyClass(jobShardingStrategyClass)
                        .reconcileIntervalMinutes(reconcileIntervalMinutes).build();
                //LiteJobConfiguration创建出来后，就可以创建最终的SpringJobScheduler
                //创建一个spring的beanDefinition
                BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
                factory.setInitMethodName("init");
                //prototype多例，singleton单例，这里因为一个代码加上注解就会自动装配一个任务，就会执行到这里一次，必然是多例模式
                factory.setScope("prototype");
                /**
                 * 之后添加rootBeanDefinition指定的Bean的构造参数，之前在构造SpringJobScheduler传的五个参数分别是：某类型Job实现类、
                 * 注册中心、当当网的LiteJobConfiguration、存储在数据库的日志跟踪JobEventConfiguration的实现类(可不要),
                 * 任务监听ElasticJobListener的实现类(可不要)
                 */
                //只要不是脚本类型的就是Java，那就可以添加进去
                if (!ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)){
                    factory.addConstructorArgValue(configBean);
                }
                factory.addConstructorArgValue(this.zookeeperRegistryCenter);
                factory.addConstructorArgValue(liteConfig);
                //数据库参数也有，那就可以创建日志跟踪对象
                if (StringUtils.hasText(eventTraceRdbDataSource)){
                    BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
                    rdbFactory.addConstructorArgReference(eventTraceRdbDataSource);
                    factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
                }
                //任务监听也有，那就也添加进去
                List<?> elasticJobListeners = getTargetElasticJobListeners(config);
                factory.addConstructorArgValue(elasticJobListeners);

                //接下来就是把SpringJobScheduler对象factory注入到IOC中
                DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
                String registryBeanName = config.name() + "SpringJobScheduler";
                defaultListableBeanFactory.registerBeanDefinition(registryBeanName,factory.getBeanDefinition());
                SpringJobScheduler springJobScheduler = (SpringJobScheduler) applicationContext.getBean(registryBeanName);
                springJobScheduler.init();
                log.info("启动elastic-job作业："+jobName);
            }
            log.info("共计启动elastic-job作业数 {} 个",beanMap.values().size());
        }catch (Exception e){
            log.error("elastic-job启动异常，系统强制退出",e);
            System.exit(1);
        }
    }

    private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConfig config){
        //配置文件传过来的可能有listener()、distributeListener()两种，分别判断，有则创建出对象放到ManagedList<BeanDefinition>
        //这个BeanDefinition容器中，这个容器容量为2(因为最多就是两者都有)
        List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
        //普通监听判断
        String listeners = config.listener();
        if (StringUtils.hasText(listeners)){
            BeanDefinitionBuilder listenFactory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
            listenFactory.setScope("prototype");
            result.add(listenFactory.getBeanDefinition());
        }
        //分布式监听判断
        String distributeListeners = config.distributeListener();
        long startedTimeoutMilliseconds = config.startedTimeoutMilliseconds();
        long completedTimeoutMilliseconds = config.completedTimeoutMilliseconds();
        if (StringUtils.hasText(distributeListeners)){
            BeanDefinitionBuilder listenFactory = BeanDefinitionBuilder.rootBeanDefinition(distributeListeners);
            listenFactory.setScope("prototype");
            listenFactory.addConstructorArgValue(Long.valueOf(startedTimeoutMilliseconds));
            listenFactory.addConstructorArgValue(Long.valueOf(completedTimeoutMilliseconds));
            result.add(listenFactory.getBeanDefinition());
        }
        return result;
    }
}
