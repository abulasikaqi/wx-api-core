package wx.third.timer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wx.common.redis.RedisCacheHelper;
import wx.jsapi.JsAPIHelper;
import wx.third.helper.ThirdPartyHelper;

import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by LL on 2018/3/15.
 */
public class ThirdPlateFormTimerTask extends Thread {

    private Logger logger = LoggerFactory.getLogger(ThirdPlateFormTimerTask.class);

    /**
     * 第三方平台工具包
     */
    private ThirdPartyHelper helper;

    /**
     * Redis 工具
     */
    private RedisCacheHelper redisCacheHelper;

    /**
     * 第三方平台 appID
     */
    private String thirdAppId;

    /**
     * 第三方平台 appSecret
     */
    private String thirdAppSecret;

    public ThirdPlateFormTimerTask(RedisCacheHelper redisCacheHelper, String thirdAppId, String thirdAppSecret) {
        this.helper = new ThirdPartyHelper();
        this.redisCacheHelper = redisCacheHelper;

        this.thirdAppId = thirdAppId;
        this.thirdAppSecret = thirdAppSecret;
    }

    @Override
    public void run() {
        try {
            // 平台授权标识
            boolean flag = false;
            // 刷新token标识
            boolean refFLag = true;
            while (!flag) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Map<?, ?> map = redisCacheHelper.get("thirdPlateForm:component_verify_ticket", Map.class);

                // 校验是否有 component_verify_ticket
                if (map != null && map.get("ComponentVerifyTicket") != null) {
                    // component_access_token
                    this.componentAccessToken();

                    // pre_auth_code
                    this.preAuthCode();
                }

                /**
                 * TODO 获取（刷新）授权公众号或小程序的接口调用凭据（令牌）
                 * TODO 刷新 jsapi_ticket
                 * 在启动刷新token的timerTask时，有如下两种情况：
                 * 1 首次启动服务，等待 component_access_token 获取到之后再运行 timerTask
                 * 2 重启服务时，component_access_token 不为空时，直接运行 timerTask
                 */
                String componentAccessToken = redisCacheHelper.get("thirdPlateForm:component_access_token", String.class);
                if (StringUtils.isNotBlank(componentAccessToken) && refFLag) {
                    // authorizer_refresh_token
                    refreshTimer();

                    // 修改刷新标识
                    refFLag = false;
                }

                String preAuthCode = redisCacheHelper.get("thirdPlateForm:pre_auth_code", String.class);
                if (StringUtils.isNotBlank(preAuthCode)) {
                    flag = true;
                    // 定时任务
                    this.component_access_token_timer();
                    this.pre_auth_code_timer();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    /**
     * 1 获取第三方平台 component_access_token 定时器
     */
    private void component_access_token_timer() {
        Timer timer = new Timer("component_access_token timer");
        long time = 110 * 60 * 1000;
        logger.info("component_access_token timer started.");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                componentAccessToken();
            }
        }, time, time);
    }

    /**
     * 2 获取预授权码 pre_auth_code
     */
    private void pre_auth_code_timer() {
        Timer timer = new Timer("pre_auth_code timer");
        logger.info("pre_auth_code timer started.");
        long time = 25 * 60 * 1000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                preAuthCode();
            }
        }, time, time);
    }

    /**
     * 3 获取（刷新）授权公众号或小程序的接口调用凭据（令牌）
     */
    private void refreshTimer() {
        Timer tt = new Timer("refresh_token timer");
        logger.info("refresh_token timer started.");
        tt.schedule(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        }, 0, 110 * 60 * 1000);
    }

    /**
     * 1.1 获取第三方平台 component_access_token
     */
    private void componentAccessToken() {

        Map<?, ?> redisData = redisCacheHelper.get("thirdPlateForm:component_verify_ticket", Map.class);
        logger.info("1.1 获取第三方平台 component_access_token**************************************--|" + redisData);
        String ticket = redisData.get("ComponentVerifyTicket").toString();
        if (StringUtils.isNotBlank(ticket)) {
            String result = helper.getComponentAccessToken(thirdAppId, thirdAppSecret, ticket);
            if (StringUtils.isNotBlank(result)) {
                Map<?, ?> resultMap = JSON.parseObject(result, Map.class);
                String token = resultMap.get("component_access_token").toString();

                redisCacheHelper.set("thirdPlateForm:component_access_token", token, 120L * 60, TimeUnit.SECONDS);
            }

        }
    }


    /**
     * 2.1 获取预授权码 pre_auth_code
     */
    private void preAuthCode() {
        String componentAccessToken = redisCacheHelper.get("thirdPlateForm:component_access_token", String.class);
        logger.info("2.1 获取预授权码 pre_auth_code#######################################--|" + componentAccessToken);
        if (StringUtils.isNotBlank(componentAccessToken)) {
            String result = helper.getPreAuthCode(componentAccessToken, thirdAppId);
            if (StringUtils.isNotBlank(result)) {
                Map<?, ?> map = JSON.parseObject(result, Map.class);
                String authCode = map.get("pre_auth_code").toString();

                redisCacheHelper.set("thirdPlateForm:pre_auth_code", authCode, 30L * 60, TimeUnit.SECONDS);
            }
        }

    }

    /**
     * 3.1 获取（刷新）授权公众号或小程序的接口调用凭据（令牌）
     */
    private void refresh() {
        Collection<String> keys = redisCacheHelper.keys("authorizer_access_token:*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                JSONObject redisData = redisCacheHelper.get(key, JSONObject.class);
                String refreshToken = redisData.getString("authorizer_refresh_token");
                String authorizerAppId = redisData.getString("authorizer_appid");

                // 获取（刷新）授权公众号或小程序的接口调用凭据（令牌）
                String componentAccessToken = redisCacheHelper.get("thirdPlateForm:component_access_token", String.class);
                String jsonData = helper.refreshToken(thirdAppId, authorizerAppId, refreshToken, componentAccessToken);
                if (StringUtils.isNotBlank(jsonData)) {
                    JSONObject obj = JSONObject.parseObject(jsonData);
                    redisData.put("authorizer_access_token", obj.getString("authorizer_access_token"));
//                    redisData.put("authorizer_refresh_token", obj.get("authorizer_refresh_token"));
                    redisCacheHelper.set(key, redisData);


                    String[] tempArr = key.split(":");
                    // 刷新 jsapi_ticket
                    String ticketJson = JsAPIHelper.getJsAPITicket(obj.getString("authorizer_access_token"));
                    if (StringUtils.isNotBlank(ticketJson)) {
                        String tempKey = "jsapi_ticket:" + tempArr[1];
                        redisCacheHelper.set(tempKey, JSONObject.parseObject(ticketJson));
                    }
                }
            }
        }
    }
}
