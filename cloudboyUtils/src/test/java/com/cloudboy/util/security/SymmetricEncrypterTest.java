package com.cloudboy.util.security;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

import com.cloudboy.util.log.MyLogger;

public class SymmetricEncrypterTest {
	private static MyLogger logger = MyLogger
			.getLogger(SymmetricEncrypterTest.class);

	@Test
	public void test() throws InvalidKeyException,
			UnsupportedEncodingException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		String data = "1234567890123456";
		String key = "1001908234";
		String encyrptedData = SymmetricEncrypter.encrypt(data, key);
		logger.info("encyrptedData:", encyrptedData);
		String decryptedData = SymmetricEncrypter.decrypt(encyrptedData, key);
		logger.info("decryptedData:", decryptedData);
		assertTrue(data.equals(decryptedData));
	}
}
