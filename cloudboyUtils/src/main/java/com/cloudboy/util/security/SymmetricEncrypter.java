package com.cloudboy.util.security;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class SymmetricEncrypter {
	
	public static final String ALGORITHM = "PBEWITHMD5andDES";
	public static String DEFAULT_CHARSET_NAME = "utf-8";
	
	public static byte[] encrypt(byte[] data, String password) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray());
			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
			SecretKey key = factory.generateSecret(spec);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			PBEParameterSpec pps = new PBEParameterSpec(new byte[] { 0xF, 0x0,
					0x1, 0x2, 0x3, 0x4, 0x5, 0x6 }, 1);
			cipher.init(Cipher.ENCRYPT_MODE, key, pps);
			byte[] debyte = cipher.doFinal(data);
			return debyte;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String encrypt(String data, String password) {
		byte[] dataBytes;
		try {
			dataBytes = data.getBytes(DEFAULT_CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			dataBytes = data.getBytes();
		}
		byte[] resultBytes = encrypt(dataBytes, password);
		String result = Base64.encode(resultBytes);
		return result;
	}

	public static byte[] decrypt(byte[] data, String password) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray());
			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
			SecretKey key = factory.generateSecret(spec);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			PBEParameterSpec pps = new PBEParameterSpec(new byte[] { 0xF, 0x0,
					0x1, 0x2, 0x3, 0x4, 0x5, 0x6 }, 1);
			cipher.init(Cipher.DECRYPT_MODE, key, pps);
			byte[] debyte = cipher.doFinal(data);
			return debyte;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String decrypt(String data, String password) {
		byte[] dataBytes = Base64.decode(data);
		byte[] resultBytes = decrypt(dataBytes, password);
		String result;
		try {
			result = new String(resultBytes, DEFAULT_CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			result = new String(resultBytes);
		}
		return result;
	}
}
