package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.user.FinanceAccount;

/**
 * ClassName:FinanceAccountService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/30 9:19
 * @author:动力节点
 */
public interface FinanceAccountService {

    /**
     * 根据用户标识获取帐户信息
     * @param uid
     * @return
     */
    FinanceAccount queryFinanceAccountByUid(Integer uid);
}
