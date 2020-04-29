package com.bjpowernode.p2p.service.loan;

/**
 * ClassName:RedisService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/29 10:55
 * @author:动力节点
 */
public interface RedisService {

    /**
     * 将值存放到redis缓存中(Redis数据类型为：String)
     * @param key
     * @param value
     */
    void put(String key, String value);

    /**
     * 获取指定key的值(Redis数据类型为：String)
     * @param key
     * @return
     */
    String get(String key);
}
