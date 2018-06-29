package wx.third.constants;

/**
 * Created by LL on 2017/9/28.
 */
public class ThirdConstants {


    //*******************************************第三方********************************************//
    public static final String THIRD_DOMAIN_API = "https://api.weixin.qq.com/cgi-bin/component";
    /**
     * 获取第三方平台component_access_token
     */
    public static final String API_COMPONENT_TOKEN_URL_SUFFIX = "/api_component_token";

    /**
     * 获取预授权码pre_auth_code
     */
    public static String API_CREATE_PREAUTHCODE_URL_SUFFIX = "/api_create_preauthcode?component_access_token=%s";

    /**
     * 微信第三方平台授权页
     */
    public static String COMPONENTLOGINPAGE_URL = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=%s&pre_auth_code=%s&redirect_uri=%s";

    /**
     * 使用授权码换取公众号或小程序的接口调用凭据和授权信息
     */

    public static String API_QUERY_AUTH_URL_SUFFIX = "/api_query_auth?component_access_token=%s";

    /**
     * 获取（刷新）授权公众号或小程序的接口调用凭据（令牌）
     */
    public static String API_AUTHORIZER_TOKEN_URL_SUFFIX = "/api_authorizer_token?component_access_token=%s";

    /**
     * 获取授权方的帐号基本信息
     */
    public static String API_GET_AUTHORIZER_INFO_URL_SUFFIX = "/api_get_authorizer_info?component_access_token=%s";

    /**
     * 获取授权方的选项设置信息
     */
    public static String API_GET_AUTHORIZER_OPTION_URL_SUFFIX = "/api_get_authorizer_option?component_access_token=%s";

    /**
     * 设置授权方的选项信息
     */
    public static String API_SET_AUTHORIZER_OPTION_URL_SUFFIX = "/api_set_authorizer_option?component_access_token=%s";

    /*
        代公众号发起网页授权
     */
    /**
     * 第一步：请求CODE
     *
     * 参数	            是否必须	    说明
     * appid	        是	        公众号的appid
     * redirect_uri	    是	        重定向地址，需要urlencode，这里填写的应是服务开发方的回调地址
     * response_type	是	        返回类型，请填写code
     * scope	        是	        应用授权作用域，snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）
     * state	        否	        重定向后会带上state参数，开发者可以填写任意参数值，最多128字节
     * component_appid	是	        服务方的appid，在申请创建公众号服务成功后，可在公众号服务详情页找到
     * #wechat_redirect	是	        无论直接打开还是做页面302重定向时候，必须带此参数
     *
     * 返回说明
     * 用户允许授权后，将会重定向到redirect_uri的网址上，并且带上code, state以及appid
     */
    public static String API_GET_OAUTH2_CODE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&component_appid=%s#wechat_redirect";

    /**
     * 第二步：通过code换取access_token
     * 参数说明
     参数	是否必须	说明
     appid	是	公众号的appid
     code	是	填写第一步获取的code参数
     grant_type	是	填authorization_code
     component_appid	是	服务开发方的appid
     component_access_token	是	服务开发方的access_token
     */
    public static String API_GET_OAUTH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/component/access_token?appid=%s&code=%s&grant_type=authorization_code&component_appid=%s&component_access_token=%s";
}
