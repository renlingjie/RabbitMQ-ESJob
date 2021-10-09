package com.rlj.test;

import com.rlj.api.message.Message;
import com.rlj.api.message.MessageType;
import com.rlj.producer.broker.ProducerClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Renlingjie
 * @name
 * @date 2021-09-30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    @Autowired
    private ProducerClient producerClient;
    @Test
    public void testProducerClient() throws Exception{
        for (int i = 0; i < 1; i ++){
            String uniqueId = UUID.randomUUID().toString();
            Map<String,Object> attributes = new HashMap<>();
            attributes.put("name","张三");
            attributes.put("age","18");
            //这里指定发送的是可靠性消息，也就是说只需编辑好message内容，并指定消息类型，就可以找到对应类型的发送方法将消息发送出去
            Message message = new Message.MessageBuilder(uniqueId,"FinalExchange1").withRoutingKey("rlj.abc")
                    .withAttributes(attributes).withDelayMills(0).withMessageType(MessageType.RELIANT).finalBuild();
            producerClient.send(message);
        }
        Thread.sleep(1000000);
    }
}
