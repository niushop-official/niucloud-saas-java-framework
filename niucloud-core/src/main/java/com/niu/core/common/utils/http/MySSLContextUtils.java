package com.niu.core.common.utils.http;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public final class MySSLContextUtils {

    /**
     * @return
     */
    public static SSLContext createDefaultSSLContext() {
        if (true) {
            // 单向认证，随意的证书与服务端可信交互，连接建立后，协商使用对称加密算法通信
            return createGenerateSSLContext();
        } else if (false) {
            // 单向认证，随意的证书与服务端可信交互，连接建立后，协商使用对称加密算法通信
            return createKeystoreSSLContext();
        } else {
            // 双向认证，服务端验证客户端证书（从证书可中获取传递），客户端验证服务端证书（代码中验证）
            return createKeystoreAliasSSLContext();
        }
    }

    /**
     * @return
     */
    private static SSLContext createGenerateSSLContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    /**
     * @return
     */
    public static SSLContext createKeystoreSSLContext() {
        File keystoreFile = new File("D:\\client.jks");
        String keystorePassword = "123456";
        return createSSLContext(keystoreFile, keystorePassword);
    }

    /**
     * keytool -genkey -dname "CN=XFDClient,OU=XFDUnit,O=XFD,L=TaiYuan, ST=ShanXi, C=CN" -keysize 2048 -alias client -keyalg RSA -keystore d:/client.jks -keypass 123456 -storepass 123456 -validity 36500
     *
     * @param keystoreFile
     * @param keystorePassword
     * @return
     */
    public static SSLContext createSSLContext(File keystoreFile, String keystorePassword) {
        SSLContext sslcontext = null;
        try {
            // 设置truststore
            KeyStore trustStore = KeyStore.getInstance("JKS");
            InputStream keystoreInput = new FileInputStream(keystoreFile);
            try {
                trustStore.load(keystoreInput, keystorePassword.toCharArray());
            } finally {
                try {
                    keystoreInput.close();
                } catch (Exception ignore) {
                }
            }
            sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
            // 创建TrustManager（空的）
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authenType) throws CertificateException {
                    // 检查客户端证书
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authenType) throws CertificateException {
                    // 检查服务器端证书
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    // 返回受信任的X509证书数组
                    return new X509Certificate[0];
                }
            };
            sslcontext.init(null, new TrustManager[]{trustManager}, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslcontext;
    }

    /**
     * @return
     */
    public static SSLContext createKeystoreAliasSSLContext() {
        File keystoreFile = new File("D:\\client.jks");
        String keystorePassword = "123456";
        String alias = "client";
        return createSSLContext(keystoreFile, keystorePassword, alias);
    }

    /**
     * keytool -genkey -dname "CN=XFDClient,OU=XFDUnit,O=XFD,L=TaiYuan, ST=ShanXi, C=CN" -keysize 2048 -alias client -keyalg RSA -keystore d:/client.jks -keypass 123456 -storepass 123456 -validity 36500
     *
     * @param keystoreFile
     * @param keystorePassword
     * @return
     */
    public static SSLContext createSSLContext(File keystoreFile, String keystorePassword, String alias) {
        SSLContext sslcontext = null;
        try {
            // 设置truststore
            KeyStore trustStore = KeyStore.getInstance("JKS");
            InputStream keystoreInput = new FileInputStream(keystoreFile);
            try {
                trustStore.load(keystoreInput, keystorePassword.toCharArray());
            } finally {
                try {
                    keystoreInput.close();
                } catch (Exception ignore) {
                }
            }
            sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
            // 从证书库中根据alias获取证书链
            Certificate[] certificates = trustStore.getCertificateChain(alias);
            // 转换成X509Certificate
            X509Certificate[] x509Certificates = new X509Certificate[certificates.length];
            for (int i = 0; i < certificates.length; i++) {
                x509Certificates[i] = (X509Certificate) certificates[i];
            }
            // 创建TrustManager（空的）
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authenType) throws CertificateException {
                    // 检查客户端证书
                    System.out.println("客户端证书 = " + authenType);
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authenType) throws CertificateException {
                    // 检查服务器端证书
                    System.out.println("服务器端证书 = " + authenType);
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    // 返回受信任的X509证书数组
                    return x509Certificates;
                }
            };
            sslcontext.init(null, new TrustManager[]{trustManager}, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslcontext;
    }

}
