package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName:BidUserTopVO
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2020/5/6 11:09
 * @author:动力节点
 */
@Data
public class BidUserTopVO implements Serializable {


    private static final long serialVersionUID = 5420529868704333913L;

    /**
     * 用户手机号码
     */
    private String phone;

    /**
     * 分数
     */
    private Double score;
}
