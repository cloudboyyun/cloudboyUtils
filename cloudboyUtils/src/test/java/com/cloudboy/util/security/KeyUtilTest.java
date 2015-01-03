package com.cloudboy.util.security;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.junit.Test;

public class KeyUtilTest {

	private static Logger logger = Logger.getLogger(KeyUtilTest.class);
	
	@Test
	public void generateRSAKeyPair() throws Exception {
		KeyPair keyPair = KeyUtil.generateRSAKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		logger.info(publicKey.getClass());
		byte[] bytePublicyKey = publicKey.getEncoded();
		String sPublicyKey = Base64.encode(bytePublicyKey);
		sun.security.rsa.RSAPublicKeyImpl impl = new sun.security.rsa.RSAPublicKeyImpl(Base64.decode(sPublicyKey));
		byte[] bytePublicyKey2 = impl.getEncoded();
		String sPublicyKey2 = Base64.encode(bytePublicyKey2);
		logger.info(keyPair.getPrivate());
		logger.info(sPublicyKey);
		logger.info(sPublicyKey2);
		logger.info(sPublicyKey.equals(sPublicyKey2));
	}
	
	/**
	 * privateKey.pem和publicKey.pem是一对密钥。
	 * @throws IOException
	 */
	@Test
	public void getKeyTest() throws IOException {
		InputStream privateKeyFile = KeyUtilTest.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = KeyUtil.getPrivateKeyFromPemFormatStream(privateKeyFile, password);
		PublicKey publicKey1 = keyPair.getPublic();
		System.out.println(publicKey1);
		
		InputStream publicKeyFile = KeyUtilTest.class.getResourceAsStream("/publicKey.pem");
		String password2 = null;
		PublicKey publicKey2 = KeyUtil.getPublicKeyFromPemFormatStream(publicKeyFile, password2);
		System.out.println(publicKey2);
		
		// 从私钥得出的公钥和公钥文件读取的公钥，应该相同
		assertTrue(publicKey1.equals(publicKey2));
	}
	
	/**
	 * 测试将私钥存入pem文件，导出密码为空
	 * @throws IOException
	 */
	@Test
	public void savePrivateKey2PemFormatWithoutPassword() throws IOException {
		// 输出
		InputStream privateKeyFile = KeyUtilTest.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = KeyUtil.getPrivateKeyFromPemFormatStream(privateKeyFile, password);
		PrivateKey privateKey = keyPair.getPrivate();
		String pemFileCopy = "d:\\temp\\privateKey-copy.pem";
		OutputStream pemFileCopyOutputStream = new FileOutputStream(pemFileCopy);
		KeyUtil.savePEM(privateKey, password, pemFileCopyOutputStream);
		PublicKey publicKey = keyPair.getPublic();
		
		// 重新读入
		FileInputStream inCopy = new FileInputStream(pemFileCopy);
		KeyPair keyPairCopy = KeyUtil.getPrivateKeyFromPemFormatStream(inCopy, password);
		PrivateKey privateKeyCopy = keyPairCopy.getPrivate();
		PublicKey publicKeyCopy = keyPair.getPublic();
		
		// 和源private key应该相同
		assertTrue(privateKey.equals(privateKeyCopy));
		assertTrue(publicKey.equals(publicKeyCopy));
	}
	
	/**
	 * 测试将私钥存入pem文件，设置新密码
	 * @throws IOException
	 */
	@Test
	public void savePrivateKey2PemFormatWithPassword() throws IOException {
		// 输出
		InputStream privateKeyFile = KeyUtilTest.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = KeyUtil.getPrivateKeyFromPemFormatStream(privateKeyFile, password);
		PrivateKey privateKey = keyPair.getPrivate();
		String pemFileCopy = "d:\\temp\\privateKey-copy2.pem";
		String newPassword = "0okm,lp-";
		OutputStream pemFileCopyOutputStream = new FileOutputStream(pemFileCopy);
		KeyUtil.savePEM(privateKey, newPassword, pemFileCopyOutputStream);
		
		// 重新读入
		FileInputStream inCopy = new FileInputStream(pemFileCopy);
		KeyPair keyPairCopy = KeyUtil.getPrivateKeyFromPemFormatStream(inCopy, newPassword);
		PrivateKey privateKeyCopy = keyPairCopy.getPrivate();
		
		// 和源private key应该相同
		assertTrue(privateKey.equals(privateKeyCopy));
	}
	
