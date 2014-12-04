package com.cloudboy.util.security;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import org.junit.Test;

import com.cloudboy.util.log.MyLogger;

public class SecurityUtilTest {
	
	private static MyLogger logger = MyLogger.getLogger(SecurityUtilTest.class);
	
	/**
	 * 验证公私钥加解密
	 * @throws Exception
	 */
	@Test
	public void encrypt() throws Exception {
		InputStream privateKeyFile = SecurityUtil.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = KeyFileUtil.getPrivateKeyFromPemFormatFile(privateKeyFile, password);
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		
		String data = "夏云1234567890123456";
		System.out.println("待加密文字：" + data);
		String transformation = "RSA/ECB/PKCS1Padding";
		// 私钥加密，公钥解密
		String s1encryptData1 = SecurityUtil.encrypt(data, privateKey, transformation);
		System.out.println("私钥加密后文字：" + s1encryptData1);
		String result1 = SecurityUtil.decrypt(s1encryptData1, publicKey, transformation);
		System.out.println("公钥解密后文字:" + result1);
		assertTrue(data.equals(result1));
		
		// 公钥加密，私钥解密
		String encryptData2 = SecurityUtil.encrypt(data, publicKey, transformation);
		System.out.println("公钥加密后文字：" + encryptData2);
		String result2 = SecurityUtil.decrypt(encryptData2, privateKey, transformation);
		System.out.println("私钥解密后文字:" + result2);
		assertTrue(data.equals(result2));
	}
	
	/**
	 * 签名和签名验证
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException 
	 */
	@Test
	public void signAndVerifySign() throws Exception {
		InputStream keyStoreFile = KeyFileUtilTest.class.getResourceAsStream("/test.pfx");
		KeyStore keyStore = KeyFileUtil.getKeyStore(keyStoreFile, "PKCS12", "111111");
		PrivateKey privateKey = (PrivateKey)keyStore.getKey("mbp", "111111".toCharArray());
		logger.info("key:", privateKey.getClass());
		Certificate certificate = keyStore.getCertificate("mbp");
		PublicKey publicKey = certificate.getPublicKey();
		
		String word = "我要被签名啦123190820384-1230841-23481-203841775453452";
		String sign = SecurityUtil.sign(privateKey, word, null);
		logger.info("sign:", sign);
		boolean verify = SecurityUtil.verifySign(publicKey, sign, word, null);
		assertTrue(verify);
		
		PublicKey publicKey2 = SecurityUtil.generatePublicKey(privateKey, "111111");
		boolean verify2 = SecurityUtil.verifySign(publicKey2, sign, word, null);
		assertTrue(verify2);
	}
	
	@Test
	public void generateRSAKeyPair() throws Exception {
		KeyPair keyPair = SecurityUtil.generateRSAKeyPair();
		logger.info(keyPair.getPrivate());
		logger.info(keyPair.getPublic());
	}
	
	@Test
	public void generatePublicKey() throws Exception {
		InputStream keyStoreFile = KeyFileUtilTest.class.getResourceAsStream("/test.pfx");
		KeyStore keyStore = KeyFileUtil.getKeyStore(keyStoreFile, "PKCS12", "111111");
		PrivateKey privateKey = (PrivateKey)keyStore.getKey("mbp", "111111".toCharArray());
		logger.info("key:", privateKey.getClass());
		Certificate certificate = keyStore.getCertificate("mbp");
		PublicKey publicKey1 = certificate.getPublicKey();
		logger.info(Base64.encode(publicKey1.getEncoded()));
		
		PublicKey publicKey2 = SecurityUtil.generatePublicKey(privateKey, "111111");
		logger.info(Base64.encode(publicKey2.getEncoded()));
		assertTrue(publicKey1.equals(publicKey2));
	}
}
