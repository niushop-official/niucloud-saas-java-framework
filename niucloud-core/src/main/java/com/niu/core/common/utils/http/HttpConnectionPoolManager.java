package com.niu.core.common.utils.http;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public final class HttpConnectionPoolManager {

    private Properties props;
    private String hostname;
    private int port;
    private PoolingHttpClientConnectionManager httpClientConnectionManager;
    private IdleConnectionEvictor idleConnectionEvictor;

    /**
     * @param hostname
     * @param port
     */
    public HttpConnectionPoolManager(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        props = new Properties();
        //==================================================================================================//
        // 最大连接数===>连接池里的最大连接数
        //==================================================================================================//
        props.setProperty("maxTotal", String.valueOf(20));
        //==================================================================================================//
        // 连接超时==>指客户端和服务器建立连接的timeout
        //==================================================================================================//
        props.setProperty("connectTimeout", String.valueOf(60 * 1000));
        //==================================================================================================//
        // 读超时===>客户端和服务器建立连接后,客户端从服务器读取数据的timeout,超出后会抛出SocketTimeOutException
        //==================================================================================================//
        props.setProperty("socketTimeout", String.valueOf(30 * 1000));
        //==================================================================================================//
        // 请求超时==>指从连接池获取连接的timeout
        //==================================================================================================//
        props.setProperty("connectionRequestTimeout", String.valueOf(10 * 1000));
        //==================================================================================================//
        // 每次能并行接收的请求数量===>某一个/每服务每次能并行接收的请求数量，这里route指的是域名
        //==================================================================================================//
        props.setProperty("defaultMaxPerRoute", String.valueOf(40));
        props.setProperty("maxPerRoute", String.valueOf(60));
        //==================================================================================================//
        // 重试次数、请求失败是否重发
        //==================================================================================================//
        props.setProperty("retryCount", String.valueOf(3));
        props.setProperty("requestSentRetryEnabled", String.valueOf(false));
        //==================================================================================================//
        // 移除空闲连接时间
        //==================================================================================================//
        props.setProperty("evictIdleConnections", String.valueOf(30 * 10000));
    }

    /**
     * @param props
     * @param hostname
     * @param port
     */
    public HttpConnectionPoolManager(Properties props, String hostname, int port) {
        this.props = props;
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * @return
     */
    private PoolingHttpClientConnectionManager getConnectionManager() {
        if (httpClientConnectionManager == null) {
            synchronized (this) {
                if (httpClientConnectionManager == null) {
                    httpClientConnectionManager = createConnectionManager();
                }
            }
        }
        return httpClientConnectionManager;
    }

    /**
     * @return
     */
    private Registry<ConnectionSocketFactory> createSocketFactoryRegistry() {
        SSLContext sslContext = MySSLContextUtils.createDefaultSSLContext();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build();
        return socketFactoryRegistry;
    }

    /**
     * @return
     */
    private PoolingHttpClientConnectionManager createConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(createSocketFactoryRegistry());
        connectionManager.setMaxTotal(getIntProperty("maxTotal", 20));
        connectionManager.setDefaultMaxPerRoute(getIntProperty("defaultMaxPerRoute", 40));
        HttpHost httpHost = new HttpHost(hostname, port);
        HttpRoute httpRoute = new HttpRoute(httpHost);
        connectionManager.setMaxPerRoute(httpRoute, getIntProperty("maxPerRoute", 40));
        // 清理无效连接
        idleConnectionEvictor = new IdleConnectionEvictor(connectionManager);
        idleConnectionEvictor.setName("httpclient-connection-" + httpHost.toString() + "-idle-thread");
        idleConnectionEvictor.setDaemon(true);
        idleConnectionEvictor.start();
        return connectionManager;
    }

    /**
     * @return
     */
    public CloseableHttpClient getDefaultCustomClient() {
        int connectTimeoutMillisecond = getIntProperty("connectTimeout", 60 * 1000);
        int socketTimeoutMillisecond = getIntProperty("socketTimeout", 30 * 1000);
        int connectionRequestTimeoutMillisecond = getIntProperty("connectionRequestTimeout", 10 * 1000);
        int retryCount = getIntProperty("retryCount", 3);
        boolean requestSentRetryEnabled = getBoolProperty("requestSentRetryEnabled", false);
        int evictIdleConnectionsMillisecond = getIntProperty("evictIdleConnections", 30 * 10000);
        return getCustomClient(connectTimeoutMillisecond, socketTimeoutMillisecond, connectionRequestTimeoutMillisecond, retryCount, requestSentRetryEnabled, evictIdleConnectionsMillisecond);
    }

    /**
     * @param name
     * @param defaultValue
     * @return
     */
    private int getIntProperty(String name, int defaultValue) {
        String value = props.getProperty(name, String.valueOf(defaultValue));
        if (!StringUtils.isEmpty(value)) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    /**
     * @param name
     * @param defaultValue
     * @return
     */
    private boolean getBoolProperty(String name, boolean defaultValue) {
        String value = props.getProperty(name, String.valueOf(defaultValue));
        if (!StringUtils.isEmpty(value)) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    /**
     * @param connectTimeoutMillisecond
     * @param socketTimeoutMillisecond
     * @param connectionRequestTimeoutMillisecond
     * @param retryCount
     * @param requestSentRetryEnabled
     * @param evictIdleConnectionsMillisecond
     * @return
     */
    public CloseableHttpClient getCustomClient(int connectTimeoutMillisecond, int socketTimeoutMillisecond, int connectionRequestTimeoutMillisecond,
                                               int retryCount, boolean requestSentRetryEnabled, int evictIdleConnectionsMillisecond) {
        //默认连接配置
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeoutMillisecond)
                .setSocketTimeout(socketTimeoutMillisecond)
                .setConnectionRequestTimeout(connectionRequestTimeoutMillisecond)
                .build();
        //  retryCount==>重试次数, requestSentRetryEnabled==>允许请求重发
        HttpRequestRetryHandler httpRetryHandler = new DefaultHttpRequestRetryHandler(retryCount, requestSentRetryEnabled);
        return HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .evictExpiredConnections()
                .setConnectionManagerShared(false)
                .evictIdleConnections(evictIdleConnectionsMillisecond, TimeUnit.MILLISECONDS)
                .setConnectionManager(getConnectionManager())
                .setRetryHandler(httpRetryHandler).build();
    }


    /**
     * 监听连接池中空闲连接，清理无效连接
     */
    class IdleConnectionEvictor extends Thread {

        private final HttpClientConnectionManager connectionManager;

        private volatile boolean shutdown;

        public IdleConnectionEvictor(HttpClientConnectionManager connectionManager) {
            this.connectionManager = connectionManager;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        //3s检查一次
                        this.wait(3000);
                        // 关闭失效的连接
                        connectionManager.closeExpiredConnections();
                    }
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    /**
     * 关闭连接池
     */
    public void shutdownConnectionManager() {
        if (idleConnectionEvictor != null) {
            idleConnectionEvictor.shutdown();
            idleConnectionEvictor = null;
        }
        if (httpClientConnectionManager != null) {
            httpClientConnectionManager.shutdown();
            httpClientConnectionManager = null;
        }
    }

}
