package com.cloudboy.util.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Base64 {
	private static final Base64Encoder encoder = new Base64Encoder();

	public static String encode(byte[] data) {
		int len = (data.length + 2) / 3 * 4;
		ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);

		try {
			encoder.encode(data, 0, data.length, bOut);
		} catch (IOException e) {
			throw new RuntimeException("exception encoding base64 string: " + e);
		}

		return new String(bOut.toByteArray());
	}

	public static byte[] decode(String data) {
		int len = data.length() / 4 * 3;
		ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);
		try {
			encoder.decode(data, bOut);
		} catch (IOException e) {
			throw new RuntimeException("exception decoding base64 string: " + e);
		}
		return bOut.toByteArray();
	}
	
	public static void main(String[] args) {
		String word = "Hello 云娃";
		String encryptedWord = Base64.encode(word.getBytes());
		System.out.println(encryptedWord);
		
		String decryptedWord = new String(Base64.decode(encryptedWord));
		System.out.println(decryptedWord);
	}
}
