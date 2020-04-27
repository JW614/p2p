package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.vo.BidExtUser;

import java.util.List;
import java.util.Map;

/**
 * ClassName:BidInfoService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/27 9:40
 * @author:动力节点
 */
public interface BidInfoService {

    /**
     * 获取平台累计投资金额
     * @return
     */
    Double queryAllBidMoney();

    /**
     * 根据产品标识获取最近的投资记录(包含:用户信息)
     * @param paramMap
     * @return
     */
    List<BidExtUser> queryRecentlyBidInfoListByLoanId(Map<String, Object> paramMap);
}
