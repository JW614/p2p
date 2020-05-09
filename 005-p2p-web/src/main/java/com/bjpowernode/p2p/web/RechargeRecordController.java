package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.bjpowernode.p2p.common.config.AlipayConfig;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.common.util.DateUtils;
import com.bjpowernode.p2p.common.util.HttpClientUtils;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.service.loan.RedisService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ClassName:RechargeRecordController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/5/7 9:06
 * @author:动力节点
 */
@Controller
public class RechargeRecordController {

    @Reference(interfaceClass = RechargeRecordService.class, version = "1.0.0", check = false)
    private RechargeRecordService rechargeRecordService;

    @Reference(interfaceClass = RedisService.class, version = "1.0.0", check = false)
    private RedisService redisService;

    @RequestMapping(value = "/loan/page/toRecharge")
    public String toRecharge() {
        return "toRecharge";
    }


    @RequestMapping(value = "/loan/toAlipayRecharge")
    public String toAlipayRecharge(HttpServletRequest request, Model model,
                                   @RequestParam(value = "rechargeMoney", required = true) Double rechargeMoney) {

        System.out.println("----------toAlipayRecharge-------------");
        String rechargeNo = "";
        try {

            //从session中获取用户的信息
            User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

            //生成充值记录且状态为充值中
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setUid(sessionUser.getId());
            rechargeRecord.setRechargeTime(new Date());
            rechargeRecord.setRechargeMoney(rechargeMoney);
            rechargeRecord.setRechargeStatus("0");//0充值中，1充值成功，2充值失败
            rechargeRecord.setRechargeDesc("支付宝充值");
            //全局唯一充值订单号 = 时间戳 + redis唯一数字
            rechargeNo = DateUtils.getTimestamp() + redisService.getOnlyNumber();
            rechargeRecord.setRechargeNo(rechargeNo);

            int addRechargeCount = rechargeRecordService.addRechargeRecord(rechargeRecord);

            if (addRechargeCount < 0) {
                model.addAttribute("trade_msg", "充值人数过多，请稍后重试");
                return "toRechargeBack";
            }

            model.addAttribute("rechargeNo", rechargeNo);
            model.addAttribute("rechargeMoney", rechargeMoney);
            model.addAttribute("rechargeDesc", "支付宝充值");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("trade_msg", "充值人数过多，请稍后重试");
            return "toRechargeBack";
        }

//        return "redirect:http://localhost:9090/pay/api/alipay?out_trade_no="+rechargeNo+"&total_amount="+rechargeMoney+"&subject=TESAT";
        return "p2pToPay";
    }


    @RequestMapping(value = "/loan/alipayBack")
    public String alipayBack(HttpServletRequest request, Model model,
                             @RequestParam (value = "out_trade_no",required = true) String out_trade_no,
                             @RequestParam (value = "total_amount",required = true) Double total_amount) {

        System.out.println("--------------alipayBack-------------------");

        try {
            Map<String, String> params = new HashMap<String, String>();

            //获取支付宝GET过来反馈信息
            Map<String, String[]> requestParams = request.getParameterMap();

            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            //调用SDK验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);

            //——请在这里编写您的程序（以下代码仅作参考）——
            if (signVerified) {
                Map<String,Object> paramMap = new HashMap<String, Object>();
                paramMap.put("out_trade_no",out_trade_no);

                //调用pay工程的订单查询接口
                String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery", paramMap);

                //将json格式的字符串转换为json对象
                JSONObject jsonObject = JSONObject.parseObject(jsonString);

                //获取alipay_trade_query_response所对应的json对象
                JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");

                //获取通信标识code
                String code = tradeQueryResponse.getString("code");

                //判断通信是否成功
                if (!StringUtils.equals("10000", code)) {
                    model.addAttribute("trade_msg","平台通信异常");
                    return "toRechargeBack";
                }

                //获取业务处理结果trade_status
                String tradeStatus = tradeQueryResponse.getString("trade_status");

                /*交易状态：
                WAIT_BUYER_PAY（交易创建，等待买家付款）
                TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
                TRADE_SUCCESS（交易支付成功）
                TRADE_FINISHED（交易结束，不可退款）*/

                //判断业务处理结果

                if (StringUtils.equals(tradeStatus, "TRADE_CLOSED")) {
                    //更新充值记录状态为充值失败
                    RechargeRecord rechargeRecord = new RechargeRecord();
                    rechargeRecord.setRechargeNo(out_trade_no);
                    rechargeRecord.setRechargeStatus("2");
                    int modifyRechargeCount = rechargeRecordService.modifyRechargeRecordByRechargeNo(rechargeRecord);
                    model.addAttribute("trade_msg","充值失败");
                    return "toRechargeBack";
                }


                if (StringUtils.equals("TRADE_SUCCESS", tradeStatus)) {

                    //再次查询订单状态
                    RechargeRecord rechargeRecord = rechargeRecordService.queryRechargeRecordByRechargeNo(out_trade_no);

                    //判断产品状态是否为0
                    if (StringUtils.equals("0", rechargeRecord.getRechargeStatus())) {
                        //从session中获取用户的信息
                        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

                       //去给用户充值[1.更新帐户的可用余额 2.更新充值记录的状态为1充值成功](用户标识，充值金额，充值订单号)
                        paramMap.put("uid",sessionUser.getId());
                        paramMap.put("rechargeMoney",total_amount);
                        paramMap.put("rechargeNo",out_trade_no);
                        rechargeRecordService.recharge(paramMap);
                    }

                }




            } else {
                model.addAttribute("trade_msg","验证签名失败");
                return "toRechargeBack";
            }


        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("trade_msg","充值失败");
            return "toRechargeBack";
        }

        return "redirect:/loan/myCenter";
    }


