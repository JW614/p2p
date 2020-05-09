package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.common.util.HttpClientUtils;
import com.bjpowernode.p2p.common.util.Result;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.BidLoanVO;
import com.bjpowernode.p2p.model.vo.IncomeRecordExtLoan;
import com.bjpowernode.p2p.service.loan.*;
import com.bjpowernode.p2p.service.user.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:UserController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/4/28 9:12
 * @author:动力节点
 */
@Controller
public class UserController {

    @Reference(interfaceClass = UserService.class, version = "1.0.0", check = false)
    private UserService userService;

    @Reference(interfaceClass = RedisService.class, version = "1.0.0", check = false)
    private RedisService redisService;

    @Reference(interfaceClass = FinanceAccountService.class, version = "1.0.0", check = false)
    private FinanceAccountService financeAccountService;

    @Reference(interfaceClass = LoanInfoService.class, version = "1.0.0", check = false)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false)
    private BidInfoService bidInfoService;

    @Reference(interfaceClass = RechargeRecordService.class, version = "1.0.0", check = false)
    private RechargeRecordService rechargeRecordService;

    @Reference(interfaceClass = IncomeRecordService.class, version = "1.0.0", check = false)
    private IncomeRecordService incomeRecordService;

    @RequestMapping(value = "/loan/page/register")
    public String pageRegister() {

        return "register";
    }

    @RequestMapping(value = "/loan/page/realName")
    public String pageRealName() {
        return "realName";
    }

    @RequestMapping(value = "/loan/page/login")
    public String pageLogin(Model model,
                            @RequestParam(value = "redirectUrl", required = false) String redirectUrl) {
        model.addAttribute("redirectUrl", redirectUrl);
        return "login";
    }


    /**
     * 接口名称：验证手机号码是否重复
     * 接口地址：http://localhost:8080/p2p/loan/checkPhone
     * 请求方式：http POST
     * 请求参数如下：
     *
     * @param phone String 必填项
     *              响应参数：
     *              成功 -> {"code":1,"success":true}
     *              错误 -> {"code":-1,"message":"错误消息","success":false}
     * @return
     */
    @PostMapping(value = "/loan/checkPhone")
    public @ResponseBody
    Object checkPhone(@RequestParam(value = "phone", required = true) String phone) {
        Map<String, Object> retMap = new HashMap<String, Object>();

        //验证手机号码是否重复(手机号码) -> 返回：boolean|int|User|String|响应结果对象
        //根据手机号码查询用户信息(手机号码) -> 返回：User对象
        User user = userService.queryUserByPhone(phone);

        //判断用户是否为空
        if (ObjectUtils.allNotNull(user)) {
            //不为空，说明该手机号码已被注册
            retMap.put("code", -1);
            retMap.put("message", "该手机号码已被注册,请更换手机号码");
            retMap.put("success", false);
            return retMap;
        }

        retMap.put("code", 1);
        retMap.put("success", true);
        return retMap;
    }

    @PostMapping(value = "/loan/register")
    public @ResponseBody
    Object register(HttpServletRequest request,
                    @RequestParam(value = "phone", required = true) String phone,
                    @RequestParam(value = "loginPassword", required = true) String loginPassword,
                    @RequestParam(value = "messageCode", required = true) String messageCode) {
        Map<String, Object> retMap = new HashMap<String, Object>();

        try {

            //从redis缓存中获取短信验证码
            String redisMessageCode = redisService.get(phone);

            //比较用户输入的短信验证码与redis中的是否一致
            if (!StringUtils.equals(redisMessageCode, messageCode)) {
                return Result.error("请输入正确的短信验证码");
            }

            //用户注册【1.新增用户 2.开立帐户】(手机号码，密码)
            User user = userService.register(phone, loginPassword);

            //将用户的信息存放到session中
            request.getSession().setAttribute(Constants.SESSION_USER, user);
        } catch (Exception e) {
            e.printStackTrace();
            /*retMap.put("code",-1);
            retMap.put("message","用户注册失败");
            retMap.put("success",false);
            return retMap;*/
            return Result.error("用户注册失败");

        }


        /*retMap.put("code",1);
        retMap.put("success",true);
        return retMap;*/
        return Result.success();
    }

    @GetMapping(value = "/loan/messageCode")
    public @ResponseBody
    Result messageCode(HttpServletRequest request,
                       @RequestParam(value = "phone", required = true) String phone) {

        String messageCode = "";

        try {

            //准备参数
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("appkey", "c412de014a0257a9e970f4a");
            paramMap.put("mobile", phone);

            //生成一个4位的随机数字
            messageCode = this.randomNumber(4);

            //编写短信验证码的正文
            String content = "【凯信通】您的验证码是：" + messageCode;
            paramMap.put("content", content);

            //发送短信验证码，调用京东万象平台-106短信接口
            String jsonString = HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong", paramMap);

            //在工作中请求参数 -> 请求报文
            //在工作中响应参数 -> 响应报文
            //模拟报文
            String jsonStr = "{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 0,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-1111611</remainpoint>\\n <taskID>101609164</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                    "}";

            /*{
                "code": "10000",
                    "charge": false,
                    "remain": 0,
                    "msg": "查询成功",
                    "result": "<?xml version=\"1.0\" encoding=\"utf-8\" ?><returnsms>\n <returnstatus>Success</returnstatus>\n <message>ok</message>\n <remainpoint>-1111611</remainpoint>\n <taskID>101609164</taskID>\n <successCounts>1</successCounts></returnsms>"
            }*/

            //使用阿里提供的fastjson来解析json格式的字符串
            //将json格式的字符串转换为json对象
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);

            //获取通信标识code
            String code = jsonObject.getString("code");

            //判断通信是否成功
            if (!StringUtils.equals("10000", code)) {
                return Result.error("异常平台，通信异常");
            }

            //获取result对应的xml格式的字符串
            String resultXmlString = jsonObject.getString("result");

            //使用domt4j+xpath来解析xml格式的字符串
            //将xml格式的字符串转换为document对象
            Document document = DocumentHelper.parseText(resultXmlString);

            /*
            <?xml version="1.0" encoding="utf-8" ?>
            <returnsms>
                <returnstatus>Success</returnstatus>
                <message>ok</message>
                <remainpoint>-1111611</remainpoint>
                <taskID>101609164</taskID>
                <successCounts>1</successCounts>
            </returnsms>
            */

            //获取returnstatus节点的xpath路径表达式：//returnstatus 或者 returnsms/returnstatus 或者 /returnsms/returnstatus
            Node returnStatusNode = document.selectSingleNode("//returnstatus");

            //获取returnStatusNode节点的文本内容
            String returnStatusNodeText = returnStatusNode.getText();

            //判断是否发送成功
            if (!StringUtils.equals("Success", returnStatusNodeText)) {
                return Result.error("短信平台发送失败，请重试");
            }

            //将生成的短信验证码存放到redis缓存中
            redisService.put(phone, messageCode);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("短信平台异常，请稍后重试");
        }

        return Result.success(messageCode);
    }


    private String randomNumber(int count) {

        String[] array = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < count; i++) {

            int index = (int) Math.round(Math.random() * 9);

            stringBuilder.append(array[index]);

        }

        return stringBuilder.toString();
    }


    @PostMapping(value = "/loan/realName")
    public @ResponseBody
    Result realName(HttpServletRequest request,
                    @RequestParam(value = "phone", required = true) String phone,
                    @RequestParam(value = "realName", required = true) String realName,
                    @RequestParam(value = "idCard", required = true) String idCard,
                    @RequestParam(value = "messageCode", required = true) String messageCode) {
        try {

            //从redis缓存中获取短信验证码
            String redisMessaageCode = redisService.get(phone);

            //用户输入的短信验证码与redis中短信验证码一致
            if (!StringUtils.equals(messageCode, redisMessaageCode)) {
                return Result.error("请输入正确的短信验证码");
            }

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("appkey", "c499b58e49512de014a0257a9e970f4a");
            paramMap.put("cardNo", idCard);
            paramMap.put("realName", realName);

            //实名认证，调用京东万象平台-身份证二要素实名认证接口
            String jsonString = HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test", paramMap);

            String jsonStr = "{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 1305,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": {\n" +
                    "        \"error_code\": 0,\n" +
                    "        \"reason\": \"成功\",\n" +
                    "        \"result\": {\n" +
                    "            \"realname\": \"乐天磊\",\n" +
                    "            \"idcard\": \"350721197702134399\",\n" +
                    "            \"isok\": true\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

            //使用fastjson来解析json格式的字符串
            //将json格式的字符串转换为JSON对象
            JSONObject jsonObject = JSONObject.parseObject(jsonString);

            //获取通信标识code
            String code = jsonObject.getString("code");

            //判断是否通信成功
            if (!StringUtils.equals(code, "10000")) {
                return Result.error("实名认证通信异常");
            }

            //获取isok，真实姓名与身份证号码是否一致的标识
            Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");

            //判断是否一致
            if (!isok) {
                return Result.error("真实姓名与身份证号码不一致");
            }

            //从session中获取用户的信息
            User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

            //将真实姓名与身份证号码更新到当前用户的信息
            User user = new User();
            user.setId(sessionUser.getId());
            user.setName(realName);
            user.setIdCard(idCard);
            int modifyUserCount = userService.modifyUserById(user);
            if (modifyUserCount < 0) {
                return Result.error("实名认证失败");
            }

            //更新session中用户的信息
            sessionUser.setName(realName);
            sessionUser.setIdCard(idCard);
            request.getSession().setAttribute(Constants.SESSION_USER, sessionUser);


        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("实名认证失败，请稍后重试");
        }


        return Result.success();
    }


    @PostMapping(value = "/loan/myFinanceAccount")
    public @ResponseBody
    FinanceAccount myFinanceAccount(HttpServletRequest request) {

        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户标识获取帐户信息
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

        return financeAccount;
    }


    @RequestMapping(value = "/loan/logout")
    public String logout(HttpServletRequest request) {

        //让session失效或者指定删除session中的值
        request.getSession().invalidate();
//        request.getSession().removeAttribute(Constants.SESSION_USER);

//        return "index";
        return "redirect:/index";
//        return "redirect:/p2p/index";
    }


    @PostMapping(value = "/loan/login")
    public @ResponseBody
    Result login(HttpServletRequest request,
                 @RequestParam(value = "phone", required = true) String phone,
                 @RequestParam(value = "loginPassword", required = true) String loginPassword,
                 @RequestParam(value = "messageCode", required = true) String messageCode) {
        try {

            //从redis中获取短信验证码
            String redisMessageCode = redisService.get(phone);

            //进行验证码可检验
            if (!StringUtils.equals(messageCode, redisMessageCode)) {
                return Result.error("请输入正确的短信验证码");
            }


            //用户登录[1.根据手机号和密码查询用户信息 2.更新最近登录时间] -> User
            User user = userService.login(phone, loginPassword);

            //判断用户是否为空
            if (!ObjectUtils.allNotNull(user)) {
                return Result.error("用户名或密码有误");
            }

            //将用户的信息存放到session中
            request.getSession().setAttribute(Constants.SESSION_USER, user);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("用户名或密码有误");
        }


        return Result.success();
    }

    @RequestMapping(value = "/loan/loadStat")
    public @ResponseBody
    Object loadStat() {

        Map<String, Object> retMap = new HashMap<String, Object>();

        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
        Long allUserCount = userService.queryAllUserCount();
        Double allBidMoney = bidInfoService.queryAllBidMoney();


        retMap.put("historyAverageRate", historyAverageRate);
        retMap.put("allUserCount", allUserCount);
        retMap.put("allBidMoney", allBidMoney);

        return retMap;
    }

    @RequestMapping(value = "/loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model) {

        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户标识获取帐户信息
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

        //将以下查看看作是一个分页
        //准备查询参数
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("uid", sessionUser.getId());
        paramMap.put("currentPage", 0);
        paramMap.put("pageSize", 5);

        //根据用户标识获取最近投资记录
        List<BidLoanVO> bidLoanVOList = bidInfoService.queryRecentlyBidInfoListByUid(paramMap);

        //根据用户标识获取最近充值记录
        List<RechargeRecord> rechargeRecordList = rechargeRecordService.queryRecentlyRechargeRecordListByUid(paramMap);

        //根据用户标识获取最近收益记录
        List<IncomeRecordExtLoan> incomeRecordExtLoanList = incomeRecordService.queryRecentlyIncomeRecordListByUid(paramMap);

        model.addAttribute("financeAccount", financeAccount);
        model.addAttribute("bidLoanVOList", bidLoanVOList);
        model.addAttribute("rechargeRecordList", rechargeRecordList);
        model.addAttribute("incomeRecordExtLoanList", incomeRecordExtLoanList);

        return "myCenter";
    }
}
