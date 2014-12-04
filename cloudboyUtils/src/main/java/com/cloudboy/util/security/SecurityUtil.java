package com.cloudboy.util.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SecurityUtil {
	
	final public static String DEFAULT_SIGN_ALGORITHM = "SHA1withRSA";
	public static String DEFAULT_CHARSET_NAME = "utf-8";
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	/**
	 * 公钥或私钥加密 
	 * @param data
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, Key key, String transformation) throws Exception {
		Cipher cipher;
		if(transformation == null) {
			cipher = Cipher.getInstance(key.getAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
		} else {
			cipher = Cipher.getInstance(transformation, BouncyCastleProvider.PROVIDER_NAME);
		}
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}
	
	/**
	 * 公钥或私钥加密 
	 * @param data
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return 使用Base64格式的加密后文字
	 * @throws Exception
	 */
	public static String encrypt(String data, Key key, String transformation) throws Exception {
		if(data == null) {
			return null;
		}
		byte[] bytes = encrypt(data.getBytes(DEFAULT_CHARSET_NAME), key, transformation);
		String result = Base64.encode(bytes);
		return result;
	}

	/**
	 * 公钥或私钥解密 
	 * @param data
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, Key key, String transformation) throws Exception {
		Cipher cipher;
		if(transformation == null) {
			cipher = Cipher.getInstance(key.getAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
		} else {
			cipher = Cipher.getInstance(transformation, BouncyCastleProvider.PROVIDER_NAME);
		}
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(data);
	}
	
	/**
	 * 公钥或私钥解密 
	 * @param data 使用Base64格式的加密后文字
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * @return 解密后文字
	 * @throws Exception
	 */
	public static String decrypt(String data, Key key, String transformation) throws Exception {
		if(data == null) {
			return null;
		}
		byte[] src = Base64.decode(data);
		byte[] dst = decrypt(src, key, transformation);
		String result = new String(dst, DEFAULT_CHARSET_NAME);
		return result;
	}
	

	/**
	 * 产生RSA密钥对
	 * @return
	 * @throws Exception
	 */
	public static KeyPair generateRSAKeyPair() throws Exception {
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
		kpGen.initialize(1024, new SecureRandom());
		return kpGen.generateKeyPair();
	}
	
	/**
	 * 签名
	 * @param key 私钥
	 * @param src 原文
	 * @param algorithm 签名算法(常用：md5withrsa, SHA1withRSA)，为空时默认为：SHA1withRSA
	 * @return 签名
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws SignatureException 
	 * @throws Exception
	 */
	public static byte[] sign(final PrivateKey key, byte[] src,
			final String algorithm) throws NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeyException, SignatureException {
		String algorithm1 = algorithm;
		if (algorithm1 == null) {
			algorithm1 = DEFAULT_SIGN_ALGORITHM;
		}
		Signature sign = Signature.getInstance(algorithm1,
				BouncyCastleProvider.PROVIDER_NAME);

		sign.initSign(key);
		sign.update(src);
		return sign.sign();
	}
	
	/**
	 * 签名
	 * @param key 私钥
	 * @param src 原文
	 * @param algorithm 签名算法(常用：md5withrsa, SHA1withRSA)，为空时默认为：SHA1withRSA
	 * @return 签名 使用base64编码
	 * @throws UnsupportedEncodingException 
	 * @throws SignatureException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws Exception
	 */
	public static String sign(final PrivateKey key, String src,
			final String algorithm) throws UnsupportedEncodingException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, SignatureException {
		byte[] srcBytes = src.getBytes(DEFAULT_CHARSET_NAME);
		byte[] resultBytes = sign(key, srcBytes, algorithm);
		String result = Base64.encode(resultBytes);
		return result;
	}

	/**
	 * 校验签名
	 * @param key 公钥
	 * @param crypt 签名
	 * @param src 原文
	 * @param algorithm 签名算法(常用：md5withrsa, SHA1withRSA)，为空时默认为：SHA1withRSA
	 * @return
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws SignatureException 
	 * @throws InvalidKeyException 
	 * @throws Exception
	 */
	public static boolean verifySign(final PublicKey key, byte[] crypt,
			byte[] src, final String algorithm)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			SignatureException, InvalidKeyException {
		String algorithm1 = algorithm;
		if (algorithm1 == null) {
			algorithm1 = DEFAULT_SIGN_ALGORITHM;
		}
		Signature sign = Signature.getInstance(algorithm1,
				BouncyCastleProvider.PROVIDER_NAME);
		sign.initVerify(key);
		sign.update(src);
		return sign.verify(crypt);
	}
	
	/**
	 * 校验签名
	 * @param key 公钥
	 * @param crypt 签名， 以Base64编码
	 * @param src 原文
	 * @param algorithm 签名算法(常用：md5withrsa, SHA1withRSA)，为空时默认为：SHA1withRSA
	 * @return
	 * @throws SignatureException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws UnsupportedEncodingException 
	 * @throws Exception
	 */
	public static boolean verifySign(final PublicKey key, String crypt,
			String src, final String algorithm) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException,
			SignatureException, UnsupportedEncodingException {
		byte[] cryptBytes = Base64.decode(crypt);
		byte[] srcBytes = src.getBytes(DEFAULT_CHARSET_NAME);
		return verifySign(key, cryptBytes, srcBytes, algorithm);
	}
	
	/**
	 * 使用私钥生成公钥<br>
	 * 在JDK库中找不到现成方法，只好用这个土方法。
	 * @param privateKey
	 * @param password
	 * @return
	 * @throws IOException
	 */
	public static PublicKey generatePublicKey(PrivateKey privateKey, String password) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		KeyFileUtil.savePEM(privateKey, password, outputStream);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		KeyPair keyPair = KeyFileUtil.getPrivateKeyFromPemFormatFile(inputStream, password);
		return keyPair.getPublic();
	}
}
