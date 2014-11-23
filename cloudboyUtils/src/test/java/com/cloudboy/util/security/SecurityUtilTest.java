package com.cloudboy.util.security;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.PublicKey;

import org.junit.Test;

public class SecurityUtilTest {

	
	/**
	 * privateKey.pem和publicKey.pem是一对密钥。
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {
		InputStream privateKeyFile = SecurityUtil.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = SecurityUtil.getPrivateKey(privateKeyFile, password);
		PublicKey publicKey1 = keyPair.getPublic();
		System.out.println(publicKey1);
		
		InputStream publicKeyFile = SecurityUtil.class.getResourceAsStream("/publicKey.pem");
		String password2 = null;
		PublicKey publicKey2 = SecurityUtil.getPublicKey(publicKeyFile, password2);
		System.out.println(publicKey2);
		
		// 从私钥得出的公钥和公钥文件读取的公钥，应该相同
		assertTrue(publicKey1.equals(publicKey2));
	}

}
