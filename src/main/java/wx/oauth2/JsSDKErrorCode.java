package wx.oauth2;

/**
 * Created by LL on 2018/3/28.
 */
public class JsSDKErrorCode extends Exception {

    public final static int OK = 0;
    public final static int systemBusy = -1;
    public final static int invalidAppId = 40013;
    public final static int appSecretErrorOrNotBelong = 40001;
    public final static int invalidGrantType = 40002;
    public final static int invalidIP = 40164;

    private int code;

    private static String getMessage(int code) {
        switch (code) {
            case invalidAppId:
                return "AppID无效";
            case systemBusy:
                return "系统繁忙，此时请开发者稍候再试";
            case OK:
                return "请求成功";
            case appSecretErrorOrNotBelong:
                return "AppSecret错误或者AppSecret不属于这个公众号，请开发者确认AppSecret的正确性";
            case invalidGrantType:
                return "请确保grant_type字段值为client_credential";
            case invalidIP:
                return "调用接口的IP地址不在白名单中，请在接口IP白名单中进行设置";
            default:
                return null; // cannot be
        }
    }

    public int getCode() {
        return code;
    }

    JsSDKErrorCode(int code) {
        super(getMessage(code));
        this.code = code;
    }
}
