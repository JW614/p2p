package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.common.util.Result;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName:BidInfoController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/5/6 10:33
 * @author:动力节点
 */
@Controller
public class BidInfoController {

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

    @PostMapping(value = "/loan/invest")
    public @ResponseBody Result invest(HttpServletRequest request,
                                       @RequestParam (value = "loanId",required = true) Integer loanId,
                                       @RequestParam (value = "bidMoney",required = true) Double bidMoney) {
        try {

            //从session中获取用户的信息
            User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

            Map<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("uid",sessionUser.getId());
            paramMap.put("loanId",loanId);
            paramMap.put("bidMoney",bidMoney);
            paramMap.put("phone",sessionUser.getPhone());

            //用户投资【1.更新产品剩余可投金额 2.更新帐户可用余额 3.新增投资记录 4.判断是否满标】(用户标识,产品标识,投资金额)
            bidInfoService.invest(paramMap);


           /* ExecutorService executorService = Executors.newFixedThreadPool(100);

            for (int i = 0; i < 10000; i++) {

                executorService.submit(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        Map<String,Object> paramMap = new HashMap<String, Object>();
                        paramMap.put("uid",1);
                        paramMap.put("loanId",3);
                        paramMap.put("bidMoney",1.0);

                        //用户投资【1.更新产品剩余可投金额 2.更新帐户可用余额 3.新增投资记录 4.判断是否满标】(用户标识,产品标识,投资金额)
                        bidInfoService.invest(paramMap);
                    }
                });
            }

            executorService.shutdown();*/


        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("投资失败");
        }
        return Result.success();
    }
}
