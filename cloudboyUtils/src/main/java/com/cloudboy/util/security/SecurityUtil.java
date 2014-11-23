package com.cloudboy.util.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PasswordFinder;

public class SecurityUtil {
	
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
	public static void savePEM(final PrivateKey key, final String password,
			final String pemFilePath) throws IOException {
		PEMWriter writer = new PEMWriter(new FileWriter(pemFilePath));
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
	
	public static void saveX509Certificate(X509Certificate certificate,
			String rootcertPath) throws Exception {
		FileOutputStream stream = new FileOutputStream(rootcertPath);
		stream.write(certificate.getEncoded());
		stream.close();
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

	public static byte[] RSASign(PrivateKey key, byte[] src) throws Exception {
		java.security.Signature sign = java.security.Signature.getInstance(
				"SHA1withRSA", "BC");
		sign.initSign(key);
		sign.update(src);
		return sign.sign();
	}

	public static boolean RSAVerifySign(PublicKey key, byte[] crypt, byte[] src)
			throws Exception {

		java.security.Signature sign = java.security.Signature.getInstance(
				"SHA1withRSA", "BC");
		sign.initVerify(key);
		sign.update(src);
		return sign.verify(crypt);
	}

	public static X509Certificate getCertificate(String rootCertPath)
			throws Exception {
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		FileInputStream inputStream = new FileInputStream(rootCertPath);
		X509Certificate certificate = (X509Certificate) factory
				.generateCertificate(inputStream);
		return certificate;
	}

	public static void write(String file, byte[] data) throws Exception {
		FileOutputStream outputStream = new FileOutputStream(file);
		outputStream.write(data);
		outputStream.close();
	}

	public static byte[] read(String file) throws Exception {
		FileInputStream stream = new FileInputStream(file);
		byte[] by = new byte[stream.available()];
		stream.read(by);
		stream.close();
		return by;
	}

	public static byte[] encrypt(Key key, byte[] b, String suan)
			throws Exception {
		Cipher newcipher = Cipher.getInstance(suan);
		newcipher.init(Cipher.ENCRYPT_MODE, key);
		return newcipher.doFinal(b);
	}

	public static byte[] encryptByKouling(byte[] s, String suan, String kou)
			throws Exception {
		PBEKeySpec spec = new PBEKeySpec(kou.toCharArray());
		SecretKeyFactory factory = SecretKeyFactory.getInstance(suan);
		SecretKey key = factory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance(suan);
		PBEParameterSpec pps = new PBEParameterSpec(new byte[] { 0xF, 0x0, 0x1,
				0x2, 0x3, 0x4, 0x5, 0x6 }, 1);
		cipher.init(Cipher.ENCRYPT_MODE, key, pps);
		byte[] debyte = cipher.doFinal(s);
		return debyte;
	}

	public static byte[] decryptByKouling(byte[] s, String suan, String kou)
			throws Exception {
		PBEKeySpec spec = new PBEKeySpec(kou.toCharArray());
		SecretKeyFactory factory = SecretKeyFactory.getInstance(suan);
		SecretKey key = factory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance(suan);
		PBEParameterSpec pps = new PBEParameterSpec(new byte[] { 0xF, 0x0, 0x1,
				0x2, 0x3, 0x4, 0x5, 0x6 }, 1);
		cipher.init(Cipher.DECRYPT_MODE, key, pps);
		byte[] debyte = cipher.doFinal(s);
		return debyte;
	}
}
