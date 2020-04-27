package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:LoanInfoServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/25 14:14
 * @author:动力节点
 */
@Component
@Service(interfaceClass = LoanInfoService.class,version = "1.0.0",timeout = 15000)
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Double queryHistoryAverageRate() {

        //将数据存放到Redis缓存中目的:提升系统性能,提高用户的检验,减少后台服务器的压力
        //修改redisTemplates对象key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //首先去redis缓存中获取历史平均年化收益率
        /*Double historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);

        //判断是否有值
        if (!ObjectUtils.allNotNull(historyAverageRate)) {

            System.out.println("从数据库中查询.........");

            //如果没有:去数据库查询,并存放到redis缓存
            historyAverageRate = loanInfoMapper.selectHistoryAverageRate();

            //将该值存放到redis缓存中
            redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE,historyAverageRate,15, TimeUnit.SECONDS);

        } else {
            System.out.println("从Redis中查询.........");
        }*/

        //以上代码在多线程高并发的时候会出现一种现象:缓存穿透
        //我们通过"双重检测+同步代码块"的方式就可以解决缓存穿透现象

        //首先去redis缓存中获取该值
        Double historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);

        //第一次判断:判断是否有值
        if (!ObjectUtils.allNotNull(historyAverageRate)) {

            //设置同步代码块
            synchronized (this) {

                //再次从redis缓存中获取该值
                historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);

                //再次判断是否有值
                if (!ObjectUtils.allNotNull(historyAverageRate)) {
                    System.out.println("从数据库中获取该值.............");

                    //去数据库查询
                    historyAverageRate = loanInfoMapper.selectHistoryAverageRate();

                    //并存放到redis缓存中
                    redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE, historyAverageRate, 15, TimeUnit.SECONDS);

                } else {
                    System.out.println("从Redis缓存中获取该值.............");
                }

            }

        } else {
            System.out.println("从Redis缓存中获取该值.............");
        }




        //如果有:直接返回
        return historyAverageRate;
    }

    @Override
    public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {
        return loanInfoMapper.selectLoanInfoListByProductType(paramMap);
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap) {

        PaginationVO<LoanInfo> paginationVO = new PaginationVO<LoanInfo>();

        //获取产品的总记录数
        Long total = loanInfoMapper.selectTotal(paramMap);
        paginationVO.setTotal(total);

        //查询每页显示的数据
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByProductType(paramMap);
        paginationVO.setDataList(loanInfoList);

        return paginationVO;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id) {
        return loanInfoMapper.selectByPrimaryKey(id);
    }
}
