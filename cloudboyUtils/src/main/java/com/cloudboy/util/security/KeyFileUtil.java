package com.cloudboy.util.security;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PasswordFinder;

import com.cloudboy.util.lang.StringUtils;

public class KeyFileUtil {
	final private static String DEFAULT_KEYSTORE_TYPE = "PKCS12";
	final public static String KEYSTORE_TYPE_JKS = "jks";
	final public static String KEYSTORE_TYPE_PKCS12 = "PKCS12";
	
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
	 * 从PKCS12格式文件(*.pfx)中生成KeyStore对象
	 * @param keyStoreFile
	 * @param type keyStore的类型，jks, PKCS12。为空默认为：jks格式
	 * @param storePassword KeyStore密码
	 * @return
	 * @throws Exception
	 */
	public static KeyStore getKeyStore(final InputStream keyStoreFile, String type, 
			final String storePassword) throws Exception {
		if(type == null) {
			type = DEFAULT_KEYSTORE_TYPE;
		}
		KeyStore ks = KeyStore.getInstance(type);
		String pwd = StringUtils.trimToEmpty(storePassword);
		ks.load(keyStoreFile, pwd.toCharArray());
		keyStoreFile.close();
		return ks;
	}
	
	/**
	 * 以X.509格式的证书文件(*.crt)，初始化一个KeyStore
	 * @param certFile X.509格式的证书文件(*.crt)
	 * @return
	 * @throws Exception
	 */
	public static KeyStore getKeyStoreByCrtFile(InputStream certFile) throws Exception {
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore ks = KeyStore.getInstance(keyStoreType);
		ks.load(null, null);
		Certificate cert = loadCertificate(certFile);
		ks.setCertificateEntry("myServer", cert);
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
	 * @param certPath X.509格式的证书文件流
	 * @return
	 * @throws Exception
	 */
	public static X509Certificate loadCertificate(InputStream certFile)
			throws Exception {
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) factory
				.generateCertificate(certFile);
		return certificate;
	}
}
