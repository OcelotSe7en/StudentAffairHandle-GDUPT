package com.ocelot.util;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class HttpPoolUtil {

    public static final String UTF8 = "UTF-8";
    public static final int maxTotalPool = 150;
    public static final int MAX_TIMEOUT = 8000;
    public static final int RequestTimeout = 5000;
    private static final RequestConfig requestConfig;
    private static final HttpClientBuilder httpClientBuilder;
    private static final PoolingHttpClientConnectionManager poolConnManager;
    private static final IdleConnectionMonitorThread idleConnectionMonitorThread;
    private static final Logger logger = LoggerFactory.getLogger(HttpPoolUtil.class);
    //设置CookieStore
    private static final CookieStore cookieStore = new BasicCookieStore();
    public static volatile boolean isClosed = false;

    static {
        // 设置连接池
        poolConnManager = new PoolingHttpClientConnectionManager();
        poolConnManager.setMaxTotal(maxTotalPool);
        poolConnManager.setDefaultMaxPerRoute(maxTotalPool);

        //设置代理,方便Fiddler抓包
        HttpHost proxy = new HttpHost("127.0.0.1", 10809, "http");

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(RequestTimeout);
        // 在提交请求之前 测试连接是否可用（有性能问题）
        //configBuilder.setStaleConnectionCheckEnabled(true);
        idleConnectionMonitorThread = new IdleConnectionMonitorThread(poolConnManager);
        idleConnectionMonitorThread.start();
        requestConfig = configBuilder
                .build();
        httpClientBuilder = HttpClients.custom()
                // TODO 设置后就可以关闭 httpClient 对象
                .setConnectionManagerShared(true)
                .disableRedirectHandling()
                .setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(requestConfig)
                .setProxy(proxy)
                .setDefaultCookieStore(cookieStore);
        logger.info(">>>>>>>>>>> PoolingHttpClientConnectionManager初始化成功 >>>>>>>>>>>");
    }

    /**
     * 从http连接池里获取客户端实例
     *
     * @return httpClient
     */
    public static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = httpClientBuilder
                .disableRedirectHandling()
                .build();
        return httpClient;
    }

    /**
     * 获得状态
     */
    public static String getTotalStats() {
        return poolConnManager.getTotalStats().toString();
    }

    /*
    获得Cookie
    */
    public static CookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * 关闭连接池资源
     */
    public static void closePool() {
        if (!isClosed) {
            isClosed = true;
            poolConnManager.close();
        }
    }

    public static class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(30 * 1000);
                        // Close expired connections
                        connMgr.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 30 sec
                        connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }
}