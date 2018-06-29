package wx.common.config;

/**
 * Created by LL on 2018/3/12.
 */
public class WxConfig {

    /**
     * 第三方用户唯一凭证
     */
    private static String appId;

    private static String token;

    /**
     * 第三方用户唯一凭证密钥，即appsecret
     */
    private static String appsecret;

    public static String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        WxConfig.appId = appId;
    }

    public static String getToken() {
        return token;
    }

    public void setToken(String token) {
        WxConfig.token = token;
    }

    public static String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        WxConfig.appsecret = appsecret;
    }

    /**
     * 微信第三方授权相关
     */
    public static class Third {

        /**
         * 第三方平台 appID
         */
        private static String appId;

        /**
         * 第三方公众平台上，开发者设置的 token
         */
        private static String token;

        /**
         * 第三方平台 appSecret
         */
        private static String appSecret;

        /**
         * 第三方平台申请时的接收消息的加密symmetric_key（也称为EncodingAESKey）来进行加密
         * 公众平台上，开发者设置的 EncodingAESKey
         */
        private static String encodingAesKey;

        /**
         * 第三方平台接口调用凭据
         * 出于安全考虑，在第三方平台创建审核通过后，微信服务器 每隔10分钟会向第三方的消息接收地址推送一次component_verify_ticket，用于获取第三方平台接口调用凭据。
         *
         * ps : 缓存redis
         */
        private String componentVerifyTicket;

        /**
         * 第三方平台通过自己的component_appid（即在微信开放平台管理中心的第三方平台详情页中的AppID和AppSecret）和component_appsecret，
         * 以及component_verify_ticket（每10分钟推送一次的安全ticket）来获取自己的接口调用凭据（component_access_token）
         *
         * 第三方平台component_access_token是第三方平台的下文中接口的调用凭据，也叫做令牌（component_access_token）。
         * 每个令牌是存在有效期（2小时）的，且令牌的调用不是无限制的，请第三方平台做好令牌的管理，在令牌快过期时（比如1小时50分）再进行刷新。
         *
         * ps : 缓存redis
         */
        private String componentAccessToken;

        /**
         * 预授权码
         * 第三方平台通过自己的接口调用凭据（component_access_token）来获取用于授权流程准备的预授权码（pre_auth_code）
         *
         * ps : 缓存redis
         */
        private String preAuthCode;

        public static String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            Third.appId = appId;
        }

        public static String getToken() {
            return token;
        }

        public void setToken(String token) {
            Third.token = token;
        }

        public static String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            Third.appSecret = appSecret;
        }

        public static String getEncodingAesKey() {
            return encodingAesKey;
        }

        public void setEncodingAesKey(String encodingAesKey) {
            Third.encodingAesKey = encodingAesKey;
        }
    }
}
