package wx.jsapi;

import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wx.common.util.WxRequest;

/**
 * Created by LL on 2018/4/27.
 */
public class JsAPIHelper {
    private static Logger logger = LoggerFactory.getLogger(JsAPIHelper.class);
    private static WxRequest wxRequest = new WxRequest();

    private static String execute(String url, String methodName) {
        return wxRequest.jsRequest(url, methodName);
    }

    /**
     * 获取 jsapi_ticket
     * @param accessToken access_token
     * @return JSON
     */
    public static String getJsAPITicket(String accessToken) {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
        url = url.replace("ACCESS_TOKEN", accessToken);

        String result = execute(url, HttpGet.METHOD_NAME);
        logger.info("3 获取（刷新）jsapi_ticket result=" + result);
        return result;
    }
}