    @RequestMapping(value = "/loan/toWxpayRecharge")
    public String toWxpayRecharge(HttpServletRequest request, Model model,
                                  @RequestParam(value = "rechargeMoney", required = true) Double rechargeMoney) {

        System.out.println("----------toWxpayRecharge-------------");

        String rechargeNo = "";
        try {

            //从session中获取用户的信息
            User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

            //生成充值记录且状态为充值中
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setUid(sessionUser.getId());
            rechargeRecord.setRechargeTime(new Date());
            rechargeRecord.setRechargeMoney(rechargeMoney);
            rechargeRecord.setRechargeStatus("0");//0充值中，1充值成功，2充值失败
            rechargeRecord.setRechargeDesc("微信充值");
            //全局唯一充值订单号 = 时间戳 + redis唯一数字
            rechargeNo = DateUtils.getTimestamp() + redisService.getOnlyNumber();
            rechargeRecord.setRechargeNo(rechargeNo);

            int addRechargeCount = rechargeRecordService.addRechargeRecord(rechargeRecord);

            if (addRechargeCount < 0) {
                model.addAttribute("trade_msg", "充值人数过多，请稍后重试");
                return "toRechargeBack";
            }

            model.addAttribute("rechargeNo",rechargeNo);
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("rechargeTime",new Date());

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("trade_msg", "充值人数过多，请稍后重试");
            return "toRechargeBack";
        }

        return "showQRCode";
    }

    @RequestMapping(value = "/loan/generateQRCode")
    public void generateQRCode(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam (value = "rechargeMoney",required = true) Double rechargeMoney,
                               @RequestParam (value = "rechargeNo",required = true) String rechargeNo) throws Exception {

        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("body","动力金融网-用户充值");
        paramMap.put("out_trade_no",rechargeNo);
        paramMap.put("total_fee",rechargeMoney);

        //调用pay工程的统一下单API接口
        String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/wxpay", paramMap);

        //将json格式的字符串转换为json对象
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        //获取通信标识return_code
        String returnCode = jsonObject.getString("return_code");

        if (!StringUtils.equals(returnCode, "SUCCESS")) {
            response.sendRedirect(request.getContextPath() + "/loan/toRechargeBack");
        }

        //获取业务处理结果result_code
        String resultCode = jsonObject.getString("result_code");
        if (!StringUtils.equals(resultCode, "SUCCESS")) {
            response.sendRedirect(request.getContextPath() + "/loan/toRechargeBack");
        }

        //获取到code_url
        String codeUrl = jsonObject.getString("code_url");

        Map<EncodeHintType,Object> hintTypeObjectMap = new HashMap<EncodeHintType, Object>();
        hintTypeObjectMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE,200,200,hintTypeObjectMap);

        OutputStream outputStream = response.getOutputStream();

        //生成二维码图片
        MatrixToImageWriter.writeToStream(bitMatrix,"jpg",outputStream);

        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/loan/toRechargeBack")
    public String toRechargeBack() {
        return "toRechargeBack";
    }

}
