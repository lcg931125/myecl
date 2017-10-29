package cn.bestsign.demo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Utils {
    /**
     * 转换字符集到utf8
     */
    public static String convertToUtf8(String src) {
        if (src == null || src.length() == 0) {
            return src;
        }
        byte[] srcData = src.getBytes();

        try {
            return new String(srcData, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 从utf8转换字符集
     */
    public static String convertFromUtf8(String src) {
        if (src == null || src.length() == 0) {
            return src;
        }
        byte[] srcData = new byte[0];
        try {
            srcData = src.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new String(srcData);
    }

    public static String urlEncode(String data) throws UnsupportedEncodingException {
        String newData = Utils.convertToUtf8(data);
        return URLEncoder.encode(newData, "UTF-8");
    }

    public static String join(String[] items, String split) {
        if (items.length == 0) {
            return "";
        }
        StringBuffer s = new StringBuffer();
        int i;
        for (i = 0; i < items.length - 1; i++) {
            s.append(items[i]);
            s.append(split);
        }
        s.append(items[i]);
        return s.toString();
    }

    public static int rand(int min, int max) {
        return (int) ((max - min + 1) * Math.random() + min);
    }
}
