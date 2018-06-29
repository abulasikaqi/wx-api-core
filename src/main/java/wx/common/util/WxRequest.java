package wx.common.util;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wx.third.constants.ThirdConstants;

import javax.net.ssl.*;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by LL on 2017/9/28.
 */
public class WxRequest {
    private static Logger log = LoggerFactory.getLogger(WxRequest.class);
    private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(8000)
            .setConnectTimeout(8000)
            .build();

    public String jsRequest(String urlSuffix, String requestMethod) {
        return jsRequest(urlSuffix, requestMethod, null);
    }

    public String jsRequest(String urlSuffix, String requestMethod, String data) {
        return httpsRequest(urlSuffix, requestMethod, data);
    }

    /**
     * 发送https请求
     *
     * @param urlSuffix   请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr   提交的数据
     * @return 返回微信服务器响应的信息
     */
    public static String httpsRequest(String urlSuffix, String requestMethod, String outputStr) {
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = {new MyTrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(urlSuffix);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes(StandardCharsets.UTF_8));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();
            return buffer.toString();
        } catch (ConnectException ce) {
            log.error("连接异常：{}", ce);
            throw new RuntimeException("连接异常" + ce);
        } catch (Exception e) {
            log.error("https请求异常：{}", e);
            throw new RuntimeException("https请求异常" + e);
        }
    }

    static class MyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
//        return new X509Certificate[0];
            return null;
        }
    }


    //***************************************************************************************************


    public String thirdRequest(String urlSuffix, String data) {
        return request(ThirdConstants.THIRD_DOMAIN_API + urlSuffix, data);
    }

    private String request(String url, String data) {

        try {
            HttpClient httpClient = ClientBuilder.createSimpleClient();

            HttpPost httpPost = new HttpPost(url);

            StringEntity postEntity = new StringEntity(data, "UTF-8");
            httpPost.setEntity(postEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    return EntityUtils.toString(entity, "UTF-8");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static class ClientBuilder {

        /**
         * default
         *
         * @return CloseableHttpClient
         */
        private static CloseableHttpClient createSimpleClient() {
            return HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build();
        }

        /**
         * 自定义Client
         * 1 请求重试机制
         * 2 连接池
         * 3 连接存活策略
         *
         * @return CloseableHttpClient
         */
        @SuppressWarnings("unused")
        private static CloseableHttpClient createCustomClient() {
            // 请求重试HANDLER，自定义异常处理机制。
            HttpRequestRetryHandler myHandler = myRetryHandler();

            // 连接池管理器
            PoolingHttpClientConnectionManager cm = connectionManager();

            // keepAliveStrategy
            ConnectionKeepAliveStrategy keepAlive = keepAliveStrategy();

            return HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)// set config
                    .setRetryHandler(myHandler)
                    .setConnectionManager(cm)
                    .setKeepAliveStrategy(keepAlive)
                    .build();
        }

        /**
         * 请求重试HANDLER，自定义异常处理机制。
         */
        private static HttpRequestRetryHandler myRetryHandler() {
            return (exception, executionCount, context) -> {
                if (executionCount >= 5) {
                    // 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    // 连接被拒绝
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    // 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    // 目标服务器不可达
                    return false;
                }
                if (exception instanceof SSLException) {
                    // ssl握手异常
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    return true;
                }
                return false;
            };
        }

        /**
         * 连接池管理器
         */
        private static PoolingHttpClientConnectionManager connectionManager() {

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            // 将最大连接数增加到200
            cm.setMaxTotal(200);
            // 将每个路由基础的连接增加到20
            cm.setDefaultMaxPerRoute(20);

            return cm;
        }

        /**
         * 连接存活策略
         */
        private static ConnectionKeepAliveStrategy keepAliveStrategy() {
            return (response, context) -> {
                HeaderElementIterator iterator = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (iterator.hasNext()) {
                    HeaderElement he = iterator.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (null != value && "timeout".equalsIgnoreCase(param)) {
                        return Long.parseLong(value) * 1000;
                    }
                }

                HttpHost target = (HttpHost) context.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
                if ("www.naughty-server.com".equalsIgnoreCase(target.getHostName())) {
                    // Keep alive for 5 seconds only
                    return 5 * 1000;
                } else {
                    // otherwise keep alive for 30 seconds
                    return 30 * 1000;
                }
            };
        }
    }
}
