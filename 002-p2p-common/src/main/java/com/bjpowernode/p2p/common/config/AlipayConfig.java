package com.bjpowernode.p2p.common.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * ClassName:AlipayConfig
 * Package:com.alipay.config
 * Description:
 *
 * @date:2020/5/7 14:14
 * @author:动力节点
 */
public class AlipayConfig {

    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016082000291278";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCQrrr0NT8NCTh+Q3DLJkEsqYuwmx3djTBi4GKUWRhy4Fs6gBFK9E+aCHvkncxj2cRPD/TStUbXte6+bDgqehFDYrm9l9mgDVH3WEZXmnLl/YL6x+Og+rMbXqdUkaGq1MmLq2AfpF3Iw2dD9ROoNNtBXLL7Pt6hbQbnrQBYu5po4jtkMIl+1G47ExYD8JD187jgpAA0/+G6BC9z2L6cPdd+pIJxJhsMsnSyMn05le2baGkv8w5hZ/KBEWE+VCAJx0rnXoboLwoUByHT8KbUuqIOutNUGfeJ6BI8M+MaiYbuFVvaGSlPVFx32MEiMrodf5sM+P4LvrElr59uMg8OQM7PAgMBAAECggEAE4UfgBc2/IFD9/UNYLs2i9oMGLW039FOt+hiJHWo0MboSApDSwOPQc7nOD759nbI/4m9lDgU6MGJBnP/V+vELH1DZgr4t5lUd7SXOPaDjfgYFdGmKm/ofkbU+Jn1X+D5mDh07Y/1f0KtygNktYr8EeN52l/vFYRqLBOxOkeFW6UGOQQjkg5rRE5TuFeyxBc2gw7tNtByiPB0IGOxt+fC75hl80Bj0Pj0JBBxgS2SHR3sL3LyiGyQzwA5M6YXDsOIkRtDlGFIFLFONjm9WcRPYx02VKpYTKyjCnJxIIAjr7YWEOYNAc/zLkIPBnYJCtI6PNg81BABZM6vB1xGOK7uoQKBgQDIZvEbcJAFUxnfQjKxTQ2F5U4ztpHVRy0oPLoVIMHzig1J85pvBnYmjkT9/xv9/D/3v2XN76D3EJHh1Eq+ldGlqVDUFru7pQo1WWgccqJNroz6EAYeLnYrnqlP6s4ugKoXwn55XqBEQcckb1b6/p63NZtUL94pwbGMXjh8i8LSWQKBgQC40nH24XLem2BXvleUntMD1sFSQ1F2UUi5QAra8fk7PNFtxgiLCWXoMoDDVhDqgZE224c651n363WXCxea3tX9lmwQhP2DavPOcHWPUcaIn4KjatrIxEjap844iYGqiqr36wN/tX8VFArboHi+HIaG3zotS6sZDqM5vfFrdCf1ZwKBgF7jVzBt6NOMmIh8uLH40PlgLNoRjRWWYQxXTKiqXQtqh4ZHgQ0m2xTAcmxtuZxWLxeomZSb7PRu9RQfx9Bb8etlsy7pILTlnWLLVZ96q9zqVDgnFC6AQ1W+B18ex8d/Boq/fdEL/Ai1uIxI579qhk4rZMPE8oWFAAN9rXNTXcjpAoGARh0fusagKy7Kh77fvrPZ3VT1Fd4P41DGw/A1rgWpxP03fetohkZUcJfoHTJjTHNGwYbacFAf41thyqEcnVX33DHaYRHyko5aW3BySKJ/LIlGp/P7d7pu4PRT2ULg+++LGG4Bb8tMoJRv4g2ogIaVykiY62IPUHwrE9ao5Vi1Z/UCgYBTxUzcQ7n5mNF9NKfbOf4VqhYYo2+y+iWbNJUxqPGsr+kCbvyafmmmcGHOvo4g3ybtS6m0No/C3XoEWY0MQRpjt8EW8+wghKjG6gNA/Gb25F6+2YMXFDCtS2+NwtXDRj86NXGUJYuLWwoZErdVPH7S4Yd7amwJQKO5XPKOyWsDBw==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuHRMf8S5r4CSiWFQSicEr/5ZfRivxol4Sr0w9Mt9RuDVEq844FyaxHXkQdkZPEBqkp9hCAkZMJM8rwVGJAvCAHV07oh8h7sFH/rVkP6Fb/DxrBlzU9pgnpoibqFEPbX/JukgFzMXhrataNk0qREBYBxdWDibemwjdCDqOkRB3oZipUcf1uXWLoy1Fw+a5TPae9DUxCklpUT7xFAk3nQxZq6o8eK9OP+vr+LD2gqQ9wiFtcJuLt/+dRjWWS/7aZubIly+JkevYDN4X7Gw/flSLE3ZSZn0TIFUDtHI/gmn4WiQFurH4eWH+PwDLr4vvjWJgfAOuK7w1M0nd5EJcTyoTwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/p2p/loan/alipayBack";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
