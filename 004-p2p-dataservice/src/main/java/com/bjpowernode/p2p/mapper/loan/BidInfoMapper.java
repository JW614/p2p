package com.bjpowernode.p2p.mapper.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.vo.BidExtUser;
import com.bjpowernode.p2p.model.vo.BidLoanVO;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    /**
     * 获取平台累计投资金额
     * @return
     */
    Double selectAllBidMoney();

    /**
     * 根据产品标识获取最近的投资记录(包含:用户信息)
     * @param paramMap
     * @return
     */
    List<BidExtUser> selectRecentlyBidInfoListByLoanId(Map<String, Object> paramMap);

    /**
     * 根据用户标识获取最近投资记录(包含：产品信息)
     * @param paramMap
     * @return
     */
    List<BidLoanVO> selectRecentlyBidInfoListByUid(Map<String, Object> paramMap);

    /**
     * 根据产品标识获取产品的所有投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> selectAllBidInfoListByLoanId(Integer loanId);
}