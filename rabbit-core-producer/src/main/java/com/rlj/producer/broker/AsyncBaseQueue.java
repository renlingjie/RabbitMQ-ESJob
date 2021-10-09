package com.rlj.producer.broker;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author Renlingjie
 * @name 异步队列发送消息
 * @date 2021-09-09
 */
@Slf4j
public class AsyncBaseQueue {
    //下面线程池所需的参数：核心线程数、队列大小、线程工厂
    private static final int THREAD_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int QUEUE_SIZE = 10000;
    private static ThreadFactory nameFactory = new ThreadFactoryBuilder()
            .setNameFormat("rabbitmq_client_async_sender").build();
    //私有化的创建一个线程池
    private static ExecutorService senderAsync =
            new ThreadPoolExecutor(
                    THREAD_SIZE, THREAD_SIZE * 2, 60L,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_SIZE),
                    nameFactory, new ThreadPoolExecutor.DiscardPolicy());
    //对外暴露的公共方法--->你过来一个线程，我在这个类中调用上面的私有化线程池给你处理
    public static void submit(Runnable runnable){
        senderAsync.submit(runnable);
    }
}
