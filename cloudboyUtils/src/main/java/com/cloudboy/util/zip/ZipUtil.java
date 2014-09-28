package com.cloudboy.util.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipUtil {
	
	public static void unzip(File file, File targetFolder) throws ZipException, IOException {
		if(file == null) {
			throw new IllegalArgumentException("The parameter file must be set.");
		}
		
		// Prepare the target folder
		File folder = targetFolder;
		if(folder == null) {
			folder = file.getParentFile();
		}
		if(!folder.exists()) {
			folder.mkdirs();
		}
		
		ZipFile zipFile = new ZipFile(file);
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file), Charset.forName("GBK"));
		ZipEntry zipEntry = null;
		BufferedInputStream bin = new BufferedInputStream(zipInputStream);
		
		while ((zipEntry = zipInputStream.getNextEntry()) != null) {
			String fileName = zipEntry.getName();
			System.out.println("name:" + fileName);
			File temp = new File(folder, fileName);
			System.out.println("temp.path:" + temp.getPath());
			if(zipEntry.isDirectory()) {
				temp.mkdir();
			} else {
				OutputStream os = new FileOutputStream(temp);
				int len = 0;
				while ((len = bin.read()) != -1) {
					os.write(len);
				}
				os.close();
			}
		}
		bin.close();
		zipInputStream.close();		
		zipFile.close();
	}
	
	public static void main(String args[]) {
		File file = new File("e:\\downloads\\武动乾坤\\武动乾坤[001-020集](播音——蜡笔小勇){有声下吧www.Ysx8.net}.zip");
		try {
			ZipUtil.unzip(file, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


