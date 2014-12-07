package com.cloudboy.util.httpClient;

import java.io.FileInputStream;
import java.net.URI;
import java.nio.charset.CodingErrorAction;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.cloudboy.base.AppRTException;
import com.cloudboy.util.lang.NumberUtils;
import com.cloudboy.util.lang.StringUtils;

public class HttpClientServiceImpl implements HttpClientService {
	private static Logger logger = Logger.getLogger(HttpClientServiceImpl.class);
    private static PoolingHttpClientConnectionManager connManager = null;
    private static RequestConfig defaultRequestConfig = null;
    private static int DEFAULT_CONNECTION_TIMEOUT = 10 * 1000;
    private static int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;
    
    static {
    	System.setProperty("jsse.enableSNIExtension", "false");
    }
	
    @PostConstruct
	public void init() {
		try {
			KeyStore trustStore = null;
			KeyStore configuredKeyStore = getConfiguredKeyStore();
			X509HostnameVerifier x509HostnameVerifier = null;
			if (configuredKeyStore == null) {
				trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				x509HostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			} else {
				trustStore = configuredKeyStore;
				x509HostnameVerifier = SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
			}

			SSLContextBuilder sslContextBuilder = SSLContexts.custom();
			if (configuredKeyStore == null) {
				sslContextBuilder = sslContextBuilder.loadTrustMaterial(
						trustStore, new TrustAllStrategy());
			} else {
				sslContextBuilder = sslContextBuilder
						.loadTrustMaterial(trustStore);
			}
			SSLContext sslcontext = sslContextBuilder.build();
			// Allow TLSv1 protocol only

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext, new String[] { "TLSv1" }, null,
					x509HostnameVerifier);
			// Create a registry of custom connection socket factories for
			// supported
			// protocol schemes.
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", sslsf).build();

			// Create message constraints
			MessageConstraints messageConstraints = MessageConstraints.custom()
					.setMaxHeaderCount(200).setMaxLineLength(2000).build();
			ConnectionConfig connectionConfig = ConnectionConfig.custom()
					.setMalformedInputAction(CodingErrorAction.IGNORE)
					.setUnmappableInputAction(CodingErrorAction.IGNORE)
					.setCharset(Consts.UTF_8)
					.setMessageConstraints(messageConstraints).build();

			connManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
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
					.setProxyPreferredAuthSchemes(
							Arrays.asList(AuthSchemes.BASIC))
					.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT).setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
					.build();
		} catch (Exception e) {
			throw new AppRTException(e);
		}
    }
    
    private KeyStore getConfiguredKeyStore() {
    	return null;
    }
    
    /**
	 * 当我们有crt证书文件时，采用这个方法初始化KeyStore
	 * @return
	 * @throws Exception
	 */
	protected KeyStore getKeyStoreByCrtFile() throws Exception {
		String keyStoreType = KeyStore.getDefaultType();
		logger.info("keyStoreType:" + keyStoreType);
		KeyStore ks = KeyStore.getInstance(keyStoreType);
		ks.load(null, null);
		
		// 读取crt证书文件(这里使用ca.crt或者server.crt都行)
		String certificationPath = "D:\\doc\\key\\server.crt";
//		String certificationPath = "D:\\doc\\key\\ca.crt";
		FileInputStream fis = new FileInputStream(certificationPath);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate cert = cf.generateCertificate(fis);
		fis.close();
		logger.info("public key:" + cert.getPublicKey());
		ks.setCertificateEntry("myServer", cert);
		return ks;
	}
	
	/**
	 * 当我们已经把证书导入java的密钥库文件时（使用keytool命令）， 使用这个方法初始化KeyStore
	 * @return
	 * @throws Exception
	 */
	protected KeyStore getKeyStoreByJKSFile() throws Exception {
		String keyStorePath = "D:\\doc\\key\\server.jks";
		String password = "0okm,lp-";
		FileInputStream is = new FileInputStream(keyStorePath);
		String keyStoreType = KeyStore.getDefaultType();
		logger.info("keyStoreType:" + keyStoreType);
		KeyStore ks = KeyStore.getInstance(keyStoreType);
		ks.load(is, password.toCharArray());
		is.close();
		return ks;
	}
	
	private CloseableHttpClient getHttpClient() {
        try {
        	CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connManager)
                    .setDefaultRequestConfig(defaultRequestConfig).build();
        	return httpClient;
        } catch (Exception e) {
        	logger.error(e);
            throw new RuntimeException(e);
        }
    }
    
	@Override
	public String postXML(String url, String xml, String reqEncoding, Integer connectionTimeout, Integer readTimeout) {
		if(StringUtils.isEmpty(url)) {
			throw new IllegalArgumentException("The url can not be blank.");
		}
		
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			URI uri = uriBuilder.build();
			HttpPost httpRequest = new HttpPost(uri);
			
			StringEntity reqEntity = new StringEntity(xml, reqEncoding);  // Consts.UTF_8
			reqEntity.setContentType("text/xml;charset=UTF-8");
			reqEntity.setChunked(true);
			httpRequest.setEntity(reqEntity);
			if(connectionTimeout != null || readTimeout != null) {
				RequestConfig requestConfig = RequestConfig.custom()
				        .setSocketTimeout(NumberUtils.defaultValue(connectionTimeout, DEFAULT_CONNECTION_TIMEOUT))
				        .setConnectTimeout(NumberUtils.defaultValue(connectionTimeout, DEFAULT_SOCKET_TIMEOUT))
				        .build();
				httpRequest.setConfig(requestConfig);
			}
			
			CloseableHttpClient httpClient = getHttpClient();
			CloseableHttpResponse response = httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			logger.info(entity.getContentType());
			String responseXML = EntityUtils.toString(entity);
			response.close();
			return responseXML;
		} catch (Exception e) {
			logger.error(e);
			throw new AppRTException(e);
		}
	}

}
