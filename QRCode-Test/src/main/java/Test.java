import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:Test
 * Package:PACKAGE_NAME
 * Description:
 *
 * @date:2020/5/9 10:26
 * @author:动力节点
 */
public class Test {

    public static void main(String[] args) throws WriterException, IOException {

        String content = "weixin://wxpay/bizpayurl?pr\\u003d4zTn6VF";

        Map<EncodeHintType,Object> hintTypeObjectMap = new HashMap<EncodeHintType, Object>();
        hintTypeObjectMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode("weixin://wxpay/bizpayurl?pr=Dff0YAC", BarcodeFormat.QR_CODE,200,200,hintTypeObjectMap);

        String filePath = "D://";
        String fileName = "qrcodeTest.png";

        Path path = FileSystems.getDefault().getPath(filePath,fileName);

        //将矩阵对象转换成二维码图片
        MatrixToImageWriter.writeToPath(bitMatrix,"png",path);

        System.out.println("生成成功");




    }
}
