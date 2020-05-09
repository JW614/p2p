package com.bjpowernode.p2p.mapper.user;

import com.bjpowernode.p2p.model.user.FinanceAccount;

import java.util.Map;

public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    /**
     * 根据用户标识获取帐户信息
     * @param uid
     * @return
     */
    FinanceAccount selectFinanceAccountByUid(Integer uid);

    /**
     * 更新帐户可用余额（用户投资）
     * @param paramMap
     * @return
     */
    int updateFinanceAccountByBid(Map<String, Object> paramMap);

    /**
     * 更新帐户可用余额（收益返还）
     * @param paramMap
     * @return
     */
    int updateFinanceAccountByIncomeBack(Map<String, Object> paramMap);

    /**
     * 更新帐户可用余额（用户充值）
     * @param paramMap
     * @return
     */
    int updateFinanceAccountByRecharge(Map<String, Object> paramMap);
}