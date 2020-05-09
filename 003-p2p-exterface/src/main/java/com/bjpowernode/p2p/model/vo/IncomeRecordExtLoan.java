package com.bjpowernode.p2p.model.vo;

import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import lombok.Data;


/**
 * ClassName:IncomeRecordExtLoan
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2020/4/30 14:42
 * @author:动力节点
 */
@Data
public class IncomeRecordExtLoan extends IncomeRecord {

    private LoanInfo loanInfo;

}
