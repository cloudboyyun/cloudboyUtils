package com.cloudboy.util.security;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import org.junit.Test;

import com.cloudboy.util.log.MyLogger;

public class KeyFileUtilTest {

	private static MyLogger logger = MyLogger.getLogger(KeyFileUtilTest.class);
	
	/**
	 * privateKey.pem和publicKey.pem是一对密钥。
	 * @throws IOException
	 */
	@Test
	public void getKeyTest() throws IOException {
		InputStream privateKeyFile = KeyFileUtilTest.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = KeyFileUtil.getPrivateKeyFromPemFormatFile(privateKeyFile, password);
		PublicKey publicKey1 = keyPair.getPublic();
		System.out.println(publicKey1);
		
		InputStream publicKeyFile = KeyFileUtilTest.class.getResourceAsStream("/publicKey.pem");
		String password2 = null;
		PublicKey publicKey2 = KeyFileUtil.getPublicKeyFromPemFormatFile(publicKeyFile, password2);
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
		InputStream privateKeyFile = KeyFileUtilTest.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = KeyFileUtil.getPrivateKeyFromPemFormatFile(privateKeyFile, password);
		PrivateKey privateKey = keyPair.getPrivate();
		String pemFileCopy = "d:\\temp\\privateKey-copy.pem";
		OutputStream pemFileCopyOutputStream = new FileOutputStream(pemFileCopy);
		KeyFileUtil.savePEM(privateKey, password, pemFileCopyOutputStream);
		PublicKey publicKey = keyPair.getPublic();
		
		// 重新读入
		FileInputStream inCopy = new FileInputStream(pemFileCopy);
		KeyPair keyPairCopy = KeyFileUtil.getPrivateKeyFromPemFormatFile(inCopy, password);
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
		InputStream privateKeyFile = KeyFileUtilTest.class.getResourceAsStream("/privateKey.pem");
		String password = null;
		KeyPair keyPair = KeyFileUtil.getPrivateKeyFromPemFormatFile(privateKeyFile, password);
		PrivateKey privateKey = keyPair.getPrivate();
		String pemFileCopy = "d:\\temp\\privateKey-copy2.pem";
		String newPassword = "0okm,lp-";
		OutputStream pemFileCopyOutputStream = new FileOutputStream(pemFileCopy);
		KeyFileUtil.savePEM(privateKey, newPassword, pemFileCopyOutputStream);
		
		// 重新读入
		FileInputStream inCopy = new FileInputStream(pemFileCopy);
		KeyPair keyPairCopy = KeyFileUtil.getPrivateKeyFromPemFormatFile(inCopy, newPassword);
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
		InputStream publicKeyFile = KeyFileUtilTest.class.getResourceAsStream("/publicKey.pem");
		String password = null;
		PublicKey publicKey = KeyFileUtil.getPublicKeyFromPemFormatFile(publicKeyFile, password);

		String pemFileCopy = "d:\\temp\\publicKey-copy.pem";
		KeyFileUtil.savePEM(publicKey, pemFileCopy);
		
		// 重新读入
		FileInputStream inCopy = new FileInputStream(pemFileCopy);
		PublicKey publicKeyCopy = KeyFileUtil.getPublicKeyFromPemFormatFile(inCopy, password);
		
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
	public void getKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		InputStream keyStoreFile = KeyFileUtilTest.class.getResourceAsStream("/test.pfx");
		KeyStore keyStore = KeyFileUtil.getKeyStore(keyStoreFile, "PKCS12", "111111");
		Enumeration<String> aliases = keyStore.aliases();
		while(aliases.hasMoreElements()) {
			String aliase = aliases.nextElement();
			logger.info("aliase:", aliase);
		}		
	}
	
	/**
	 * 读和存证书
	 * @throws Exception
	 */
	@Test
	public void saveLoadX509Certificate() throws Exception {
		InputStream keyStoreFile = KeyFileUtilTest.class.getResourceAsStream("/test.pfx");
		KeyStore keyStore = KeyFileUtil.getKeyStore(keyStoreFile, "PKCS12", "111111");
		Certificate certificate = keyStore.getCertificate("mbp");
		String certFile = "d:\\temp\\mbp.crt";
		KeyFileUtil.saveCertificate(certificate, certFile);
		
		Certificate certificate2 = KeyFileUtil.loadCertificate(certFile);
		// 输出后的证书再次读取进来，两个证书应该一样
		assertTrue(certificate.equals(certificate2));
	}
}
