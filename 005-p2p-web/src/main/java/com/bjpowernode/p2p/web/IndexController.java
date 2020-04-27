package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jws.WebParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName:IndexController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/4/25 11:09
 * @author:动力节点
 */
@Controller
public class IndexController {

    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",check = false)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = UserService.class,version = "1.0.0",check = false)
    private UserService userService;

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

    @RequestMapping(value = "/index")
    public String index(Model model) {

        //创建一个固定的线程池
        /*ExecutorService executorService = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 1000; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
                    model.addAttribute(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);
                }
            });

        }

        executorService.shutdown();*/

        //获取平台历史平均年化收益率
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);

        //获取平台注册总人数
        Long allUserCount = userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT,allUserCount);

        //获取平台累计投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY,allBidMoney);

        //将以下查询看作是一个分页(实际上它们不是一个分页的功能)
        //共同点都是产品,不同点是:类型不同,显示的个数不同
        //产品业务接口层提供一个方法:根据产品类型获取产品信息列表(产品类型,页码,每页显示条数) -> 返回List<产品>
        //使用MySQL数据库的分页:limit 起始下标,截取长度; 起始下标是从0开始

        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("currentPage",0);

        //获取新手宝,产品类型:0,显示第1页,每页显示1条
        paramMap.put("productType",Constants.PRODUCT_TYPE_X);
        paramMap.put("pageSize",1);
        List<LoanInfo> xLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("xLoanInfoList",xLoanInfoList);

        //获取优选产品,产品类型:1,显示第1页,每页显示4条
        paramMap.put("productType",Constants.PRODUCT_TYPE_U);
        paramMap.put("pageSize",4);
        List<LoanInfo> uLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("uLoanInfoList",uLoanInfoList);

        //获取散标产品,产品类型:2,显示第1页,每页显示8条
        paramMap.put("productType",Constants.PRODUCT_TYPE_S);
        paramMap.put("pageSize",8);
        List<LoanInfo> sLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("sLoanInfoList",sLoanInfoList);


        return "index";
    }

}
