package com.rlj.api.exception;

/**
 * @author Renlingjie
 * @name 项目的常规异常
 * @date 2021-09-07
 */
public class MessageCommonException extends Exception{

    private static final long serialVersionUID = 2083125770315827213L;
    //四种抛异常时构建该异常的方式：直接抛、抛的时候带提示信息、抛的时候带异常信息、带提示信息和异常信息
    public MessageCommonException(){
        super();
    }
    public MessageCommonException(String message){
        super(message);
    }
    public MessageCommonException(Throwable cause){
        super(cause);
    }
    public MessageCommonException(String message, Throwable cause){
        super(message,cause);
    }
}
