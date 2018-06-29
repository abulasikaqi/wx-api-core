package wx.third.helper;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wx.common.util.WxRequest;

import static wx.oauth2.constants.OAuth2Constants.API_USER_INFO_URL;
import static wx.third.constants.ThirdConstants.API_GET_OAUTH2_CODE_URL;
import static wx.third.constants.ThirdConstants.API_GET_OAUTH_TOKEN_URL;

/**
 * Created by LL on 2018/3/28.
 */
public class ThirdOAuth2Helper {

    private static Logger logger = LoggerFactory.getLogger(ThirdOAuth2Helper.class);

    private static WxRequest wxRequest = new WxRequest();

    private static String execute(String url, String methodName) {
        return  wxRequest.jsRequest(url, methodName);
    }

    /**
     * 统一请求
     * @param url url
     * @param methodName 请求方式（GET、POST）
     * @param reqData reqData
     * @return STR
     */
    private static String execute(String url, String methodName, String reqData) {

        return wxRequest.jsRequest(url, methodName, reqData);
    }

    /**
     * 代公众号发起网页授权-- 第一步：请求CODE
     * @param appId 公众号的 appId
     * @param redirectUrl 重定向地址，需要urlencode，这里填写的应是服务开发方的回调地址
     * @param scope 应用授权作用域，snsapi_base （不弹出授权页面），snsapi_userinfo （弹出授权页面）
     * @param state 重定向后会带上state参数，开发者可以填写任意参数值，最多128字节
     * @param componentAppId 服务方的 appId
     * @return url
     */
    public static String getOAuthCodeUrl(String appId, String redirectUrl, String scope, String state, String componentAppId) {
        /*
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
        return String.format(API_GET_OAUTH2_CODE_URL, appId, redirectUrl, scope, state, componentAppId);
    }

    /**
     * 代公众号发起网页授权-- 第二步：通过code换取access_token
     * @param appId 公众号的 appId
     * @param code 填写第一步获取的code参数
     * @param componentAppId 服务开发方的 appId
     * @param componentAccessToken 服务开发方的access_token
     * @return JSON
     */
    public static String getOAuthToken(String appId, String code, String componentAppId, String componentAccessToken) {
        /*
            参数	                    是否必须	    说明
            appid	                是	        公众号的appid
            code	                是	        填写第一步获取的code参数
            grant_type	            是	        填authorization_code
            component_appid	        是	        服务开发方的appid
            component_access_token	是	        服务开发方的access_token
         */
        String url = String.format(API_GET_OAUTH_TOKEN_URL, appId, code, componentAppId, componentAccessToken);

        return execute(url, HttpPost.METHOD_NAME);
    }

    /**
     * 通过网页授权access_token获取用户基本信息（需授权作用域为snsapi_userinfo）
     * @param accessToken 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     * @param openId 用户的唯一标识
     * @return JSON
     */
    public static String getUserInfo(String accessToken, String openId) {

        String url = String.format(API_USER_INFO_URL, accessToken, openId);
        return execute(url, HttpGet.METHOD_NAME);
    }
}
