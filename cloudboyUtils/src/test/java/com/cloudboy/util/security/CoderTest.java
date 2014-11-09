package com.cloudboy.util.security;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

public class CoderTest {

	@Test
	public void test() throws Exception {
		String inputStr = "简单加密";
		System.out.println("原文:" + inputStr);
		byte[] inputData = inputStr.getBytes();

		// 1. Base64
		String code = Coder.encryptBASE64(inputData);
		System.out.println("BASE64加密后:" + code);

		byte[] output = Coder.decryptBASE64(code);
		String outputStr = new String(output);
		System.out.println("BASE64解密后:" + outputStr);

		// 验证BASE64加密解密一致性
		assertEquals(inputStr, outputStr);

		// 2. MD5
		// 验证MD5对于同一内容加密是否一致
		assertArrayEquals(Coder.encryptMD5(inputData),
				Coder.encryptMD5(inputData));

		// 3. SHA
		// 验证SHA对于同一内容加密是否一致
		assertArrayEquals(Coder.encryptSHA(inputData),
				Coder.encryptSHA(inputData));

		// 4. MAC
		String key = Coder.initMacKey();
		System.out.println("Mac密钥:" + key);

		// 验证HMAC对于同一内容，同一密钥加密是否一致
		assertArrayEquals(Coder.encryptHMAC(inputData, key),
				Coder.encryptHMAC(inputData, key));

		BigInteger md5 = new BigInteger(Coder.encryptMD5(inputData));
		System.out.println("MD5:" + md5.toString(16));

		BigInteger sha = new BigInteger(Coder.encryptSHA(inputData));
		System.out.println("SHA:" + sha.toString(32));

		BigInteger mac = new BigInteger(Coder.encryptHMAC(inputData, key));
		System.out.println("HMAC:" + mac.toString(16));
	}
}
