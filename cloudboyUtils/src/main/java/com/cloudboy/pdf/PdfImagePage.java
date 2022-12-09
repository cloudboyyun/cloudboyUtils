package com.cloudboy.pdf;

import java.io.File;

public class PdfImagePage {
	/**
	 * 页码，从0�?始编�?
	 */
	private int page;
	
	/**
	 * pdf转换的图片文�?
	 */
	private File imageFile;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public File getImageFile() {
		return imageFile;
	}

	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}
}
