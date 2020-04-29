package com.bjpowernode.p2p.common.util;

import java.util.HashMap;

/**
 * ClassName:Result
 * Package:com.bjpowernode.p2p.common.util
 * Description:封装控制层响应的结果信息
 *
 * @date:2020/4/28 11:02
 * @author:动力节点
 */
public class Result extends HashMap {

    public static Result success() {
        Result result = new Result();
        result.put("code",1);
        result.put("success",true);

        return result;
    }

    public static Result error(String message) {
        Result result = new Result();
        result.put("code",-1);
        result.put("success",false);
        result.put("message",message);

        return result;
    }

    public static Result success(String data) {
        Result result = new Result();
        result.put("code",1);
        result.put("success",true);
        result.put("data",data);
        return result;
    }
}
