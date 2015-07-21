package com.cloudboy.util.barcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

public class ZXingTest {
	
	private static File folder = new File("d:\\work\\二维码");

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncode() throws Exception {
		String text = "287813935866036888";   
        int width = 100;
        int height = 100;
        String format = "png";   
        Hashtable<EncodeHintType, String> hints= new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");   
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height,hints);   
        File outputFile = new File(folder, "new.png");   
        MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
	}
	
	@Test
	public void testDecode() throws Exception {
		String[] files = {"new.png", "微信二维码(bill99邮件).jpg", "支付宝登陆二维码.png", "支付宝付款码.jpg", };
		for(String fileString : files) {
			File file = new File(folder, fileString);
			decode(file);
		}
	}
	
	public void decode(File file) throws Exception {
        BufferedImage bufferedImage = null;  
        bufferedImage = ImageIO.read(file);
        
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);  
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));  
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();  
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");  
        Result result = null;  
        result = new MultiFormatReader().decode(bitmap, hints);
        BarcodeFormat barcodeFormat = result.getBarcodeFormat();
        System.out.println(file.getName());
        System.out.println(result.getText());
        System.out.println(barcodeFormat.toString());
        System.out.println("--------------------------------");
	}
}
