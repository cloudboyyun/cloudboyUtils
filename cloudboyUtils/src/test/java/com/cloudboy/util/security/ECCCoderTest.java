package com.cloudboy.util.security;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class ECCCoderTest {
	@Test
	public void test() throws Exception {
		String inputStr = "我要加密";
		byte[] data = inputStr.getBytes();
		Map<String, Object> keyMap = ECCCoder.initKey();
		String publicKey = ECCCoder.getPublicKey(keyMap);
		String privateKey = ECCCoder.getPrivateKey(keyMap);
		System.out.println("公钥: \n" + publicKey);
		System.out.println("私钥： \n" + privateKey);
		byte[] encodedData = ECCCoder.encrypt(data, publicKey);
		byte[] decodedData = ECCCoder.decrypt(encodedData, privateKey);
		String outputStr = new String(decodedData);
		System.out.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);
		assertEquals(inputStr, outputStr);
	}
}