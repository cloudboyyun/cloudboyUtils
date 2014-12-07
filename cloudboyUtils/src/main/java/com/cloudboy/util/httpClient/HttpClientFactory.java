package com.cloudboy.util.httpClient;

import java.nio.charset.CodingErrorAction;
import java.security.KeyStore;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;


/**
 * 
 * @author cloudboy(yun.xia)
 */
public class HttpClientFactory {
    private static Logger logger = Logger.getLogger(HttpClientFactory.class);
    private static PoolingHttpClientConnectionManager connManager = null;
    private static RequestConfig defaultRequestConfig = null;
    
    static {
    	System.setProperty("jsse.enableSNIExtension", "false");
    }
    
    /**
     * 
     * @return
     */
    public synchronized static CloseableHttpClient getHttpClient() {
        try {
        	if(connManager == null) {
        		initClient(null);
        	}
        	CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connManager)
                    .setDefaultRequestConfig(defaultRequestConfig).build();
        	return httpClient;
        } catch (Exception e) {
        	logger.error(e);
            throw new RuntimeException(e);
        }
        
    }
    
    /**
     * 初始化client
     * @param trustKeyStore
     * @throws Exception
     */
    public synchronized static void initClient(KeyStore trustKeyStore) throws Exception {
    	KeyStore trustStore = null;
    	X509HostnameVerifier x509HostnameVerifier = null;
    	if(trustKeyStore == null) {
    		trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    		x509HostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
    	} else {
    		trustStore = trustKeyStore;
    		x509HostnameVerifier = SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
    	}
    	
    	SSLContextBuilder sslContextBuilder = SSLContexts.custom();
    	if(trustKeyStore == null) {
    		sslContextBuilder = sslContextBuilder.loadTrustMaterial(trustStore, new TrustAllStrategy());
    	} else {
    		sslContextBuilder = sslContextBuilder.loadTrustMaterial(trustStore);
    	}
        SSLContext sslcontext = sslContextBuilder.build();
        // Allow TLSv1 protocol only
        
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[] { "TLSv1" }, null,
                x509HostnameVerifier);
        // Create a registry of custom connection socket factories for supported
        // protocol schemes.
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https",sslsf)
                .build();
        
        // Create message constraints
        MessageConstraints messageConstraints = MessageConstraints.custom()
                .setMaxHeaderCount(200).setMaxLineLength(2000).build();
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .setMessageConstraints(messageConstraints).build();
        
        connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connManager.setDefaultMaxPerRoute(10);
        connManager.setMaxTotal(50);
        connManager.setDefaultConnectionConfig(connectionConfig);
        defaultRequestConfig = RequestConfig
                .custom()
                .setCookieSpec(CookieSpecs.BEST_MATCH)
                .setExpectContinueEnabled(true)
                .setStaleConnectionCheckEnabled(true)
                .setTargetPreferredAuthSchemes(
                        Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .setConnectTimeout(1000 * 30).setSocketTimeout(1000 * 60)
                .build();
    }
}
