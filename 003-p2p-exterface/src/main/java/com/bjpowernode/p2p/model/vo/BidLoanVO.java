package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName:BidLoanVO
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2020/4/30 14:13
 * @author:动力节点
 */
@Data
public class BidLoanVO implements Serializable {

    private String productName;

    private Double bidMoney;

    private Date bidTime;


}
