package com.scc.toolutils.javautils;

import com.scc.toolutils.redis.JedisUtil;

/** 
 * @ClassName: RetryHelper 
 * @Description: 重试类
 */
public class RetryHelper {
	
	//指定异常的重试 如超时
	public static <T> T retryExecute(RetryWrapper<T> operator,int retryNum){
		for(int index = retryNum -1 ;index >= 0; index-- ){
			if(index == Constants.CONSTANT_ZERO_INT){
				return operator.invoke();
			}else{
				try{
					return operator.invoke();
				}catch(Exception ex){
					if(!operator.retryStrategy(ex)){
						throw new RuntimeException(ex);
					}
				}
			}
		}
		return null;
	}
	
	//控制重试次数
	public static <T> void holdRetryNum(RetryWrapper<T> operator,int retryNum){
		Long cacheNum = JedisUtil.getJedisInstance().execIncrToCache(operator.getCacheKey());
		JedisUtil.getJedisInstance().execExpireToCache(operator.getCacheKey(), operator.getExpireTime());
		if(cacheNum <= retryNum){
			operator.invoke();
		}
	}
}
