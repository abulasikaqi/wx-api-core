package wx.common.util;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by LL on 2018/4/27.
 */
public class SignUtil {

    /**
     * 时间戳（秒）
     * @return long
     */
    public static long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取随机字符串
     * @return String 随机字符串
     */
    public static String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    /**
     * 生成 MD5
     *
     * @param data 待处理数据
     * @return MD5结果
     */
    public static String MD5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后，
     * 使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1
     * @param reqData data
     * @return str
     */
    public static String generateSignature(Map<String, Object> reqData) {
        Set<String> keySet = reqData.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k != null) {
                if (sb.length() == 0) {
                    // 所有参数名均为小写字符
                    sb.append(k.toLowerCase()).append("=").append(reqData.get(k));
                } else {
                    sb.append("&").append(k.toLowerCase()).append("=").append(reqData.get(k));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 进行sha1签名
     * @param reqData data
     * @return signature
     * @throws AesException
     */
    public static String jsapiSHA1(Map<String, Object> reqData) throws AesException {
        String signature = generateSignature(reqData);

        return SHA1.SHA1(signature);
    }




    public static void main(String[] args) {
        System.out.println(generateNonceStr());
    }
}
