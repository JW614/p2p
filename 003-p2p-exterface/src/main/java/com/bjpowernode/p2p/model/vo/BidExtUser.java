package com.bjpowernode.p2p.model.vo;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.user.User;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName:BidExtUser
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2020/4/27 14:39
 * @author:动力节点
 */
public class BidExtUser extends BidInfo {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
