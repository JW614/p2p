package com.alipay.config;

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
    public static String app_id = "2016102300744391";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCiKeDpcp8Vj0yxgMWwPlXn0wwEaBaC6bo6ih3/ilLhR3n1l+WPnBiSmTKxvop548uEMSx/15ncCRFNeTmWevlLOEkrCuJl1xJVRw8/eX0XCT/eNzLjYYZ9UAppIbrYfPuztoUDExzfBIOYrT1rHjP7V9BKh1wFLk31nUc2RAeX/Yq3gOWW3rpVMxog1bSrLmWAbhdcxrABF40sBYJt9XmM8pwaYmOzX9zm3mDsRvh2oSgPBvUwBVe17+bbGwEkv4YUJp6MvZD6XIhVvo7E8/Ennax/EzMS66O+srWUEu6n156/kNm6XwGC4ELYdO1cJskGQ2MtyeFD9QKfScDKuzH3AgMBAAECggEAD0oJhZkj/KFlJrswaFyINvJECMpTjQZCY7CQAX7mL41Qp2Ip569c9b1x9SY04icjEhSc0HTgccl9EyPh1RlFFd15sJA3Jkjv1pB7lI18yMY7elUT/DI+vuPkB2OBvVWTWa9UvSR5LWG8kpGK3+WaGxF9dvlqKKbj/MtWYnxpht/fXUTQkgFBeNJkIVQMrtUJdcXFpk3yO4qD9qqogyqlotU7eKQW7kGoBOezQ7wxX/RpOT94nrJ3pZ887Q4x+zU3EqcItOK5yXzmigd7NjDWANdEn9zIeTNPBGFYLtvFjlASjPGU0xkY/mcpjEx7HTbU64M8WNf0oIRLtiaNnsq0OQKBgQDQPTKxlnUdRVBkSxPTlI5Kpaq8r/AyBFKcAjLR6Q7hpcFYAa6Fs0gm40yaG8cJ7LixXYuEp5e3SjURMmczDBerYwRQGoL2SDVNuekK51BMSfyYKR4+0ue+DjAr++O+Y4l1wR9/uHFmclT5VzoUDqB05pFwFf8nzBrVu4SRaICJZQKBgQDHW1lwACvSFw8LBy05wT+ZzlMEqXURASvgVcFSmmWLiXC5o/gfCDrqZoR4aNzbLzmpC7B57kLbH9VpDp3jAIjvdESYnENcFdksJv+ueFbdbpV1pqWZuYpUp1a40SdCFBhuJBkH8lp7TtltZ/S7J2kn5y1gl0Lvzj0E+ThpyWDGKwKBgEBeSmc+nOD5Zgo6ctx6FSnZ39cHg3XShD6ZJ2BfbCwv8n3jtzC45Fqw9CLG51WYCNc6lT/iFjGgDJtOOzw6Rq7BormoGEdMtr1Z9EFckyOh0yStwR2mT+AdvqI58IIPfpQZqETwnI0QVlfksJ02kD7Sbq54/jAtFTZwCmBwLxtJAoGBAJW6cFQEbOUf2HaJ71e/YstcAVIxC/G3lYKqJcaqm7XPhlCHbGWyQr2mPbxJ1gbxUzc5xlPttVzqbdi317GUx0RBaPvN8XGKH1BgAgzB6UvqMGrqvNnWVqT6AdJRlFC20xp2Fi63wWl3cSoQt+iQ0xPbN2Oid+2wUngXzlz1mK99AoGAcdrzngAxgBK0ZbkoU/3Spw7XK9dFUjT9BCmlcJ120lMykFwgUnpg1LGI6OO5EAjBNM6kH+wQb6PY1JytVy1daXUvx+iiaWCrvIeh27lqHmlFfEr8r7p4baO9ls8ybaWR7zfJyFtFzgaoPNrWKxpfZHdgSXc4B5PGinDu+EJ468I=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlwAmSxRGXIvxVeBZkhWiQxnFDTJX62h0nDZcNFMBQVP1WIgj7JoJfZ96+3HKoeZvm1NN9E3iNBV0AKySY0G6xOcAuYCN8JCIxKsMK/MhnCckGu399XxcQ9/JJ9zAd2zpSaZdo3pEe3RY8KWePmFGkZ3/cr8QAv7tydvFazDDXc43anK37DON+lZgmfCJvnApUknaCM1/K/Flr508PIwR82H6P7dH0jeVRptk1yRCDnEqm56B6h1k/OSStsF62Df9c/PtEn3/JpvlGr/2oN+A6/JcP85uY87w9m+J32kTUotnuo/tb+ecLlsKZL41C8u+p5y3eehYu67URMXuRim5cQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";

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
