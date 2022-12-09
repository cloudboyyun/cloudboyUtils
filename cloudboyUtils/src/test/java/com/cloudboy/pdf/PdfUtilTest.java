package com.cloudboy.pdf;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cloudboy.util.json.FastJsonUtils;

public class PdfUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void pdf2PngTest() {
		String sFile = "D:\\temp\\1\\中华人民共和国职业分类大典（2022年版）社会公示稿.pdf";
		File file = new File(sFile);
		try {
			List<PdfImagePage> list = PdfUtil.pdf2png(file);
			System.out.println(FastJsonUtils.toJSONString(list));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void readPdfTest() {
		String sFile = "D:\\temp\\1\\中华人民共和国职业分类大典（2022年版）社会公示稿.pdf";
		File file = new File(sFile);
		try {
			String content = PdfUtil.readPdf(file, null, null);
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void readPdfByPageTest() {
		String sFile = "D:\\temp\\1\\中华人民共和国职业分类大典（2022年版）社会公示稿.pdf";
		File file = new File(sFile);
		try {
			String content = PdfUtil.readPdf(file, 3, 7);
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
