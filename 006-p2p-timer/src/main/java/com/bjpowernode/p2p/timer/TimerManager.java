package com.bjpowernode.p2p.timer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ClassName:TimerManager
 * Package:com.bjpowernode.p2p.timer
 * Description:
 *
 * @date:2020/5/6 14:04
 * @author:动力节点
 */
@Component
@Slf4j
public class TimerManager {

    @Reference(interfaceClass = IncomeRecordService.class,version = "1.0.0",check = false)
    private IncomeRecordService incomeRecordService;

    @Reference(interfaceClass = RechargeRecordService.class,version = "1.0.0",check = false)
    private RechargeRecordService rechargeRecordService;


//    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomePlan() throws Exception {
        log.info("-----------生成收益计划开始--------------");

        incomeRecordService.generateIncomePlan();

        log.info("-----------生成收益计划结束--------------");
    }

//    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomeBack() throws Exception {
        log.info("-----------收益返还开始--------------");

        incomeRecordService.generateIncomeBack();

        log.info("-----------收益返还结束--------------");
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void dealRechargeRecord() throws Exception {
        log.info("-----------处理掉单开始--------------");

        rechargeRecordService.dealRechargeRecord();

        log.info("-----------处理掉单结束--------------");
    }
}
