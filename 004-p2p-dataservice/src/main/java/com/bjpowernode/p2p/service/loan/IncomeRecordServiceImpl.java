package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.IncomeRecordExtLoan;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.Configuration;
import javax.xml.crypto.Data;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:IncomeRecordServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/30 14:38
 * @author:动力节点
 */
@Component
@Service(interfaceClass = IncomeRecordService.class, version = "1.0.0",timeout = 15000)
public class IncomeRecordServiceImpl implements IncomeRecordService {

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public List<IncomeRecordExtLoan> queryRecentlyIncomeRecordListByUid(Map<String, Object> paramMap) {
        return incomeRecordMapper.selectRecentlyIncomeRecordListByUid(paramMap);
    }

    @Transactional
    @Override
    public void generateIncomePlan() throws Exception {

        //查询产品状态为1已满标的产品 -> 返回List<已满标产品>
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByProductStatus(1);

        //循环遍历，获取到每个已满标产品
        for (LoanInfo loanInfo : loanInfoList) {

            //根据产品标识获取产品的所有投资记录 -> 返回List<投资记录>
            List<BidInfo> bidInfoList = bidInfoMapper.selectAllBidInfoListByLoanId(loanInfo.getId());

            //循环遍历，获取到每一条投资记录
            for (BidInfo bidInfo : bidInfoList) {

                //将当前的投资记录生成对应的收益计划
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setLoanId(loanInfo.getId());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(0);//0未返还，1已返还

                //判断当前产品类型
                //收益金额 = 投资金额 * 日利率 * 投资天数
                Double incomeMoney = null;

                //收益时间(Date) = 满标时间(Date) + 产品周期(int)天|月
                Date incomeDate = null;
                if (Constants.PRODUCT_TYPE_X == loanInfo.getProductType()) {
                    //新手宝
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle();
                    incomeDate = DateUtils.addDays(loanInfo.getProductFullTime(),loanInfo.getCycle());
                } else {
                    //优选或散标
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle()*30;
                    incomeDate = DateUtils.addMonths(loanInfo.getProductFullTime(),loanInfo.getCycle());
                }
                incomeMoney = Math.round(Math.pow(10,2)*incomeMoney)/Math.pow(10,2);

                incomeRecord.setIncomeMoney(incomeMoney);
                incomeRecord.setIncomeDate(incomeDate);

                int i = incomeRecordMapper.insertSelective(incomeRecord);
                if (i < 0) {
                    throw new Exception();
                }


            }
            //更新当前产品的状态为2满标且生成收益计划
            LoanInfo loanDetail = new LoanInfo();
            loanDetail.setId(loanInfo.getId());
            loanDetail.setProductStatus(2);
            int updateLoanStatus = loanInfoMapper.updateByPrimaryKeySelective(loanDetail);
            if (updateLoanStatus < 0) {
                throw new Exception();
            }
        }
    }

    @Transactional
    @Override
    public void generateIncomeBack() throws Exception {

        //查询收益状态为0且收益时间与当前时间一致的收益记录
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordListByIncomeStatusAndCurDate(0);

        Map<String,Object> paramMap = new HashMap<String, Object>();

        //循环遍历收益记录，获取到每一条收益记录
        for (IncomeRecord incomeRecord : incomeRecordList) {

            paramMap.put("uid",incomeRecord.getUid());
            paramMap.put("bidMoney",incomeRecord.getBidMoney());
            paramMap.put("incomeMoney",incomeRecord.getIncomeMoney());

            //将此收益返还对应的帐户
            int updateCount = financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);
            if (updateCount < 0) {
                throw new Exception();
            }

            //将当前收益的状态更新1已返还
            IncomeRecord updateIncome = new IncomeRecord();
            updateIncome.setId(incomeRecord.getId());
            updateIncome.setIncomeStatus(1);
            int i = incomeRecordMapper.updateByPrimaryKeySelective(updateIncome);
            if (i < 0) {
                throw new Exception();
            }
        }

    }
}














