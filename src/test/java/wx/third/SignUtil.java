package wx.third;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.codec.binary.Hex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;


/**
 * 签名工具类
 */
public class SignUtil {

    private static final String CONNECTOR = "=";
    private static final String SEPARATOR = "&";

    /**
     * 对字符串进行散列签名
     *
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     */
    public static String hmacsha256(final String key, final String data) {
        try {
            final Mac mac = Mac.getInstance("HmacMD5");
            final byte[] secretByte = key.getBytes("UTF-8");
            final byte[] dataBytes = data.getBytes("UTF-8");
            final SecretKey secret = new SecretKeySpec(secretByte, "HmacMD5");
            mac.init(secret);
            final byte[] doFinal = mac.doFinal(dataBytes);
            final byte[] hexB = new Hex().encode(doFinal);
            return new String(hexB);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//	public static void main(final String[] args)
//	{
//		final String key = "test";
//		final int index = 2;
//
//		Map map = new HashMap<String , Object>();
//
//		map.put(null, "asdfa");
//		map.put("1", 4231);
//		map.put("2", new ArrayList());
//		map.put("3", false);
//		map.put("4", 23l);
//		map.put("5", "测试");
//		map.put("6", null);
//
//		final String sign = SignUtil.sign(key, map);
//
//		System.out.println("sign: " + sign);
//		// 8880cd2a5df78df3dea1d4ef26b8b1fb
//	}

    /**
     * @param secretKey 密钥
     * @param bizPara   业务参数map
     * @return
     */
    protected static String sign(final String secretKey, final Map<String, Object> bizPara) {
        if (bizPara == null || bizPara.size() == 0) {
            throw new IllegalArgumentException("parameters is null or empty");
        }
        if (secretKey == null || secretKey.trim().length() == 0) {
            throw new IllegalArgumentException("secretKey is null or empty");
        }
        final List<String> params = new ArrayList<String>();
        final Iterator<String> iterator = bizPara.keySet().iterator();
        String name;
        Object obj;
        while (iterator.hasNext()) {
            name = iterator.next();
            obj = bizPara.get(name);


            if (obj != null) {
                params.add(name + CONNECTOR + obj.toString());
            }
        }
        Collections.sort(params);
        final StringBuilder sb = new StringBuilder();
        for (final String item : params) {
            sb.append(item + SEPARATOR);
        }

        String value = sb.toString().replace("\"", "");

        return hmacsha256(secretKey, value);
    }

    /**
     * 特殊处理
     * @param obj
     * @return
     */
    private static Object sort(Object obj){

        if (obj instanceof Map) {
            LinkedHashMap<String, Object> tempMap = new LinkedHashMap<>();
            JSONObject jsonObject = (JSONObject) obj;
            Set<String> keys = jsonObject.keySet();

            Iterator<String> iter = keys.iterator();

            while (iter.hasNext()) {
                String key = iter.next();
                Object val = jsonObject.get(key);

//                System.out.println(val.getClass().getTypeName());
                // 基础数据类型
                if (val instanceof String || val instanceof Number){
                    tempMap.put(key, val);
                } else if (val instanceof List || val instanceof Map) {
//                    System.out.println(val.getClass().getTypeName());
//                    System.out.println(val);
                    // 递归搞起
                    Object tempObj = sort(val);
                    tempMap.put(key, tempObj);
                }
            }


            return tempMap;

        }

        if (obj instanceof List) {
            List<Object> tempList = new ArrayList<>();
            JSONArray jsonArray = (JSONArray) obj;
            Iterator<Object> iters = jsonArray.iterator();

            while (iters.hasNext()) {
                Object o = iters.next();
                tempList.add(sort(o));
            }
            return tempList;
        }

        return null;
    }


    public static void main(String[] args) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        System.out.println(sign(resultMap, "qZRqK7kOt4QgmvJmfzb0CnYV7cxbOkQJ", "{\"app_auth_token\":\"\",\"charset\":\"utf-8\",\"method\":\"hly.api.openorder.createorder\",\"format\":\"JSON\",\"reqDtos\":{\"passengerDTOs\":[{\"nameen\":\"SAN ZHANG\",\"documentno\":\"4111199909090909\",\"birthday\":\"1998-07-08\",\"gender\":1,\"phone\":\"13301110222\",\"documentexpiredate\":\"2020-07-08\",\"isCanUpdatePassenger\":1,\"namech\":\"张三\"}],\"singleSupplementNum\":1,\"adultNum\":1,\"childNum\":0,\"productId\":110014985039,\"orderContactsForApiDTO\":{\"phone\":\"13301110222\",\"name\":\"张三\"},\"totalPrice\":10000,\"distributorId\":\"462\",\"departureDate\":\"2018-06-14\",\"typeOrigin\":2},\"moduleId\":\"10101\",\"app_id\":\"hi98193772\",\"sign_type\":\"hmacMD5\",\"version\":\"1.0\",\"timestamp\":\"2018-06-27 05:28:39\"}"));
//		System.out.println(sign("qZRqK7kOt4QgmvJmfzb0CnYV7cxbOkQJ","{\"app_auth_token\":\"\",\"app_id\":\"hi98193772\",\"charset\":\"utf-8\",\"format\":\"JSON\",\"method\":\"hly.api.openorder.createorder\",\"moduleId\":\"10101\",\"reqDtos\":{\"adultNum\":1,\"childNum\":0,\"departureDate\":\"2018-06-14\",\"distributorId\":\"462\",\"orderContactsForApiDTO\":{\"name\":\"张三\",\"phone\":\"13301110222\"},\"passengerDTOs\":[{\"birthday\":\"1998-07-08\",\"documentexpiredate\":\"2020-07-08\",\"documentno\":\"4111199909090909\",\"gender\":1,\"isCanUpdatePassenger\":1,\"namech\":\"张三\",\"nameen\":\"SAN ZHANG\",\"phone\":\"13301110222\"}],\"productId\":110014985039,\"singleSupplementNum\":1,\"totalPrice\":10000,\"typeOrigin\":2},\"sign_type\":\"hmacMD5\",\"timestamp\":\"2018-06-27 05:28:39\",\"version\":\"1.0\"}"));
//		System.out.println(sign("qZRqK7kOt4QgmvJmfzb0CnYV7cxbOkQJ","{\"app_auth_token\":\"\",\"app_id\":\"hi98193772\",\"charset\":\"utf-8\",\"format\":\"JSON\",\"method\":\"hly.api.openorder.createorder\",\"moduleId\":\"10101\",\"reqDtos\":{\"adultNum\":1,\"childNum\":0,\"departureDate\":\"2018-06-14\",\"distributorId\":\"462\",\"orderContactsForApiDTO\":{\"name\":\"张三\",\"phone\":\"13301110222\"},\"passengerDTOs\":[{\"birthday\":\"1998-07-08\",\"documentexpiredate\":\"2020-07-08\",\"documentno\":\"4111199909090909\",\"gender\":1,\"isCanUpdatePassenger\":1,\"namech\":\"张三\",\"nameen\":\"SAN ZHANG\",\"phone\":\"13301110222\"}],\"productId\":110014985039,\"singleSupplementNum\":1,\"totalPrice\":10000,\"typeOrigin\":2},\"sign_type\":\"hmacMD5\",\"timestamp\":\"2018-06-27 05:28:39\",\"version\":\"1.0\"}"));
//		System.out.println(sign("qZRqK7kOt4QgmvJmfzb0CnYV7cxbOkQJ","{\"app_auth_token\":\"\",\"app_id\":\"hi98193772\",\"charset\":\"utf-8\",\"format\":\"JSON\",\"method\":\"hly.api.openorder.createorder\",\"moduleId\":\"10101\",\"reqDtos\":{\"adultNum\":1,\"childNum\":0,\"departureDate\":\"2018-06-14\",\"distributorId\":\"462\",\"orderContactsForApiDTO\":{\"name\":\"张三\",\"phone\":\"13301110222\"},\"passengerDTOs\":[{\"birthday\":\"1998-07-08\",\"documentexpiredate\":\"2020-07-08\",\"documentno\":\"4111199909090909\",\"gender\":1,\"isCanUpdatePassenger\":1,\"namech\":\"张三\",\"nameen\":\"SAN ZHANG\",\"phone\":\"13301110222\"}],\"productId\":110014985039,\"singleSupplementNum\":1,\"totalPrice\":10000,\"typeOrigin\":2},\"sign_type\":\"hmacMD5\",\"timestamp\":\"2018-06-27 05:28:39\",\"version\":\"1.0\"}"));
//		System.out.println(sign("qZRqK7kOt4QgmvJmfzb0CnYV7cxbOkQJ","{\"app_auth_token\":\"\",\"app_id\":\"hi98193772\",\"charset\":\"utf-8\",\"format\":\"JSON\",\"method\":\"hly.api.openorder.createorder\",\"moduleId\":\"10101\",\"reqDtos\":{\"adultNum\":1,\"childNum\":0,\"departureDate\":\"2018-06-14\",\"distributorId\":\"462\",\"orderContactsForApiDTO\":{\"name\":\"张三\",\"phone\":\"13301110222\"},\"passengerDTOs\":[{\"birthday\":\"1998-07-08\",\"documentexpiredate\":\"2020-07-08\",\"documentno\":\"4111199909090909\",\"gender\":1,\"isCanUpdatePassenger\":1,\"namech\":\"张三\",\"nameen\":\"SAN ZHANG\",\"phone\":\"13301110222\"}],\"productId\":110014985039,\"singleSupplementNum\":1,\"totalPrice\":10000,\"typeOrigin\":2},\"sign_type\":\"hmacMD5\",\"timestamp\":\"2018-06-27 05:28:39\",\"version\":\"1.0\"}"));
//		System.out.println(sign("qZRqK7kOt4QgmvJmfzb0CnYV7cxbOkQJ","{\"app_auth_token\":\"\",\"app_id\":\"hi98193772\",\"charset\":\"utf-8\",\"format\":\"JSON\",\"method\":\"hly.api.openorder.createorder\",\"moduleId\":\"10101\",\"reqDtos\":{\"adultNum\":1,\"childNum\":0,\"departureDate\":\"2018-06-14\",\"distributorId\":\"462\",\"orderContactsForApiDTO\":{\"name\":\"张三\",\"phone\":\"13301110222\"},\"passengerDTOs\":[{\"birthday\":\"1998-07-08\",\"documentexpiredate\":\"2020-07-08\",\"documentno\":\"4111199909090909\",\"gender\":1,\"isCanUpdatePassenger\":1,\"namech\":\"张三\",\"nameen\":\"SAN ZHANG\",\"phone\":\"13301110222\"}],\"productId\":110014985039,\"singleSupplementNum\":1,\"totalPrice\":10000,\"typeOrigin\":2},\"sign_type\":\"hmacMD5\",\"timestamp\":\"2018-06-27 05:28:39\",\"version\":\"1.0\"}"));
//		System.out.println(sign("qZRqK7kOt4QgmvJmfzb0CnYV7cxbOkQJ","{\"app_auth_token\":\"\",\"app_id\":\"hi98193772\",\"charset\":\"utf-8\",\"format\":\"JSON\",\"method\":\"hly.api.openorder.createorder\",\"moduleId\":\"10101\",\"reqDtos\":{\"adultNum\":1,\"childNum\":0,\"departureDate\":\"2018-06-14\",\"distributorId\":\"462\",\"orderContactsForApiDTO\":{\"name\":\"张三\",\"phone\":\"13301110222\"},\"passengerDTOs\":[{\"birthday\":\"1998-07-08\",\"documentexpiredate\":\"2020-07-08\",\"documentno\":\"4111199909090909\",\"gender\":1,\"isCanUpdatePassenger\":1,\"namech\":\"张三\",\"nameen\":\"SAN ZHANG\",\"phone\":\"13301110222\"}],\"productId\":110014985039,\"singleSupplementNum\":1,\"totalPrice\":10000,\"typeOrigin\":2},\"sign_type\":\"hmacMD5\",\"timestamp\":\"2018-06-27 05:28:39\",\"version\":\"1.0\"}"));
//		System.out.println(sign("qZRqK7kOt4QgmvJmfzb0CnYV7cxbOkQJ","{\"app_auth_token\":\"\",\"app_id\":\"hi98193772\",\"charset\":\"utf-8\",\"format\":\"JSON\",\"method\":\"hly.api.openorder.createorder\",\"moduleId\":\"10101\",\"reqDtos\":{\"adultNum\":1,\"childNum\":0,\"departureDate\":\"2018-06-14\",\"distributorId\":\"462\",\"orderContactsForApiDTO\":{\"name\":\"张三\",\"phone\":\"13301110222\"},\"passengerDTOs\":[{\"birthday\":\"1998-07-08\",\"documentexpiredate\":\"2020-07-08\",\"documentno\":\"4111199909090909\",\"gender\":1,\"isCanUpdatePassenger\":1,\"namech\":\"张三\",\"nameen\":\"SAN ZHANG\",\"phone\":\"13301110222\"}],\"productId\":110014985039,\"singleSupplementNum\":1,\"totalPrice\":10000,\"typeOrigin\":2},\"sign_type\":\"hmacMD5\",\"timestamp\":\"2018-06-27 05:28:39\",\"version\":\"1.0\"}"));
//		System.out.println(sign("qZRqK7kOt4QgmvJmfzb0CnYV7cxbOkQJ","{\"app_auth_token\":\"\",\"app_id\":\"hi98193772\",\"charset\":\"utf-8\",\"format\":\"JSON\",\"method\":\"hly.api.openorder.createorder\",\"moduleId\":\"10101\",\"reqDtos\":{\"adultNum\":1,\"childNum\":0,\"departureDate\":\"2018-06-14\",\"distributorId\":\"462\",\"orderContactsForApiDTO\":{\"name\":\"张三\",\"phone\":\"13301110222\"},\"passengerDTOs\":[{\"birthday\":\"1998-07-08\",\"documentexpiredate\":\"2020-07-08\",\"documentno\":\"4111199909090909\",\"gender\":1,\"isCanUpdatePassenger\":1,\"namech\":\"张三\",\"nameen\":\"SAN ZHANG\",\"phone\":\"13301110222\"}],\"productId\":110014985039,\"singleSupplementNum\":1,\"totalPrice\":10000,\"typeOrigin\":2},\"sign_type\":\"hmacMD5\",\"timestamp\":\"2018-06-27 05:28:39\",\"version\":\"1.0\"}"));
//		System.out.println(sign("qZRqK7kOt4QgmvJmfzb0CnYV7cxbOkQJ","{\"app_auth_token\":\"\",\"app_id\":\"hi98193772\",\"charset\":\"utf-8\",\"format\":\"JSON\",\"method\":\"hly.api.openorder.createorder\",\"moduleId\":\"10101\",\"reqDtos\":{\"adultNum\":1,\"childNum\":0,\"departureDate\":\"2018-06-14\",\"distributorId\":\"462\",\"orderContactsForApiDTO\":{\"name\":\"张三\",\"phone\":\"13301110222\"},\"passengerDTOs\":[{\"birthday\":\"1998-07-08\",\"documentexpiredate\":\"2020-07-08\",\"documentno\":\"4111199909090909\",\"gender\":1,\"isCanUpdatePassenger\":1,\"namech\":\"张三\",\"nameen\":\"SAN ZHANG\",\"phone\":\"13301110222\"}],\"productId\":110014985039,\"singleSupplementNum\":1,\"totalPrice\":10000,\"typeOrigin\":2},\"sign_type\":\"hmacMD5\",\"timestamp\":\"2018-06-27 05:28:39\",\"version\":\"1.0\"}"));
    }


    @SuppressWarnings("unchecked")
    public static String sign(LinkedHashMap<String, Object> resultMap, final String key, final String json) {
        if (key == null || key.trim().length() == 0) {
            throw new IllegalArgumentException("key is null or empty");
        }
        if (json == null || json.trim().length() == 0) {
            throw new IllegalArgumentException("json is null or empty");
        }
        Map<String, Object> req = null;
        try {
//            System.out.println("加密前:" + json);
            req = JSON.parseObject(json, new TypeReference<LinkedHashMap<String, Object>>() {
            }, Feature.OrderedField);

            Object o = sort(JSONObject.parse(json));

            System.out.println(o);

//            System.out.println("加密后:" + JSONObject.toJSONString(req));
            req.remove("sign");
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
        return sign(key, req);
    }
}
