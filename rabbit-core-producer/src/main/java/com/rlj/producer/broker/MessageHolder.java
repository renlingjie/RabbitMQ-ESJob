package com.rlj.producer.broker;

import com.google.common.collect.Lists;
import com.rlj.api.message.Message;

import java.util.List;

/**
 * @author Renlingjie
 * @name
 * @date 2021-10-05
 */
public class MessageHolder {
    //用于做缓存，拿到ThreadLocal中的所有message
    private List<Message> messages = Lists.newArrayList();
    //之后使用ThreadLocal存储该线程所有发送的message
    @SuppressWarnings({"rawtypes","unchecked"})
    private static final ThreadLocal<MessageHolder> holder = new ThreadLocal(){
        @Override
        protected Object initialValue(){
            //我们的ThreadLocal对象，装的就是MessageHolder
            return new MessageHolder();
        }
    };
    public static void add(Message message){
        //先get.拿到ThreadLocal，之后再拿到ThreadLocal中的messages，然后往里面添加
        holder.get().messages.add(message);
    }
    //拿到messages，之后将holder中的messages清除
    public static List<Message> fetchAndClear(){
        List<Message> tmp = Lists.newArrayList(holder.get().messages);
        holder.remove();
        return tmp;
    }
}
