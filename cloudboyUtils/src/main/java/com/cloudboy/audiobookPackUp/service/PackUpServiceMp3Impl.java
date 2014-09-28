package com.cloudboy.audiobookPackUp.service;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.log4j.Logger;
import org.farng.mp3.MP3File;
import org.farng.mp3.id3.ID3v1;

public class PackUpServiceMp3Impl implements PackUpService {
	
	private static final Logger logger = Logger.getLogger(PackUpServiceMp3Impl.class);

	@Override
	public void packUp(File folder, String[] removeStrings) {
		if(folder == null) {
			throw new IllegalArgumentException("The parameter 'folder' must be set.");
		}
		if(!folder.exists()) {
			throw new IllegalArgumentException("The folder" + folder.getPath() + " does not exist.");
		}
		
		File[] files = folder.listFiles(getFilenameFilerter());
		for(File file : files) {
			dealFile(file, removeStrings);
		}
	}
	
	protected FilenameFilter getFilenameFilerter() {
		return new Mp3Filter();
	}
	
	protected void dealFile(File file, String[] removeStrings) {
		File folder = file.getParentFile();
		String oldFileName = file.getName();
		String newFileName = oldFileName;
		for(int i=0; i<removeStrings.length; i++) {
			newFileName = newFileName.replaceAll(removeStrings[i], "");
		}
		File dest = new File(folder, newFileName);
		if(!newFileName.equals(oldFileName)) {
			logger.info("old File Name:" + oldFileName + " is changed to: " + newFileName);
			file.renameTo(dest);
		}
		
		changeTitle(dest);		
	}
	
	private void changeTitle(File file) {
		MP3File mp3;
		try {
			mp3 = new MP3File(file);
			String newTitle = file.getName().replace(".mp3", "");
			ID3v1 v1 = mp3.getID3v1Tag();
			logger.info("v1:" + v1.getSongTitle());
			v1.setSongTitle(newTitle);
			mp3.setID3v1Tag(v1);
			mp3.save();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
