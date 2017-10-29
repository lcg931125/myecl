package cn.bestsign.demo;

import cn.bestsign.demo.utils.EncodeUtils;
import cn.bestsign.demo.utils.HttpSender;
import cn.bestsign.demo.utils.Utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class BestSignDemo {

    private String developerId = null;
    private String pem = null;
    private String host = null;

    public static BestSignDemo getInstance(String developerId, String pem, String host) {
        return new BestSignDemo(developerId, pem, host);
    }

    private BestSignDemo(String developerId, String pem, String host) {
        this.developerId = developerId;
        this.pem = pem;
        this.host = host;
    }

    public JSONObject userReg(final String account, final String mobile, final String name, final String userType, final String mail) throws Exception {
        final String path = "/user/reg";

        @SuppressWarnings("serial")
        Map<String, Object> data = new HashMap<String, Object>() {
            {
                put("account", account);
                put("mobile", mobile);
                put("name", name);
                put("userType", userType);
                put("mail", mail);
            }
        };

        String url = host + getPostUrlByRsa(data, path); // rsa的话 pem为私钥

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String dataString  = JSONObject.toJSONString(data);
        Map<String, Object> res  = HttpSender.getResponseString("POST", url, dataString, headers);
        String resString = (String) res.get("responseData");

        return parseExecutorResult(resString);
    }

    public byte[] downloadSignatureImage(final String account, final String imageName) throws Exception {
        final String path = "/signatureImage/user/download";

        String url = host + getUrlByRsa(account, imageName,null, path); // rsa的话 pem为私钥
        url = url + "&account=" + account + "&imageName=" + URLEncoder.encode(imageName, "UTF-8");
        System.out.println("url:" + url);

        Map<String, String> headers = new HashMap<>();
        Map<String, Object> res  = HttpSender.getResponseBytes("GET", url, "", headers);
        byte[] data = (byte[]) res.get("responseData");
        return data;
    }

    /**
     * 仅限下载用户签名图片，其他接口请自行实现
     * @param account
     * @param imageName
     * @param data
     * @param path
     * @return
     * @throws Exception
     */
    private String getUrlByRsa(String account, String imageName, Map<String, Object> data, String path) throws Exception {

        String randomStr = Utils.rand(1000, 9999) + "";
        String unix = Long.toString(System.currentTimeMillis());
        String rtick = unix + randomStr;

        String dataMd5 = "";
        if(data != null) {
            String jsonData = JSON.toJSONString(data);
            dataMd5 = EncodeUtils.md5(jsonData.getBytes("UTF-8"));
        }
        String sign = String.format("account=%sdeveloperId=%simageName=%srtick=%ssignType=rsa/openapi/v3%s/%s", account,developerId, imageName,rtick, path, dataMd5); // 生成签名字符串
        System.out.println(sign);

        String signDataString = this.getSignData(sign);
        String signData = Base64.encodeBase64String(EncodeUtils.rsaSign(signDataString.getBytes("UTF-8"), pem));

        signData = URLEncoder.encode(signData, "UTF-8");
        path = path + "/?developerId=" + developerId + "&rtick=" + rtick + "&sign=" + signData + "&signType=rsa";
        System.out.println(path);
        return path;
    }

    private String getPostUrlByRsa(Map<String, Object> data, String path) throws Exception {

        String randomStr = Utils.rand(1000, 9999) + "";
        String unix = Long.toString(System.currentTimeMillis());
        String rtick = unix + randomStr;

        String jsonData = JSON.toJSONString(data);
        String dataMd5 = EncodeUtils.md5(jsonData.getBytes("UTF-8"));
        String sign = String.format("developerId=%srtick=%ssignType=rsa/openapi/v3%s/%s", developerId, rtick, path, dataMd5); // 生成签名字符串
        System.out.println(sign);

        String signDataString = this.getSignData(sign);
        String signData = Base64.encodeBase64String(EncodeUtils.rsaSign(signDataString.getBytes("UTF-8"), pem));

        signData = URLEncoder.encode(signData, "UTF-8");
        path = path + "/?developerId=" + developerId + "&rtick=" + rtick + "&sign=" + signData + "&signType=rsa";
        System.out.println(path);
        return path;
    }

    private JSONObject parseExecutorResult(String executorResult) {
        if (StringUtils.isBlank(executorResult)) {
            return null;
        }
        return JSON.parseObject(executorResult);
    }

    private String getSignData(final String... args) {
        StringBuilder builder = new StringBuilder();
        int len = args.length;
        for (int i = 0; i < args.length; i++) {
            builder.append(Utils.convertToUtf8(args[i]));
            if (i < len - 1) {
                builder.append("\n");
            }
        }

        return builder.toString();
    }

}
