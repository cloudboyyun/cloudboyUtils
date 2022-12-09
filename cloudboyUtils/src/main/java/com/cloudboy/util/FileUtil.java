package com.cloudboy.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.cloudboy.util.lang.StringUtils;

public class FileUtil {
	public static void writeFile(File folder, String fileName, String content) throws IOException {
		if(!folder.isDirectory()) {
			throw new java.lang.IllegalArgumentException("folder is not correct");
		}
		if(StringUtils.isEmpty(fileName)) {
			throw new java.lang.IllegalArgumentException("Please tell me the file name.");
		}
		File newFile = new File(folder, fileName);
		if(newFile.exists()) {
			newFile.delete();
		}
		FileWriter fileWriter = new FileWriter(newFile);
		fileWriter.write(content);
		fileWriter.close();
	}
}
