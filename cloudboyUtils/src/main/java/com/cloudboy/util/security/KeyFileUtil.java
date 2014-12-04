package com.cloudboy.util.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PasswordFinder;

public class KeyFileUtil {
	final private static String DEFAULT_KEYSTORE_ALGORITHM = "PKCS12";
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	/**
	 * 从PEM格式的私钥密钥文件中获取密钥对
	 * @param in 私钥密钥文件
	 * @param rootMiyuePwd 密码
	 * @return 私钥可以推导出公钥，所以返回的是包含私钥和公钥的密钥对
	 * @throws IOException 
	 * @throws Exception
	 */
	public static KeyPair getPrivateKeyFromPemFormatFile(final InputStream pemFile,
			final String password) throws IOException {
		PEMReader reader = new PEMReader(new InputStreamReader(pemFile), new PasswordFinder() {
			public char[] getPassword() {
				if(password == null) {
					return new char[]{};
				}
				return password.toCharArray();
			}

		});
		Object obj = reader.readObject();
		reader.close();
		KeyPair key = (KeyPair) obj;
		return key;
	}
	
	/**
	 * 把私钥保存为PEM格式文件
	 * @param key 私钥
	 * @param password 密码 
	 * @param pemFilePath 输出的pem文件全路径
	 * @throws IOException
	 */
	public static void savePEM(final PrivateKey key, final String password,	OutputStream pemFileOutputStream) throws IOException {
		PEMWriter writer = new PEMWriter(new OutputStreamWriter(pemFileOutputStream));
		if(password == null) {
			writer.writeObject(key);
		} else {
			writer.writeObject(key, "DESEDE", password.toCharArray(),
				new SecureRandom());
		}
		writer.close();
	}
	
	/**
	 * 虽然一般公钥会存在证书(crt格式）里，但也能保存为pem格式（虽然一般不会这么干），但不能加密码。而且OpenSSL好像也不知道怎么读取这个文件。
	 * @param key 公钥
	 * @param pemFilePath 输出的pem文件全路径
	 * @throws IOException
	 */
	public static void savePEM(final PublicKey key, final String pemFilePath) throws IOException {
		PEMWriter writer = new PEMWriter(new FileWriter(pemFilePath));
		writer.writeObject(key);
		writer.close();
	}
	
	/**
	 * 从PEM格式的公钥密钥文件中获取公钥.
	 * 通常公钥不会存储为PEM格式，而是存储在证书(crt格式)里。但使用本类的savePEM方法，也能把公钥保存为PEM格式。
	 * @param in 公钥密钥文件
	 * @param password 密码
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public static PublicKey getPublicKeyFromPemFormatFile(final InputStream pemFile,
			final String password) throws IOException  {
		PEMReader reader = new PEMReader(new InputStreamReader(pemFile), new PasswordFinder() {
			public char[] getPassword() {
				if(password == null) {
					return new char[]{};
				}
				return password.toCharArray();
			}

		});
		Object obj = reader.readObject();
		reader.close();
		PublicKey key = (PublicKey) obj;		
		return key;
	}
	
	/**
	 * 从KeyStore文件中生成KeyStore对象
	 * @param storeAlgorithm keyStore算法。为空默认为：PKCS12格式
	 * @param path keyStore文件路径
	 * @param storePassword KeyStore密码
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static KeyStore getKeyStore(final InputStream keyStoreFile, final String storeAlgorithm, 
			final String storePassword) throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException {
		String storeAlgorithm1 = storeAlgorithm;
		if(storeAlgorithm1 == null) {
			storeAlgorithm1 = DEFAULT_KEYSTORE_ALGORITHM;
		}
		KeyStore ks = KeyStore.getInstance(storeAlgorithm);
		String pwd = StringUtils.defaultString(storePassword);
		ks.load(keyStoreFile, pwd.toCharArray());
		keyStoreFile.close();
		return ks;
	}
	
	/**
	 * 导出证书
	 * @param certificate
	 * @param certPath
	 * @throws IOException 
	 * @throws CertificateEncodingException 
	 * @throws Exception
	 */
	public static void saveCertificate(Certificate certificate,
			String certPath) throws CertificateEncodingException, IOException  {
		FileOutputStream stream = new FileOutputStream(certPath);
		stream.write(certificate.getEncoded());
		stream.close();
	}
	
	/**
	 * 读取证书
	 * @param certPath
	 * @return
	 * @throws Exception
	 */
	public static X509Certificate loadCertificate(String certPath)
			throws Exception {
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		FileInputStream inputStream = new FileInputStream(certPath);
		X509Certificate certificate = (X509Certificate) factory
				.generateCertificate(inputStream);
		return certificate;
	}
}
