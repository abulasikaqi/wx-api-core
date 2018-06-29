package wx.fans;

/**
 * 用户管理
 * Created by LL on 2018/4/26.
 */
public class FansConstants {

    /**
     * 获取用户基本信息（包括UnionID机制）
     * http请求方式: GET
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140839
     */
    public static String  API_FANS_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";


    /**
     * 批量获取用户基本信息
     * 开发者可通过该接口来批量获取用户基本信息。最多支持一次拉取100条。
     * http请求方式: POST
     * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140839
     */
    public static String API_FANS_INFO_BATCH_URL = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=%";
}
