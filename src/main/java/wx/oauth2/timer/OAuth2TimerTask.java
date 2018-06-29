package wx.oauth2.timer;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wx.common.redis.RedisCacheHelper;
import wx.oauth2.helper.OAuth2Helper;

import java.util.Timer;

/**
 * 原公众号token刷新
 * Created by LL on 2018/3/28.
 */
public class OAuth2TimerTask extends Thread {
    private Logger logger = LoggerFactory.getLogger(OAuth2TimerTask.class);

    /**
     * Redis 工具
     */
    private RedisCacheHelper redisCacheHelper;

    private OAuth2Helper oAuth2Helper;

    private String appId;

    private String secret;

    public OAuth2TimerTask(RedisCacheHelper redisCacheHelper, String appId, String secret) {
        this.redisCacheHelper = redisCacheHelper;

        this.oAuth2Helper = new OAuth2Helper();
        this.appId = appId;
        this.secret = secret;
    }


    @Override
    public void run() {

        // 启动定时器
        refreshAccessTokenTimer();
    }


    /**
     * 原公众号token刷新 timerTask
     */
    private void refreshAccessTokenTimer() {
        Timer tt = new Timer("refresh_access_token");
        logger.info("refresh_access_token timer started.");
        tt.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                refreshAccessToken();
            }
        }, 0, 110 * 60 * 1000);
    }

    /**
     * 原公众号token刷新
     */
    private void refreshAccessToken() {
        String result = oAuth2Helper.getAccessToken(appId, secret);
        if (StringUtils.isNotBlank(result)) {
            if (result.contains("errcode")) {
//                JSONObject errorInfo = JSONObject.parseObject(result);

                String msg = String.format("原公众号token刷新,%s", result);

                if (logger.isErrorEnabled()) {
                    logger.error(msg);
                } else {
                    logger.info(msg);
                }
            } else {
                JSONObject obj = JSONObject.parseObject(result);

                redisCacheHelper.set("oauth2:access_token", obj.get("access_token"));
            }
        }
    }


}
