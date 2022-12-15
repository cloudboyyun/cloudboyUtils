package com.cloudboy.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import com.cloudboy.util.FileUtil;

public class PdfUtil {
	private final static String IMAGE_TYPE = "png";
	
	/**
	   *    将PDF按页，拆解为一张张图片，使用了PDFBox库
	 * @param pdfFile
	 * @return
	 * @throws IOException
	 */
	public static List<PdfImagePage> pdf2png(File pdfFile) throws IOException {
		String fileName = pdfFile.getName();
		List<PdfImagePage> result = new ArrayList<PdfImagePage>();
		PDDocument doc = Loader.loadPDF(pdfFile);
		PDFRenderer renderer = new PDFRenderer(doc);
		int pageCount = doc.getNumberOfPages();
		int pageNum = 0;
		for (int page = 0; page < pageCount; page++) {
			BufferedImage image = renderer.renderImageWithDPI(page, 144);
			File imageFile = new File(pdfFile.getParent(), fileName + "_" + (page + 1) + "." + IMAGE_TYPE);
			ImageIO.write(image, IMAGE_TYPE, imageFile);
			PdfImagePage pdfImagePage = new PdfImagePage();
			pdfImagePage.setPage(pageNum++);
			pdfImagePage.setImageFile(imageFile);
			result.add(pdfImagePage);
		}
		return result;
	}
	
	/**
	 * 读取pdf内容。
	 * 注意：无法读出pdf中图片包含的文字
	 * @param pdfFile
	 * @param startPage 起始页（从1开始）。如果为空，则从第一页开始
	 * @param endPage 终止页,如果为空，则读到最后一页
	 * @return
	 * @throws IOException
	 */
	public static String readPdf(File pdfFile, Integer startPage, Integer endPage) throws IOException {
		PDDocument doc = Loader.loadPDF(pdfFile);
		PDFTextStripper textStripper = new PDFTextStripper();
		if(startPage != null) {
			textStripper.setStartPage(startPage);
		}
		if(endPage != null) {
			textStripper.setEndPage(endPage);
		}
		String content = textStripper.getText(doc);
		return content;
	}
	
	public static void main(String[] args) {
		String sFile = "D:\\temp\\4\\国寿乐鑫宝终身寿险-15629.pdf_1.png";
		File file = new File(sFile);
		try {
			String content = PdfUtil.readPdf(file, 3, 7);
			File folder = new File("d:\\temp\\3");
			if(!folder.exists()) {
				folder.mkdir();
			}
			FileUtil.writeFile(folder, "codes.csv", content);
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
