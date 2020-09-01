package com.taotao.service;

public interface JedisClient {
    String set(String key,String value);
    String get(String key);
    //key存在
    Boolean exists(String key);
    //key过期时间
    Long expire(String key,int seconds);
    //key存活时间
    Long ttl(String key);
    //自增
    Long incr(String key);
    //hash散列
    Long hset(String key,String field,String value);
    //从hash散列取数据
    String hget(String key,String field);
    //删除key
    Long hdel(String key,String... field);
}
