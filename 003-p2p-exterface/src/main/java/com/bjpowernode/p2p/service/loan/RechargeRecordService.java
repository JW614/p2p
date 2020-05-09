package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;

import java.util.List;
import java.util.Map;

/**
 * ClassName:RechargeRecordService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/30 14:22
 * @author:动力节点
 */
public interface RechargeRecordService {

    /**
     * 根据用户标识获取最近充值记录
     * @param paramMap
     * @return
     */
    List<RechargeRecord> queryRecentlyRechargeRecordListByUid(Map<String, Object> paramMap);

    /**
     * 新增充值记录
     * @param rechargeRecord
     * @return
     */
    int addRechargeRecord(RechargeRecord rechargeRecord);

    /**
     * 根据充值订单号更新充值记录
     * @param rechargeRecord
     * @return
     */
    int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);

    /**
     * 用户充值
     * @param paramMap
     */
    void recharge(Map<String, Object> paramMap) throws Exception;

    /**
     * 处理掉单
     */
    void dealRechargeRecord() throws Exception;

    /**
     * 根据充值订单号查询订单详情
     * @param rechargeNo
     * @return
     */
    RechargeRecord queryRechargeRecordByRechargeNo(String rechargeNo);
}
