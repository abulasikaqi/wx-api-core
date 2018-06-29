package wx.oauth2.constants;

/**
 * Created by LL on 2018/4/11.
 */
public class OAuth2Constants {

    /**
     * 获取 access_token
     */
    public static String API_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /**
     * 通过网页授权access_token获取用户基本信息（需授权作用域为snsapi_userinfo）
     */
    public static String API_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
}
