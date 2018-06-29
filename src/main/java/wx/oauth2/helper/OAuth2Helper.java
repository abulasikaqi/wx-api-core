package wx.oauth2.helper;

import org.apache.http.client.methods.HttpGet;
import wx.common.util.WxRequest;

import static wx.oauth2.constants.OAuth2Constants.API_ACCESS_TOKEN_URL;

/**
 * Created by LL on 2018/4/11.
 */
public class OAuth2Helper {
    private static WxRequest wxRequest = new WxRequest();

    private static String execute(String url, String methodName) {
        return wxRequest.jsRequest(url, methodName);
    }

    /**
     * 获取access_token (GET)
     * @param appId 第三方用户唯一凭证
     * @param secret 第三方用户唯一凭证密钥，即 appsecret
     * @return s
     */
    public String getAccessToken(String appId, String secret){

        String url = String.format(API_ACCESS_TOKEN_URL, appId, secret);

        return execute(url, HttpGet.METHOD_NAME);
    }


}
