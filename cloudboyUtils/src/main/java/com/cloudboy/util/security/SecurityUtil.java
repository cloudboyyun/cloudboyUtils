package com.cloudboy.util.security;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
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
		
		// 获得加密块大小，如:加密前数据为128个byte，而key_size=1024
		// 加密块大小为127byte,加密后为128个byte;
		// 因此共有2个加密块，第一个127 byte第二个为1个byte
		int blockSize = cipher.getBlockSize();
		int outputSize = cipher.getOutputSize(data.length); // 获得加密块加密后块大小
		int leavedSize = data.length % blockSize;
		int blocksSize = (leavedSize != 0 ? data.length / blockSize + 1
				: data.length / blockSize);
		byte[] raw = new byte[outputSize * blocksSize];
		int i = 0;
		while (data.length - i * blockSize > 0) {
			// 这里面doUpdate方法不可用，查看源代码后发现每次doUpdate后并没有什么实际动作除了把byte[]放到ByteArrayOutputStream中
			// 而最后doFinal的时候才将所有的byte[]进行加密，可是到了此时加密块大小很可能已经超出了OutputSize所以只好用dofinal方法。
			if (data.length - i * blockSize > blockSize) {
				cipher.doFinal(data, i * blockSize, blockSize, raw, i
						* outputSize);

			} else {
				cipher.doFinal(data, i * blockSize, data.length - i
						* blockSize, raw, i * outputSize);
			}

			i++;
		}
		return raw;
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
		
		int blockSize = cipher.getBlockSize();
		ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
		int j = 0;
		while (data.length - j * blockSize > 0) {
			bout.write(cipher.doFinal(data, j * blockSize, blockSize));
			j++;
		}
		return bout.toByteArray();
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
}
