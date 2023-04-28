package com.scc.toolutils.javautils;
/**
 *
* @ClassName: RetryWrapper
* @Description: 重试包装类
 */

public abstract class RetryWrapper<T>{
	
	//调用方法
	public abstract T invoke();
	
	//是否重试策略
	public boolean retryStrategy(Exception ex){
		Throwable cause = ex.getCause();
		return RespPackUtil.isTimeoutThrowable(cause);
	}

	//控制重试次数key
	public String getCacheKey(){
		return null;
	}
	
	//期满时间
	public int getExpireTime(){
		return Constants.REDIS_ONEDAY;
	}
}
