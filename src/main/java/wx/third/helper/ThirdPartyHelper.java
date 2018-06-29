package wx.third.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import wx.third.constants.ThirdConstants;
import wx.common.util.WXBizMsgCrypt;
import wx.common.util.WxRequest;
import wx.common.util.WxUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LL on 2018/3/12.
 */
public class ThirdPartyHelper {
    private Logger logger = LoggerFactory.getLogger(ThirdPartyHelper.class);

    private WxRequest wxRequest = new WxRequest();

    private Map<String, Object> reqData;

    /**
     * 统一请求
     * @param url url
     * @param reqData reqData
     * @return STR
     */
    private String execute(String url, String reqData) {

        return wxRequest.thirdRequest(url, reqData);
    }

    /**
     * 使用授权码换取公众号或小程序的接口调用凭据和授权信息
     * @param componentAppId 第三方平台 appId
     * @param authorizationCode 授权code
     * @param componentAccessToken 第三方平台access_token
     */
    public String authorizerAccessToken(String componentAppId, String authorizationCode, String componentAccessToken) {
        reqData = new HashMap<>();
        reqData.put("component_appid", componentAppId);
        reqData.put("authorization_code", authorizationCode);

        String url = String.format(ThirdConstants.API_QUERY_AUTH_URL_SUFFIX, componentAccessToken);

        return this.execute(url, JSON.toJSONString(reqData));
    }

    /**
     * 获取（刷新）授权公众号或小程序的接口调用凭据（令牌）
     * @param componentAppId 第三方平台appId
     * @param authorizerAppId 授权方appId
     * @param refreshToken 授权方的刷新令牌，刷新令牌主要用于第三方平台获取和刷新已授权用户的access_token，只会在授权时刻提供，请妥善保存。一旦丢失，只能让用户重新授权，才能再次拿到新的刷新令牌
     * @param componentAccessToken 第三方平台access_token
     * @return STR
     */
    public String refreshToken(String componentAppId, String authorizerAppId, String refreshToken, String componentAccessToken) {

        String url = String.format(ThirdConstants.API_AUTHORIZER_TOKEN_URL_SUFFIX, componentAccessToken);

        reqData = new HashMap<>();
        reqData.put("component_appid", componentAppId);
        reqData.put("authorizer_appid", authorizerAppId);
        reqData.put("authorizer_refresh_token", refreshToken);

        String params = JSON.toJSONString(reqData);
        logger.info("3 获取（刷新）授权公众号或小程序的接口调用凭据（令牌） params=" + params);

        String result = this.execute(url, params);
        logger.info("3 result=" + result);

        return result;
    }

    /**
     * 获取授权方的帐号基本信息
     *
     * 该API用于获取授权方的基本信息，包括头像、昵称、帐号类型、认证类型、微信号、原始ID和二维码图片URL。
     * 需要特别记录授权方的帐号类型，在消息及事件推送时，对于不具备客服接口的公众号，需要在5秒内立即响应；而若有客服接口，则可以选择暂时不响应，而选择后续通过客服接口来发送消息触达粉丝。
     *
     * @param componentAccessToken 第三方平台 access_token
     * @param componentAppId 第三方平台 appId
     * @param authorizerAppId 授权方 appId
     * @return s
     */
    public String getAuthorizerInfo(String componentAccessToken, String componentAppId, String authorizerAppId) {
        reqData = new HashMap<>();
        reqData.put("component_appid", componentAppId);
        reqData.put("authorizer_appid", authorizerAppId);

        String url = String.format(ThirdConstants.API_GET_AUTHORIZER_INFO_URL_SUFFIX, componentAccessToken);
        return this.execute(url, JSONObject.toJSONString(reqData));
    }

    /**
     * 获取授权方的选项设置信息
     * @param componentAccessToken 第三方平台 access_token
     * @param componentAppId 第三方平台 appId
     * @param authorizerAppId 授权公众号或小程序的 appId
     * @param optionName 选项名称
     * @return s
     */
    public String getAuthorizerOption(String componentAccessToken, String componentAppId, String authorizerAppId, String optionName) {

        String url = String.format(ThirdConstants.API_GET_AUTHORIZER_OPTION_URL_SUFFIX, componentAccessToken);
        reqData = new HashMap<>();
        reqData.put("component_appid", componentAppId);
        reqData.put("authorizer_appid", authorizerAppId);
        reqData.put("option_name", optionName);

        return this.execute(url, JSONObject.toJSONString(reqData));
    }

