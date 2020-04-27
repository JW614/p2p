package com.bjpowernode.p2p.service.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.ObjIntConsumer;

/**
 * ClassName:UserServiceImpl
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 *
 * @date:2020/4/27 9:24
 * @author:动力节点
 */
@Component
@Service(interfaceClass = UserService.class,version = "1.0.0",timeout = 15000)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Long queryAllUserCount() {

        /*//首先去redis缓存中查询
        Long allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);

        //判断是否有值
        if (!ObjectUtils.allNotNull(allUserCount)) {

            //没有:去数据库查询,并存放到redis缓存中
            //去数据库查询
            allUserCount = userMapper.selectAllUserCount();

            //并存放到redis缓存中
            redisTemplate.opsForValue().set(Constants.ALL_USER_COUNT,allUserCount,15, TimeUnit.SECONDS);
        }*/

        //以上写法在多线程高并的时候会出现缓存穿透

        //获取操作string数据类型的操作对象,该操作对象需要首先绑定指定的key
        BoundValueOperations<Object, Object> boundValueOperations = redisTemplate.boundValueOps(Constants.ALL_USER_COUNT);

        //首先去redis缓存中获取该值
        Long allUserCount = (Long) boundValueOperations.get();

        //判断是否有值
        if (!ObjectUtils.allNotNull(allUserCount)) {

            //设置同步代码块
            synchronized (this) {

                //再次从redis缓存中获取该值
                allUserCount = (Long) boundValueOperations.get();

                //再次判断是否有值
                if (!ObjectUtils.allNotNull(allUserCount)) {

                    //去数据库查询
                    allUserCount = userMapper.selectAllUserCount();

                    //并存放到redis缓存中
                    boundValueOperations.set(allUserCount,15,TimeUnit.SECONDS);

                }


            }

        }



        //有:直接返回
        return allUserCount;
    }
}
