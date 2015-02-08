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

import org.apache.log4j.Logger;
import org.junit.Test;

public class SecurityUtilTest {
	
	private static Logger logger = Logger.getLogger(SecurityUtilTest.class);
	
	/**
	 * 验证公私钥加解密
	 * @throws Exception
	 */
	@Test
	public void encrypt() throws Exception {
		InputStream privateKeyFile = SecurityUtil.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = KeyUtil.getPrivateKeyFromPemFormatStream(privateKeyFile, password);
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
		InputStream keyStoreFile = KeyUtilTest.class.getResourceAsStream("/test.pfx");
		KeyStore keyStore = KeyUtil.getKeyStore(keyStoreFile, "PKCS12", "111111");
		PrivateKey privateKey = (PrivateKey)keyStore.getKey("mbp", "111111".toCharArray());
		logger.info("key:" + privateKey.getClass());
		Certificate certificate = keyStore.getCertificate("mbp");
		PublicKey publicKey = certificate.getPublicKey();
		
		String word = "我要被签名啦123190820384-1230841-23481-203841775453452";
		String sign = SecurityUtil.sign(privateKey, word, null);
		logger.info("sign:" + sign);
		boolean verify = SecurityUtil.verifySign(publicKey, sign, word, null);
		assertTrue(verify);
		
		PublicKey publicKey2 = KeyUtil.generatePublicKey(privateKey, "111111");
		boolean verify2 = SecurityUtil.verifySign(publicKey2, sign, word, null);
		assertTrue(verify2);
	}
	
	@Test
	public void encryptAndDecrypt() throws Exception {
		String data = "我要加密";
		for(int i=0; i<50; i++) {
			data = data + "1234567890";
		}
		logger.info(data);
		String transformation = "RSA/ECB/PKCS1Padding";
		KeyPair keyPair = KeyUtil.generateRSAKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		
		String encryptedData = SecurityUtil.encrypt(data, publicKey, transformation);
		String decryptedData = SecurityUtil.decrypt(encryptedData, privateKey, transformation);
		logger.info(decryptedData);
		assertTrue(data.equals(decryptedData));
	}
}
