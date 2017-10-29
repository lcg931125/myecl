package cn.bestsign.demo;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class Demo {
    private static String developerId = "";
    private static String pem = "";
    private static String host = "https://openapi.bestsign.info/openapi/v3";
    private static BestSignDemo bestSignDemo = null;

    static {
        bestSignDemo = BestSignDemo.getInstance(developerId, pem, host);
    }

    public static void main(String[] args) throws Exception {
        userReg();
        downloadSignatureImage();
    }

    private static void userReg() throws Exception {
        String account = "7654321001@qq.com";
        String mobile = "13995434949";
        String name = "测试用户";
        String userType = "1";
        String mail = "7654321001@qq.com";

        JSONObject result = bestSignDemo.userReg(account, mobile, name, userType, mail);

        printResult(result);
    }

    private static void downloadSignatureImage() throws Exception {
        String account = "7654321001@qq.com";
        String imageName = "test";

        byte[] imageData = bestSignDemo.downloadSignatureImage(account, imageName);
        FileOutputStream fo = new FileOutputStream(new File("D:/image.png"));
        fo.write(imageData);
        fo.flush();
        fo.close();
        printResult("ok");
    }

    private static void printResult(Object value) {
        String result = JSONObject.toJSONString(value);
        System.out.println(result);
    }
}
