package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.BidExtUser;
import com.bjpowernode.p2p.model.vo.BidLoanVO;
import com.bjpowernode.p2p.model.vo.BidUserTopVO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

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

    @Override
    public List<BidLoanVO> queryRecentlyBidInfoListByUid(Map<String, Object> paramMap) {
        return bidInfoMapper.selectRecentlyBidInfoListByUid(paramMap);
    }

    @Transactional
    @Override
    public void invest(Map<String, Object> paramMap) throws Exception {

        Integer uid = (Integer) paramMap.get("uid");
        Integer loanId = (Integer) paramMap.get("loanId");
        Double bidMoney = (Double) paramMap.get("bidMoney");
        String phone = (String) paramMap.get("phone");

        //如果在多线程高并发的时候会引发超卖现象
        //通过数据库乐观锁机制来解决以上问题
        //更新之前先获取产品的版本号
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        paramMap.put("version",loanInfo.getVersion());
        //更新产品剩余可投金额
        int updateLeftProductMoneyCount = loanInfoMapper.updateLeftProductMoney(paramMap);
        if (updateLeftProductMoneyCount < 0) {
            throw new Exception("更新产品剩余可投金额失败");
        }

        //更新帐户可用余额
        int updateFinanceAccountCount = financeAccountMapper.updateFinanceAccountByBid(paramMap);
        if (updateFinanceAccountCount < 0) {
            throw new Exception("更新帐户可用余额失败");
        }

        //新增投资记录
        BidInfo bidInfo = new BidInfo();
        bidInfo.setUid(uid);
        bidInfo.setLoanId(loanId);
        bidInfo.setBidMoney(bidMoney);
        bidInfo.setBidTime(new Date());
        bidInfo.setBidStatus(1);
        int insertBidInfoCount = bidInfoMapper.insertSelective(bidInfo);
        if (insertBidInfoCount < 0) {
            throw new Exception("新增投资记录失败");
        }

        //再次查询产品详情
        LoanInfo loanInfoDetail = loanInfoMapper.selectByPrimaryKey(loanId);

        //判断产品是否满标
        if (0 == loanInfoDetail.getLeftProductMoney()) {

            //更新产品状态及满标时间
            LoanInfo updateLoanInfo = new LoanInfo();
            updateLoanInfo.setId(loanId);
            updateLoanInfo.setProductFullTime(new Date());
            updateLoanInfo.setProductStatus(1);
            int i = loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
            if (i < 0) {
                throw new Exception("新增投资记录失败");
            }
        }

        //将投资的信息存放到redis缓存中
        redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP,phone,bidMoney);

    }

    @Override
    public List<BidUserTopVO> queryBidUserTop() {
        List<BidUserTopVO> bidUserTopVOList = new ArrayList<BidUserTopVO>();

        //从redis缓存中获取年投资排行榜
        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 5);

        //获取set集合的迭代器
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = set.iterator();

        //循环遍历
        while (iterator.hasNext()) {

            ZSetOperations.TypedTuple<Object> next = iterator.next();
            String phone = (String) next.getValue();
            Double score = next.getScore();

            BidUserTopVO bidUserTopVO = new BidUserTopVO();
            bidUserTopVO.setPhone(phone);
            bidUserTopVO.setScore(score);

            bidUserTopVOList.add(bidUserTopVO);
        }


        return bidUserTopVOList;
    }
}
