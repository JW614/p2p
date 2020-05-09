package com.bjpowernode.p2p.mapper.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;

import java.util.List;
import java.util.Map;

public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);

    /**
     * 根据用户标识获取最近充值记录
     * @param paramMap
     * @return
     */
    List<RechargeRecord> selectRecentlyRechargeRecordListByUid(Map<String, Object> paramMap);

    /**
     * 根据充值订单号更新充值记录
     * @param rechargeRecord
     * @return
     */
    int updateRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);

    /**
     * 根据充值记录状态获取列表
     * @param rechargeStatus
     * @return
     */
    List<RechargeRecord> selectRechargeRecordListByRechargeStatus(String rechargeStatus);

    /**
     * 根据充值订单号查询详情
     * @param rechargeNo
     * @return
     */
    RechargeRecord selectRechargeCordByRechargeNo(String rechargeNo);
}