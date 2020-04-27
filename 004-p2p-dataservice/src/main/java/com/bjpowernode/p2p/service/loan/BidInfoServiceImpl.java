package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.model.vo.BidExtUser;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:BidInfoServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/27 9:41
 * @author:动力节点
 */
@Component
@Service(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 15000)
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Double queryAllBidMoney() {

        //首先去redis缓存中获取该值
        Double allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);

        //判断是否有值
        if (!ObjectUtils.allNotNull(allBidMoney)) {

            //设置同步代码块
            synchronized (this) {

                //再次从redis缓存中获取该值
                allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);

                //再次判断是否有值
                if (!ObjectUtils.allNotNull(allBidMoney)) {

                    //去数据库查询
                    allBidMoney = bidInfoMapper.selectAllBidMoney();

                    //并存放到redis缓存中
                    redisTemplate.opsForValue().set(Constants.ALL_BID_MONEY,allBidMoney,15, TimeUnit.SECONDS);
                }

            }
        }



        return allBidMoney;
    }

    @Override
    public List<BidExtUser> queryRecentlyBidInfoListByLoanId(Map<String, Object> paramMap) {
        return bidInfoMapper.selectRecentlyBidInfoListByLoanId(paramMap);
    }
}
