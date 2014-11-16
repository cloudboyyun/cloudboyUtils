package com.cloudboy.util.security;

import static org.junit.Assert.*;
import org.junit.Test;

public class PBECoderTest {
	@Test
	public void test() throws Exception {
		String inputStr = "我要加密";
		System.out.println("原文: " + inputStr);
		byte[] input = inputStr.getBytes();
		String pwd = "efg";
		System.out.println("密码: " + pwd);
		byte[] salt = PBECoder.initSalt();
		byte[] data = PBECoder.encrypt(input, pwd, salt);
		System.out.println("加密后: " + PBECoder.encryptBASE64(data));
		byte[] output = PBECoder.decrypt(data, pwd, salt);
		String outputStr = new String(output);
		System.out.println("解密后: " + outputStr);
		assertEquals(inputStr, outputStr);
	}
}