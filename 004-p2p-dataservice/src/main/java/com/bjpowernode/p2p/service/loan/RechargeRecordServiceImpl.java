package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.common.util.HttpClientUtils;
import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:RechargeRecordServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/30 14:23
 * @author:动力节点
 */
@Component
@Service(interfaceClass = RechargeRecordService.class, version = "1.0.0", timeout = 15000)
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public List<RechargeRecord> queryRecentlyRechargeRecordListByUid(Map<String, Object> paramMap) {
        return rechargeRecordMapper.selectRecentlyRechargeRecordListByUid(paramMap);
    }

    @Override
    public int addRechargeRecord(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    @Override
    public int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
    }

    @Transactional
    @Override
    public void recharge(Map<String, Object> paramMap) throws Exception {

        //更新帐户可用余额
        int updateFinanceCount = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);
        if (updateFinanceCount < 0) {
            throw new Exception();
        }

        //更新充值记录的状态为1充值成功
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setRechargeNo((String) paramMap.get("rechargeNo"));
        rechargeRecord.setRechargeStatus("1");
        int updateRechargeCount = rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
        if (updateRechargeCount < 0) {
            throw new Exception();
        }

    }

    @Transactional
    @Override
    public void dealRechargeRecord() throws Exception {
        //查询充值记录状态为0的异常充值记录 -> 返回List<充值记录>
        List<RechargeRecord> rechargeRecordList = rechargeRecordMapper.selectRechargeRecordListByRechargeStatus("0");

        Map<String, Object> paramMap = new HashMap<String, Object>();

        //循环遍历，获取到每条充值记录
        for (RechargeRecord rechargeRecord : rechargeRecordList) {

            paramMap.put("out_trade_no", rechargeRecord.getRechargeNo());

            //调用pay工程的订单查询接口
            String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery", paramMap);

            //将json格式的字符串转换为Json对象
            JSONObject jsonObject = JSONObject.parseObject(jsonString);

            //获取alipay_trade_query_response所对应的json对象
            JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");

            //获取通信标识
            String code = tradeQueryResponse.getString("code");

            //判断通信是否成功
            if (!StringUtils.equals("10000", code)) {
                System.out.println("------通信异常------");
            }

            //获取业务处理结果trade_status
            String tradeStatus = tradeQueryResponse.getString("trade_status");

             /*交易状态：
            WAIT_BUYER_PAY（交易创建，等待买家付款）
            TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
            TRADE_SUCCESS（交易支付成功）
            TRADE_FINISHED（交易结束，不可退款）*/

            if (StringUtils.equals("TRADE_CLOSED", tradeStatus)) {
                //更新充值记录的状态为2充值失败
                RechargeRecord updateRecharge = new RechargeRecord();
                updateRecharge.setRechargeNo(rechargeRecord.getRechargeNo());
                updateRecharge.setRechargeStatus("2");
                int updateCount = rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRecharge);
                if (updateCount < 0) {
                    throw new Exception();
                }
            }

            if (StringUtils.equals("TRADE_SUCCESS", tradeStatus)) {
                //给用户充值
                //1.更新帐户可用余额
                paramMap.put("uid",rechargeRecord.getUid());
                paramMap.put("rechargeMoney",rechargeRecord.getRechargeMoney());
                int i = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);
                if (i < 0) {
                    throw new Exception();
                }

                //判断充值记录详情
                RechargeRecord rechargeRecordDetail = rechargeRecordMapper.selectRechargeCordByRechargeNo(rechargeRecord.getRechargeNo());

                //判断充值记录的状态
                if (StringUtils.equals("0", rechargeRecordDetail.getRechargeStatus())) {
                    //更新充值记录的状态
                    RechargeRecord updateRechargeRecord = new RechargeRecord();
                    updateRechargeRecord.setRechargeNo(rechargeRecord.getRechargeNo());
                    updateRechargeRecord.setRechargeStatus("1");
                    int updateRechargeRecordCount = rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRechargeRecord);
                    if (updateRechargeRecordCount < 0) {
                        throw new Exception();
                    }

                }



            }

        }

    }

    @Override
    public RechargeRecord queryRechargeRecordByRechargeNo(String rechargeNo) {
        return rechargeRecordMapper.selectRechargeCordByRechargeNo(rechargeNo);
    }
}
