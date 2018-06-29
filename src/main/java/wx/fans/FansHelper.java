package wx.fans;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import wx.common.util.WxRequest;

import java.util.ArrayList;
import java.util.List;

import static wx.fans.FansConstants.API_FANS_INFO_BATCH_URL;
import static wx.fans.FansConstants.API_FANS_INFO_URL;

/**
 * Created by LL on 2018/4/11.
 */
public class FansHelper {
    private static WxRequest wxRequest = new WxRequest();

    private static String execute(String url, String methodName) {
        return execute(url, methodName, null);
    }

    private static String execute(String url, String methodName, String data) {
        return wxRequest.jsRequest(url, methodName, data);
    }

    /**
     * 获取用户基本信息（包括UnionID机制）
     * @param accessToken 调用接口凭证
     * @param openId 普通用户的标识，对当前公众号唯一
     * @return json
     */
    public static String getFansInfo(String accessToken, String openId){

        String url = String.format(API_FANS_INFO_URL, accessToken, openId);

        return execute(url, HttpGet.METHOD_NAME);
    }

    /**
     * 批量获取用户基本信息
     * @param accessToken 调用接口凭证
     * @param openIds 用户的标识，对当前公众号唯一
     * @return json
     */
    public static String batchGetFansInfo(String accessToken, String... openIds){

        String url = String.format(API_FANS_INFO_BATCH_URL, accessToken);

        List<JSONObject> list = new ArrayList<>();
        for (String id : openIds) {
            JSONObject obj = new JSONObject();
            obj.put("openid", id);
            obj.put("lang", "zh_CN");

            list.add(obj);
        }

        JSONObject object = new JSONObject();
        object.put("user_list", list);

        return execute(url, HttpPost.METHOD_NAME, object.toJSONString());
    }
}