	/**
	 * 测试将公钥存入pem文件（公钥无法加密码）
	 * @throws IOException
	 */
	@Test
	public void savePublicKey2PemFormat() throws IOException {
		// 输出
		InputStream publicKeyFile = KeyUtilTest.class.getResourceAsStream("/publicKey.pem");
		String password = null;
		PublicKey publicKey = KeyUtil.getPublicKeyFromPemFormatStream(publicKeyFile, password);

		String pemFileCopy = "d:\\temp\\publicKey-copy.pem";
		KeyUtil.savePEM(publicKey, pemFileCopy);
		
		// 重新读入
		FileInputStream inCopy = new FileInputStream(pemFileCopy);
		PublicKey publicKeyCopy = KeyUtil.getPublicKeyFromPemFormatStream(inCopy, password);
		
		// 和源public key应该相同
		assertTrue(publicKey.equals(publicKeyCopy));
	}
	
	/**
	 * 从pfx文件中导入密钥库
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	@Test
	public void getKeyStore() throws Exception {
		InputStream keyStoreFile = KeyUtilTest.class.getResourceAsStream("/test.pfx");
		KeyStore keyStore = KeyUtil.getKeyStore(keyStoreFile, KeyUtil.KEYSTORE_TYPE_PKCS12, "111111");
		Enumeration<String> aliases = keyStore.aliases();
		while(aliases.hasMoreElements()) {
			String aliase = aliases.nextElement();
			logger.info("aliase:" + aliase);
		}		
	}
	
	/**
	 * 读和存证书
	 * @throws Exception
	 */
	@Test
	public void saveLoadX509Certificate() throws Exception {
		InputStream keyStoreFile = KeyUtilTest.class.getResourceAsStream("/test.pfx");
		KeyStore keyStore = KeyUtil.getKeyStore(keyStoreFile, "PKCS12", "111111");
		Certificate certificate = keyStore.getCertificate("mbp");
		String certFile = "d:\\temp\\test.crt";
		KeyUtil.saveCertificate(certificate, certFile);
		
		File file = new File(certFile);
		FileInputStream in = new FileInputStream(file);
		Certificate certificate2 = KeyUtil.loadCertificate(in);
		// 输出后的证书再次读取进来，两个证书应该一样
		assertTrue(certificate.equals(certificate2));
	}
	
	@Test
	public void generatePublicKey() throws Exception {
		InputStream keyStoreFile = KeyUtilTest.class.getResourceAsStream("/test.pfx");
		KeyStore keyStore = KeyUtil.getKeyStore(keyStoreFile, "PKCS12", "111111");
		PrivateKey privateKey = (PrivateKey)keyStore.getKey("mbp", "111111".toCharArray());
		logger.info("key:" + privateKey.getClass());
		Certificate certificate = keyStore.getCertificate("mbp");
		PublicKey publicKey1 = certificate.getPublicKey();
		logger.info(Base64.encode(publicKey1.getEncoded()));
		
		PublicKey publicKey2 = KeyUtil.generatePublicKey(privateKey, "111111");
		logger.info(Base64.encode(publicKey2.getEncoded()));
		assertTrue(publicKey1.equals(publicKey2));
	}
	
	@Test
	public void convertKeyToString() throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
		KeyPair keyPair = KeyUtil.generateRSAKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		String publicKeyStr = KeyUtil.convertKey2String(publicKey);
		logger.info("publicKey:" + publicKeyStr);
		PublicKey publicKey2 = KeyUtil.convert2PublicKey(publicKeyStr, "RSA");
		logger.info(publicKey2.equals(publicKey));
		assertTrue(publicKey2.equals(publicKey));
		
		PrivateKey privateKey = keyPair.getPrivate();
		String privateKeyStr = KeyUtil.convertKey2String(privateKey);
		logger.info("privateKey:" + privateKeyStr);
		PrivateKey privateKey2 = KeyUtil.convert2PrivateKey(privateKeyStr, "RSA");
		logger.info(privateKey2.equals(privateKey));
		assertTrue(privateKey2.equals(privateKey));
	}	
}
