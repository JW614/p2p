package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.vo.IncomeRecordExtLoan;

import java.util.List;
import java.util.Map;

/**
 * ClassName:IncomeRecordService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/30 14:36
 * @author:动力节点
 */
public interface IncomeRecordService {

    /**
     * 根据用户标识获取最近的收益记录
     * @param paramMap
     * @return
     */
    List<IncomeRecordExtLoan> queryRecentlyIncomeRecordListByUid(Map<String, Object> paramMap);

    /**
     * 生成收益计划
     */
    void generateIncomePlan() throws Exception;

    /**
     * 收益返还
     */
    void generateIncomeBack() throws Exception;
}
