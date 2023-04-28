package com.scc.toolutils.exceptions;

/**
 * @author : scc
 * @date : 2023/04/28
 **/
public class RedisClientException extends RuntimeException{
    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = 1027452261936311766L;

    /**
     * 构造异常对象
     *
     * @param msg
     */
    public RedisClientException(String msg) {
        super(msg);
    }

    /**
     * RedisClientException
     *
     * @param exception
     */
    public RedisClientException(Throwable exception) {
        super(exception);
    }

    /**
     * RedisClientException
     *
     * @param mag
     * @param exception
     */
    public RedisClientException(String mag, Exception exception) {
        super(mag, exception);
    }

}
