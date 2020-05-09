package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.BidExtUser;
import com.bjpowernode.p2p.model.vo.BidUserTopVO;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.FinanceAccountService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:LoanInfoController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/4/27 11:02
 * @author:动力节点
 */
@Controller
public class LoanInfoController {

    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",check = false)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

    @Reference(interfaceClass = FinanceAccountService.class,version = "1.0.0",check = false)
    private FinanceAccountService financeAccountService;

    @GetMapping(value = "/loan/loan")
    public String loan(HttpServletRequest request, Model model,
                       @RequestParam (value = "ptype",required = false) Integer ptype,
                       @RequestParam (value = "currentPage",defaultValue = "1") Integer currentPage,
                       @RequestParam (value = "pageSize",defaultValue = "9") Integer pageSize) {

        //产品分页展示的数据
        //分页查询产品(产品类型,页码,每页显示条数) -> 返回:每页显示的数据,总条数
        // 封装一个分页模型对象PaginationVO,该对象有两个属性:每页显示数据,总条数
        //将查询参数存放到map集合中
        Map<String,Object> paramMap = new HashMap<String, Object>();
        if (ObjectUtils.allNotNull(ptype)) {
            paramMap.put("productType",ptype);
        }
        paramMap.put("currentPage",(currentPage-1)*pageSize);
        paramMap.put("pageSize",pageSize);

        //调用业务方法
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoListByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }


        model.addAttribute("totalPage",totalPage);
        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("currentPage",currentPage);
        model.addAttribute("loanInfoList",paginationVO.getDataList());
        if (ObjectUtils.allNotNull(ptype)) {
            model.addAttribute("ptype",ptype);
        }

        //用户投资排行榜
        List<BidUserTopVO> bidUserTopVOList = bidInfoService.queryBidUserTop();
        model.addAttribute("bidUserList",bidUserTopVOList);


        return "loan";
    }


    @GetMapping(value = "/loan/loanInfo")
    public String loanInfoDetail(HttpServletRequest request,Model model,
                                 @RequestParam (value = "id",required = true) Integer id) {

        //根据产品标识获取产品详情
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);
        model.addAttribute("loanInfo",loanInfo);

        //准备查询参数
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loanId",id);
        paramMap.put("currentPage",0);
        paramMap.put("pageSize",10);

        //根据产品标识获取产品最近的投资记录
        List<BidExtUser> bidInfoList = bidInfoService.queryRecentlyBidInfoListByLoanId(paramMap);
        model.addAttribute("bidInfoList",bidInfoList);

        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //判断用户是否为空
        if (ObjectUtils.allNotNull(sessionUser)) {

            //根据用户标识获取帐户信息
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());
            model.addAttribute("financeAccount",financeAccount);
        }



        return "loanInfo";
    }

}
