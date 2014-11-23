package com.cloudboy.util.security;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.Test;

public class SecurityUtilTest {

	
	/**
	 * privateKey.pem和publicKey.pem是一对密钥。
	 * @throws IOException
	 */
	@Test
	public void getKeyTest() throws IOException {
		InputStream privateKeyFile = SecurityUtil.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = SecurityUtil.getPrivateKeyFromPemFormatFile(privateKeyFile, password);
		PublicKey publicKey1 = keyPair.getPublic();
		System.out.println(publicKey1);
		
		InputStream publicKeyFile = SecurityUtil.class.getResourceAsStream("/publicKey.pem");
		String password2 = null;
		PublicKey publicKey2 = SecurityUtil.getPublicKeyFromPemFormatFile(publicKeyFile, password2);
		System.out.println(publicKey2);
		
		// 从私钥得出的公钥和公钥文件读取的公钥，应该相同
		assertTrue(publicKey1.equals(publicKey2));
	}
	
	@Test
	public void savePrivateKey2PemFormatWithoutPassword() throws IOException {
		// 输出
		InputStream privateKeyFile = SecurityUtil.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = SecurityUtil.getPrivateKeyFromPemFormatFile(privateKeyFile, password);
		PrivateKey privateKey = keyPair.getPrivate();
		String pemFileCopy = "d:\\temp\\privateKey-copy.pem";
		SecurityUtil.savePEM(privateKey, password, pemFileCopy);
		
		// 重新读入
		FileInputStream inCopy = new FileInputStream(pemFileCopy);
		KeyPair keyPairCopy = SecurityUtil.getPrivateKeyFromPemFormatFile(inCopy, password);
		PrivateKey privateKeyCopy = keyPairCopy.getPrivate();
		
		// 和源private key应该相同
		assertTrue(privateKey.equals(privateKeyCopy));
	}
	
	@Test
	public void savePrivateKey2PemFormatWithPassword() throws IOException {
		// 输出
		InputStream privateKeyFile = SecurityUtil.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = SecurityUtil.getPrivateKeyFromPemFormatFile(privateKeyFile, password);
		PrivateKey privateKey = keyPair.getPrivate();
		String pemFileCopy = "d:\\temp\\privateKey-copy2.pem";
		String newPassword = "0okm,lp-";
		SecurityUtil.savePEM(privateKey, newPassword, pemFileCopy);
		
		// 重新读入
		FileInputStream inCopy = new FileInputStream(pemFileCopy);
		KeyPair keyPairCopy = SecurityUtil.getPrivateKeyFromPemFormatFile(inCopy, newPassword);
		PrivateKey privateKeyCopy = keyPairCopy.getPrivate();
		
		// 和源private key应该相同
		assertTrue(privateKey.equals(privateKeyCopy));
	}
	
	@Test
	public void savePublicKey2PemFormat() throws IOException {
		// 输出
		InputStream publicKeyFile = SecurityUtil.class.getResourceAsStream("/publicKey.pem");
		String password = null;
		PublicKey publicKey = SecurityUtil.getPublicKeyFromPemFormatFile(publicKeyFile, password);

		String pemFileCopy = "d:\\temp\\publicKey-copy.pem";
		SecurityUtil.savePEM(publicKey, pemFileCopy);
		
		// 重新读入
		FileInputStream inCopy = new FileInputStream(pemFileCopy);
		PublicKey publicKeyCopy = SecurityUtil.getPublicKeyFromPemFormatFile(inCopy, password);
		
		// 和源public key应该相同
		assertTrue(publicKey.equals(publicKeyCopy));
	}
}