    /**
     * 设置授权方的选项信息
     * @param componentAccessToken 第三方平台 access_token
     * @param componentAppId 第三方平台 appId
     * @param authorizerAppId 授权公众号或小程序的 appId
     * @param optionName 选项名称
     * @param optionValue 设置的选项值
     * @return s
     */
    public String setAuthorizerOption(String componentAccessToken, String componentAppId, String authorizerAppId, String optionName, String optionValue) {

        String url = String.format(ThirdConstants.API_SET_AUTHORIZER_OPTION_URL_SUFFIX, componentAccessToken);
        reqData = new HashMap<>();
        reqData.put("component_appid", componentAppId);
        reqData.put("authorizer_appid", authorizerAppId);
        reqData.put("option_name", optionName);
        reqData.put("option_value", optionValue);

        return this.execute(url, JSONObject.toJSONString(reqData));
    }

    /**
     * 第二步： 获取预授权码 pre_auth_code
     * @param componentAccessToken 第三方平台 componentAccessToken
     * @param componentAppId 第三方平台 appId
     * @return s
     */
    public String getPreAuthCode(String componentAccessToken, String componentAppId) {

        logger.info("2 获取预授权码 pre_auth_code componentAccessToken=" + componentAccessToken + ",componentAppId=" + componentAppId);
        String url = String.format(ThirdConstants.API_CREATE_PREAUTHCODE_URL_SUFFIX, componentAccessToken);

        reqData = new HashMap<>();
        reqData.put("component_appid", componentAppId);

        String result = this.execute(url, JSON.toJSONString(reqData));
        logger.info("2 result=" + result);
        return result;
    }

    /**
     * 第一步： 获取第三方平台 component_access_token
     * @param componentAppId 第三方平台 appId
     * @param componentAppSecret 第三方平台 appsecret
     * @param ticket 微信后台推送的 ticket
     * @return s
     */
    public String getComponentAccessToken(String componentAppId, String componentAppSecret, String ticket) {
        String url = ThirdConstants.API_COMPONENT_TOKEN_URL_SUFFIX;

        reqData = new HashMap<>();
        reqData.put("component_appid", componentAppId);
        reqData.put("component_appsecret", componentAppSecret);
        reqData.put("component_verify_ticket", ticket);

        String params = JSON.toJSONString(reqData);
        logger.info("1 获取第三方平台 component_access_token params=" + params);

        String result = this.execute(url, params);
        logger.info("1 result=" + result);
        return result;
    }

    /**
     * 微信授权页URL
     * @param componentAppId 第三方平台 appId
     * @param preAuthCode 预授权码
     * @param redirectUrl 回调URI
     * @return s
     */
    public String getAuthUrl(String componentAppId, String preAuthCode, String redirectUrl) {
        return  String.format(ThirdConstants.COMPONENTLOGINPAGE_URL, componentAppId, preAuthCode, redirectUrl);
    }

    //**************************************************************************************************************

    /**
     * 消息解密
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param msgSignature 消息体签名，用于验证消息体的正确性
     * @param postData XML消息体
     * @return map
     */
    public Map<String, String> getPostData(String timestamp, String nonce, String msgSignature, String postData,
                                           String token, String aesKey, String componentAppId) {

        try {
            WXBizMsgCrypt pc = new WXBizMsgCrypt(token, aesKey, componentAppId);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(postData);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);

            Element root = document.getDocumentElement();
            NodeList nodelist1 = root.getElementsByTagName("Encrypt");

            String encrypt = nodelist1.item(0).getTextContent();

            String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";

            String fromXML = String.format(format, encrypt);

            // 第三方收到公众号平台发送的消息
            String result = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);

//            logger.info("解密后明文: " + result);
            return WxUtil.xmlToMap(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
