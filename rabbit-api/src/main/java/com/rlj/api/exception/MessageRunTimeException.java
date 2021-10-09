package com.rlj.api.exception;

/**
 * @author Renlingjie
 * @name 运行时自定义异常
 * @date 2021-09-07
 */
public class MessageRunTimeException extends RuntimeException{
    private static final long serialVersionUID = -6307965383310914362L;
    public MessageRunTimeException(){
        super();
    }
    public MessageRunTimeException(String message){
        super(message);
    }
    public MessageRunTimeException(Throwable cause){
        super(cause);
    }
    public MessageRunTimeException(String message,Throwable cause){
        super(message,cause);
    }
}
